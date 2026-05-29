package com.unipar.desafio_final_rpg.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

// @RestController: marca como controlador HTTP que retorna dados no corpo da resposta
// @RequestMapping("/chat"): todos os endpoints desta classe comecam com /chat
@RestController
@RequestMapping("/chat")
class ChatController(

    // Injeta a URL do /chat/ouvir do rival definida em application.properties
    @Value("\${rival.chat.url}") private val rivalChatUrl: String,

    // RestClient: cliente HTTP para enviar mensagens ao servidor do rival
    val restClient: RestClient

) {
    // Nome do jogador definido ao escolher a classe
    // Formato: "Joao (Mago)"
    var nomeJogador: String = ""

    @GetMapping("/definirNome")
    fun definirNome(
        @RequestParam nome: String,
        @RequestParam classe: String
    ): ResponseEntity<String> {
        // Monta o identificador unico: "Joao (Mago)"
        nomeJogador = "$nome ($classe)"
        println("[CHAT] Nome do jogador definido: $nomeJogador")
        return ResponseEntity.ok("Nome definido: $nomeJogador")
    }


    @GetMapping("/msg")
    fun enviarMensagem(@RequestParam mensagem: String): ResponseEntity<String> {

        if (nomeJogador.isEmpty()) {
            return ResponseEntity.badRequest().body("Nome nao definido! Use /chat/definirNome primeiro.")
        }


        val msgFormatada = "$nomeJogador: $mensagem"
        println("[CHAT] Enviando: $msgFormatada")

        return try {

            restClient.post()
                .uri(rivalChatUrl)
                .contentType(MediaType.TEXT_PLAIN)
                .body(msgFormatada)
                .retrieve()
                .toBodilessEntity()

            ResponseEntity.ok("Mensagem enviada: $msgFormatada")
        } catch (e: Exception) {
            println("[CHAT] Erro ao enviar: ${e.message}")
            ResponseEntity.internalServerError().body("Erro ao enviar mensagem: ${e.message}")
        }
    }


    @PostMapping("/ouvir", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun receberMensagem(@RequestBody mensagem: String): ResponseEntity<String> {
        // Imprime a mensagem recebida no console para o jogador local ver
        println("\n[CHAT] $mensagem\n")
        return ResponseEntity.ok("ok")
    }
}
