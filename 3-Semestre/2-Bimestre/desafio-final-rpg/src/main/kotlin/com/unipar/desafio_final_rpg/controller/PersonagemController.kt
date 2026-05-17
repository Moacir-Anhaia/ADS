package com.unipar.desafio_final_rpg.controller


import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient

// @RestController combina @Controller + @ResponseBody
// Indica que essa classe é um controlador HTTP e que todos os métodos
// retornam dados direto no corpo da resposta (não renderiza páginas HTML)
@RestController
class PersonagemController (
    // @Value injeta o valor da propriedade "rival.url" do application.properties
    // Ex: rival.url=http://192.168.1.12:8080/ouvir
    // Isso evita hardcodar IPs/URLs no código
    @Value("\${rival.url}") private val rivalUrl: String,

    // Injeção de dependência via construtor (padrão do Spring/Kotlin)
    // O Spring cria e gerencia as instâncias de PersonagemService e RestClient
    val personagemService: PersonagemService,

    // RestClient é o cliente HTTP do Spring (substituto moderno do RestTemplate)
    // Usado para fazer requisições HTTP para outros servidores
    val restClient: RestClient
) {
    // @GetMapping: este endpoint responde a requisições HTTP GET em "/msg"
    // Serve como o "gatilho" para enviar uma mensagem ao rival
    // Uso: GET http://localhost:8080/msg?mensagem=Olá
    @GetMapping("/msg")
    fun mandarMensagemParaPersonagemRival(
        @RequestParam mensagem: String
    ): String { // Adicione o tipo de retorno String
        return try {
            restClient.post()
                .uri(rivalUrl)
                .contentType(MediaType.TEXT_PLAIN)
                .body(mensagem)
                .retrieve()
                .toBodilessEntity()

            "Mensagem enviada com sucesso!" // Retorno em caso de sucesso
        } catch (e: Exception) {
            "Erro ao enviar: ${e.message}" // Retorno em caso de erro
        }
    }

    // @PostMapping: este endpoint responde a requisições HTTP POST em "/ouvir"
    // É aqui que esta máquina RECEBE mensagens enviadas pelo rival
    // consumes: garante que só aceita requisições com Content-Type: text/plain
    // (rejeita outros formatos como JSON ou XML com erro 415 Unsupported Media Type)
    @PostMapping("/ouvir", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun receberMensagemDoPersonagemRival(
        // @RequestBody lê o corpo da requisição HTTP e mapeia para a variável
        @RequestBody mensagem: String
    ) {
        println("Mensagem recebida do rival: $mensagem")
    }

    @GetMapping
    fun atacar(poder: Int) {
        println("Atacando meu rival")
        try {
            restClient.post()
                .uri(rivalUrl).contentType(MediaType.TEXT_PLAIN)
                .body(poder.toString()).retrieve().toBodilessEntity()
        } catch (e: Exception) {
            println("Deu erro: ${e.message}")
        }
    }

    @PostMapping("/apanhar", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun apanhar(@RequestBody poder: Int) {
        println("Seu personagem perdeu $poder de vida")
    }

    @PostMapping("/salvar")
    fun salvarMeuPersonagem(@RequestBody personagem: Personagem) {
        personagemService.salvar(personagem)
    }
}