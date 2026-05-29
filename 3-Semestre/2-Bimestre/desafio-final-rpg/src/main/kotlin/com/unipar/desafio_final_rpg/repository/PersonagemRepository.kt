package com.unipar.desafio_final_rpg.repository

import com.unipar.desafio_final_rpg.model.Personagem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

// @Repository: marca a interface como componente de acesso a dados do Spring
@Repository
interface PersonagemRepository : JpaRepository<Personagem, Long> {
    // Spring Data gera automaticamente a query "SELECT * FROM personagem WHERE nome = ?"
    // Retorna Optional para evitar NullPointerException caso o nome nao exista
    fun findByNome(nome: String): Optional<Personagem>
}
