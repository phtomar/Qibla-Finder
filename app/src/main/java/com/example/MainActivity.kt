package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.QiblaFinderScreen
import com.example.ui.theme.GeoBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.QiblaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: QiblaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            MyApplicationTheme(
                themeId = uiState.preferences.themeId,
                language = uiState.preferences.language
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GeoBackground
                ) {
                    QiblaFinderScreen(viewModel = viewModel)
                }
            }
        }
    }
}
