package com.buenhijogames.primon.componentes

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.buenhijogames.primon.NumeroViewModel
import com.buenhijogames.primon.R
import com.buenhijogames.primon.data.RecordDataStore
import com.buenhijogames.primon.ui.theme.Amarillo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BotonUsuario(viewModel: NumeroViewModel, numero: Int, color: Color, context: Context) {
    val context = LocalContext.current
    // Creamos un ExoPlayer y lo liberamos cuando el Composable se destruye
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val milisegundos = 10L

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Boton(
        numero = numero,
        color = color,
        listaDeEnteros = viewModel.listaNumeros,
        listaNumeros = viewModel.numbers,
        onUpdated = { viewModel.listaNumeros = it },
        onClicked = {
            when (numero) {
                0 -> {
                    viewModel.sonar(context, exoPlayer, R.raw.sonidoamarillo)
                }

                1 -> {
                    viewModel.sonar(context, exoPlayer, R.raw.sonidoverde)
                }

                2 -> {
                    viewModel.sonar(context, exoPlayer, R.raw.sonidorojo)
                }

                3 -> {
                    viewModel.sonar(context, exoPlayer, R.raw.sonidoazul)
                }
            }

            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    milisegundos,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )

            viewModel.shouldRecompose = false
            //Si las lista no coinciden se lanza un mensaje de error
            if (viewModel.listaNumeros != viewModel.numbers) {
                Log.e("Error", "Las listas no coinciden")
                Log.e("listaNumeros", viewModel.listaNumeros.toString())
                Log.e("numbers", viewModel.numbers.toString())
            } else {
                //Si las listas coinciden, se añade un número aleatorio a numbers
                viewModel.numbers += viewModel.numeroAleatorio()
                viewModel.puntos += 10
                //Se limpia la lista de números
                viewModel.listaNumeros = emptyList()
                viewModel.shouldRecompose = !viewModel.shouldRecompose
                viewModel.mostrarError = false
            }
            secuenciaIncorrecta(viewModel.listaNumeros, viewModel.numbers, viewModel)
        }
    )
}


@Composable
fun Boton(
    numero: Int,
    color: Color,
    listaDeEnteros: List<Int>,
    listaNumeros: List<Int>,
    onUpdated: (List<Int>) -> Unit,
    onClicked: (List<Int>) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .height(screenHeightDp.dp * .2f)
            .width(screenWidthDp.dp * .40f)
            .padding(horizontal = 4.dp)
            .background(color)
            .clickable { onUpdated(listaDeEnteros + numero); onClicked(listaNumeros) },
        contentAlignment = Alignment.Center
    ) { }
}

fun secuenciaIncorrecta(
    listaNumeros: List<Int>,
    numbers: List<Int>,
    viewModel: NumeroViewModel,
) {
    val minSize = minOf(listaNumeros.size, numbers.size)
    for (i in 0 until minSize) {
        if (listaNumeros[i] != numbers[i]) {
            viewModel.mostrarError = true
            viewModel.jugar = false
        }
    }

}

@Composable
fun Caja(viewModel: NumeroViewModel, color: Color) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .height(screenHeightDp.dp * .2f)
            .width(screenWidthDp.dp * .80f)
            .background(color),
    ) {}
}

@Composable
fun ControlCaja(
    numbers: List<Int>,
    viewModel: NumeroViewModel,
) {
    if (viewModel.mostrarError) {
        MostrarError()
    } else {
        if (numbers.size == 1) {
            NumberScroller(numbers = numbers, viewModel = viewModel, context = LocalContext.current)
        } else {
            if (viewModel.shouldRecompose) {
                NumberScroller(
                    numbers = numbers,
                    viewModel = viewModel,
                    context = LocalContext.current
                )
            } else {
                Caja(viewModel = viewModel, color = Color.Black)
            }
        }
    }
}

@Composable
fun MostrarErrorParpadeante(viewModel: NumeroViewModel, modifier: Modifier = Modifier) {

    var showError by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(600)
            showError = !showError
        }
    }
    if (showError) MostrarError() else Text("Hola", fontSize = 55.sp)
}

@Composable
fun MostrarError() {
    Text("Game Over", color = Color.Red, fontSize = 55.sp)
}


@Composable
fun NumberScroller(numbers: List<Int>, viewModel: NumeroViewModel, context: Context) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    val currentIndex = remember { mutableIntStateOf(-1) } // -1 para que no se muestre nada
    var mostrarColor by remember { mutableStateOf(false) }
    var tiempoExposicion = 500L
    var pausaEntreExposiciones = 500L

    val context = LocalContext.current
    // Creamos un ExoPlayer y lo liberamos cuando el Composable se destruye
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(key1 = Unit) {
        var cont = 0
        while (cont < numbers.size) {
            val salidaColores: Pair<Long, Long> =
                viewModel.funcionSalidaColores(numbers, pausaEntreExposiciones, tiempoExposicion)
            pausaEntreExposiciones = salidaColores.first
            tiempoExposicion = salidaColores.second
            delay(pausaEntreExposiciones)
            currentIndex.intValue = (currentIndex.intValue + 1) % numbers.size
            mostrarColor = true
            delay(tiempoExposicion)
            mostrarColor = false
            cont++
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .height(screenHeightDp.dp * .2f)
            .width(screenWidthDp.dp * .80f)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        MostrarSonidosYColores(currentIndex, mostrarColor, numbers, viewModel, context, exoPlayer)
    }
}

@Composable
private fun MostrarSonidosYColores(
    currentIndex: MutableIntState,
    mostrarColor: Boolean,
    numbers: List<Int>,
    viewModel: NumeroViewModel,
    context: Context,
    exoPlayer: ExoPlayer,
) {
    if (currentIndex.intValue >= -2 && mostrarColor) {
        if (numbers[currentIndex.intValue] == 0) {
            Caja(viewModel = viewModel, color = Amarillo)
            viewModel.sonar(context, exoPlayer, R.raw.sonidoamarillo)
        } else if (numbers[currentIndex.intValue] == 1) {
            Caja(viewModel = viewModel, color = Color.Green)
            viewModel.sonar(context, exoPlayer, R.raw.sonidoverde)
        } else if (numbers[currentIndex.intValue] == 2) {
            Caja(viewModel = viewModel, color = Color.Red)
            viewModel.sonar(context, exoPlayer, R.raw.sonidorojo)
        } else if (numbers[currentIndex.intValue] == 3) {
            Caja(viewModel = viewModel, color = Color.Blue)
            viewModel.sonar(context, exoPlayer, R.raw.sonidoazul)
        }
    }
}


@Composable
fun MostrarMarcador(viewModel: NumeroViewModel) {
    val context: Context = LocalContext.current
    val scope = rememberCoroutineScope()
    val recordDataStore = RecordDataStore(context)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        LaunchedEffect(key1 = viewModel.record) {
            scope.launch {
                recordDataStore.saveRecord(
                    viewModel.record
                )
            }
        }
        val userRecord = recordDataStore.getRecord.collectAsState(initial = 0)

        Text("Score: ${viewModel.puntos}", color = Color.White, fontSize = 18.sp)
        Text(
            " Record: ${userRecord.value}",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Right
        )
    }
}