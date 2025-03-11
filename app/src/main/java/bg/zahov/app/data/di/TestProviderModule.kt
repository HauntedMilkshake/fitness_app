package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.interfaces.SettingsProvider
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.interfaces.WorkoutRepository
import bg.zahov.app.data.mock.MockAuthenticationImpl
import bg.zahov.app.data.repository.AuthenticationImpl
import bg.zahov.app.data.repository.MeasurementRepositoryImpl
import bg.zahov.app.data.repository.SettingsRepositoryImpl
import bg.zahov.app.data.repository.UserRepositoryImpl
import bg.zahov.app.data.repository.WorkoutRepositoryImpl
import com.google.firebase.functions.dagger.Module
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ProviderModule::class]
)
object TestProviderModule {
    @dagger.Provides
    @Singleton
    fun provideWorkoutRepository(): WorkoutRepository = WorkoutRepositoryImpl()

    @dagger.Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepositoryImpl()

    @dagger.Provides
    @Singleton
    fun provideSettingsRepository(): SettingsProvider = SettingsRepositoryImpl()

    @dagger.Provides
    @Singleton
    fun provideAuthenticationRepository(): Authentication = MockAuthenticationImpl()

    @dagger.Provides
    @Singleton
    fun provideMeasurementRepository(): MeasurementRepository = MeasurementRepositoryImpl()
}