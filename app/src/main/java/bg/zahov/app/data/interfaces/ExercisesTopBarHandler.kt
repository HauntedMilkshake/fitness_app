package bg.zahov.app.data.interfaces

import kotlinx.coroutines.flow.StateFlow

interface ExercisesTopBarHandler {
    val openDialog: StateFlow<Boolean>
    suspend fun changeOpenDialog(isOpen: Boolean)
    val search: StateFlow<String>
    suspend fun changeSearch(text: String)
}