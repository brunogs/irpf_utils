package br.com.bgs.irpfutils.config

import br.com.bgs.irpfutils.component.NegotiationOperationProcessor
import br.com.bgs.irpfutils.component.NegotiationReader
import br.com.bgs.irpfutils.domain.Negotiation
import br.com.bgs.irpfutils.domain.Operation
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.file.FlatFileItemWriter
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor
import org.springframework.batch.item.file.transform.DelimitedLineAggregator
import org.springframework.batch.item.file.transform.LineAggregator
import org.springframework.batch.item.file.transform.RecursiveCollectionLineAggregator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource


@Configuration
class NegotiationPdfImporterBatchConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Value("\${negotiations.path}")
    val inputResources: Array<Resource>,
    @Value("\${operations.output}")
    val operationsOutputPath: String
) {

    @Bean
    fun negotiationPdfImporter(negotiationStep: Step): Job {
        return jobBuilderFactory.get("negotiationPdfImporter")
            .incrementer(RunIdIncrementer())
            .flow(negotiationStep)
            .end()
            .build()
    }

    @Bean
    fun negotiationStep(): Step {
        return stepBuilderFactory["negotiationStep"]
            .chunk<Negotiation, Collection<Operation>>(1)
            .reader(negotatiationOperationsReader())
            .processor(NegotiationOperationProcessor())
            .writer(negotiationOperationsWriter())
            .build()
    }

    @Bean
    fun negotatiationOperationsReader(): MultiResourceItemReader<Negotiation> {
        val negotiationReader = NegotiationReader().apply {
            setName("negotatiationReader")
        }
        return MultiResourceItemReader<Negotiation>().apply {
            setName("negotitationsPdf")
            setResources(inputResources)
            setDelegate(negotiationReader)
        }
    }

    @Bean
    fun negotiationOperationsWriter(): FlatFileItemWriter<Collection<Operation>> {
        val outputResource = FileSystemResource(operationsOutputPath)
        val operationLine = DelimitedLineAggregator<Operation>().apply {
            setDelimiter(",")
            setFieldExtractor(BeanWrapperFieldExtractor<Operation>().apply {
                setNames(arrayOf("operationType", "market", "title", "quantity", "unitPrice", "operationPrice", "operationDate"))
            })
        }
        val lineAggregator: LineAggregator<Collection<Operation>> = RecursiveCollectionLineAggregator<Operation>().apply {
            setDelegate(operationLine)
        }
        return FlatFileItemWriter<Collection<Operation>>().apply {
            setResource(outputResource)
            setHeaderCallback { writer ->
                writer.write("operationType, market, title, quantity, unitPrice, operationPrice, operationDate")
            }
            setAppendAllowed(false)
            setShouldDeleteIfExists(true)
            setLineAggregator(lineAggregator)
        }
    }
}