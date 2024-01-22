package bg.zahov.app.ui.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Filter
import kotlinx.coroutines.launch

//FIXME see AuthViewModel comments
class ExerciseViewModel : ViewModel(){
    private val _userExercises = MutableLiveData<List<Exercise>>()
    val userExercises: LiveData<List<Exercise>> get() = _userExercises
    private val _searchFilters = MutableLiveData<List<Filter>>(listOf()) //FIXME use emptyList()
    val searchFilters: LiveData<List<Filter>> = _searchFilters

    private val allExercises: MutableList<Exercise> = mutableListOf()

    private var search: String? = null

    fun getBodyPartItems(): List<Filter> {
        val bodyPartFilters = enumValues<BodyPart>().map { Filter(it.name) }
        bodyPartFilters.forEach {
            it.selected = _searchFilters.value?.any { filter -> filter.name == it.name } == true
        }
        return bodyPartFilters
    }

    fun getCategoryItems(): List<Filter> {
        val categoryFilters = enumValues<Category>().map { Filter(it.name) }
        categoryFilters.forEach {
            it.selected = _searchFilters.value?.any { filter -> filter.name == it.name } == true
        }
        return categoryFilters
    }

    init {
        getUserExercises()
    }

    private fun getUserExercises() {
        viewModelScope.launch {
//            repo.getTemplateExercises()?.collect { exercises ->
//                when (exercises) {
//                    is InitialResults -> {
//                        _userExercises.postValue(exercises.list)
//                        allExercises.addAll(exercises.list)
//                    }
//
//                    is UpdatedResults -> {
//                        _userExercises.postValue(exercises.list)
//                        allExercises.addAll(exercises.list)
//                    }
//                }
//            }
        }
    }

//    fun addFilter(filter: Filter) {
//        val filters = _searchFilters.value?.toMutableList() ?: mutableListOf()
//        filters.add(filter)
//        _searchFilters.value = filters
//
//        searchExercises(search, filters)
//    }
//
//    fun removeFilter(filter: Filter) {
//        val filters = _searchFilters.value?.toMutableList() ?: mutableListOf()
//        filters.remove(filter)
//        _searchFilters.value = filters
//
//        searchExercises(search, filters)
//    }

//    fun searchExercises(name: String?, filters: List<Filter?>) {
//        //FIXME let {} would be more suitable in this case
//        val newExercises = _userExercises.value?.let {
//            when {
//                name.isNullOrEmpty() && filters.isEmpty() -> allExercises
//                name.isNullOrEmpty() && filters.isNotEmpty() -> {
//                    it.filter { exercise ->
//                        filters.any { filter ->
//                            //this is not ok
//                            filter?.name == exercise.category.name || filter?.name == BodyPart.fromKey(exercise.bodyPart.key)
//                        }
//                    }
//                }
//
//                !name.isNullOrEmpty() && filters.isEmpty() -> filter {
//                    it.exerciseName?.contains(name, true) == true
//                }
//
//                else -> filter {
//                    val nameMatches = name.isNullOrEmpty() || it.exerciseName?.contains(name, true) == true
//                    val categoryMatches = filters.any { filter -> filter?.name == it.category }
//                    val bodyPartMatches = filters.any { filter -> filter?.name == it.bodyPart }
//
//                    nameMatches && (categoryMatches || bodyPartMatches)
//                }
//            }
//        } ?: emptyList()
//
//        search = name
//        _userExercises.value = newExercises
//    }
}
