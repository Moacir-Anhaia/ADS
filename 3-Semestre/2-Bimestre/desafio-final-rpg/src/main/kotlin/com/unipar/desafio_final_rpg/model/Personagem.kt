package com.unipar.desafio_final_rpg.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Personagem(
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE)
    val nome: String,
    var forca: Double,
    var velocidade: Double,
    var vida: Double,
)






/*
   open fun usarPoder(monstro: Monstro) {
        println("O herói usou o poder especial!!")
    }

    open fun atacar(monstro: Monstro) {
        println("$nome ataca ${monstro.nome}")
        if (this.velocidade >= monstro.velocidade) {
            println("Você é mais rapido, acertando o ataque.")
            monstro.vida -= this.forca
            println("Vida do ${monstro.nome} foi para: ${monstro.vida}")
        } else {
            println("O ${monstro.nome} desviou do seu ataque!")
        }
    }

    open fun defender(monstro: Monstro) {
        println(" $nome tentando se defender ${monstro.nome} ")
        if (this.velocidade > monstro.forca) {
            println("Você se defendeu do ataque")
        } else {
            println("Você falhou em se defender")
            this.vida -= monstro.forca
            println("Sua vida caiu para ${this.vida}")
        }
    }
}

class Guerreiro(var defesa: Double = 150.0) : Personagem(
    nome = "Guerreiro",
    forca = 100.0,
    velocidade = 100.0,
    vida = 100.0
) {
    override fun usarPoder(monstro: Monstro) {
        if (this.defesa >= monstro.forca) {
            this.defesa -= monstro.forca
            println("Usou a defesa!!")
        }
    }
}

class Mago(var magia: Double = Random.nextDouble(20.0,180.0)) : Personagem(
    nome = "Mago",
    forca = 100.0,
    velocidade = 100.0,
    vida = 100.0
) {
    override fun usarPoder(monstro: Monstro) {
        val poderespecial = this.magia + this.forca
        monstro.vida -= poderespecial
        println("Usou o poder especial, reduzindo a Vvida do ${monstro.nome} para ${monstro.vida}")
    }
}

class Ladino(var sagacidade: Double = Random.nextDouble(50.0,150.0)) : Personagem(
    nome = "Ladino",
    forca = 100.0,
    velocidade = 100.0,
    vida = 100.0
) {
    override fun usarPoder(monstro: Monstro) {
        println("Usou um ataque furtivo!")
        monstro.vida -= this.sagacidade + this.forca
        println("Reduzindo a vida do ${monstro.nome} para ${monstro.vida}")
    }
}
* */