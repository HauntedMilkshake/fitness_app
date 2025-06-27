package bg.zahov.app.data.repository.mock

import bg.zahov.app.data.interfaces.WorkoutRepository
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.data.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class MockWorkoutRepository @Inject constructor(): WorkoutRepository {
    private val templateWorkouts = MutableStateFlow<MutableList<Workout>>(
        mutableListOf(
            Workout(
                name = "Full Body Strength",
                isTemplate = true,
                exercises = listOf(
                    Exercise(
                        name = "Bench Press",
                        bodyPart = BodyPart.Chest,
                        category = Category.Dumbbell
                    ),
                    Exercise(name = "Squat", bodyPart = BodyPart.Legs, category = Category.Barbell),
                    Exercise(
                        name = "Pull Up",
                        bodyPart = BodyPart.Back,
                        category = Category.Machine
                    )
                )
            ),
            Workout(
                name = "Cardio Blast",
                isTemplate = true,
                exercises = listOf(
                    Exercise(
                        name = "Running",
                        bodyPart = BodyPart.Legs,
                        category = Category.Cardio
                    ),
                    Exercise(
                        name = "Jump Rope",
                        bodyPart = BodyPart.Legs,
                        category = Category.Cardio
                    )
                )
            ),
            Workout(
                name = "Leg Day",
                isTemplate = true,
                exercises = listOf(
                    Exercise(
                        name = "Leg Press",
                        bodyPart = BodyPart.Legs,
                        category = Category.Machine
                    ),
                    Exercise(
                        name = "Leg Curl",
                        bodyPart = BodyPart.Legs,
                        category = Category.Dumbbell
                    ),
                    Exercise(
                        name = "Calf Raise",
                        bodyPart = BodyPart.Legs,
                        category = Category.Cable
                    )
                )
            ),
            Workout(
                name = "Upper Body Push",
                isTemplate = true,
                exercises = listOf(
                    Exercise(
                        name = "Shoulder Press",
                        bodyPart = BodyPart.Shoulders,
                        category = Category.Barbell
                    ),
                    Exercise(
                        name = "Tricep Dip",
                        bodyPart = BodyPart.Arms,
                        category = Category.Dumbbell
                    ),
                    Exercise(
                        name = "Push Up",
                        bodyPart = BodyPart.Chest,
                        category = Category.AdditionalWeight
                    )
                )
            )
        )
    )
    private val workouts = MutableStateFlow<MutableList<Workout>>(
        mutableListOf(
            Workout(
                name = "Full Body Strength",
                duration = 60,
                volume = 1000.0,
                date = LocalDateTime.now().minusDays(1),
                exercises = listOf(
                    Exercise(
                        name = "Bench Press",
                        bodyPart = BodyPart.Chest,
                        category = Category.Dumbbell,
                        sets = mutableListOf(
                            Sets(SetType.DEFAULT, 80.0, 8),
                            Sets(SetType.DEFAULT, 85.0, 6)
                        ),
                        bestSet = Sets(SetType.DEFAULT, 85.0, 6)
                    ),
                    Exercise(
                        name = "Squat",
                        bodyPart = BodyPart.Legs,
                        category = Category.Barbell,
                        sets = mutableListOf(
                            Sets(SetType.DEFAULT, 100.0, 10),
                            Sets(SetType.DEFAULT, 105.0, 8)
                        ),
                        bestSet = Sets(SetType.DEFAULT, 105.0, 8)
                    )
                ),
                personalRecords = 1
            ),
            Workout(
                name = "Cardio Blast",
                duration = 45,
                volume = 500.0,
                date = LocalDateTime.now().minusDays(2)
            ),
            Workout(
                name = "Leg Day",
                duration = 70,
                volume = 1200.0,
                date = LocalDateTime.now().minusDays(3),
                exercises = listOf(
                    Exercise(
                        name = "Leg Press",
                        bodyPart = BodyPart.Legs,
                        category = Category.Barbell,
                        sets = mutableListOf(
                            Sets(SetType.DEFAULT, 200.0, 10),
                            Sets(SetType.DEFAULT, 220.0, 8)
                        ),
                        bestSet = Sets(SetType.DEFAULT, 220.0, 8)
                    )
                ),
                personalRecords = 2
            ),
            Workout(
                name = "Upper Body Push",
                duration = 50,
                volume = 900.0,
                date = LocalDateTime.now().minusDays(4),
                exercises = listOf(
                    Exercise(
                        name = "Shoulder Press",
                        bodyPart = BodyPart.Shoulders,
                        category = Category.Machine,
                        sets = mutableListOf(
                            Sets(SetType.DEFAULT, 50.0, 10),
                            Sets(SetType.DEFAULT, 55.0, 8)
                        ),
                        bestSet = Sets(SetType.DEFAULT, 55.0, 8)
                    )
                ),
                personalRecords = 1
            ),
            Workout(
                name = "Core Workout",
                duration = 40,
                volume = 400.0,
                date = LocalDateTime.now().minusDays(5)
            ),
            Workout(
                name = "Arms & Abs",
                duration = 45,
                volume = 600.0,
                date = LocalDateTime.now().minusDays(6)
            ),
            Workout(
                name = "HIIT Session",
                duration = 30,
                volume = 350.0,
                date = LocalDateTime.now().minusDays(7)
            ),
            Workout(
                name = "Back & Biceps",
                duration = 55,
                volume = 1100.0,
                date = LocalDateTime.now().minusDays(8)
            ),
            Workout(
                name = "Endurance Cardio",
                duration = 60,
                volume = 700.0,
                date = LocalDateTime.now().minusDays(9)
            )
        )
    )
    private val templateExercises = MutableStateFlow<MutableList<Exercise>>(
        mutableListOf(
            Exercise(name = "Bench Press", bodyPart = BodyPart.Chest, category = Category.Dumbbell),
            Exercise(name = "Squat", bodyPart = BodyPart.Legs, category = Category.Barbell),
            Exercise(name = "Pull Up", bodyPart = BodyPart.Back, category = Category.Machine),
            Exercise(name = "Running", bodyPart = BodyPart.Legs, category = Category.Cardio),
            Exercise(name = "Jump Rope", bodyPart = BodyPart.Legs, category = Category.Cardio),
            Exercise(name = "Leg Press", bodyPart = BodyPart.Legs, category = Category.Machine),
            Exercise(name = "Leg Curl", bodyPart = BodyPart.Legs, category = Category.Dumbbell),
            Exercise(name = "Calf Raise", bodyPart = BodyPart.Legs, category = Category.Cable),
            Exercise(
                name = "Shoulder Press",
                bodyPart = BodyPart.Shoulders,
                category = Category.Barbell
            ),
            Exercise(name = "Tricep Dip", bodyPart = BodyPart.Arms, category = Category.Dumbbell),
            Exercise(
                name = "Push Up",
                bodyPart = BodyPart.Chest,
                category = Category.AdditionalWeight
            ),
            Exercise(name = "Bench Press", bodyPart = BodyPart.Chest, category = Category.Dumbbell),
            Exercise(name = "Squat", bodyPart = BodyPart.Legs, category = Category.Barbell),
            Exercise(name = "Leg Press", bodyPart = BodyPart.Legs, category = Category.Barbell),
            Exercise(
                name = "Shoulder Press",
                bodyPart = BodyPart.Shoulders,
                category = Category.Machine
            )
        )
    )

    override suspend fun getTemplateWorkouts(): Flow<List<Workout>> = templateWorkouts

    override suspend fun getPastWorkouts(): Flow<List<Workout>> = workouts

    override suspend fun addTemplateWorkout(newWorkout: Workout) {
        templateWorkouts.value.add(newWorkout)
    }

    override suspend fun getTemplateExercises(): Flow<List<Exercise>> = templateExercises
    override suspend fun addTemplateExercise(newExercise: Exercise) {
        templateExercises.value.add(newExercise)
    }

    override suspend fun addWorkoutToHistory(newWorkout: Workout) {
        workouts.value.add(newWorkout)
    }

    override suspend fun deleteTemplateWorkout(workout: Workout) {
        templateWorkouts.value.remove(workout)
    }

    override suspend fun deleteWorkout(workout: Workout) {
        workouts.value.remove(workout)
    }

    override suspend fun getWorkoutById(id: String): Flow<Workout> =
        workouts.map { it.find { workout -> workout.id == id } ?: Workout() }

    override suspend fun getWorkoutByName(name: String): Flow<Workout> =
        workouts.map { it.find { workout -> workout.name == name } ?: Workout() }

    override suspend fun updateExercises(exercises: List<Exercise>) {
        /* TODO() */
    }

    override suspend fun getPastWorkoutById(id: String): Workout = Workout() /* TODO() */

    override suspend fun updateTemplateWorkout(
        workoutId: String,
        date: LocalDateTime,
        newExercise: List<Exercise>,
    ) {
        /* TODO() */
    }

    override suspend fun clearWorkoutState() {
    }
}