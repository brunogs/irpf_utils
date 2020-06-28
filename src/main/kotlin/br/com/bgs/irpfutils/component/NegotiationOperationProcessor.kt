package br.com.bgs.irpfutils.component

import br.com.bgs.irpfutils.domain.Negotiation
import br.com.bgs.irpfutils.domain.Operation
import org.springframework.batch.item.ItemProcessor

class NegotiationOperationProcessor: ItemProcessor<Negotiation, Collection<Operation>> {

    override fun process(item: Negotiation): Collection<Operation>? {
        println(item)
        return item.operations.map {
            it.operationDate = item.negotiationDate
            it
        }
    }

}