package bg.zahov.app.data.model

import bg.zahov.app.data.local.Sets

data class ClickableSet(
    val set: Sets,
    var clicked: Boolean
)
