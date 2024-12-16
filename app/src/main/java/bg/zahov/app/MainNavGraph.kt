package bg.zahov.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bg.zahov.app.ui.authentication.signup.SignupScreen
import bg.zahov.app.ui.authentication.login.LoginScreen
import bg.zahov.app.ui.home.HomeScreen
import bg.zahov.app.ui.loading.LoadingScreen
import bg.zahov.app.ui.welcome.WelcomeScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    topBarCall: (TopBar?) -> Unit
) {

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = WelcomeScreen
    ) {
        composable<WelcomeScreen> {
            topBarCall(null)
            WelcomeScreen(
                onSignup = { navController.navigate(SignupScreen) },
                onLogin = { navController.navigate(LoginScreen) }
            )
        }
        composable<SignupScreen> {
            topBarCall(null)
            SignupScreen(
                onAuthenticate = { navController.navigate(LoadingScreen) },
                onNavigateToLogin = { navController.navigate(LoginScreen) }
            )
        }
        composable<LoginScreen> {
            topBarCall(null)
            LoginScreen(
                onAuthenticate = { navController.navigate(LoadingScreen) },
                onNavigateToSignUp = { navController.navigate(SignupScreen) }
            )
        }
        composable<LoadingScreen> {
            topBarCall(null)
            LoadingScreen(
                navigateWelcome = { navController.navigate(WelcomeScreen) },
                navigateHome = { navController.navigate(HomeScreen) }
            )
        }
        composable<HomeScreen> {
            topBarCall(TopBar.Home)
            HomeScreen()
        }
    }
}