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
        // FIXME when inflating a layout to define a custom view, use merge tag as root tag in the
        //  inflated layout and set the android:parentTag attribute to the class that the custom view inherits
        //  this way you will avoid nesting two layouts
        inflate(context, R.layout.default_settings_view, this)
    }

    // FIXME use proper naming that describes what this method does - sets a title
    //  also keep references for the views that are accessed to avoid traversal of the view three
    //  in this case you should have a field private val title: MaterialTextView that is assigned in init
    fun initViewInformation(text: String) {
        val title: MaterialTextView = findViewById(R.id.title)
        title.text = text
    }
}