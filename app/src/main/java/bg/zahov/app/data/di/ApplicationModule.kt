package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.Authentication
import bg.zahov.app.data.interfaces.ExercisesTopBarHandler
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.interfaces.RestProvider
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.data.interfaces.UserRepository
import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.interfaces.WorkoutProvider
import bg.zahov.app.data.interfaces.WorkoutRepository
import bg.zahov.app.data.provider.AddExerciseToWorkoutProvider
import bg.zahov.app.data.provider.ExercisesTopBarManager
import bg.zahov.app.data.provider.FilterProvider
import bg.zahov.app.data.provider.MeasurementProviderImpl
import bg.zahov.app.data.provider.ReplaceableExerciseProvider
import bg.zahov.app.data.provider.RestTimerProvider
import bg.zahov.app.data.provider.SelectableExerciseProvider
import bg.zahov.app.data.provider.ServiceErrorHandlerImpl
import bg.zahov.app.data.provider.UserProviderImpl
import bg.zahov.app.data.provider.WorkoutProviderImpl
import bg.zahov.app.data.provider.WorkoutStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideUserProvider(userRepo: UserRepository, auth: Authentication): UserProvider = UserProviderImpl(userRepo, auth)

    @Provides
    @Singleton
    fun provideWorkoutProvider(workoutRepo: WorkoutRepository): WorkoutProvider = WorkoutProviderImpl(workoutRepo)

    @Provides
    @Singleton
    fun provideMeasurementProvider(measurementRepo: MeasurementRepository): MeasurementProvider =
        MeasurementProviderImpl(measurementRepo)

    @Singleton
    fun provideSelectableExerciseProvider(): SelectableExerciseProvider =
        SelectableExerciseProvider.getInstance()


    @Singleton
    fun provideReplaceableExerciseProvider(): ReplaceableExerciseProvider =
        ReplaceableExerciseProvider.getInstance()

    @Singleton
    fun provideWorkoutAddedExerciseProvider(): AddExerciseToWorkoutProvider =
        AddExerciseToWorkoutProvider.getInstance()

    @Singleton
    fun provideFilterProvider(): FilterProvider =
        FilterProvider.getInstance()

    @Provides
    @Singleton
    fun provideWorkoutStateProvider(): WorkoutActions = WorkoutStateManager.getInstance()

    @Provides
    @Singleton
    fun provideRestTimerProvider(): RestProvider = RestTimerProvider.getInstance()

    @Provides
    @Singleton
    fun provideServiceErrorHandler(): ServiceErrorHandler = ServiceErrorHandlerImpl.getInstance()

    @Provides
    @Singleton
    fun provideExercisesTopAppHandler(): ExercisesTopBarHandler =
        ExercisesTopBarManager.getInstance()
}