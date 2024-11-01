package bg.zahov.app.data.model

import bg.zahov.fitness.app.R

enum class BodyPart(val body: String, val image: Int) {
    Core("Core", R.drawable.ic_abs),
    Arms("Arms", R.drawable.ic_arms),
    Back("Back", R.drawable.ic_back),
    Chest("Chest", R.drawable.ic_chest),
    Legs("Legs", R.drawable.ic_legs),
    Shoulders("Shoulders", R.drawable.ic_shoulders),
    Other("Other", R.drawable.ic_olympic),
    Olympic("Olympic", R.drawable.ic_olympic);

    companion object {
        fun fromKey(key: String) = entries.firstOrNull { it.body == key }
    }
}