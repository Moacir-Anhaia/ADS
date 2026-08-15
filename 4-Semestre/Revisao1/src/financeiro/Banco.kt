package financeiro

import pessoas.Pessoa
import java.math.BigDecimal
import java.time.LocalDate

class Banco (
    val dinheiro : BigDecimal,
    val dataMovimentacao : LocalDate,
    val pessoa: Pessoa
)