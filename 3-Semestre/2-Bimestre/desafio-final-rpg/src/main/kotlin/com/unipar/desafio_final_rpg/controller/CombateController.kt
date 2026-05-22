package com.unipar.desafio_final_rpg.controller

import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
@RestController
@RequestMapping("/combate")
class CombateController (
    // @Value injeta o valor da propriedade "rival.url" do application.properties
    // Ex: rival.url=http://192.168.1.12:8080/ouvir
    // Isso evita hardcodar IPs/URLs no código
    @Value("\${rival.url}") private val rivalUrl: String,
    // RestClient é o cliente HTTP do Spring (substituto moderno do RestTemplate)
    // Usado para fazer requisições HTTP para outros servidores
    val restClient: RestClient,
    val personagemService: PersonagemService

    ){
    var minhaEscolhaDePersonagem = Personagem(null, "", 0.0, 0.0, 0.0 )
    // para acessar o atacar eu uso: localhost:8080/combate/atacar
    @GetMapping
    fun atacar() {
        println("Atacando meu rival")
        try {
            restClient.put()
                .uri(rivalUrl).contentType(MediaType.TEXT_PLAIN)
                .body(minhaEscolhaDePersonagem.forca.toString())
                .retrieve().toBodilessEntity()
        } catch (e: Exception) {
            println("Deu erro: ${e.message}")
        }
    }

    @PutMapping("/apanhar", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun apanhar(@RequestBody forca: Int) {
        println("Escolha seu personagem: ")
        //1° saber quem apanhou
        //2° saber o quanto apanhou
       print( personagemService.buscarTodos().forEach {
           println(it.nome)
       })

        val personagem = readln() // Aqui o usuario vai digitar o nome que quer jogar
        try {
            minhaEscolhaDePersonagem = personagemService.buscarPorNome(personagem)
            minhaEscolhaDePersonagem.vida -= forca // desconta a vida de acordo com o soco
            personagemService.salvar(minhaEscolhaDePersonagem) // salva a vida no banco
        }catch (e: Exception){
            println("Personagem não encontrado")
        }
    }
    @GetMapping("/escolha/{nome}")
    fun escolherMeuPersonagem(@PathVariable nome : String) : Personagem {
        println("Escolha seu personagem: ")
        //1° saber quem apanhou
        //2° saber o quanto apanhou
        print( personagemService.buscarTodos().forEach {
            println(it.nome)
        })

        val personagem = readln() // Aqui o usuario vai digitar o nome que quer jogar
        try {
             minhaEscolhaDePersonagem = personagemService.buscarPorNome(nome)
            println("Meu personagem escolhido $minhaEscolhaDePersonagem")
        }catch (e: Exception){
            println("Personagem não encontrado")
        }
        return minhaEscolhaDePersonagem
    }
}