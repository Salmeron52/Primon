package com.buenhijogames.primon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.buenhijogames.primon.ui.theme.PrimonTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel = NumeroViewModel(this)
        val context = this
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrimonTheme {
                Column { Pantalla5(viewModel = viewModel, context = this@MainActivity) }            }
        }
    }
}
