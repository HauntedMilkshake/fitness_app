package bg.zahov.app.ui.custom

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import bg.zahov.app.util.SettingsChangeListener
import bg.zahov.app.data.model.Language
import bg.zahov.app.data.model.Sound
import bg.zahov.app.data.model.Theme
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.local.Settings
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView


class RadioGroupSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {

    var settingsChangeListener: SettingsChangeListener? = null

    init {
        inflate(context, R.layout.radio_group_settings_view, this)
    }

    //FIXME again - meaningful names, see other comments in ExerciseView.
    // This component looks very similar, to the other one, can you use the same component for both?
    // I see that you are using a listener here, so maybe use this one as a starting point
    fun initViewInformation(title: String, radioOptions: List<String>, settings: Settings) {
        val titleTextView: MaterialTextView = findViewById(R.id.titleTextView)
        val subtitleTextView: MaterialTextView = findViewById(R.id.subtitleTextView)

        when (title) {
            "Language" -> subtitleTextView.text = settings.language
            "Units" -> subtitleTextView.text = settings.units
            "Timer increment value" -> subtitleTextView.text = settings.restTimer.toString()
            "Sound" -> subtitleTextView.text = settings.soundSettings
            "Theme" -> subtitleTextView.text = settings.theme

        }

        titleTextView.text = title

        val highlightAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f)
        highlightAnimator.duration = 300
        highlightAnimator.repeatMode = ValueAnimator.REVERSE
        highlightAnimator.repeatCount = 1

        setOnClickListener {
            highlightAnimator.start()
            showPopupWindow(title, radioOptions, settings, subtitleTextView)

        }
    }

    private fun showPopupWindow(
        title: String,
        radioOptions: List<String>,
        settings: Settings,
        subtitleTextView: MaterialTextView,
    ) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.settings_popup, null)
        popupView.setBackgroundResource(R.drawable.custom_popup_background)
        val popupTitleTextView: MaterialTextView = popupView.findViewById(R.id.popupTitleTextView)
        val radioGroup: RadioGroup = popupView.findViewById(R.id.radioGroup)

        popupTitleTextView.text = title

        radioOptions.forEachIndexed { index, _ ->
            val radioButton = RadioButton(context)
            val currOption = radioOptions[index]
            radioButton.text = currOption
            radioButton.layoutParams =
                RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                )
            radioButton.setPadding(16, 16, 16, 16)
            radioButton.setTextColor(ContextCompat.getColor(context, R.color.white))
            radioButton.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            radioGroup.addView(radioButton)

            if (currOption == getSelectedOption(title, settings)) {
                radioButton.isChecked = true
            }
        }
        radioGroup.setOnCheckedChangeListener { _, index ->
            val radioButton = radioGroup.findViewById<RadioButton>(index)
            val selectedOption = radioButton.text.toString()

            when (title) {
                "Language" -> Language.valueOf(selectedOption)
                "Units" -> Units.valueOf(selectedOption)
                "Timer increment value" -> selectedOption.split(" ").first().toInt()
                "Sound" -> Sound.valueOf(selectedOption)
                "Theme" -> Theme.valueOf(selectedOption)
                else -> null
            }?.let {
                settingsChangeListener?.onSettingChanged(title, it)
                subtitleTextView.text = when (title) {
                    "Language" -> (it as Language).name
                    "Units" -> (it as Units).name
                    "Timer increment value" -> "$it s"
                    "Sound" -> (it as Sound).name
                    "Theme" -> (it as Theme).name
                    else -> null
                }
            }
        }
        val scaleUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        popupView.startAnimation(scaleUpAnimation)

        val popupWindow = PopupWindow(
            popupView,
            resources.getDimension(R.dimen.popup_width).toInt(),
            resources.getDimension(R.dimen.popup_height).toInt(),
            true
        )
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
    }

    private fun getSelectedOption(title: String, settings: Settings): String? {
        return when (title) {
            "Language" -> settings.language
            "Units" -> settings.units
            "Timer increment value" -> "${settings.restTimer} s"
            "Sound" -> settings.soundSettings
            "Theme" -> settings.theme
            else -> null
        }
    }
}


