package ec.edu.uisek.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class CalculatorState(
    val display: String = "0"
)

sealed class CalculatorEvent {
    data class Number (val number: String) : CalculatorEvent()
    data class Operator (val operator: String) : CalculatorEvent()
    object Clear : CalculatorEvent()
    object AllClear : CalculatorEvent()
    object Calculate : CalculatorEvent()
    object Decimal : CalculatorEvent()
}

class CalculatorViewModel: ViewModel() {
    private var number1: String = ""
    private var number2: String = ""
    private var operator: String? = null

    var state by mutableStateOf(CalculatorState())
        private set

    fun onEvent(event: CalculatorEvent) {
        when (event) {
            is CalculatorEvent.Number -> enterNumber(event.number)
            is CalculatorEvent.Operator -> enterOperator(event.operator)
            is CalculatorEvent.Decimal -> enterDecimal()
            is CalculatorEvent.AllClear -> clearAll()
            is CalculatorEvent.Clear -> clearLast()
            is CalculatorEvent.Calculate -> calculateAutomatically()
        }
    }
    private fun enterNumber(number: String) {
        if (operator == null) {
            number1 += number
            state = state.copy(display = number1)
        } else {
            number2 += number
            calculateAutomatically()
        }
    }

    private fun enterDecimal() {
        val currentNumber = if (operator == null) number1 else number2
        if (!currentNumber.contains(".")) {
            if (operator == null) {
            number1 += "."
            state = state.copy(display = number1)
            } else {
            number2 += "."
            calculateAutomatically()
            }
        }
    }

    private fun enterOperator(op: String) {
        if (number1.isNotBlank()) {
            this.operator = op
        }
    }

    private fun clearAll () {
        number1 = ""
        number2 = ""
        operator = null
        state = state.copy(display = "0")
    }

    private fun clearLast() {
        if (operator == null) {
            number1 = number1.dropLast(1)
            state = state.copy(display = number1.ifBlank { "0" })
        } else {
            number2 = number2.dropLast(1)
            state = state.copy(display = number2.ifBlank { "0" })
        }
    }

    private fun calculateAutomatically() {
        val num1 = number1.toDoubleOrNull() ?: return
        val num2 = number2.toDoubleOrNull() ?: return
        val op = operator ?: return

        val result = when (op) {
                "+" -> num1 + num2
                "−" -> num1 - num2
                "×" -> num1 * num2
                "÷" -> if (num2 != 0.0) num1 / num2 else Double.NaN
                else -> return
            }

            val resultString = if (result.isNaN()) "ERROR" else result.toString()
                .removeSuffix(".0")
            state = state.copy(display = resultString)
        }
    }

