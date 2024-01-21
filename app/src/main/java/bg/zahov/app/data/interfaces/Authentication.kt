package bg.zahov.app.data.interfaces

interface Authentication {
    suspend fun login(email: String, password: String)
    suspend fun signup(username: String, email: String, password: String)
    suspend fun logout()
    suspend fun deleteAccount()
    suspend fun passwordReset(email: String)

}