package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.backend.Sets

//FIXME replace these adapters with factory method in entity, also - use constants instead of hardcoded strings
class FirestoreSetsAdapter : Adapter<Map<String, Any>?, Sets> {
    override fun adapt(t: Map<String, Any>?): Sets {
        return Sets().apply {
            t?.let {
                firstMetric = it["firstMetric"] as? Int
                secondMetric = it["secondMetric"] as? Int
            }
        }
    }
}