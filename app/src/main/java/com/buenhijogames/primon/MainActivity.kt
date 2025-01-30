package com.buenhijogames.primon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import com.buenhijogames.primon.ui.theme.PrimonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel = NumeroViewModel(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimonTheme {
                Column { Pantalla5(viewModel = viewModel) }
            }
        }
    }
}
