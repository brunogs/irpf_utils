package br.com.bgs.irpfutils.domain

import br.com.bgs.irpfutils.extensions.cleanupAcronyms

object OperationAverage {

    fun computeOperationsAverage(items: List<Operation>): List<OperationSummary> {
        val operationsGrouped = items
            .filter { it.operationType == OperationType.C }
            .groupBy { it.title?.cleanupAcronyms() }
        return operationsGrouped.map {
            val operationsByCompany = it.value
            val averageOperations = operationsByCompany.map { operation -> OperationQuantityPrice(operation.unitPrice!!, operation.quantity!!.toBigDecimal()) }
                .reduce { acc, operation ->
                    val quantity = operation.quantity + acc.quantity
                    val averagePrice = ((operation.quantity * operation.averagePrice) + (acc.quantity * acc.averagePrice)) / quantity
                    OperationQuantityPrice(averagePrice, quantity)
                }
            OperationSummary(
                company = it.key!!,
                averagePrice = averageOperations.averagePrice,
                quantity = averageOperations.quantity,
                cost = averageOperations.averagePrice * averageOperations.quantity,
                firstPrice = operationsByCompany.first().unitPrice!!,
                lastPrice = operationsByCompany.last().unitPrice!!
            )
        }
    }

}