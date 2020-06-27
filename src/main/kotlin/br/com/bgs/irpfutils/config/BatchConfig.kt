package br.com.bgs.irpfutils.config

import br.com.bgs.irpfutils.component.ConsoleItemWriter
import br.com.bgs.irpfutils.component.NegotiationReader
import br.com.bgs.irpfutils.domain.Negotiation
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.item.file.MultiResourceItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource


@Configuration
@EnableBatchProcessing
class BatchConfig(
    val jobBuilderFactory: JobBuilderFactory,
    val stepBuilderFactory: StepBuilderFactory,
    @Value("\${app.negotiations.path}")
    val inputResources: Array<Resource>
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
            .chunk<Negotiation, Negotiation>(1)
            .reader(multiResourceReader())
            .writer(writer())
            .build()
    }

    @Bean
    fun multiResourceReader(): MultiResourceItemReader<Negotiation> {
        val resourceItemReader = MultiResourceItemReader<Negotiation>()
        resourceItemReader.setName("negotitationsPdf")
        resourceItemReader.setResources(inputResources)
        val negotiationReader = NegotiationReader()
        negotiationReader.setName("negotatiationReader")
        resourceItemReader.setDelegate(negotiationReader)
        return resourceItemReader
    }

    @Bean
    fun writer(): ConsoleItemWriter<Negotiation> {
        return ConsoleItemWriter<Negotiation>()
    }
}