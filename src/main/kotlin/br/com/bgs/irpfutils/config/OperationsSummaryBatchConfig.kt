package br.com.bgs.irpfutils.config

import br.com.bgs.irpfutils.component.ConsoleItemWriter
import br.com.bgs.irpfutils.domain.Operation
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.batch.item.file.mapping.DefaultLineMapper
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource


@Configuration
class OperationsSummaryBatchConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Value("\${operations.input}")
    val operationsResource: Resource
) {

    @Bean
    fun operationsSummarizing(operationsStep: Step): Job {
        return jobBuilderFactory.get("operationsSummarizing")
            .incrementer(RunIdIncrementer())
            .flow(operationsStep)
            .end()
            .build()
    }

    @Bean
    fun operationsStep(operationsSummaryWriter: ConsoleItemWriter): Step {
        return stepBuilderFactory["operationsStep"]
            .chunk<Operation, Operation>(1)
            .reader(operationsReader())
            .writer(operationsSummaryWriter())
            .build()
    }

    @Bean
    fun operationsReader(): FlatFileItemReader<Operation> {
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
    fun operationsSummaryWriter(): ConsoleItemWriter {
        return ConsoleItemWriter()
    }
}