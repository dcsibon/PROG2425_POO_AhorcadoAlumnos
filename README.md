
## TRABAJO POR GRUPOS - Guía para realizar la práctica

Se trata de realizar el juego del Ahorcado siguiendo las indicaciones que se incluyen a continuación. Un alumno de cada grupo de trabajo debe realizar un `FORK` del repositorio y agregar al resto de miembros de su equipo de trabajo como colaboradores.

Es un proyecto que utiliza el sistema de creación GRADLE y las siguientes dependencias *(ver fichero `build.gradle.kts`)*

   ```kotlin
   dependencies {
       testImplementation(kotlin("test"))
       implementation("io.ktor:ktor-client-core:2.0.0")
       implementation("io.ktor:ktor-client-cio:2.0.0")
       implementation("io.ktor:ktor-client-content-negotiation:2.0.0")
       implementation("io.ktor:ktor-serialization-gson:2.0.0")
   }
   ```

### La clase Palabra:

1. Debe tener los siguientes imports:

   ```kotlin
   package es.iesra.prog2425_ahorcado
    
   import io.ktor.client.*
   import io.ktor.client.call.*
   import io.ktor.client.plugins.contentnegotiation.*
   import io.ktor.client.request.*
   import io.ktor.serialization.gson.*
   import kotlinx.coroutines.runBlocking
   ```

2. Debe contener las propiedades:
   - `palabraOculta` *(String)*, se debe pasar al construir una instancia de Palabra.
   - `progreso` *(Array de caracteres)*, no será visible fuera de la clase y se inicializará con las mismas posiciones 
     que la `palabraOculta` y todos los caracteres `'_'`.

3. Métodos:
   - `revelarLetra()`, que recibirá una letra, buscará coincidencias recorriendo la `palabraOculta`. Si la encuentra, 
   deberá actrualizar la misma posición en `progreso` con dicha letra. Retornará true/false, indicando si ha encontrado 
   alguna coincidencia.
   - `obtenerProgreso()`, que retornará un String con las letras de `progreso` separadas por un espacio.
   - `esCompleta()`, que retornará true/false, indicando si `progreso` contiene algún caráceter `'_'`.

4. Leer, intentad entender e incluir dentro de un `companion object` el siguiente método de la propia clase:

   - Este método estático de la propia clase realiza una llamada a una API web y retornará un conjunto de elementos de tipo `Palabra`.

   ```kotlin
   fun generarPalabras(cantidad: Int, tamanioMin: Int, tamanioMax: Int, idioma: Idioma = Idioma.ES): MutableSet<Palabra> {
       val client = HttpClient {
           install(ContentNegotiation) {
               gson()
           }
       }

       val palabras = mutableSetOf<Palabra>() // Usamos un conjunto para evitar repeticiones
       val url = "https://random-word-api.herokuapp.com/word?number=${cantidad * 5}&lang=${idioma.codigo}"

       val patron = if (idioma == Idioma.ES) {
           "^[a-záéíóúüñ]+$"
       } else {
           "^[a-z]+$"
       }

      runBlocking {
           try {
               while (palabras.size < cantidad) {
                   // Hacemos la solicitud GET
                   val respuesta: Array<String> = client.get(url).body()

                   // Filtramos las palabras según las condiciones
                   val filtradas = respuesta
                       .map { it.trim().lowercase() } // Convertimos a minúsculas
                       .filter { it.length in tamanioMin..tamanioMax } // Filtramos por tamaño
                       .filter { it.matches(Regex(patron)) } // Solo letras
                       .filter { !it.contains(" ") } // Excluye palabras que contengan espacios
                       .map { Palabra(it) } // Mapeamos a la data class

                   palabras.addAll(filtradas)
               }
           } catch (e: Exception) {
               println("Error al obtener las palabras: ${e.message}")
           }
       }
    
       client.close()
       return palabras.take(cantidad).toMutableSet()
   }
   ```

### La clase Jugador:

1. Debe contener las propiedades:
   - `intentos`: solo modificable desde la clase. Costruiremos un jugador pasándole el número de intentos máximos que 
     tiene para jugar.
   - `letrasUsadas`: solo accesible desde la clase y será el conjunto de las letras que se van introduciendo para 
     adivinar la palabra oculta.

