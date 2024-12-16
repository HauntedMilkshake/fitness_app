package bg.zahov.app

import bg.zahov.fitness.app.R
import kotlinx.serialization.Serializable

//TODO(Write out other data objects for screens)
@Serializable
data class WelcomeScreen(val topBar: TopBar = TopBar.Welcome)

@Serializable
data object SignupScreen

@Serializable
data object LoginScreen

enum class TopBar(titleId:Int?=null){
    Register,
    Login,
    Welcome,
    Home(R.string.home),


}