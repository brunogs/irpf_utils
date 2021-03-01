package br.com.bgs.irpfutils.component

import br.com.bgs.irpfutils.domain.Operation
import br.com.bgs.irpfutils.domain.StatusInvestOperation
import br.com.bgs.irpfutils.repository.StockRepository
import org.springframework.batch.item.ItemProcessor

class OperationToStatusInvestProcessor(
    private val stockRepository: StockRepository
): ItemProcessor<Operation, StatusInvestOperation> {

    override fun process(item: Operation): StatusInvestOperation {
        println("process item: $item")
        return StatusInvestOperation(
            dataOperacao = item.operationDate,
            categoria = if (item.title?.contains("FII") == true) "FII's" else "Ações",
            codigoAtivo = item.title?.let { stockRepository.findCode(it) },
            operacao = item.operationType.toString(),
            quantidade = item.quantity,
            precoUnitario = item.unitPrice,
            //TODO extract from file
            corretora = "CLEAR CORRETORA"
        )
    }

}