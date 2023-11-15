import android.util.Log
import com.example.fitness_app.realm_db.Exercise
import com.example.fitness_app.realm_db.Sets
import com.example.fitness_app.realm_db.User
import com.example.fitness_app.realm_db.Workout
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.BaseRealmObject
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RealmManager {
    object RealmProcessor {
        private var realmInstance: Realm? = null
        fun startRealm(someSpecialProperties: <Type>, completion: () -> Unit? = {}) ({
            runInSafeQueue {
                try {
                    val config = RealmConfiguration.Builder(setOf(User::class, Workout::class, Exercise::class, Sets::class))
                    config.schemaVersion(1)
                    config.deleteRealmIfMigrationNeeded()

                    // We're using also Realm-JS, since we want the same directory that the JS thread created.
//                    config.name("my-percious-realm-$my_custom_property.realm")
                      config.name("realm-fitness.realm")
                    realmInstance = Realm.open(config.build())
                } catch(e: Error) {
                    Log.d("Realm start error", thrown = e)
                }
            })
        }

        // Since the threads has to be same for write operations which we used for opening Realm making it singleton with one dispatcher.
        private fun runInSafeQueue(runner: suspend () -> Unit?, didCatch: (Error) -> Unit = { _ -> }) {
            GlobalScope.launch {
                try {
                    runner()
                } catch (e: Error) {
                    didCatch(e)
                }
            }
        }

        // This is very basic example with making this Object class a generic Realm accessor so you initialize it in very first activity that your app used you can easily keep accessing it from any activity
        inline fun <reified T: BaseRealmObject>getFromRealm(id: Int): RealmResults<T>? {
            return realmInstance?.query(T::class, "id == $0", id)?.find()
        }

        fun <T: RealmObject>createInRealm(objectToCopyRealm: T) {
            runInSafeQueue({
                realmInstance?.write {
                    copyToRealm(objectToCopyRealm)
                    null
                }
            })
        }

        fun changeUserValue(changedValue: Int) {
            runInSafeQueue({
                realmInstance?.write {
                    val objectToChange = getFromRealm<User>(20)
                    objectToChange?.first()?.personalMessageRoom = changedValue
                }
            })
        }
    }
}