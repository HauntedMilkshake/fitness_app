package bg.zahov.app.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.Switch
import bg.zahov.fitness.app.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SwitchSettingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {

    init {
        inflate(context, R.layout.switch_settings_view, this)

        val switchView = findViewById<Switch>(R.id.settings_switch)

        switchView.setOnCheckedChangeListener { buttonView, isChecked ->
            handleSwitchStateChanged(isChecked)
        }
    }

    fun initViewInformation(title: String, subTitle: String, state: Boolean){
        val titleView = findViewById<MaterialTextView>(R.id.titleTextView)
        val subTitleView = findViewById<MaterialTextView>(R.id.subtitleTextView)
        val switchView = findViewById<Switch>(R.id.settings_switch)
        titleView.text = title
        subTitleView.text = subTitle
        switchView.isChecked = state
    }
    //TODO(implement shared preferences storage here with a type for automation purposes)
    private fun handleSwitchStateChanged(isChecked: Boolean){
        when(isChecked){
            true -> {
            }
            false -> {

            }
        }
    }

}
