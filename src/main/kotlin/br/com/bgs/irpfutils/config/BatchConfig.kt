package br.com.bgs.irpfutils.config

import br.com.bgs.irpfutils.component.NegotiationOperationProcessor
import br.com.bgs.irpfutils.component.NegotiationReader
import br.com.bgs.irpfutils.domain.Negotiation
import br.com.bgs.irpfutils.domain.Operation
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
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
class BatchConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Value("\${negotiations.path}")
    val inputResources: Array<Resource>,
    @Value("\${operations.output}")
    val operationsOutputPath: String
) {

    @Bean
    fun importNegotiationFiles(step1: Step): Job {
        return jobBuilderFactory.get("importNegotiationFiles")
            .incrementer(RunIdIncrementer())
            .flow(step1)
            .end()
            .build()
    }

    @Bean
    fun step1(): Step {
        return stepBuilderFactory["step1"]
            .chunk<Negotiation, Collection<Operation>>(1)
            .reader(multiResourceReader())
            .processor(NegotiationOperationProcessor())
            .writer(writer())
            .build()
    }

    @Bean
    fun multiResourceReader(): MultiResourceItemReader<Negotiation> {
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
    fun writer(): FlatFileItemWriter<Collection<Operation>> {
        val outputResource = FileSystemResource(operationsOutputPath)

        val writer = FlatFileItemWriter<Collection<Operation>>().apply {
            setResource(outputResource)
            setAppendAllowed(true)
        }
        val operationLine = DelimitedLineAggregator<Operation>().apply {
            setDelimiter(",")
            setFieldExtractor(BeanWrapperFieldExtractor<Operation>().apply {
                setNames(arrayOf("operationType", "market", "title", "quantity", "unitPrice", "operationPrice", "operationDate"))
            })
        }
        val lineAggregator: LineAggregator<Collection<Operation>> = RecursiveCollectionLineAggregator<Operation>().apply {
            setDelegate(operationLine)
        }

        writer.setLineAggregator(lineAggregator)
        return writer
    }
}