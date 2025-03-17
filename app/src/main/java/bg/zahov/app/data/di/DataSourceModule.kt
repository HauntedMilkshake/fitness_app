package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.FirebaseAuthentication
import bg.zahov.app.data.interfaces.FirestoreManager
import bg.zahov.app.data.mock.MockFirebaseAuthImp
import bg.zahov.app.data.mock.MockFirestoreManagerImp
import bg.zahov.app.data.remote.FirebaseAuthenticationImp
import bg.zahov.app.data.remote.FirestoreManagerImp
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
        firestore: FirestoreManager,
    ): FirebaseAuthentication = FirebaseAuthenticationImp(auth, firestore)

    @Provides
    @Singleton
    fun provideFirestoreManager(firestore: FirebaseFirestore): FirestoreManager =
        FirestoreManagerImp(firestore)

// MOCK DATA FOR BASELINE PROFILE
//    @Provides
//    @Singleton
//    fun provideFirebaseAuthentication(): FirebaseAuthentication = MockFirebaseAuthImp()
//
//    @Provides
//    @Singleton
//    fun provideFirestoreManager(): FirestoreManager = MockFirestoreManagerImp()
}