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
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class DefaultSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.default_settings_view, this, true)
    }

    fun initViewInformation(title: String, subtitle: String, radioOptions: List<String>) {
        val titleTextView: MaterialTextView = findViewById(R.id.titleTextView)
        val subtitleTextView: MaterialTextView = findViewById(R.id.subtitleTextView)

        titleTextView.text = title
        subtitleTextView.text = subtitle

        setOnClickListener {
            showPopupWindow(title, radioOptions)
        }
    }

    private fun showPopupWindow(title: String, radioOptions: List<String>) {
        val popupView: View = LayoutInflater.from(context).inflate(R.layout.settings_popup, null)

        val popupTitleTextView: MaterialTextView = popupView.findViewById(R.id.popupTitleTextView)
        val radioGroup: RadioGroup = popupView.findViewById(R.id.radioGroup)

        popupTitleTextView.text = title

        radioOptions.forEach {
            val radioButton = RadioButton(context)
            radioButton.text = it
            radioGroup.addView(radioButton)

        }

        val popupWindow = PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
    }
}
