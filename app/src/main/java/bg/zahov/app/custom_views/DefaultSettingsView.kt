package bg.zahov.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class DefaultSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {
    init {
        inflate(context, R.layout.default_settings_view, this)
    }

    fun initViewInformation(text: String) {
        val title: MaterialTextView = findViewById(R.id.title)
        title.text = text
    }
}