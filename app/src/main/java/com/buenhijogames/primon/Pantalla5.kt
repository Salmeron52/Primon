package com.buenhijogames.primon

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.buenhijogames.primon.componentes.BotonUsuario
import com.buenhijogames.primon.componentes.ControlCaja
import com.buenhijogames.primon.componentes.MostrarErrorParpadeante
import com.buenhijogames.primon.componentes.MostrarMarcador
import com.buenhijogames.primon.publicidad.PrimonBanner

@Composable
fun Pantalla5(modifier: Modifier = Modifier, viewModel: NumeroViewModel, context: Context) {

    val context = LocalContext.current
    // Creamos un ExoPlayer y lo liberamos cuando el Composable se destruye
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    viewModel.calcularRecord()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (viewModel.jugar) {
            Text(
                text = "Primon",
                color = Color.White,
                fontSize = 36.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            MostrarMarcador(viewModel)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("by ", color = Color.White, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "buenhijoGames", color = Color.White, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(64.dp))

            ControlCaja(viewModel.numbers, viewModel)
            Spacer(modifier = Modifier.height(24.dp))

            Row {
                BotonUsuario(viewModel, 0, Color.Yellow, context = LocalContext.current)
                BotonUsuario(viewModel, 1, Color.Green, context = LocalContext.current)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                BotonUsuario(viewModel, 2, Color.Red, context = LocalContext.current)
                BotonUsuario(viewModel, 3, Color.Blue, context = LocalContext.current)
            }

        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MostrarErrorParpadeante(viewModel, modifier = Modifier.weight(1f))
                viewModel.sonar(context, exoPlayer, R.raw.error)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = {
                        viewModel.inicializar(viewModel)
                        viewModel.mostrarError = false
                        viewModel.puntos = 0
                        viewModel.jugar = true
                    },
                    modifier = Modifier.padding(bottom = 32.dp)
                ) { Text("New Game", color = Color.Yellow, fontSize = 24.sp) }
            }
        }
        PrimonBanner(
            modifier = Modifier.fillMaxSize(),
            adId = "ca-app-pub-3940256099942544/9214589741"
        )
    }

}





