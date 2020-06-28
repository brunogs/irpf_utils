package br.com.bgs.irpfutils.component

import br.com.bgs.irpfutils.domain.Negotiation
import br.com.bgs.irpfutils.domain.Operation
import br.com.bgs.irpfutils.domain.OperationType
import io.github.jonathanlink.PDFLayoutTextStripper
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader
import org.springframework.core.io.Resource
import java.math.BigDecimal


class NegotiationReader(private var resource: Resource? = null): AbstractItemCountingItemStreamItemReader<Negotiation>(), ResourceAwareItemReaderItemStream<Negotiation> {

    private var document: PDDocument? = null
    private var pdfContent: String? = null

    companion object {
        val NEGOTIATION_PATTERN = "1-BOVESPA\\s+(.{1})\\s+([^\\s]+)\\s+(.+)".toRegex()
        val NUMBERS_PATTERN = " \\d*[.,]?\\d*[.,]?\\d+ ".toRegex()

        const val QUANTITY = 0
        const val UNIT_PRICE = 1
        const val OPERATION_PRICE = 2
    }

    override fun doOpen() {
        if (!resource!!.exists()) {
            println("file not exists")
            return
        }
        if (!resource!!.isReadable) {
            println("file not readable")
            return
        }
        this.document = PDDocument.load(resource!!.inputStream)
    }

    override fun doRead(): Negotiation? {
        return if (pdfContent == null) {
            val pdfTextStripper = PDFLayoutTextStripper()
            this.pdfContent = pdfTextStripper.getText(document)

            val operationsTitle = "Negócios  realizados"
            val startOfOperations = this.pdfContent!!.indexOf(operationsTitle)
            val lines = this.pdfContent!!.substring(startOfOperations + operationsTitle.length).split("\\n".toRegex())

            val operations = lines.drop(2).takeWhile { it.contains("1-BOVESPA") }
                .map {
                    val (type, market, remainderContent) = NEGOTIATION_PATTERN.find(it)!!.destructured
                    val numbers = NUMBERS_PATTERN.findAll(remainderContent).map { it.value }.toList()
                    var title = remainderContent
                    numbers.forEach {
                        title = title.replace(it, "")
                    }
                    Operation(
                        operationType = OperationType.valueOf(type),
                        market = market,
                        title = title.trim().dropLast(1).trim(),
                        quantity = numbers[QUANTITY].trim().toInt(),
                        unitPrice = BigDecimal(numbers[UNIT_PRICE].trim().replace(".", "").replace(",", ".")),
                        operationPrice = BigDecimal(numbers[OPERATION_PRICE].trim().replace(".", "").replace(",", "."))
                    )
                }

            Negotiation(
                operations = operations,
                netValueOperations = BigDecimal.ZERO,
                settlementTax = BigDecimal.ZERO,
                registerTax = BigDecimal.ZERO
            )
        } else {
            null
        }
    }

    override fun doClose() {
        this.document?.run { close() }
    }

    override fun setResource(resource: Resource) {
        this.resource = resource
        this.pdfContent = null
    }
}