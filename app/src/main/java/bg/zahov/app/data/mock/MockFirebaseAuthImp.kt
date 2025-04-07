package bg.zahov.app.data.mock

import android.os.Parcel
import bg.zahov.app.data.interfaces.FirebaseAuthentication
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthResult
import com.google.firebase.functions.dagger.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Module
class MockFirebaseAuthImp @Inject constructor(
) : FirebaseAuthentication {

    private var isLoggedIn = true
    private var userEmail: String = "test@example.com"

    override suspend fun signup(email: String, password: String): AuthResult {
        return mockAuthResult()
    }

    override suspend fun login(email: String, password: String): AuthResult {
        isLoggedIn = true
        userEmail = email
        return mockAuthResult()
    }

    override fun logout() {
        isLoggedIn = false
    }

    override fun deleteAccount() {
        isLoggedIn = false
    }

    override suspend fun passwordResetForLoggedUser(): Task<Void> {
        return Tasks.forResult(null)
    }

    override suspend fun passwordResetByEmail(email: String): Task<Void> = Tasks.forResult(null)

    override fun getAuthStateFlow(): Flow<Boolean> = flow {
        emit(isLoggedIn)
    }

    override fun initFirestoreUser() {
        // Simulate initializing Firestore user
    }

    override suspend fun create(username: String, userId: String) {
        // Simulated user creation
    }

    override suspend fun updatePassword(newPassword: String): Task<Void> = Tasks.forResult(null)

    override suspend fun reauthenticate(password: String): Task<Void> = Tasks.forResult(null)

    override suspend fun getEmail(): String = userEmail

    private fun mockAuthResult(): AuthResult {
        return object : AuthResult {
            override fun describeContents(): Int = 0

            override fun writeToParcel(p0: Parcel, p1: Int) {
            }

            override fun getAdditionalUserInfo() = null

            override fun getCredential() = null

            override fun getUser() = null
        }
    }
}