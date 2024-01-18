package bg.zahov.app.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.local.Exercise
import bg.zahov.app.data.local.Workout
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.UserRepository
import bg.zahov.app.utils.toExerciseList
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

//FIXME see comments in AuthViewModel and EditProfileViewModel
class AddWorkoutViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)
    private val _currExercises = MutableLiveData<List<Exercise>>(listOf())
    val currExercises: LiveData<List<Exercise>> get() = _currExercises

    fun addWorkout(name: String, ids: List<String>, callback: (String, Boolean) -> Unit) {
        when {
            ids.isNotEmpty() -> {
                viewModelScope.launch {
                    repo.addWorkout(
                        Workout().apply {
                            workoutName = name
                            exerciseIds = ids.toRealmList()
                            isTemplate = true
                            date = LocalDate.now().format(
                                // FIXME extract formatter
                                DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd",
                                    Locale.getDefault()
                                )
                            )
                        }
                    )
                }
            }

            else -> callback("Cannot have an empty template", false)
        }
    }

    fun addSelectedExercises(selectedExercises: List<SelectableExercise>) {
        val captured = _currExercises.value?.toMutableList()
        captured?.addAll(selectedExercises.toExerciseList())
        captured?.forEach {
            Log.d("EXERCISES", it.exerciseName ?: "penis")
        }
        _currExercises.value = captured ?: listOf()
    }

    fun addExercise(newExercise: SelectableExercise) {
        val captured = _currExercises.value?.toMutableList()
        captured?.add( newExercise.exercise )
        captured?.forEach {
            Log.d("EXERCISES", it.exerciseName ?: "penis")
        }
        _currExercises.value = captured ?: listOf()
    }
    fun removeExercise(position: Int) {
        val captured = _currExercises.value?.toMutableList()
        captured?.removeAt(position)
        _currExercises.value = captured ?: listOf()

    }

    fun addSet(ePosition: Int, newSet: ClickableSet) {
        val captured = _currExercises.value?.toMutableList()
        captured?.get(ePosition)?.sets?.add(newSet.set)
        _currExercises.value = captured ?: listOf()
    }
    //
    fun removeSet(ePosition: Int, sPosition: Int) {
        val captured = _currExercises.value?.toMutableList()
        captured?.get(ePosition)?.sets?.removeAt(sPosition)
    }

    fun resetSelectedExercises() {
        _currExercises.value = listOf()
    }
}