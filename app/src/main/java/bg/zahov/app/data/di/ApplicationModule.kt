package bg.zahov.app.data.di

import bg.zahov.app.data.interfaces.ExercisesTopBarHandler
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.interfaces.RestProvider
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.interfaces.SettingsProvider
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
import bg.zahov.app.data.provider.SettingsProviderImpl
import bg.zahov.app.data.provider.UserProviderImpl
import bg.zahov.app.data.provider.WorkoutProviderImpl
import bg.zahov.app.data.provider.WorkoutStateManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {

    @Binds
    @Singleton
    abstract fun provideWorkoutStateProvider(workoutStateProvider: WorkoutStateManager): WorkoutActions

    @Binds
    @Singleton
    abstract fun provideRestTimerProvider(restProvider: RestTimerProvider): RestProvider

    @Binds
    @Singleton
    abstract fun provideServiceErrorHandler(serviceErrorHandler: ServiceErrorHandlerImpl): ServiceErrorHandler

    @Binds
    @Singleton
    abstract fun provideExercisesTopAppHandler(exerciseTopBarManager: ExercisesTopBarManager): ExercisesTopBarHandler
}

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideUserProvider(): UserProvider = UserProviderImpl()

    @Provides
    @Singleton
    fun provideSettingsProvider(): SettingsProvider = SettingsProviderImpl()

    @Provides
    @Singleton
    fun provideWorkoutProvider(): WorkoutProvider = WorkoutProviderImpl()

    @Provides
    @Singleton
    fun provideMeasurementProvider(): MeasurementProvider =
        MeasurementProviderImpl()

    @Singleton
    fun provideSelectableExerciseProvider(): SelectableExerciseProvider =
        SelectableExerciseProvider()


    @Singleton
    fun provideReplaceableExerciseProvider(): ReplaceableExerciseProvider =
        ReplaceableExerciseProvider()

    @Singleton
    fun provideWorkoutAddedExerciseProvider(): AddExerciseToWorkoutProvider =
        AddExerciseToWorkoutProvider()

    @Singleton
    fun provideFilterProvider(): FilterProvider =
        FilterProvider()

    @Provides
    @Singleton
    fun provideWorkoutStateProvider(): WorkoutActions = WorkoutStateManager.getInstance()
}