package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.interfaces.WorkoutRepository
import bg.zahov.app.data.remote.FirebaseAuthentication
import bg.zahov.app.data.remote.FirestoreManager
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.data.repository.MeasurementRepositoryImpl
import bg.zahov.app.data.repository.SettingsRepositoryImpl
import bg.zahov.app.data.repository.UserRepositoryImpl
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import bg.zahov.app.data.repository.mock.MockAuthentication
import bg.zahov.app.data.repository.mock.MockMeasurementRepository
import bg.zahov.app.data.repository.mock.MockUserRepository
import bg.zahov.app.data.repository.mock.MockWorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideWorkoutRepository(firestore: FirestoreManager): WorkoutRepository =
//        MockWorkoutRepository()
        WorkoutRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirestoreManager): UserRepository =
//        MockUserRepository()
        UserRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsProvider = SettingsRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthenticationRepository(auth: FirebaseAuthentication): Authentication =
//        MockAuthentication()
        AuthenticationImpl(auth)

    @Provides
    @Singleton
    fun provideMeasurementRepository(firestore: FirestoreManager): MeasurementRepository =
//        MockMeasurementRepository()
        MeasurementRepositoryImpl(firestore)
}