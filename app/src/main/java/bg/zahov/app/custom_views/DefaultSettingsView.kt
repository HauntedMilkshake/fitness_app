package bg.zahov.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import bg.zahov.app.data.Language
import bg.zahov.app.data.Settings
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Units
import bg.zahov.app.settings.SettingsViewModel
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class DefaultSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {
    init {
        LayoutInflater.from(context).inflate(R.layout.default_settings_view, this, true)
    }
//    private val settingsViewModel: SettingsViewModel by activityViewModels()
    fun initViewInformation(title: String, radioOptions: List<String>, settings: Settings, settingsVM: SettingsViewModel) {
        val titleTextView: MaterialTextView = findViewById(R.id.titleTextView)
        val subtitleTextView: MaterialTextView = findViewById(R.id.subtitleTextView)

        when (title) {
            "Language" -> {
                subtitleTextView.text = settings.language.name
            }
            "Weight" -> {
                subtitleTextView.text = settings.weight.name
            }
            "Distance" -> {
                subtitleTextView.text = settings.distance.name
            }
            "Timer increment value" -> subtitleTextView.text = settings.restTimer.toString()
            "Sound" -> {
                subtitleTextView.text = settings.soundSettings.name
            }
        }

        titleTextView.text = title

        setOnClickListener {
            showPopupWindow(title, radioOptions, settings, settingsVM)
        }
    }

    private fun showPopupWindow(title: String, radioOptions: List<String>, settings: Settings, settingsVM: SettingsViewModel) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.settings_popup, null)
        val popupTitleTextView: MaterialTextView = popupView.findViewById(R.id.popupTitleTextView)
        val radioGroup: RadioGroup = popupView.findViewById(R.id.radioGroup)

        popupTitleTextView.text = title
        //generating radio buttons and pre-selecting the one that corresponds to user's settings
        radioOptions.forEachIndexed { index, item ->
//            radioGroup.removeAllViews()
//            Log.d("INDEX", index.toString())
            val radioButton = RadioButton(context)
            val currOption = radioOptions.get(index = index)
            radioButton.text = currOption
            radioButton.layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            radioGroup.addView(radioButton)

//            Log.d("Pre-selected", "${getSelectedOption(title, settings)}")

            //naming scheme has to be exact(settings fragment in init)
            if ( currOption == getSelectedOption(title, settings)){
                radioButton.isChecked = true
            }
//            radioButton.visibility = View.VISIBLE
        }

        //TODO(Save only if needed)
        radioGroup.setOnCheckedChangeListener { _, index ->
            val radioButton = radioGroup.findViewById<RadioButton>(index)
            val selectedOption = radioButton.text.toString()

            when (title) {
                "Language" -> Language.valueOf(selectedOption)
                "Weight", "Distance" -> Units.valueOf(selectedOption)
                "Timer increment value" -> selectedOption.toLongOrNull()
                "Sound" -> Sound.valueOf(selectedOption)
                else -> null
            }?.let {
                updateSettings(title, settingsVM, it)
            }
            //TODO(Subtitle might not update after we change the setting)
        }

        val popupWindow = PopupWindow(popupView, resources.getDimension(R.dimen.popup_width).toInt(), resources.getDimension(R.dimen.popup_height).toInt(), true)
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0)

    }
    private fun getSelectedOption(title: String, settings: Settings): String? {
        return when (title) {
            "Language" -> settings.language.name
            "Weight" -> settings.weight.name
            "Distance" -> settings.distance.name
            "Timer increment value" -> settings.restTimer.toString()
            "Sound" -> settings.soundSettings.name
            else -> null
        }
    }
    private fun updateSettings(title: String, settingsViewModel: SettingsViewModel, newValue: Any) {
        settingsViewModel.updateSettings(title, newValue)
    }
}
