package com.unipar.desafio_final_rpg.model

import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import kotlin.random.Random

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Define estratégia de herança em uma unica tabela
@DiscriminatorColumn(name = "tipo_personagem") // Nome da coluna no BD
open class Personagem(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null, // id gerenciado automatico atraves de sequencia 0.1.2.3
    var nome: String = "",
    var forca: Double = 0.0,
    var velocidade: Double = 0.0,
    var vida: Double = 0.0
)

@Entity
@DiscriminatorValue("GUERREIRO")
class Guerreiro(
    nome: String = "Guerreiro",
    forca: Double = 100.0,
    velocidade: Double = 100.0,
    vida: Double = 100.0,
    var defesa: Double = Random.nextDouble(50.0, 200.0) // atributo unico
) : Personagem(null, nome, forca, velocidade, vida)

@Entity
@DiscriminatorValue("MAGO")
class Mago(
    nome: String = "Mago",
    forca: Double = 100.0,
    velocidade: Double = 100.0,
    vida: Double = 100.0,
    var magia: Double = Random.nextDouble(50.0, 200.0) // atributo unico
) : Personagem(null, nome, forca, velocidade, vida)

@Entity
@DiscriminatorValue("LADINO")
class Ladino(
    nome: String = "Ladino",
    forca: Double = 100.0,
    velocidade: Double = 100.0,
    vida: Double = 100.0,
    var sagacidade: Double = Random.nextDouble(50.0, 200.0) // atributo unico
) : Personagem(null, nome, forca, velocidade, vida)