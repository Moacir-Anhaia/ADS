fun main() {
    atividade()
}
fun atividade() {
    var sair = true
    var acerto: Int = 0
    val listaBaralho = mutableListOf<String>(
        "Coringa",
        "Ás de Copas", "2 de Copas", "3 de Copas", "4 de Copas", "5 de Copas", "6 de Copas", "7 de Copas", "8 de Copas", "9 de Copas", "10 de Copas", "Valete de Copas", "Dama de Copas", "Rei de Copas",
        "Ás de Ouros", "2 de Ouros", "3 de Ouros", "4 de Ouros", "5 de Ouros", "6 de Ouros", "7 de Ouros", "8 de Ouros", "9 de Ouros", "10 de Ouros", "Valete de Ouros", "Dama de Ouros", "Rei de Ouros",
        "Ás de Paus", "2 de Paus", "3 de Paus", "4 de Paus", "5 de Paus", "6 de Paus", "7 de Paus", "8 de Paus", "9 de Paus", "10 de Paus", "Valete de Paus", "Dama de Paus", "Rei de Paus",
        "Ás de Espadas", "2 de Espadas", "3 de Espadas", "4 de Espadas", "5 de Espadas", "6 de Espadas", "7 de Espadas", "8 de Espadas", "9 de Espadas", "10 de Espadas", "Valete de Espadas", "Dama de Espadas", "Rei de Espadas",
        "Coringa")

    println("------ Bem vindo ao jogo de Adivinhaçao de cartas ------")
    println("As cartas disponivel no baralho são:")

    listaBaralho.chunked(10).forEach { baralho ->
        println(baralho)
    }
    println("Como jogar: apenas digite o nome da carta e se acertar ganhe um ponto")
    listaBaralho.shuffle()
    val pilhaBaralho: ArrayDeque<String> = ArrayDeque<String>(listaBaralho)

    do {
        if(pilhaBaralho.isEmpty()) {
            println("As cartas acabaram!! recomece o jogo")
            break
        }
        println("Informe a sua carta: ")
        val cartaJogador = readln()
        if (cartaJogador.equals(pilhaBaralho.last(), ignoreCase = true)) {
            println("Você acertou a carta")
            acerto += 1
        } else {
            println("Você errou a carta!!")
            println("A carta era: ${pilhaBaralho.last()}")
        }
        pilhaBaralho.removeLast()
        println("Deseja continuar: S ou N")
        var continuar = readln()
        when (continuar) {
            "S", "s" -> println("Seu placar: $acerto acertos")

            "N", "n" -> {
                println("Obrigado por jogar!!!")
                println("Seu placar: $acerto acertos")
                sair = false
            }

            else -> {
                println("Informe corretamente!!! (S ou N)")
            }
        }

    } while (sair)

}