package br.com.bgs.irpfutils

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableBatchProcessing
@SpringBootApplication
class IrpfUtilsApplication

fun main(args: Array<String>) {
	runApplication<IrpfUtilsApplication>(*args)
}
