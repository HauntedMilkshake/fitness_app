package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.backend.User

//FIXME replace these adapters with factory method in entity, also - use constants instead of hardcoded strings
class FirestoreUserAdapter : Adapter<Map<String, Any>?, User> {
    override fun adapt(t: Map<String, Any>?): User {
        return User().apply {
            t?.let {
                username = it["username"] as? String
                numberOfWorkouts = (it["numberOfWorkouts"] as? Long)?.toInt()
            }
        }
    }
}
