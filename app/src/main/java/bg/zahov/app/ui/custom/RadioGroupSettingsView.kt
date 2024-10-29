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
import bg.zahov.app.data.model.state.TypeSettings
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView


class RadioGroupSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {

    private var settingsChangeListener: SettingsChangeListener? = null

    init {
        inflate(context, R.layout.view_radio_group_settings, this)
    }

    //FIXME again - meaningful names, see other comments in ExerciseView.
    // This component looks very similar, to the other one, can you use the same component for both?
    // I see that you are using a listener here, so maybe use this one as a starting point
    fun initViewInformation(type: TypeSettings, radioOptions: List<String>, settings: Settings) {
        val titleTextView: MaterialTextView = findViewById(R.id.titleTextView)
        val subtitleTextView: MaterialTextView = findViewById(R.id.subtitleTextView)

        when (type) {
            TypeSettings.LANGUAGE_SETTING -> subtitleTextView.text = settings.language.toString()
            TypeSettings.UNIT_SETTING -> subtitleTextView.text = settings.units.toString()
            TypeSettings.REST_TIMER_SETTING -> subtitleTextView.text = settings.restTimer.toString()
            TypeSettings.SOUND_SETTING -> subtitleTextView.text = settings.soundSettings.toString()
            TypeSettings.THEME_SETTING -> subtitleTextView.text = settings.theme.toString()
            else -> {}
        }

        titleTextView.text = type.name

        val highlightAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f)
        highlightAnimator.duration = 300
        highlightAnimator.repeatMode = ValueAnimator.REVERSE
        highlightAnimator.repeatCount = 1

        setOnClickListener {
            highlightAnimator.start()
            showPopupWindow(type, radioOptions, settings, subtitleTextView)
        }
    }

    private fun showPopupWindow(
        type: TypeSettings,
        radioOptions: List<String>,
        settings: Settings,
        subtitleTextView: MaterialTextView,
    ) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.popup_settings, null)
        popupView.setBackgroundResource(R.drawable.custom_popup_background)
        val popupTitleTextView: MaterialTextView =
            popupView.findViewById(R.id.popup_title_text_view)
        val radioGroup: RadioGroup = popupView.findViewById(R.id.radio_group)

        popupTitleTextView.text = type.name

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

            if (currOption == getSelectedOption(type, settings)) {
                radioButton.isChecked = true
            }
        }
        radioGroup.setOnCheckedChangeListener { _, index ->
            val radioButton = radioGroup.findViewById<RadioButton>(index)
            val selectedOption = radioButton.text.toString()

            when (type) {
                TypeSettings.LANGUAGE_SETTING -> Language.valueOf(selectedOption)
                TypeSettings.UNIT_SETTING -> Units.valueOf(selectedOption)
                TypeSettings.REST_TIMER_SETTING -> selectedOption.split(" ").first().toInt()
                TypeSettings.SOUND_SETTING -> Sound.valueOf(selectedOption)
                TypeSettings.THEME_SETTING -> Theme.valueOf(selectedOption)
                else -> null
            }?.let {
                settingsChangeListener?.onSettingChanged(type, it)
                subtitleTextView.text = when (type) {
                    TypeSettings.LANGUAGE_SETTING -> (if (it is Language) {
                        it.name
                    } else {
                        null
                    }).toString()

                    TypeSettings.UNIT_SETTING -> (if (it is Units) it.name else null).toString()
                    TypeSettings.REST_TIMER_SETTING -> "$it s"
                    TypeSettings.SOUND_SETTING -> (if (it is Sound) it.name else null).toString()
                    TypeSettings.THEME_SETTING -> (if (it is Theme) it.name else null).toString()
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

    private fun getSelectedOption(type: TypeSettings, settings: Settings): String? {
        return when (type) {
            TypeSettings.LANGUAGE_SETTING -> settings.language.toString()
            TypeSettings.UNIT_SETTING -> settings.units.toString()
            TypeSettings.REST_TIMER_SETTING -> "${settings.restTimer} s"
            TypeSettings.SOUND_SETTING -> settings.soundSettings.toString()
            TypeSettings.THEME_SETTING -> settings.theme.toString()
            else -> null
        }
    }
}


