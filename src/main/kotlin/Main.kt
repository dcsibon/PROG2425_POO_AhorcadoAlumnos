package es.iesra.prog2425_ahorcado

fun main() {

    val palabras = Palabra.generarPalabras(cantidad = 10, tamanioMin = 7, tamanioMax = 7, idioma = Idioma.ES)

    var seguirJugando : Boolean
    do {
        val palabraOculta = palabras.pop()
        if (palabraOculta != null) {
            val jugador = Jugador(intentosMaximos = 10)
            val juego = Juego(palabraOculta, jugador)

            juego.iniciar()
            seguirJugando = juego.preguntar("¿Quieres jugar otra partida?")
        } else {
            println("No existen más palabras ocultas...")
            seguirJugando = false
        }
    } while (seguirJugando)
}

//TODO: Crear una función de extensión quitarAcentos para la clase Char
//      Intentad utilizarlo en el programa para ser capaces de encontrar coincidencias con vocales acentuadas.
fun Char.quitarAcentos(): Char {
    //Yo crearía un mapa de vocales acentuadas como clave con el valor como la vocal sin acentuar
    //Vocales minúsculas y mayúsculas.
    // Después retornaría el valor de la clave para el reciever si se ha encontrado o el mismo reciever.
    /*
    El receiver es la instancia del tipo al que se extiende la función. En otras palabras, es el objeto
    sobre el cual la función de extensión será llamada. Dentro de la función de extensión, puedes acceder
    a las propiedades y métodos de esta instancia utilizando this.
    */

    
    return 'a'
}

/**
 * Elimina y retorna un elemento aleatorio de este [MutableSet].
 * Si el conjunto está vacío, retorna `null`.
 *
 * @receiver MutableSet<T> El conjunto mutable del cual se eliminará el elemento.
 * @return [T]? El elemento eliminado del conjunto o `null` si el conjunto está vacío.
 * @param T El tipo de elementos que contiene el conjunto.
 */
fun <T> MutableSet<T>.pop(): T? {
    val elemento = this.randomOrNull()
    if (elemento != null) {
        this.remove(elemento)
    }
    return elemento

}