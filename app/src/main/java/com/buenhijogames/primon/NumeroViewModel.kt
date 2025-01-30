package com.buenhijogames.primon

import android.content.Context
import android.net.Uri
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.buenhijogames.primon.data.RecordDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NumeroViewModel(private val context: Context) : ViewModel() {

    var listaNumeros by mutableStateOf(listOf<Int>())
    var numbers by mutableStateOf(listOf<Int>(numeroAleatorio()))
    var shouldRecompose by mutableStateOf(false)
    var mostrarError by mutableStateOf(false)
    var jugar by mutableStateOf(true)
    var puntos: Int by mutableIntStateOf(0)
    var record by mutableIntStateOf(0)
    private val recordDataStore = RecordDataStore(context)

    init {
        viewModelScope.launch {
            record = recordDataStore.getRecord.first() // Obtener record inicial
        }
    }

    fun sonar(
        context: Context,
        exoPlayer: ExoPlayer,
        sonido: Int,
    ) {
        val uri = Uri.parse("android.resource://" + context.packageName + "/" + sonido)
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun calcularRecord() {
        if (record < puntos) {
            record = puntos
            viewModelScope.launch {
                recordDataStore.saveRecord(record)
            }
        }
    }

    private fun numeroAleatorio(): Int {
        return (0..3).random() // 0, 1, 2, 3
    }

    fun inicializar() {
        //vaciamos numbers
        numbers = emptyList<Int>()
        numbers = numbers + numeroAleatorio()
        //vaciamos listaNumeros
        listaNumeros = emptyList<Int>()
    }

    fun funcionSalidaColores(
        numbers: List<Int>,
        pausaEntreExposiciones: Long,
        tiempoExposicion: Long,
    ): Pair<Long, Long> {
        var pausaEntreExposiciones1 = pausaEntreExposiciones
        var tiempoExposicion1 = tiempoExposicion
        if (numbers.size in 3..5) {
            pausaEntreExposiciones1 = 400L
            tiempoExposicion1 = 300L
        } else if (numbers.size in 6..8) {
            pausaEntreExposiciones1 = 300L
            tiempoExposicion1 = 300L
        } else if (numbers.size in 9..11) {
            pausaEntreExposiciones1 = 250L
            tiempoExposicion1 = 300L
        } else if (numbers.size > 11) {
            pausaEntreExposiciones1 = 150L
            tiempoExposicion1 = 200L
        }
        return Pair(pausaEntreExposiciones1, tiempoExposicion1)
    }

    fun sumarNuevoColor() {
        //Si las lista no coinciden se lanza un mensaje de error
        if (listaNumeros != numbers) {
            Log.e("Error", "Las listas no coinciden")
            Log.e("listaNumeros", listaNumeros.toString())
            Log.e("numbers", numbers.toString())
        } else {
            //Si las listas coinciden, se añade un número aleatorio a numbers
            numbers += numeroAleatorio()
            puntos += 10
            //Se limpia la lista de números
            listaNumeros = emptyList()
            shouldRecompose = !shouldRecompose
            mostrarError = false
        }
    }

    private fun lanzarSonido(
        numero: Int,
        context: Context,
        exoPlayer: ExoPlayer,
    ) {
        when (numero) {
            0 -> sonar(context, exoPlayer, R.raw.sonidoamarillo)
            1 -> sonar(context, exoPlayer, R.raw.sonidoverde)
            2 -> sonar(context, exoPlayer, R.raw.sonidorojo)
            3 -> sonar(context, exoPlayer, R.raw.sonidoazul)
        }
    }

    private fun vibrar(vibrator: Vibrator, milisegundos: Long) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                milisegundos,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }

    fun acciones(
        numero: Int,
        context: Context,
        exoPlayer: ExoPlayer,
        vibrator: Vibrator,
        milisegundos: Long,
    ) {
        lanzarSonido(numero, context, exoPlayer)
        vibrar(vibrator, milisegundos)
        shouldRecompose = false
        sumarNuevoColor()
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

}



