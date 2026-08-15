package sistema.caixadaagua

import enumeradores.Cor
import enumeradores.Material
import produto.CaixaDaAgua

fun cadastrarNovaCaixa(listaDeTeste: MutableList<CaixaDaAgua>) {
    println("Digite a marca: ")
    val marca = readln()

    println("Digite a modelo: ")
    val modelo = readln()

    println("Digite a largura: ")
    val largura = readln().toDouble()
    println("Digite a altura: ")
    val altura = readln().toDouble()
    println("Digite a profundidade: ")
    val profundidade = readln().toDouble()
    val dimensao = mutableListOf<Double>(largura, altura, profundidade)

    println("Escolha a cor: ")
    Cor.entries.forEach { cor ->
        println("${cor.ordinal} - ${cor.name}")
    }
    println("Numero da cor: ")
    val cor = readln().toInt()

    println("Escolha o material: ")
    Material.entries.forEach { material ->
        println("${material.ordinal} - ${material.name}")
    }
    println("Numero do material: ")
    val material = readln().toInt()

    println("Escolha o formato: ")
    val formato = readln()

    println("Qual é o preço: ")
    val preco = readln().toBigDecimal()


    listaDeTeste.add(
        CaixaDaAgua(
            marca = marca,
            modelo = modelo,
            dimensao = dimensao,
            cor = Cor.entries[cor],
            material = Material.entries[material],
            formato = formato,
            preco = preco
        )
    )
}
