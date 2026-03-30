import kotlin.random.Random

fun main() {

    val Chevette: Carro = Carro("Chevette",80.00, 52.00, 24, 82, Random.nextInt(1, 14), 40)
    val Opala: Carro = Carro("Opala",76.00, 54.00, 21, 86, Random.nextInt(1, 14), 45)
    val Brasília: Carro = Carro("Brasília",78.00, 50.00, 26, 79, Random.nextInt(1, 14), 42)
    val Corcel: Carro = Carro("Corcel",88.00, 60.00, 20, 90, Random.nextInt(1, 14), 38)
    val Belina: Carro = Carro("Belina",70.00, 62.00, 24, 80, Random.nextInt(1, 14), 47)

    val listCarros = listOf(Chevette, Opala, Brasília, Corcel, Belina)

    val piloto1: Piloto = Piloto("Paulo", 32, Random.nextDouble(1.00, 50.00))
    val piloto2: Piloto = Piloto("Guilherme", 40, Random.nextDouble(1.00, 50.00))
    val piloto3: Piloto = Piloto("Felix", 28, Random.nextDouble(1.00, 50.00))
    val piloto4: Piloto = Piloto("Jorge", 37, Random.nextDouble(1.00, 50.00))
    val piloto5: Piloto = Piloto("Alfred", 40, Random.nextDouble(1.00, 50.00))

    val carros = listOf(
        Motorista(piloto1, listCarros[0]),
        Motorista(piloto2, listCarros[1]),
        Motorista(piloto3, listCarros[2]),
        Motorista(piloto4, listCarros[3]),
        Motorista(piloto5, listCarros[4])
    )
println("Bem vindo a corrida dos antigos!!")
    val pista = Pista(carros, 5, 10, 0)
    pista.placar()
}