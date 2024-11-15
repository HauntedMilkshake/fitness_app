package bg.zahov.app.data.model

/**
 * A sealed class representing different types of filters that can be applied.
 * The two types of filters are `CategoryFilter` and `BodyPartFilter`.
 */
sealed class Filter {

    /**
     * Represents a filter by category.
     * @property category The category to filter by, can be null.
     * @property name The name of the category (an empty string if null).
     */
    data class CategoryFilter(val category: Category?) : Filter() {
        val name: String = category?.name ?: ""
    }

    /**
     * Represents a filter by body part.
     * @property bodyPart The body part to filter by, can be null.
     * @property name The name of the body part (an empty string if null).
     */
    data class BodyPartFilter(val bodyPart: BodyPart?) : Filter() {
        val name: String = bodyPart?.name ?: ""
    }
}

/**
 * A wrapper for a filter, representing a filter and its selected state.
 * This is used to track the selected filter and its corresponding name.
 *
 * @property filter The actual filter (either `CategoryFilter` or `BodyPartFilter`).
 * @property name The name of the filter, either category or body part name.
 * @property selected Boolean value indicating whether this filter is selected.
 */
data class FilterItem(
    val filter: Filter,
    var selected: Boolean = false
) {

    /**
     * The name of the filter. Returns the name of the category or body part based on the type of filter.
     */
    val name: String
        get() = when (filter) {
            is Filter.BodyPartFilter -> filter.name
            is Filter.CategoryFilter -> filter.name
        }
}
