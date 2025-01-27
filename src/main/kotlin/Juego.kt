package es.iesra.prog2425_ahorcado

class Juego(val palabra: Palabra, val jugador: Jugador) {

    fun iniciar() {
        println("¡Bienvenido al juego del Ahorcado!")
        println("La palabara tiene ${palabra.palabraOculta.length} letras")

        var terminado = false

        while (!terminado) {
            println("Palabra: ${palabra.obtenerProgreso()}")
            println("Intentos restantes: ${jugador.intentos}")
            println("Letras usadas: ${jugador.obtenerLetrasUsadas()}")
            println("Introduce una letra: ")

            val letra: Char = readln().lowercase().firstOrNull()!!.toChar()
            require(letra.isLetter())

            if (!jugador.intentarLetra(letra)) {
                println("Letra no válida o ya utilizada. Intenta otra vez.")
            }
            else if (letra in palabra.palabraOculta) {
                println("¡Bien hecho! La letra '$letra' está en la palabra.")
                jugador.intentarLetra(letra)
                palabra.revelarLetra(letra)
            }

            if (jugador.intentos >= 0) {
                terminado = true
            }

            if (palabra.esCompleta()) {
                terminado = true
            }
        }

        if (palabra.esCompleta()) {
            println("\n¡Felicidades! Has adivinado la palabra: ${palabra.obtenerProgreso()}")
        } else {
            println("\nLo siento, te has quedado sin intentos. La palabra era: ${palabra.palabraOculta}")
        }


    }

    fun preguntar(msj: String): Boolean {
        do {
            print("$msj (s/n): ")
            val respuesta = readln().trim().lowercase()
            when (respuesta) {
                "s" -> return true
                "n" -> return false
                else -> println("Respuesta no válida! Inténtelo de nuevo...")
            }
        } while (true)
    }

}