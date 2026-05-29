package com.unipar.desafio_final_rpg.service

import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.repository.PersonagemRepository
import org.springframework.stereotype.Service

// @Service: marca a classe como componente de servico
@Service
class PersonagemService(
    // Injecao de dependencia via construtor
    val personagemRepository: PersonagemRepository
) {

    fun salvar(personagem: Personagem): Personagem {
        return personagemRepository.save(personagem)
    }

    fun buscarTodos(): List<Personagem> {
        return personagemRepository.findAll()
    }

    fun buscarPorNome(nome: String): Personagem {
        return personagemRepository.findByNome(nome)
            .orElseThrow { NoSuchElementException("Personagem '$nome' nao encontrado no banco") }
    }

    fun buscarPorId(id: Long): Personagem {
        return personagemRepository.findById(id)
            .orElseThrow { NoSuchElementException("Personagem com ID $id nao encontrado") }
    }


    fun editar(personagem: Personagem): Personagem {
        // Verifica se o personagem existe antes de tentar editar
        buscarPorId(personagem.id ?: throw IllegalArgumentException("ID nao informado para edicao"))
        return personagemRepository.save(personagem)
    }


    fun excluir(id: Long) {
        personagemRepository.deleteById(id)
    }
}
