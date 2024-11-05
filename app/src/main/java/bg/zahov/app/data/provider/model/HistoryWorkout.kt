package bg.zahov.app.data.provider.model
/**
 * @property duration The duration of the workout in a string format (e.g., "00:30:00").
 * @property volume The total volume of weight lifted during the workout.
 * @property date The date when the workout was performed.
 * @property exercises exercises performed in the workout( 3 x exerciseName - example).
 * @property bestSets best sets for the workout(the ones where the most weight was lifted).
 * @property personalRecords number of personal records set during the workout.
 */
data class HistoryWorkout(
    val id: String,
    val name: String,
    val duration: String,
    val volume: String,
    val date: String,
    val exercises: List<String>,
    val bestSets: List<String>,
    val personalRecords: String
)
