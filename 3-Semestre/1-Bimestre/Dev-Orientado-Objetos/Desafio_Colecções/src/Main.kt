fun main() {
supermercado()
}

fun supermercado() {
    var sair: Boolean = true
    val listaEstoque = mutableMapOf<String, Double>()

    do {
        println("------Bem vindo ao Supermercado!!!------")
        println("Escolha uma opção abaixo")
        println(
            "1 - Adicionar produto \n" +
                    "2 - Remover Produto \n" +
                    "3 - Editar valor do Produto \n" +
                    "4 - Lista do estoque\n" +
                    "5 - Sair"
        )
        val op: String = readln()
        when (op) {
            "1" -> {
                println("Adicionar Produto")
                println("Informe o nome do produto!")
                val nomeProduto = readln()
                println("Informe o valor do produto!")
                val precoProduto = readln().toDouble()

                listaEstoque[nomeProduto] = precoProduto

                println("$nomeProduto com o valor R$ $precoProduto, adicionado com sucesso!")
            }

            "2" -> {
                println("-----Remover Produto-----")
                println("Informe o nome do produto que deseja remover!")

                val nomeProdutoRemove = readln()
                listaEstoque.remove(nomeProdutoRemove)

                println("$nomeProdutoRemove removido com sucesso!")
            }

            "3" -> {
                println("Editar o valor do produto!")
                println("Informe o nome do produto que queira mudar o valor!")
                val nomeEditar = readln()
                println("Informe o novo valor do produto!")
                val valorEditar = readln().toDouble()
                listaEstoque[nomeEditar] = valorEditar

                println("$nomeEditar com o valor editado para: R$ $valorEditar!")
            }

            "4" -> {
                println("------Produtos Disponível------")
                listaEstoque.forEach { produto, valor ->
                    println("$produto com o valor de: R$: $valor")
                }
            }

            "5" -> {
                println("Obrigado! Volte sempre. ")
                sair = false
            }
            else -> println("Informe uma opção valida!!! (1 a 5)")
        }

    } while (sair)

}
    





