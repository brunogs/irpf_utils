package br.com.bgs.irpfutils.config

import br.com.bgs.irpfutils.component.ConsoleItemWriter
import br.com.bgs.irpfutils.component.OperationToStatusInvestProcessor
import br.com.bgs.irpfutils.domain.Operation
import br.com.bgs.irpfutils.domain.StatusInvestOperation
import br.com.bgs.irpfutils.repository.StockRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource


@Configuration
class StatusInvestBatchConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    val stockRepository: StockRepository,
    @Value("\${status-invest.input}")
    val operationsResource: Resource,
    @Value("\${status-invest.output}")
    val operationsOutputPath: String
) {

    @Bean
    fun operationsToStatusInvestSheet(operationsToStatusInvestStep: Step): Job {
        return jobBuilderFactory.get("operationsToStatusInvestSheet")
            .incrementer(RunIdIncrementer())
            .flow(operationsToStatusInvestStep)
            .end()
            .build()
    }

    @Bean
    fun operationsToStatusInvestStep(operationsSummaryWriter: ConsoleItemWriter): Step {
        return stepBuilderFactory["operationsToStatusInvestStep"]
            .chunk<Operation, StatusInvestOperation>(1)
            .reader(operationsStatusInvestReader())
            .processor(OperationToStatusInvestProcessor(stockRepository))
            .writer(statusInvestWriter())
            .build()
    }

    @Bean
    fun operationsStatusInvestReader(): FlatFileItemReader<Operation> {
        val lineMapper = DefaultLineMapper<Operation>().apply {
            setLineTokenizer(DelimitedLineTokenizer().apply {
                setNames("operationType", "market", "title", "quantity", "unitPrice", "operationPrice", "operationDate")
            })
            setFieldSetMapper(BeanWrapperFieldSetMapper<Operation>().apply {
                setTargetType(Operation::class.java)
            })
        }
        return FlatFileItemReader<Operation>().apply {
            setLinesToSkip(1)
            setLineMapper(lineMapper)
            setResource(operationsResource)
        }
    }

    @Bean
    fun statusInvestWriter(): FlatFileItemWriter<StatusInvestOperation> {
        val outputResource = FileSystemResource(operationsOutputPath)
        val operationLine = DelimitedLineAggregator<StatusInvestOperation>().apply {
            setDelimiter(",")
            setFieldExtractor(BeanWrapperFieldExtractor<StatusInvestOperation>().apply {
                setNames(arrayOf("dataOperacao", "categoria", "codigoAtivo", "operacao", "quantidade", "precoUnitario", "corretora", "corretagem", "taxas", "impostos", "irrf"))
            })
        }
        return FlatFileItemWriter<StatusInvestOperation>().apply {
            setResource(outputResource)
            setHeaderCallback { writer ->
                writer.write("Data operação, Categoria, Código Ativo, Operação C/V, Quantidade, Preço unitário, Corretora, Corretagem, Taxas, Impostos, IRRF")
            }
            setAppendAllowed(false)
            setShouldDeleteIfExists(true)
            setLineAggregator(operationLine)
        }
    }
}