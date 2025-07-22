package com.example.prmcar.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.prmcar.data.preferences.TokenManager
import com.example.prmcar.data.repository.AuthRepository
import com.example.prmcar.data.repository.CarRepository
import com.example.prmcar.data.repository.CarTypeRepository
import com.example.prmcar.data.repository.TransactionRepository
import com.example.prmcar.data.repository.UserRepository
import com.example.prmcar.presentation.screen.CarListScreen
import com.example.prmcar.presentation.screen.LoginScreen
import com.example.prmcar.presentation.viewmodel.AuthViewModel
import com.example.prmcar.presentation.viewmodel.CarViewModel
import com.example.prmcar.presentation.screen.AddEditCarScreen
import com.example.prmcar.presentation.screen.CarDetailScreen
import com.example.prmcar.presentation.screen.CarTypeListScreen
import com.example.prmcar.presentation.viewmodel.CarTypeViewModel
import com.example.prmcar.presentation.screen.TransactionListScreen
import com.example.prmcar.presentation.viewmodel.TransactionViewModel
import com.example.prmcar.presentation.screen.UserListScreen
import com.example.prmcar.presentation.viewmodel.UserViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object CarList : Screen("car_list")
    object CarDetail : Screen("car_detail/{carId}") {
        fun createRoute(carId: Int) = "car_detail/$carId"
    }
    object AddCar : Screen("add_car")
    object EditCar : Screen("edit_car/{carId}") {
        fun createRoute(carId: Int) = "edit_car/$carId"
    }
    object CarTypeList : Screen("car_type_list")
    object TransactionList : Screen("transaction_list")
    object UserList : Screen("user_list")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val authRepository = remember { AuthRepository(tokenManager) }
    val carRepository = remember { CarRepository(tokenManager) }
    val carTypeRepository = remember { CarTypeRepository(tokenManager) }
    val transactionRepository = remember { TransactionRepository(tokenManager) }
    val userRepository = remember { UserRepository(tokenManager) }
    
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(authRepository) }
    val carViewModel: CarViewModel = viewModel { CarViewModel(carRepository) }
    val carTypeViewModel: CarTypeViewModel = viewModel { CarTypeViewModel(carTypeRepository) }
    val transactionViewModel: TransactionViewModel = viewModel { TransactionViewModel(transactionRepository) }
    val userViewModel: UserViewModel = viewModel { UserViewModel(userRepository) }
    
    val authUiState by authViewModel.uiState.collectAsState()
    val carUiState by carViewModel.uiState.collectAsState()
    val carTypeUiState by carTypeViewModel.uiState.collectAsState()
    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()

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
            LaunchedEffect(Unit) { carViewModel.loadCars() }
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
                },
                onCarTypeManageClick = {
                    navController.navigate(Screen.CarTypeList.route)
                },
                onTransactionManageClick = {
                    navController.navigate(Screen.TransactionList.route)
                },
                onUserManageClick = {
                    navController.navigate(Screen.UserList.route)
                }
            )
        }

        composable(
            route = Screen.CarDetail.route,
            arguments = listOf(navArgument("carId") { type = NavType.IntType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId") ?: 0
            CarDetailScreen(
                carViewModel = carViewModel,
                selectedCar = carUiState.selectedCar,
                carId = carId,
                onNavigateBack = { navController.popBackStack() },
                onEditClick = {
                    navController.navigate(Screen.EditCar.createRoute(it))
                },
                userType = authUiState.userType,
                currentUserId = authUiState.userId
            )
        }
        
        composable(Screen.AddCar.route) {
            AddEditCarScreen(
                carViewModel = carViewModel,
                carUiState = carUiState,
                carId = null, // null for Add mode
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditCar.route,
            arguments = listOf(navArgument("carId") { type = NavType.IntType })
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getInt("carId")
            AddEditCarScreen(
                carViewModel = carViewModel,
                carUiState = carUiState,
                carId = carId, // Pass the carId for Edit mode
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CarTypeList.route) {
            LaunchedEffect(Unit) { carTypeViewModel.loadCarTypes() }
            CarTypeListScreen(
                carTypeUiState = carTypeUiState,
                carTypeViewModel = carTypeViewModel,
                onEditCarType = { /* Có thể mở dialog hoặc màn hình edit riêng nếu muốn */ },
                onBack = { navController.popBackStack() },
                userType = authUiState.userType
            )
        }

        composable(Screen.TransactionList.route) {
            LaunchedEffect(Unit) { transactionViewModel.loadTransactions() }
            TransactionListScreen(
                transactionUiState = transactionUiState,
                transactionViewModel = transactionViewModel,
                onEditTransaction = { /* Có thể mở dialog hoặc màn hình edit riêng nếu muốn */ },
                onBack = { navController.popBackStack() },
                userType = authUiState.userType,
                currentUserId = authUiState.userId,
                cars = carUiState.cars
            )
        }

        composable(Screen.UserList.route) {
            UserListScreen(
                userUiState = userUiState,
                userViewModel = userViewModel,
                onEditUser = { /* Có thể mở dialog hoặc màn hình edit riêng nếu muốn */ },
                onBack = { navController.popBackStack() },
                userType = authUiState.userType,
                currentUserId = authUiState.userId
            )
        }
    }
} 