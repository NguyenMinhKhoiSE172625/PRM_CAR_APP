package com.example.prmcar.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prmcar.data.preferences.TokenManager
import com.example.prmcar.data.repository.AuthRepository
import com.example.prmcar.data.repository.CarRepository
import com.example.prmcar.presentation.screen.CarListScreen
import com.example.prmcar.presentation.screen.LoginScreen
import com.example.prmcar.presentation.viewmodel.AuthViewModel
import com.example.prmcar.presentation.viewmodel.CarViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CarList : Screen("car_list")
    object CarDetail : Screen("car_detail/{carId}") {
        fun createRoute(carId: Int) = "car_detail/$carId"
    }
    object AddCar : Screen("add_car")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager) }
    val carRepository = remember { CarRepository(tokenManager) }
    
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }
    val carViewModel: CarViewModel = viewModel { CarViewModel(carRepository) }
    
    val authUiState by authViewModel.uiState.collectAsState()
    val carUiState by carViewModel.uiState.collectAsState()

    // Navigation logic based on auth state
    LaunchedEffect(authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn) {
            navController.navigate(Screen.CarList.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (authUiState.isLoggedIn) Screen.CarList.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authUiState,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onClearError = {
                    authViewModel.clearError()
                }
            )
        }

        composable(Screen.CarList.route) {
            CarListScreen(
                carUiState = carUiState,
                authUiState = authUiState,
                onCarClick = { carId ->
                    navController.navigate(Screen.CarDetail.createRoute(carId))
                },
                onAddCarClick = {
                    navController.navigate(Screen.AddCar.route)
                },
                onRefresh = {
                    carViewModel.refresh()
                },
                onLogout = {
                    authViewModel.logout()
                },
                onSearch = { query ->
                    carViewModel.loadCars(search = query.ifBlank { null })
                }
            )
        }

        composable(Screen.CarDetail.route) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId")?.toIntOrNull()
            if (carId != null) {
                LaunchedEffect(carId) {
                    carViewModel.getCarById(carId)
                }
                
                // Placeholder for CarDetailScreen - you can implement this later
                CarListScreen(
                    carUiState = carUiState,
                    authUiState = authUiState,
                    onCarClick = { },
                    onAddCarClick = { },
                    onRefresh = { },
                    onLogout = { authViewModel.logout() },
                    onSearch = { }
                )
            }
        }

        composable(Screen.AddCar.route) {
            // Placeholder for AddCarScreen - you can implement this later
            CarListScreen(
                carUiState = carUiState,
                authUiState = authUiState,
                onCarClick = { },
                onAddCarClick = { },
                onRefresh = { },
                onLogout = { authViewModel.logout() },
                onSearch = { }
            )
        }
    }
} 