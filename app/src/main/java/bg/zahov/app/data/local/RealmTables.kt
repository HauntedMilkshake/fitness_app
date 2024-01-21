package bg.zahov.app.data.local

import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.LanguageKeys
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.SoundKeys
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.ThemeKeys
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.UnitsKeys
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

// FIXME Realm is an object database so maybe revise the file name
//  One big advantage of Kotlin is that there is a distinction between nullable and non-nullable objects
//  Whenever designing model classes prefer to have non-nullable fields and consider carefully whether
//  it makes sense to have a null value
//  Although Realm's documentation encourages you to use these objects directly in your project, doing that
//  wil
//  ic. It's
//  better to keep the usage of these objects limited to the scope of the Realm storage infrastructure and
//  use separate model classes (your domain objects) for your business logic and map between types when
//  persisted data is served to clients (your ViewModels by a repository)
//TODO(Potentially add other enums for the boolean and int variables)
class Settings : RealmObject {
    var language: String = Language.fromKey(LanguageKeys.ENGLISH)
    var units: String = Units.fromKey(UnitsKeys.METRIC)
    var soundEffects: Boolean = true
    var theme: String = Theme.fromKey(ThemeKeys.DARK)
    var restTimer: Int = 30
    var vibration: Boolean = true
    var soundSettings: String = Sound.fromKey(SoundKeys.SOUND_1)
    var updateTemplate: Boolean = true
    var fit: Boolean = false
    var automaticSync: Boolean = true
}