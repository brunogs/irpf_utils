package br.com.bgs.irpfutils.component

import br.com.bgs.irpfutils.domain.Operation
import br.com.bgs.irpfutils.domain.OperationAverage
import br.com.bgs.irpfutils.domain.OperationQuantityPrice
import br.com.bgs.irpfutils.domain.OperationSummary
import org.springframework.batch.core.annotation.AfterStep
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.ItemWriter

@JobScope
class ConsoleItemWriter(
    val items: MutableSet<Operation> = mutableSetOf()
) : ItemWriter<Operation> {

    @Throws(Exception::class)
    override fun write(items: List<Operation>) {
        for (item in items) {
            this.items.add(item)
        }
    }

    @AfterStep
    fun afterWriterItems() {
        val summaries = OperationAverage.computeOperationsAverage(items)
        summaries.forEach {
            println(it)
        }
    }

}