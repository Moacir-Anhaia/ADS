package pessoas

import financeiro.Banco
import java.math.BigDecimal
import java.time.LocalDate

open class Pessoa (
    val nome: String,
    val cpf: String,
    val idade: Int
)//Criação da classe-mãe via Construtor
{
    open fun receberConta(valor : BigDecimal, conta : Pessoa  ) : Banco{
        return Banco(
            dinheiro = valor,
            pessoa = conta,
            dataMovimentacao = LocalDate.now()
        )
    }
}