package bg.zahov.app.utils

import bg.zahov.app.util.Adapter
import bg.zahov.app.data.local.Sets

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