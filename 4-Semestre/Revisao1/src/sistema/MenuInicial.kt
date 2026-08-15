package sistema
import produto.CaixaDaAgua
import sistema.caixadaagua.listarCaixa
import sistema.caixadaagua.cadastrarNovaCaixa

fun menuInicial() {
    var listaDeTest : MutableList<CaixaDaAgua> = mutableListOf()
    do {
        println("0 - SAIR")
        println("1 - CADASTRAR CAIXA DE ÁGUA")
        println("2 - EDITAR CAIXA DE ÁGUA")
        println("3 - LISTAR CAIXA DE ÁGUA")
        println("4 - EXCLUIR CAIXA DE ÁGUA")
        val op = readln()
        when (op) {
            "1" -> cadastrarNovaCaixa(listaDeTest)
            "2" -> println("")
            "3" -> listarCaixa(listaDeTest)
            "4" -> println("")
            "0" -> {
                println("Adeus amigo")
                break
            }

            else -> println("Opção inválida!")
        }
    } while (true)
}