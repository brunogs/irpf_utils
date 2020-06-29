package br.com.bgs.irpfutils.domain

object OperationAverage {

    fun computeOperationsAverage(items: List<Operation>): List<OperationSummary> {
        val operationsGrouped = items.groupBy { it.title?.cleanupAcronyms() }
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

    private fun String?.cleanupAcronyms() = this?.replace(" (ON|CI|S|ERS|ER|ED|EJ|NM|N\\d)".toRegex(), "")
        ?.replace(" #(\\d+)?".toRegex(), "")
        ?.replace("  +".toRegex(), " ")
        ?.trim()
}