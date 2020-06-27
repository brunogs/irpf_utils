package br.com.bgs.irpfutils.component

import org.springframework.batch.item.ItemWriter

class ConsoleItemWriter<T> : ItemWriter<T> {

    @Throws(Exception::class)
    override fun write(items: List<T>) {
        for (item in items) {
            println(item)
        }
    }

}