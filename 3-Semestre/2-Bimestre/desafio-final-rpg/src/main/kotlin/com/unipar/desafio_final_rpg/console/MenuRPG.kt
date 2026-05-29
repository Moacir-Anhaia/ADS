package com.unipar.desafio_final_rpg.console

import com.unipar.desafio_final_rpg.service.PersonagemService
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import kotlin.system.exitProcess

// @Component: marca esta classe como um bean gerenciado pelo Spring
// O MenuRPG e o ORQUESTRADOR do jogo
@Component
class MenuRPG(

    // Usado para buscar a vida atualizada do personagem do banco a cada rodada
    val personagemService: PersonagemService,

    val restClient: RestClient,


    @Value("\${rival.base.url}") private val rivalBaseUrl: String

) : CommandLineRunner {

    // URL base local — todas as chamadas HTTP internas usam este prefixo
    val baseUrl = "http://localhost:8080"

    // Nome do jogador digitado no inicio de cada partida
    var nomeJogador = ""

    var classeEscolhida = ""


    override fun run(vararg args: String) {
        Thread.sleep(1500)

        while (true) {
            exibirBanner()      //Mostra o inicio do jogo "Título"
            escolherNomeEClasse()
            aguardarRivalPronto()
            sortearTurno()
            val jogarDenovo = loopBatalha()
            if (!jogarDenovo) break
        }

        println("\n Obrigado por jogar! Encerrando...")
        exitProcess(0) // finaliza
    }

    private fun exibirBanner() {
        println("\n" + "=".repeat(50))
        println(" BEM-VINDO AO RPG BATTLE ")
        println("=".repeat(50))
    }

    private fun escolherNomeEClasse() {
        print("\nDigite seu nome: ")
        nomeJogador = readLine()?.trim()?.ifEmpty { "Jogador" } ?: "Jogador"

        println("\n  ESCOLHA SEU PERSONAGEM:")
        println("─".repeat(50))
        println("1 - Guerreiro  (Vida: 200 | Forca: 120 | Poder: Escudo)")
        println("2 - Mago       (Vida: 120 | Forca: 150 | Poder: Bola de Fogo)")
        println("3 - Ladino     (Vida: 150 | Forca: 100 | Poder: Golpe Furtivo)")
        println("─".repeat(50))
        print("Sua escolha: ")

        //Verificação da classe
        classeEscolhida = when (readLine()?.trim()) {
            "1" -> "Guerreiro"
            "2" -> "Mago"
            "3" -> "Ladino"
            else -> { println("Opcao invalida. Guerreiro selecionado."); "Guerreiro" }
        }
        //apaga registro anteriores no BD
        get("/combate/resetar")

        //Cria e Salva no BD
        get("/combate/escolha/$classeEscolhida?nomeJogador=${encode(nomeJogador)}")

        //Ajusta o nome do jogador junto com a classe
        get("/chat/definirNome?nome=${encode(nomeJogador)}&classe=$classeEscolhida")

        //reseta o pronto para iniciar
        get("/combate/resetarPronto")

        println("\n $nomeJogador escolheu $classeEscolhida!")
        println(get("/combate/status"))
    }

    // espera o enter
    private fun aguardarRivalPronto() {
        println("\n Pressione ENTER quando estiver pronto para comecar")
        readLine() // Bloqueia até o enter

        try {
            restClient.post()
                .uri("$rivalBaseUrl/combate/notificar") // Endpoint de notificacao do rival
                .contentType(MediaType.TEXT_PLAIN)
                .body("PRONTO")
                .retrieve()
                .toBodilessEntity()
        } catch (e: Exception) {
            println("[AVISO] Nao foi possivel notificar o rival — continuando...")
        }

        println("Voce esta pronto! Aguardando o rival confirmar...")

        while (get("/combate/rivalPronto") != "SIM") {
            Thread.sleep(500) // Aguarda 500ms antes de verificar
        }

        println(" Ambos prontos! Partida iniciando!\n")
    }

    private fun sortearTurno() {
        println(" Sorteando quem comeca...")
        val resultado = get("/combate/sortearTurno")

        when {
            resultado.startsWith("PRIMEIRO:") -> {
                // Eu ataco Primeiro
                println("\n🏆 ${resultado.removePrefix("PRIMEIRO:")}")
                println("  VOCE COMECA! Prepare seu ataque.\n")
            }
            resultado.startsWith("SEGUNDO:") -> {
                // Rival comeca atacando
                println("\n ${resultado.removePrefix("SEGUNDO:")}")
                println("   Aguarde o rival agir primeiro...\n")
            }
        }
        Thread.sleep(1500) // Pausa para o jogador ler
    }

    //loop da batalha

    private fun loopBatalha(): Boolean {
        var rodada = 1 // Contador de rodadas, comeca em 1

        while (true) {

            // Verifica se o rival encerrou
            if (get("/combate/rivalEncerrou") == "SIM") {
                println("\n" + "=".repeat(50))
                println(" O rival encerrou o jogo. Partida encerrada!")
                println("=".repeat(50))
                return perguntarReiniciar()
            }

            //Verifica a vida do personagem no banco
            val personagem = try {
                personagemService.buscarPorNome(classeEscolhida) // Busca pelo nome da classe
            } catch (e: Exception) {
                println("\n Personagem nao encontrado no banco. Fim de partida!")
                return perguntarReiniciar()
            }

            //Condição de derrota
            if (personagem.vida <= 0) {
                println("\n" + "=".repeat(50))
                println(" $nomeJogador ($classeEscolhida) FOI DERROTADO!")
                println("=".repeat(50))
                return perguntarReiniciar()
            }

            // Controla a Vez
            val minhavez = get("/combate/minhavez") == "SIM"

            if (minhavez) {
                // Mostra oque antecedeu a sua vez
                val ultimaAcao = get("/combate/ultimaAcao")
                if (ultimaAcao.isNotEmpty()) {
                    println("\n" + "─".repeat(50))
                    println("ACAO DO RIVAL:")
                    println("  $ultimaAcao")
                }
            //Gera o cabeçario
                println("\n" + "─".repeat(50))
                println("  RODADA $rodada  |  $nomeJogador ($classeEscolhida)  |   ${personagem.vida}/${personagem.vidaMax}")
                println("  E SUA VEZ!")
                println("─".repeat(50))
                println("1 - Atacar")
                println("2 - Defender")
                println("3 - Usar Poder Especial")
                println("4 - Enviar mensagem no Chat (nao gasta turno)")
                println("0 - Sair do jogo")
                print("Sua escolha: ")

                when (readLine()?.trim()) {

                    //Ataque
                    // GET /combate/atacar: envia forca ao rival via POST /combate/apanhar

                    "1" -> {
                        val resposta = get("/combate/atacar")
                        if (resposta.startsWith("VITORIA:")) {
                            exibirVitoria(resposta.removePrefix("VITORIA:"))
                            return perguntarReiniciar()
                        }
                        println("  $resposta")
                        rodada++ // Avanca a rodada somente apos acao de combate
                    }

                    // Defender
                    "2" -> {
                        println("  " + get("/combate/defender"))
                        rodada++
                    }

                   //Poder especial
                    "3" -> {
                        val poder = when (classeEscolhida) {
                            "Guerreiro" -> "Escudo "
                            "Mago"      -> "Bola de Fogo"
                            "Ladino"    -> "Golpe Furtivo 🗡"
                            else        -> "Poder "
                        }
                        println("\n Usando: $poder")
                        val resposta = get("/combate/poder")
                        if (resposta.startsWith("VITORIA:")) {
                            exibirVitoria(resposta.removePrefix("VITORIA:"))
                            return perguntarReiniciar()
                        }
                        println("  $resposta")
                        rodada++
                    }

                    //Chat
                    "4" -> {
                        print("  Digite sua mensagem: ")
                        val msg = readLine()?.trim() ?: ""
                        if (msg.isNotEmpty()) {
                            // encodeUrl() converte caracteres especiais para a URL
                            println("  " + get("/chat/msg?mensagem=${encodeUrl(msg)}"))
                        }
                    }

                    //Sair do jogo
                    "0" -> {
                        notificarRival("ENCERRADO") // Avisa o rival que estamos saindo
                        println("\n Saindo. Ate a proxima!")
                        return false
                    }

                    else -> println(" Opcao invalida. Tente novamente.")
                }

            } else {
               //laço de espera do rival

                println("\n" + "─".repeat(50))
                println("  RODADA $rodada  |  $nomeJogador ($classeEscolhida)  |   ${personagem.vida}/${personagem.vidaMax}")
                println(" AGUARDANDO O RIVAL AGIR...")
                println("  [4] Chat  [0] Sair")
                println("─".repeat(50))

                var saiuEspera = false
                while (!saiuEspera) {

                    // Verifica se o rival encerrou enquanto aguardavamos
                    if (get("/combate/rivalEncerrou") == "SIM") {
                        println("\n O rival encerrou o jogo enquanto voce aguardava!")
                        return perguntarReiniciar()
                    }

                    // Verifica se o rival ja agiu

                    if (get("/combate/minhavez") == "SIM") {
                        saiuEspera = true
                        continue
                    }


                    val input = lerComTimeout(500)
                    when (input?.trim()) {
                        // Chat durante a espera — envia mensagem sem perder a vez
                        "4" -> {
                            print("  Mensagem: ")
                            val msg = readLine()?.trim() ?: ""
                            if (msg.isNotEmpty()) println("  " + get("/chat/msg?mensagem=${encodeUrl(msg)}"))
                        }
                        // Sair durante a espera — notifica o rival e encerra
                        "0" -> {
                            notificarRival("ENCERRADO")
                            println("\n Saindo. Ate a proxima!")
                            return false
                        }
                        null -> { /* Timeout sem input — continua aguardando silenciosamente */ }
                        else -> println(" [4] Chat  [0] Sair")
                    }
                }

                rodada++
            }
        }
    }


    private fun lerComTimeout(timeoutMs: Long): String? {
        val inicio = System.currentTimeMillis() // Marca o tempo de inicio
        while (System.currentTimeMillis() - inicio < timeoutMs) {
            if (System.`in`.available() > 0) {
                return readLine() // Ha bytes no buffer — le e retorna a linha
            }
            Thread.sleep(100) // Aguarda 100ms antes de verificar novamente
        }
        return null
    }
    // Vitoria
    private fun exibirVitoria(detalhe: String) {
        println("\n" + "=".repeat(50))
        println("  VOCE VENCEU! $nomeJogador ($classeEscolhida) e o campeao!")
        println("  $detalhe")
        println("=".repeat(50))
    }

    //Reiniciar o jogo

    private fun perguntarReiniciar(): Boolean {
        print("\nDeseja jogar novamente? (S/N): ")
        return readLine()?.trim()?.uppercase() == "S"
    }

    private fun notificarRival(mensagem: String) {
        try {
            restClient.post()
                .uri("$rivalBaseUrl/combate/notificar")
                .contentType(MediaType.TEXT_PLAIN)
                .body(mensagem)
                .retrieve()
                .toBodilessEntity()
        } catch (e: Exception) {
            println("[AVISO] Nao foi possivel notificar o rival: ${e.message}")
        }
    }

    private fun get(path: String): String {
        return try {
            restClient.get()
                .uri("$baseUrl$path") // Monta URL completa: http://localhost:8080/combate/atacar
                .retrieve()
                .body(String::class.java) ?: "" // Retorna "" se o corpo for nulo
        } catch (e: Exception) {
            "Erro: ${e.message}"
        }
    }

   //substitui os caracteres especiais
    private fun encode(texto: String) = texto.replace(" ", "%20")

    private fun encodeUrl(texto: String): String {
        return java.net.URLEncoder.encode(texto, "UTF-8") // Codifica tudo incluindo acentos
    }
}
