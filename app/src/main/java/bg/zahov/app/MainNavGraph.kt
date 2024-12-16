package bg.zahov.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bg.zahov.app.ui.authentication.signup.SignupScreen
import bg.zahov.app.ui.welcome.WelcomeScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    NavHost(modifier = modifier, navController = navController, startDestination = WelcomeScreen() {
        composable<WelcomeScreen> {
            WelcomeScreen(onSignup = { navController.navigate(SignupScreen(5)) }, onLogin = {
                navController.navigate(
                    LoginScreen
                )
            })
        }
        composable<SignupScreen> {
            SignupScreen(
                onAuthenticate = {},
                onNavigateToLogin = {}
            )
        }

    }
}