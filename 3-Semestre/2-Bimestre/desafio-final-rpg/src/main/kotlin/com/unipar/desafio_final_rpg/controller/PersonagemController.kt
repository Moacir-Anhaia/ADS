package com.unipar.desafio_final_rpg.controller

import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// @RestController: combina @Controller + @ResponseBody
// Todos os metodos retornam dados diretamente no corpo da resposta HTTP (JSON)
// @RequestMapping("/personagem"): todos os endpoints desta classe comecam com /personagem
@RestController
@RequestMapping("/personagem")
class PersonagemController(
    // Injecao de dependencia
    val personagemService: PersonagemService
) {

    @PostMapping("/salvar")
    fun salvar(@RequestBody personagem: Personagem): ResponseEntity<Personagem> {
        val salvo = personagemService.salvar(personagem)
        return ResponseEntity.ok(salvo)
    }

    @GetMapping("/todos")
    fun buscarTodos(): ResponseEntity<List<Personagem>> {
        val lista = personagemService.buscarTodos()
        return ResponseEntity.ok(lista)
    }

    @GetMapping("/{nome}")
    fun buscarPorNome(@PathVariable nome: String): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(personagemService.buscarPorNome(nome))
        } catch (e: NoSuchElementException) {
            ResponseEntity.status(404).body(e.message)
        }
    }

    @PutMapping("/editar")
    fun editar(@RequestBody personagem: Personagem): ResponseEntity<Any> {
        return try {
            ResponseEntity.ok(personagemService.editar(personagem))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(e.message)
        }
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<String> {
        personagemService.excluir(id)
        return ResponseEntity.ok("Personagem com ID $id removido com sucesso")
    }
}
