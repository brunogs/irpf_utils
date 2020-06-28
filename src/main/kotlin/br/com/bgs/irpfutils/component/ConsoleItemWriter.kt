package br.com.bgs.irpfutils.component

import org.springframework.batch.core.annotation.AfterStep
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.ItemWriter

@JobScope
class ConsoleItemWriter<T>(
    val items: MutableSet<T> = mutableSetOf()
) : ItemWriter<T> {

    @Throws(Exception::class)
    override fun write(items: List<T>) {
        for (item in items) {
            this.items.add(item)
        }
    }

    @AfterStep
    fun afterWriterItems() {
        println(items)
    }

}