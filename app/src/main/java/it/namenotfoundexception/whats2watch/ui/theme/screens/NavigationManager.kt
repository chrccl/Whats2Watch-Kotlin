package it.namenotfoundexception.whats2watch.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.namenotfoundexception.whats2watch.viewmodels.AuthViewModel
import it.namenotfoundexception.whats2watch.viewmodels.RecommendationViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Homepage : Screen("homepage")
    object Rooms : Screen("rooms")
    object Reviews : Screen("reviews")
    object Swipe : Screen("swipe/{roomCode}/{username}") {
        fun createRoute(roomCode: String, username: String) = "swipe/$roomCode/$username"
    }
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    val startDestination = if (currentUser != null) {
        Screen.Homepage.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Homepage.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.Homepage.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Homepage.route) {
            HomepageScreen(
                authViewModel = authViewModel,
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homepage.route) { inclusive = true }
                    }
                },
                onRoomsClick = { roomCode, username ->
                    navController.navigate(Screen.Swipe.createRoute(roomCode, username))
                },
                onRoomMenuClick = {
                    navController.navigate(Screen.Rooms.route)
                },
                onReviewsClick = {
                    navController.navigate(Screen.Reviews.route)
                }
            )
        }

        composable(Screen.Rooms.route) {
            RoomsScreen(
                authViewModel = authViewModel,
                roomViewModel = hiltViewModel(),
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homepage.route) { inclusive = true }
                    }
                },
                onJoinRoomSuccess = { roomCode, username ->
                    navController.navigate(Screen.Swipe.createRoute(roomCode, username))
                },
                onHomeClick = {
                    navController.navigate(Screen.Homepage.route)
                },
                onReviewsClick = {
                    navController.navigate(Screen.Reviews.route)
                }
            )
        }

        composable(Screen.Reviews.route) {
            ReviewScreen(
                authViewModel = authViewModel,
                reviewViewModel = hiltViewModel(),
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homepage.route) { inclusive = true }
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Homepage.route)
                },
                onRoomsClick = {
                    navController.navigate(Screen.Rooms.route)
                },
            )
        }

        composable(Screen.Swipe.route) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val username = backStackEntry.arguments?.getString("username") ?: ""

            SwipeScreen(
                roomCode = roomCode,
                username = username,
                viewModel = hiltViewModel<RecommendationViewModel>(),
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Homepage.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}