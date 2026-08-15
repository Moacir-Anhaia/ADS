package sistema.caixadaagua

import produto.CaixaDaAgua
import java.text.NumberFormat
import java.util.Locale

fun listarCaixa(listaDeTeste : MutableList<CaixaDaAgua>) {
    val formatador = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var caixaDaAgua : List<CaixaDaAgua> = listOf()
    listaDeTeste.forEach { c ->
        println("Modelo: ${c.modelo}")
        println("Marca: ${c.marca}")
        println("Dimensão: ${c.dimensao}")
        println("Cor: ${c.cor}")
        println("Formato: ${c.formato}")
        println("Material: ${c.material}")
        println("Preço: ${formatador.format(c.preco)}")

    }

}