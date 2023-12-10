package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.realm_db.User

class FirestoreUserAdapter : Adapter<Map<String, Any>, User> {
    override fun adapt(t: Map<String, Any>): User {
        return User().apply {
            username = t["username"] as? String
            numberOfWorkouts = (t["numberOfWorkouts"] as? Long)?.toInt()

        }
    }
}
