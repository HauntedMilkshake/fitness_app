package bg.zahov.app

import bg.zahov.fitness.app.R
import kotlinx.serialization.Serializable

//TODO(Write out other data objects for screens)
@Serializable
data object WelcomeScreen

@Serializable
data object SignupScreen

@Serializable
data object LoginScreen

@Serializable
data object HomeScreen

@Serializable
data object LoadingScreen

enum class TopBar(val titleId:Int){
    Home(titleId = R.string.home)
}