package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.FirebaseAuthentication
import bg.zahov.app.data.interfaces.FirestoreManager
import bg.zahov.app.data.mock.MockFirebaseAuthImp
import bg.zahov.app.data.mock.MockFirestoreManagerImp
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DataSourceModule::class])
object MockDataSourceModule {
    @Provides
    @Singleton
    fun provideFirebaseAuthentication(): FirebaseAuthentication = MockFirebaseAuthImp()

    @Provides
    @Singleton
    fun provideFirestoreManager(): FirestoreManager = MockFirestoreManagerImp()
}