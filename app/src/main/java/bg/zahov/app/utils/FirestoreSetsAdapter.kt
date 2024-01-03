package bg.zahov.app.utils

import bg.zahov.app.common.Adapter
import bg.zahov.app.realm_db.Sets

class FirestoreSetsAdapter: Adapter<Map<String, Any>?, Sets> {
    override fun adapt(t: Map<String, Any>?): Sets {
        return Sets().apply {
            t?.let {
                firstMetric = it["firstMetric"] as? Int
                secondMetric = it["secondMetric"] as? Int
            }
        }
    }
}