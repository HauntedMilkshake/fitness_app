package bg.zahov.app.data.di

import bg.zahov.fitness.app.BuildConfig
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
    ): FirebaseAuthentication = if (BuildConfig.USE_MOCK_DATA) MockFirebaseAuth(
        auth,
        firestore
    ) else FirebaseAuthentication(auth, firestore)

    @Provides
    @Singleton
    fun provideFirestoreManager(
        firestore: FirebaseFirestore
    ): FirestoreManager =
        if (BuildConfig.USE_MOCK_DATA) MockFirestoreManager(firestore) else FirestoreManager(
            firestore
        )
}