2. Métodos:
   - `intentarLetra(letra: Char): Boolean`, si no ha usado la letra la agrega a `lestrasUsadas` y retorna true. En caso 
     contrario, retornará false.
   - `fallarIntento()`: decrementará los intentos.
   - `obtenerLetrasUsadas(): String`: retornará las letras usadas, separadas por un espacio.

### La clase Juego:

1. Tiene un constructor al que se pasan `palabra` y `jugador`, que serán propiedades de tipo Palabra y Jugador.

2. Contiene los siguientes métodos:
   - `iniciar()`:
      * Muestra en pantalla el siguiente texto de inicio del juego en dos líneas: 
      ```
      ¡Bienvenido al juego del Ahorcado!
      La palabra tiene N letras.
      ```
      * Donde N es el númeo de caracteres de la propiedad `palabraOculta` de la palabra.
      * Ejecutará un bucle, mientras el jugador tenga intentos y la palabra no se haya acertado *(ver método esCompleta)*
      * Dentro del bucle se mostrará lo siguiente:
      ```
      Palabra: _ _ _ _ _ _ _
      Intentos restantes: 6
      Letras usadas:
      Introduce una letra:
      ```
      * Se pedirá un carácter (aplicar lowercase() y firstOrNull()):
      * Si es nula o no se puede usar esa letra, *(ver método intentarLetra de jugador)*, mostrará por pantalla 
        "Letra no válida o ya utilizada. Intenta otra vez."
      * En caso contrario, comprobará si la letra se encuentra en la palabraOculta, *(ver método revelarLetra de palabra)*,
        en cuyo caso mostrará "¡Bien hecho! La letra '$letra' está en la palabra.".
      * Sino mostrará "La letra '$letra' no está en la palabra." y decrementará los intentos del jugador, 
        *(ver método fallarIntento de jugador)*
      * Al salir del bucle, ejecutará lo siguiente:
        ```kotlin
        if (palabra.esCompleta()) {
        println("\n¡Felicidades! Has adivinado la palabra: ${palabra.obtenerProgreso()}")
        } else {
        println("\nLo siento, te has quedado sin intentos. La palabra era: ${palabra.palabraOculta}")
        }
        ```
   - preguntar:
      * Recibe un String cómo parámetro de entrada y retorna true/false.
      * El mensaje que recibe es la pregunta a realizar al usuario.
      * Mostrar por consola el mensaje y agregar a dicho mensaje la cadena de caracteres " (s/n)"
      * Debe pedir una respuesta, solo serán válidas las respuestas "s", "S", "n" o "N".
      * Permanecerá dentro de un bucle mientras que el usuario no responda correctamente.
      * Una vez proporcione una respuesta adecuada, retornará true si es "s" o "S" o false en caso de ser "n" o "N".
      * Ejemplo de salida por consola al ejecutar `val = respuesta = preguntar("¿Desea jugar otra partida?")`:

   ```kotlin
   > ¿Desea jugar otra partida? (s/n) no
   > Respuesta no válida! Inténtelo de nuevo...
   > ¿Desea jugar otra partida? (s/n) n
   
   ** En este momento la variable respuesta tendría el valor false **
   ```
     
### Main.kt:

  ```kotlin
  package es.iesra.prog2425_ahorcado
  
  fun main() {
  
      val palabras = Palabra.generarPalabras(cantidad = 10, tamanioMin = 7, tamanioMax = 7, idioma = Idioma.ES)
  
      var seguirJugando : Boolean
      do {
          val palabraOculta = palabras.pop()
          if (palabraOculta != null) {
              val jugador = Jugador(intentosMaximos = 6)
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
  //      IMPORTANTE: Intentad utilizar este método en el programa para mejorar el programa y ser capaces de encontrar
  //                  coincidencias con vocales acentuadas también.
  fun Char.quitarAcentos(): Char {
    //Yo crearía un mapa de vocales acentuadas como clave con el valor como la vocal sin acentuar.
    //Vocales minúsculas y mayúsculas.
    //Después retornaría el valor de la clave para el reciever si se ha encontrado o el mismo reciever.
    /*
    El receiver es la instancia del tipo al que se extiende la función. En otras palabras, es el objeto 
    sobre el cual la función de extensión será llamada. Dentro de la función de extensión, puedes acceder 
    a las propiedades y métodos de esta instancia utilizando this.
    */ 
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
  ```
