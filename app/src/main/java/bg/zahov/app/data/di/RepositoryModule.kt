package bg.zahov.app.data.di

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
object InternalRepositoryModule {
    @Provides
    @Singleton
    fun provideInternalFirebaseAuthentication(
        auth: FirebaseAuth,
        firestore: FirestoreManager,
    ): FirebaseAuthentication = FirebaseAuthentication(auth, firestore)

    @Provides
    @Singleton
    fun provideInternalFirestore(firestore: FirebaseFirestore): FirestoreManager =
        FirestoreManager(firestore)
}

@Module
@InstallIn(SingletonComponent::class)
object ExternalDependenciesModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

}