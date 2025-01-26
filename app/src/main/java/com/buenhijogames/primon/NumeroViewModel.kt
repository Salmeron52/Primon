package com.buenhijogames.primon

import android.content.Context
import android.net.Uri
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

    fun numeroAleatorio(): Int {
        return (0..3).random() // 0, 1, 2, 3
    }

    fun inicializar(viewModel: NumeroViewModel) {
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

}



