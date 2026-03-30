import kotlin.random.Random

class Pista(
    val carros: List<Motorista>,
    val posicoes: Int,
    val voltas: Int = 10,
    var clima: Int
) {
    fun posicao(): List<Pair<String, Double>> {
        clima = Random.nextInt(1, 3)
        val resultado = mutableListOf<Pair<String, Double>>()

        for (motorista in carros) {
            val combustivel = motorista.carro.consumo * voltas
            if (combustivel > motorista.carro.tanque) {
                println("${motorista.piloto.nome} com o  ${motorista.carro.nome} está fora da corrida por falta de combustível!!")
                continue
            }
            var pontuacao: Double = 0.0
            pontuacao += motorista.carro.velocidade
            pontuacao += motorista.carro.aceleracao
            pontuacao += motorista.carro.freio
            pontuacao += motorista.carro.resistencia
            pontuacao += motorista.piloto.habilidade

            if (clima == 1) {
                pontuacao += motorista.carro.freio * 1.5
                pontuacao -= 10
            } else {
                pontuacao += 10
            }

            resultado.add((motorista.piloto.nome + " com " + motorista.carro.nome) to pontuacao)
        }
        return resultado.sortedByDescending { it.second }
    }

    fun placar() {
        val resultadoCorrida = posicao()
        println("-----PLACAR------")

        if (clima == 1) {
            println("O clima está chuvoso")
        } else {
            println("O clima está ensolarado")
        }

        resultadoCorrida.forEach{
        println("${it.first} - ${it.second}")
        }

        println("Vencedor ${resultadoCorrida[0].first}")
    }

}

