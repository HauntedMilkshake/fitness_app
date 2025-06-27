package bg.zahov.app.data.di

import bg.zahov.app.data.mock.MockFirebaseAuth
import bg.zahov.app.data.mock.MockFirestoreManager
import bg.zahov.app.data.remote.FirebaseAuthentication
import bg.zahov.app.data.remote.FirestoreManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideFirebaseAuthentication(
        auth: FirebaseAuth,
        firestore: FirestoreManager
    ): FirebaseAuthentication = MockFirebaseAuth(auth, firestore)

    @Provides
    @Singleton
    fun provideFirestoreManager(
        firestore: FirebaseFirestore
    ): FirestoreManager = MockFirestoreManager(firestore)
}