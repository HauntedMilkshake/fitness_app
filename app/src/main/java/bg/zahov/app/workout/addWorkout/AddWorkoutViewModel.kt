package bg.zahov.app.workout.addWorkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.realm_db.Workout
import bg.zahov.app.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddWorkoutViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val repo = UserRepository.getInstance(auth.currentUser!!.uid)

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
}