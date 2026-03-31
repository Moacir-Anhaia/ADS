val pilha = mutableMapOf<Int, String>()
var nivel = 0 //Essa variável controla os níveis da pilha
var aux = "" + ""

fun main() {
    val regex = Regex("^[0-9+\\-*/()]+$")

    println("Digite aqui sua expressão matemática: ")
    val exp = readln().replace(" ", "")

    if (exp.length < 9) {
        println("A operação de conter mais de 9 caracteres")
        return
    }

    if (exp.matches(regex)) {
        println("Expressão aceita")
    } else {
        println("Expressão negada")
        return
    }

    exp.forEach { c ->
        //variável de controle
        var nivelAtual = nivel
        prioridade(c)

        if (nivel == nivelAtual) {
            //Formando a equação nova em um determinado nivel da pilha
            aux += c.toString()//A Classe String ignora concatenação nula
            pilha[nivel] = aux //Informação velha concatenando com a nova
        } else {
            aux = ""
        }

    }//Fim do FOREACH
/*
    pilha.forEach { (i, s) ->
        println("Nivel da pilha: $i")
        println("Valor da pilha: $s")
    }
*/
    //Resolve do nível mais alto para o mais baixo
    var resultadoAnterior = ""
    for (n in pilha.keys.sortedDescending()) {
        val operacao = pilha[n]!! + resultadoAnterior //Concatena o resultado do nível anterior
        val resultado = calcular(operacao)
        println("Nivel $n: $operacao = $resultado")
        resultadoAnterior = resultado.toInt().toString() //Guarda o resultado para o próximo nível
    }

    println("Resultado final: $resultadoAnterior")

}
//Fim do MAIN


fun prioridade(c: Char): Int {
    when (c) {
        '(' -> {
            return nivel++
        }//subir de nivel da pilha
        ')' -> {
            return nivel--
        }//descer de nivel da pilha
        else -> {
            return nivel
        }//continuo no mesmo nivel
    }
}

fun calcular(expr: String): Double {
    var numero = ""
    var resultado = 0.0
    var operador = '+' //Começa somando para pegar o primeiro número corretamente

    for (c in expr) {
        if (c.isDigit()) {
            numero += c //Acumula os dígitos do número
        } else {
            //Chegou num operador, aplica a operação acumulada
            resultado = when (operador) {
                '+' -> resultado + numero.toDouble()
                '-' -> resultado - numero.toDouble()
                '*' -> resultado * numero.toDouble()
                '/' -> resultado / numero.toDouble()
                else -> resultado
            }
            numero = ""
            operador = c //Guarda o próximo operador
        }
    }

    //Processa o último número
    resultado = when (operador) {
        '+' -> resultado + numero.toDouble()
        '-' -> resultado - numero.toDouble()
        '*' -> resultado * numero.toDouble()
        '/' -> resultado / numero.toDouble()
        else -> resultado
    }

    return resultado
}