package com.unipar.desafio_final_rpg.controller

import com.unipar.desafio_final_rpg.model.Guerreiro
import com.unipar.desafio_final_rpg.model.Ladino
import com.unipar.desafio_final_rpg.model.Mago
import com.unipar.desafio_final_rpg.model.Personagem
import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import kotlin.random.Random

// @RestController: diz ao Spring que esta classe e um controlador HTTP
// @RequestMapping: define o prefixo de URL para todos os endpoints comecam com /combate (ex: /combate/atacar, /combate/apanhar)
@RestController
@RequestMapping("/combate")
class CombateController(

    @Value("\${rival.combate.url}") private val rivalCombateUrl: String,

    @Value("\${rival.base.url}") private val rivalBaseUrl: String,


    val restClient: RestClient,

    val personagemService: PersonagemService

) {
    // Personagem ativo nesta sessao de combate, comeca com valores vazios e e preenchido ao chamar GET
    var meuPersonagem = Personagem(null, "", 0.0, 0.0, 0.0, 0.0)

    // Nome do jogador desta sessao

    var nomeJogador = ""

    // Controla de quem e a vez no sistema

    var minhavez = false

   //verifica se encerrou
    var rivalEncerrou = false

    //verefica se o rival deu o pronto
    var rivalPronto = false

    //verefica a ultima ação
    var ultimaAcaoRival = ""


    @GetMapping("/escolha/{classe}")
    fun escolherPersonagem(
        @PathVariable classe: String,
        @RequestParam nomeJogador: String
    ): ResponseEntity<String> {

        // Guarda o nome do jogador para usar nas notificacoes e logs
        this.nomeJogador = nomeJogador

        // Instancia a subclasse
        val personagem: Personagem = when (classe.uppercase()) {
            "GUERREIRO" -> Guerreiro(nome = classe)
            "MAGO"      -> Mago(nome = classe)
            "LADINO"    -> Ladino(nome = classe)
            else -> return ResponseEntity.badRequest().body("Classe invalida: $classe")
        }

        // Salva no banco usando o JPA

        meuPersonagem = personagemService.salvar(personagem)

        println("[COMBATE] Personagem criado: ${meuPersonagem.nome} | Jogador: $nomeJogador | Vida: ${meuPersonagem.vida}")
        return ResponseEntity.ok("${meuPersonagem.nome} pronto! Jogador: $nomeJogador")
    }

    @GetMapping("/sortearTurno")
    fun sortearTurno(): ResponseEntity<String> {

        // Gera numero aleatorio entre 1 e 999
        val meuNumero = Random.nextInt(1, 1000)
        println("[TURNO] Meu numero sorteado: $meuNumero")

        return try {
            // Envia meu numero ao rival via POST e recebe o numero dele como resposta
            val numeroRival = restClient.post()
                .uri("$rivalBaseUrl/combate/receberSorteio")
                .contentType(MediaType.TEXT_PLAIN)           // Tipo do conteudo texto simples
                .body(meuNumero.toString())                   // //retorna em string
                .retrieve()
                .body(String::class.java)?.toIntOrNull() ?: 0 // Converte resposta para Int

            println("[TURNO] Numero do rival: $numeroRival")

            when {

                meuNumero > numeroRival -> {
                    minhavez = true // Define que e minha vez de agir
                    println("[TURNO] Voce comeca atacando!")
                    ResponseEntity.ok("PRIMEIRO:Voce tirou $meuNumero, rival tirou $numeroRival. VOCE COMECA!")
                }
                // Rival tirou maior: RIVAL comeca
                meuNumero < numeroRival -> {
                    minhavez = false
                    println("[TURNO] Rival comeca atacando.")
                    ResponseEntity.ok("SEGUNDO:Voce tirou $meuNumero, rival tirou $numeroRival. RIVAL COMECA!")
                }
                // Empate
                else -> {
                    println("[TURNO] Empate! Sorteando novamente...")
                    sortearTurno()
                }
            }
        } catch (e: Exception) {
            // Se o rival nao respondeu, assume que comeco atacando por padrao
            minhavez = true
            ResponseEntity.ok("PRIMEIRO:Rival nao respondeu. Voce comeca por padrao!")
        }
    }

    @PostMapping("/receberSorteio", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun receberSorteio(@RequestBody numeroRivalStr: String): ResponseEntity<String> {

        val inicio = System.currentTimeMillis()
        val timeoutMs = 60_000L
        while (!rivalPronto && System.currentTimeMillis() - inicio < timeoutMs) {
            Thread.sleep(200) //verefica o pronto do rival
        }


        val meuNumero   = Random.nextInt(1, 100)
        val numeroRival = numeroRivalStr.toIntOrNull() ?: 0

        println("[TURNO] Rival sorteou: $numeroRival | Meu numero: $meuNumero")

        minhavez = meuNumero > numeroRival
        println("[TURNO] ${if (minhavez) "Eu comeco atacando!" else "Rival comeca atacando."}")

        return ResponseEntity.ok(meuNumero.toString())
    }

    //metodo da consulta da vez
    @GetMapping("/minhavez")
    fun consultarVez(): ResponseEntity<String> {
        return ResponseEntity.ok(if (minhavez) "SIM" else "NAO")
    }

   //consulta se o rival encerrou
    @GetMapping("/rivalEncerrou")
    fun consultarRivalEncerrou(): ResponseEntity<String> {
        return ResponseEntity.ok(if (rivalEncerrou) "SIM" else "NAO")
    }

   //consulta se rival está pronto
    @GetMapping("/rivalPronto")
    fun consultarRivalPronto(): ResponseEntity<String> {
        return ResponseEntity.ok(if (rivalPronto) "SIM" else "NAO")
    }

    //consulta qual foi a ultima ação
    @GetMapping("/ultimaAcao")
    fun ultimaAcao(): ResponseEntity<String> {
        return ResponseEntity.ok(ultimaAcaoRival)
    }


    @GetMapping("/atacar")
    fun atacar(): ResponseEntity<String> {

        // Valida se um personagem foi selecionado antes de atacar
        if (meuPersonagem.nome.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum personagem escolhido!")
        }

        if (!minhavez) {
            return ResponseEntity.badRequest().body("Nao e sua vez!")
        }

        meuPersonagem.emDefesa = false

        // Monta o payload com acao, dano e identificacao do atacante
        val payload = "ATAQUE|${meuPersonagem.forca}|$nomeJogador (${meuPersonagem.nome})"
        println("[COMBATE] ⚔ Atacando com forca ${meuPersonagem.forca}!")

        return try {
            // Envia o ataque ao rival e le a resposta para detectar derrota
            val resposta = restClient.post()
                .uri(rivalCombateUrl)
                .contentType(MediaType.TEXT_PLAIN)
                .body(payload)                     // Corpo: "ATAQUE|120.0|Joao (Guerreiro)"
                .retrieve()
                .body(String::class.java) ?: ""    // Le o corpo da resposta como String


            minhavez = false


            if (resposta.contains("DERROTADO")) {
                ResponseEntity.ok("VITORIA:⚔ Ataque de ${meuPersonagem.forca} acertou em cheio!")
            } else {

                ResponseEntity.ok("⚔ Ataque enviado! Dano: ${meuPersonagem.forca} | $resposta")
            }
        } catch (e: Exception) {
            println("[COMBATE] Erro ao atacar: ${e.message}")
            ResponseEntity.internalServerError().body("Erro ao atacar: ${e.message}")
        }
    }


    @GetMapping("/defender")
    fun defender(): ResponseEntity<String> {

        if (meuPersonagem.nome.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum personagem escolhido!")
        }
        if (!minhavez) {
            return ResponseEntity.badRequest().body("Nao e sua vez!")
        }


        meuPersonagem.emDefesa = true
        personagemService.salvar(meuPersonagem) // Persiste a flag no banco


        notificarRival("🛡 $nomeJogador (${meuPersonagem.nome}) entrou em DEFESA! Proximo ataque sera reduzido.")

        minhavez = false // Passa a vez para o rival apos defender
        println("[COMBATE] Em posicao de defesa! Vez do rival.")
        return ResponseEntity.ok("🛡 ${meuPersonagem.nome} em defesa! Aguardando o rival...")
    }


    @GetMapping("/poder")
    fun usarPoder(): ResponseEntity<String> {

        if (meuPersonagem.nome.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum personagem escolhido!")
        }
        if (!minhavez) {
            return ResponseEntity.badRequest().body("Nao e sua vez!")
        }

        // Cast seguro (as?): tenta converter meuPersonagem para a subclasse

        val guerreiro = meuPersonagem as? Guerreiro
        val mago      = meuPersonagem as? Mago
        val ladino    = meuPersonagem as? Ladino

        return when {


            guerreiro != null -> {
                meuPersonagem.emDefesa = true
                personagemService.salvar(meuPersonagem) // Persiste a flag de defesa

                notificarRival(" $nomeJogador (Guerreiro) ativou o ESCUDO! Absorcao: ${guerreiro.defesa}")
                minhavez = false // Passa a vez apos usar o poder
                println("[COMBATE]  Escudo ativado! Absorcao: ${guerreiro.defesa}")
                ResponseEntity.ok(" ESCUDO ativado! Absorcao: ${guerreiro.defesa}. Aguardando rival...")
            }


            mago != null -> {
                val dano    = meuPersonagem.forca + mago.magia //
                val payload = "BOLA_DE_FOGO|$dano|$nomeJogador (Mago)"
                println("[COMBATE]  Bola de Fogo! Dano: $dano")
                try {
                    val resposta = restClient.post()
                        .uri(rivalCombateUrl)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(payload)
                        .retrieve()
                        .body(String::class.java) ?: ""
                    minhavez = false
                    if (resposta.contains("DERROTADO")) {
                        ResponseEntity.ok("VITORIA: Bola de Fogo destruiu o rival! Dano: $dano")
                    } else {
                        ResponseEntity.ok("BOLA DE FOGO! Dano: $dano. Aguardando rival...")
                    }
                } catch (e: Exception) {
                    ResponseEntity.internalServerError().body("Erro ao usar poder: ${e.message}")
                }
            }

            ladino != null -> {

                val dano    = meuPersonagem.forca * (ladino.sagacidade / 100)
                val payload = "GOLPE_FURTIVO|$dano|$nomeJogador (Ladino)"
                println("[COMBATE]  Golpe Furtivo! Dano: $dano")
                try {
                    val resposta = restClient.post()
                        .uri(rivalCombateUrl)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body(payload)
                        .retrieve()
                        .body(String::class.java) ?: ""
                    minhavez = false
                    if (resposta.contains("DERROTADO")) {
                        ResponseEntity.ok("VITORIA: Golpe Furtivo eliminou o rival! Dano: $dano")
                    } else {
                        ResponseEntity.ok("GOLPE FURTIVO! Dano: $dano. Aguardando rival...")
                    }
                } catch (e: Exception) {
                    ResponseEntity.internalServerError().body("Erro ao usar poder: ${e.message}")
                }
            }

            else -> ResponseEntity.badRequest().body("Classe nao reconhecida")
        }
    }

    @PostMapping("/apanhar", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun apanhar(@RequestBody payload: String): ResponseEntity<String> {

        if (meuPersonagem.nome.isEmpty()) {
            return ResponseEntity.badRequest().body("Nenhum personagem em campo!")
        }
        //Payload separado por tres parte
        val partes        = payload.split("|")
        val acao          = partes.getOrElse(0) { "ATAQUE" }  // Tipo da acao recebida
        val danoStr       = partes.getOrElse(1) { "0" }        // Dano como String
        val identificacao = partes.getOrElse(2) { "Rival" }    // Quem atacou

        // Converte o dano para Double
        val danoRecebido = danoStr.toDoubleOrNull()
            ?: return ResponseEntity.badRequest().body("Dano invalido: $danoStr")

        val acaoStr = when (acao) {
            "ATAQUE"        -> " $identificacao usou ATAQUE! Dano recebido: $danoRecebido"
            "BOLA_DE_FOGO"  -> " $identificacao usou BOLA DE FOGO! Dano magico: $danoRecebido"
            "GOLPE_FURTIVO" -> " $identificacao usou GOLPE FURTIVO! Dano critico: $danoRecebido"
            else            -> " $identificacao atacou! Dano: $danoRecebido"
        }

        ultimaAcaoRival = acaoStr
        println("\n[COMBATE] $acaoStr")

        // Cast seguro para verificar se e Guerreiro
        val guerreiro = meuPersonagem as? Guerreiro

        // Calcula o dano final considerando o modo defesa ativo
        val danoFinal = when {

            guerreiro != null && meuPersonagem.emDefesa -> {
                val absorvido = minOf(guerreiro.defesa, danoRecebido)
                println("[COMBATE] 🛡 Escudo absorveu $absorvido de dano!")
                maxOf(0.0, danoRecebido - absorvido)
            }
            meuPersonagem.emDefesa -> {
                println("[COMBATE]  Defesa ativa! Dano reduzido a metade.")
                danoRecebido / 2
            }

            else -> danoRecebido
        }

        // Desconta o dano da vida atual
        meuPersonagem.vida    -= danoFinal
        meuPersonagem.vida     = maxOf(0.0, meuPersonagem.vida)
        meuPersonagem.emDefesa = false


        personagemService.salvar(meuPersonagem)

        minhavez = true

        println("[COMBATE] Vida restante: ${meuPersonagem.vida}/${meuPersonagem.vidaMax} | Minha vez!")


        return if (meuPersonagem.vida <= 0) {
            println("[COMBATE]  ${meuPersonagem.nome} foi DERROTADO!")
            ResponseEntity.ok(" DERROTADO")
        } else {
            ResponseEntity.ok("${meuPersonagem.nome} levou $danoFinal de dano. Vida restante: ${meuPersonagem.vida}/${meuPersonagem.vidaMax}")
        }
    }

    @PostMapping("/notificar", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun receberNotificacao(@RequestBody mensagem: String): ResponseEntity<String> {

        when (mensagem) {
            "PRONTO" -> {
                rivalPronto = true
                println("\n[NOTIFICACAO]  Rival esta pronto!")
            }

            "ENCERRADO" -> {
                rivalEncerrou = true
                println("\n[NOTIFICACAO]  O rival encerrou o jogo!")
            }

            else -> {
                ultimaAcaoRival = mensagem
                println("\n[NOTIFICACAO] $mensagem")
                minhavez = true
            }
        }

        return ResponseEntity.ok("ok")
    }

    @GetMapping("/resetarPronto")
    fun resetarPronto(): ResponseEntity<String> {
        rivalPronto = false
        println("[COMBATE]  rivalPronto resetado.")
        return ResponseEntity.ok("rivalPronto resetado.")
    }


    @GetMapping("/resetar")
    fun resetar(): ResponseEntity<String> {
        personagemService.buscarTodos().forEach { personagemService.excluir(it.id!!) }

        meuPersonagem   = Personagem(null, "", 0.0, 0.0, 0.0, 0.0)
        nomeJogador     = ""
        minhavez        = false
        rivalEncerrou   = false
        rivalPronto     = false
        ultimaAcaoRival = ""

        println("[COMBATE]  Partida resetada.")
        return ResponseEntity.ok("Partida resetada.")
    }


    @GetMapping("/status")
    fun status(): ResponseEntity<String> {
        if (meuPersonagem.nome.isEmpty()) return ResponseEntity.badRequest().body("Nenhum personagem em campo.")
        return ResponseEntity.ok(
            "Nome: ${meuPersonagem.nome} | Jogador: $nomeJogador | " +
            "Vida: ${meuPersonagem.vida}/${meuPersonagem.vidaMax} | " +
            "Forca: ${meuPersonagem.forca} | " +
            "Defesa: ${if (meuPersonagem.emDefesa) "SIM 🛡" else "NAO"} | " +
            "Minha vez: ${if (minhavez) "SIM" else "NAO"}"
        )
    }

    private fun notificarRival(mensagem: String) {
        try {
            restClient.post()
                .uri("$rivalBaseUrl/combate/notificar") // Endpoint de notificacao do rival
                .contentType(MediaType.TEXT_PLAIN)
                .body(mensagem)
                .retrieve()
                .toBodilessEntity() // Nao precisa ler o corpo da resposta
        } catch (e: Exception) {
            println("[AVISO] Nao foi possivel notificar o rival: ${e.message}")
        }
    }
}
