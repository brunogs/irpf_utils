package br.com.bgs.irpfutils.component

import br.com.bgs.irpfutils.domain.Negotiation
import io.github.jonathanlink.PDFLayoutTextStripper
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader
import org.springframework.core.io.Resource
import java.math.BigDecimal


class NegotiationReader(private var resource: Resource? = null): AbstractItemCountingItemStreamItemReader<Negotiation>(), ResourceAwareItemReaderItemStream<Negotiation> {

    private var document: PDDocument? = null

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

    override fun doRead(): Negotiation {
        val pdfTextStripper = PDFLayoutTextStripper()
        val text = pdfTextStripper.getText(document)
        println(text)
        return Negotiation(
            operations = listOf(),
            netValueOperations = BigDecimal.ZERO,
            settlementTax = BigDecimal.ZERO,
            registerTax = BigDecimal.ZERO
        )
    }

    override fun doClose() {
        this.document?.run { close() }
    }

    override fun setResource(resource: Resource) {
        this.resource = resource
    }
}