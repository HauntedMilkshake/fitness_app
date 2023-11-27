package bg.zahov.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import bg.zahov.app.data.Language
import bg.zahov.app.data.Settings
import bg.zahov.app.data.Sound
import bg.zahov.app.data.Theme
import bg.zahov.app.data.Units
import bg.zahov.app.settings.SettingsViewModel
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class ExerciseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {
    init {
        inflate(context, R.layout.radio_group_exercise_create_view, this)
    }
    fun initViewInformation(title: String, radioOptions: List<String>) {
        val titleTextView: MaterialTextView = findViewById(R.id.titleTextView)

        titleTextView.text = title

        setOnClickListener {
            showPopupWindow(title, radioOptions)
        }
    }
    private fun showPopupWindow(title: String, radioOptions: List<String>) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.settings_popup, null)
        val popupTitleTextView: MaterialTextView = popupView.findViewById(R.id.popupTitleTextView)
        val radioGroup: RadioGroup = popupView.findViewById(R.id.radioGroup)

        popupTitleTextView.text = title

        radioOptions.forEachIndexed { index, item ->
            val radioButton = RadioButton(context)
            val currOption = radioOptions[index]
            radioButton.text = currOption
            radioButton.layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            radioGroup.addView(radioButton)

        }
        radioGroup.setOnCheckedChangeListener { _, index ->
            val radioButton = radioGroup.findViewById<RadioButton>(index)
            val selectedOption = radioButton.text.toString()

        }

        val popupWindow = PopupWindow(popupView, resources.getDimension(R.dimen.popup_width).toInt(), resources.getDimension(
            R.dimen.popup_height).toInt(), true)
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
    }
}
