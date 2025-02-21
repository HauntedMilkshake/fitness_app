package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.ExercisesTopBarHandler
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.interfaces.RestProvider
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.UserProvider
import bg.zahov.app.data.interfaces.WorkoutActions
import bg.zahov.app.data.interfaces.WorkoutProvider
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
    fun provideUserProvider(): UserProvider = UserProviderImpl.getInstance()

    @Provides
    @Singleton
    fun provideWorkoutProvider(): WorkoutProvider = WorkoutProviderImpl.getInstance()

    @Provides
    @Singleton
    fun provideMeasurementProvider(): MeasurementProvider =
        MeasurementProviderImpl.getInstance()

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
    fun provideWorkoutStateProvider(): WorkoutActions = WorkoutStateManager()

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