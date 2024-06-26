package bg.zahov.app.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.appcompat.widget.SwitchCompat
import bg.zahov.app.util.SettingsChangeListener
import bg.zahov.app.data.local.Settings
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class SwitchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {

    var settingsChangeListener: SettingsChangeListener? = null

    init {
        inflate(context, R.layout.view_switch_settings, this)
    }

    // FIXME check comments in ExerciseView
    fun initViewInformation(title: String, subTitle: String, settings: Settings) {
        val titleView = findViewById<MaterialTextView>(R.id.titleTextView)
        val subTitleView = findViewById<MaterialTextView>(R.id.subtitleTextView)
        // FIXME pay attention to the warning  - use SwitchCompat or SwitchMaterial
        val switchView = findViewById<SwitchCompat>(R.id.settings_switch)

        val state = when (title) {
            "Sound effects" -> settings.soundEffects
            "Vibrate upon finish" -> settings.vibration
            "Use samsung watch during workout" -> settings.fit
            "Show update template" -> settings.updateTemplate
            "Automatic between device sync" -> settings.automaticSync
            else -> false
        }
        titleView.text = title
        subTitleView.text = subTitle
        switchView.isChecked = state

        switchView.setOnCheckedChangeListener { _, isChecked ->
            handleSwitchStateChanged(title, isChecked)
        }
    }

    private fun handleSwitchStateChanged(title: String, isChecked: Boolean) {
        settingsChangeListener?.onSettingChanged(title, isChecked)
    }
}