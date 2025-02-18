package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.interfaces.WorkoutRepository
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.data.repository.MeasurementRepositoryImpl
import bg.zahov.app.data.repository.SettingsRepositoryImpl
import bg.zahov.app.data.repository.UserRepositoryImpl
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    @Singleton
    fun provideWorkoutRepository(): WorkoutRepository = WorkoutRepositoryImpl()

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @Provides
    @Singleton
    fun provideSettingsRepository(): SettingsProvider = SettingsRepositoryImpl()

    @Provides
    @Singleton
    fun provideAuthenticationRepository(): Authentication = AuthenticationImpl()

    @Provides
    @Singleton
    fun provideMeasurementRepository(): MeasurementRepository = MeasurementRepositoryImpl()
}