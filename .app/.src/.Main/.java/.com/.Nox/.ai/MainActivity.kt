package com.nox.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nox.ai.ui.screens.ChatScreen
import com.nox.ai.ui.screens.HomeScreen
import com.nox.ai.ui.screens.KnowledgeBankScreen
import com.nox.ai.ui.screens.PersonaStudioScreen
import com.nox.ai.ui.theme.NoxTheme
import com.nox.ai.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoxTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToStudio = { personaId ->
                    if (personaId != null) {
                        navController.navigate("studio/$personaId")
                    } else {
                        navController.navigate("studio/new")
                    }
                },
                onNavigateToChat = { personaId ->
                    navController.navigate("chat/$personaId")
                },
                onNavigateToKnowledgeBank = {
                    navController.navigate("knowledge")
                }
            )
        }

        composable(
            route = "studio/{personaId}",
            arguments = listOf(
                navArgument("personaId") {
                    type = NavType.StringType
                    defaultValue = "new"
                }
            )
        ) { backStackEntry ->
            val personaIdArg = backStackEntry.arguments?.getString("personaId")
            val personaId = if (personaIdArg == null || personaIdArg == "new") null else personaIdArg.toLongOrNull()

            PersonaStudioScreen(
                personaId = personaId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToChat = { id ->
                    navController.navigate("chat/$id")
                }
            )
        }

        composable(
            route = "chat/{personaId}",
            arguments = listOf(
                navArgument("personaId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val personaId = backStackEntry.arguments?.getLong("personaId") ?: 0L

            ChatScreen(
                personaId = personaId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("knowledge") {
            KnowledgeBankScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
