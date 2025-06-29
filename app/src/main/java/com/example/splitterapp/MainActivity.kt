package com.example.splitterapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.splitterapp.ui.theme.SplitterAppTheme
import com.example.splitterapp.presentation.group.GroupScreen
import com.example.splitterapp.presentation.group.GroupViewModel
import com.example.splitterapp.presentation.welcome.WelcomeScreen

// Add these imports
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    private val viewModel: GroupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplitterAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "welcome"
                    ) {
                        composable("welcome") {
                            WelcomeScreen(
                                onGetStarted = {
                                    navController.navigate("group")
                                }
                            )
                        }

                        composable("group") {
                            GroupScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}
