package bg.zahov.app.workout.addWorkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.SelectableExercise
import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.Workout
import bg.zahov.app.repository.UserRepository
import bg.zahov.app.utils.toExerciseList
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddWorkoutViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _currExercises = MutableLiveData<List<Exercise>>()
    val currExercises: LiveData<List<Exercise>> get() = _currExercises

    fun addWorkout(name: String, ids: List<String>, callback: (String, Boolean) -> Unit){
        when {
            ids.isNotEmpty() -> {
                viewModelScope.launch {
                    repo.addWorkout(
                        Workout().apply {
                            workoutName = name
                            exerciseIds = ids.toRealmList()
                            isTemplate = true
                            date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
                        }
                    )
                }
            }
            else -> callback("Cannot have an empty template", false)
        }
    }
    fun addSelectedExercises(selectedExercises: List<SelectableExercise>){
        val captured = _currExercises.value?.toMutableList()
        captured?.addAll(selectedExercises.toExerciseList())
        _currExercises.value = captured ?: listOf()
    }
}