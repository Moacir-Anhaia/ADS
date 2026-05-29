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

// @Entity: marca a classe como uma entidade JPA, ou seja, sera mapeada para uma tabela no banco de dados
// @Inheritance: define a estrategia de heranca SINGLE_TABLE — todas as subclasses ficam na mesma tabela
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_personagem")
open class Personagem(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    var nome: String = "",
    var forca: Double = 0.0,
    var velocidade: Double = 0.0,
    var vida: Double = 0.0,
    var vidaMax: Double = 0.0,
    var emDefesa: Boolean = false
)

@Entity
@DiscriminatorValue("GUERREIRO")
class Guerreiro(
    nome: String = "Guerreiro",
    forca: Double = Random.nextDouble(50.0, 100.0),
    velocidade: Double = Random.nextDouble(50.0, 80.0),
    vida: Double = 200.0,
    vidaMax: Double = 200.0,

    var defesa: Double = Random.nextDouble(50.0, 150.0)
) : Personagem(null, nome, forca, velocidade, vida, vidaMax)

@Entity
@DiscriminatorValue("MAGO")
class Mago(
    nome: String = "Mago",
    forca: Double = Random.nextDouble(50.0, 120.0),
    velocidade: Double = Random.nextDouble(55.0, 85.0),
    vida: Double = 120.0,
    vidaMax: Double = 120.0,

    var magia: Double = Random.nextDouble(50.0, 150.0)
) : Personagem(null, nome, forca, velocidade, vida, vidaMax)

@Entity
@DiscriminatorValue("LADINO")
class Ladino(
    nome: String = "Ladino",
    forca: Double = Random.nextDouble(50.0, 110.0),
    velocidade: Double = Random.nextDouble(70.0, 120.0),
    vida: Double = 150.0,
    vidaMax: Double = 150.0,

    var sagacidade: Double = Random.nextDouble(50.0, 150.0)
) : Personagem(null, nome, forca, velocidade, vida, vidaMax)
