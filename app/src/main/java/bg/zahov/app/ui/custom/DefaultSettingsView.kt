package bg.zahov.app.ui.custom

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

    private var titleTextView: MaterialTextView? = null

    init {
        inflate(context, R.layout.default_settings_view, this)

        titleTextView = findViewById(R.id.title)
    }

    fun setViewTitle(text: String) {
        titleTextView?.text = text
    }
}