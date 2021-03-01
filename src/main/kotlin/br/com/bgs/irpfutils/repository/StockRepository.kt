package br.com.bgs.irpfutils.repository

import br.com.bgs.irpfutils.domain.Stock
import br.com.bgs.irpfutils.domain.StockType
import br.com.bgs.irpfutils.extensions.cleanupAcronyms
import org.springframework.stereotype.Repository

//TODO use some service or a lib
@Repository
class StockRepository {

    private fun retrieveMyStocks(): Set<Stock> {
        return setOf(
            Stock(code = "ABEV3", companyName = "AMBEV", type = StockType.ON),
            Stock(code = "B3SA3", companyName = "B3", type = StockType.ON),
            Stock(code = "BBSE3", companyName = "BBSEGURIDADE", type = StockType.ON),
            Stock(code = "CARD3", companyName = "CARDSYST", type = StockType.ON),
            Stock(code = "ENBR3", companyName = "ENERGIAS", type = StockType.ON),
            Stock(code = "EGIE3", companyName = "ENGIE", type = StockType.ON),
            Stock(code = "FLRY3", companyName = "FLEURY", type = StockType.ON),
            Stock(code = "GRND3", companyName = "GRENDENE", type = StockType.ON),
            Stock(code = "IRBR3", companyName = "IRBBRASIL", type = StockType.ON),
            Stock(code = "ITSA3", companyName = "ITAUSA", type = StockType.ON),
            Stock(code = "LINX3", companyName = "LINX", type = StockType.ON),
            Stock(code = "TAESA", companyName = "TAEE11", type = StockType.UNT),
            Stock(code = "UNIP6", companyName = "UNIPAR", type = StockType.PNB),
            Stock(code = "WIZS3", companyName = "WIZ", type = StockType.ON),
            Stock(code = "WEGE3", companyName = "WEG", type = StockType.ON),
            Stock(code = "VALE3", companyName = "VALE", type = StockType.ON),
            Stock(code = "AZUL4", companyName = "AZUL", type = StockType.PN),

            Stock(code = "VISC11", companyName = "FII VINCI SC", type = StockType.PN),
            Stock(code = "BCFF11", companyName = "FII BC FFII", type = StockType.PN),
            Stock(code = "HGLG11", companyName = "FII CSHG LOG", type = StockType.PN),
            Stock(code = "HGBS11", companyName = "FII HEDGEBS", type = StockType.PN),
            Stock(code = "HGRE11", companyName = "FII HG REAL", type = StockType.PN),
            Stock(code = "KNRI11", companyName = "FII KINEA", type = StockType.PN),
            Stock(code = "MCCI11", companyName = "FII MAUA", type = StockType.PN),
            Stock(code = "RBRP11", companyName = "FII RBR PROP", type = StockType.PN),
            Stock(code = "VGIP11", companyName = "FII VALORAIP", type = StockType.PN)
        )
    }

    fun findCode(title: String): String? {
        return retrieveMyStocks().find { title.contains(it.companyName) || title.contains(it.code) }?.code
    }

}