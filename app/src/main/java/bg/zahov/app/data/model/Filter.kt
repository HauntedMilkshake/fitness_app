package bg.zahov.app.data.model

sealed class Filter {
    data class CategoryFilter(val category: Category?) : Filter() {
        val name: String = category?.name ?: ""
    }

    data class BodyPartFilter(val bodyPart: BodyPart?) : Filter() {
        val name: String = bodyPart?.name ?: ""
    }
}

data class FilterWrapper(val filter: Filter) {
    val name: String
        get() = when (filter) {
            is Filter.BodyPartFilter -> filter.name
            is Filter.CategoryFilter -> filter.name
        }
    var selected: Boolean = false
}