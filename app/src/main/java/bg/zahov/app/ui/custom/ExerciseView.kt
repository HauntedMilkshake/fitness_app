package bg.zahov.app.ui.custom

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
import androidx.core.view.setPadding
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.ui.exercise.add.AddExerciseViewModel
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class ExerciseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RelativeLayout(context, attrs, defStyle) {
    init {
        inflate(context, R.layout.create_exercise_view, this)
    }

    //FIXME don't pass the VM directly to a view, if you need some data to initialize the view/popup
    // pass it as a parameter. Then instead of invoking a concrete VM function on given UI event,
    // create a listener interface, set it to the view and notify it on change
    fun initViewInformation(
        title: String,
        radioOptions: List<String>,
        exerciseVm: AddExerciseViewModel,
    ) {
        val titleTextView: MaterialTextView = findViewById(R.id.titleTextView)

        titleTextView.text = title

        setOnClickListener {
            showPopupWindow(title, radioOptions, exerciseVm)
        }
    }

        //FIXME This looks like a pretty generic component -
        // you have an list of items, you have an initial selection and you want to track selection changes.
        // You don't need to handle category and body part input differently here, just pass the initial state,
        // and items here and then handle changes in a listener. I would advise against using the labels passed
        // in radioOptions as a way to map to actual model classes - this will not work with localization.
        // Instead you can make the method generic and pass a lambda that maps the generic type with a string for
        // the label or use an interface for the options that has a getLabel method, or take another approach - you
        // get the idea
        private fun showPopupWindow(
            title: String,
            radioOptions: List<String>,
            exerciseVm: AddExerciseViewModel,
        ) {
            val popupView: View = LayoutInflater.from(context).inflate(R.layout.settings_popup, null)
            popupView.setBackgroundResource(R.drawable.custom_popup_background)
            val popupTitleTextView: MaterialTextView = popupView.findViewById(R.id.popupTitleTextView)
            val radioGroup: RadioGroup = popupView.findViewById(R.id.radioGroup)

            popupTitleTextView.text = title

            radioOptions.forEachIndexed { _, currOption ->
                val radioButton = RadioButton(context).apply {
                    text = currOption
                    layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
                    setPadding(R.dimen.pop_up_radius)
                }
                radioButton.setPadding(16, 16, 16, 16)
                radioButton.setTextColor(ContextCompat.getColor(context, R.color.white))
                radioButton.buttonTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
                radioButton.layoutParams = RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
                )
                // FIXME remove this
                when (title) {
                    "Category" -> {
                        if (currOption == getCategory(exerciseVm.getCurrCategory().toString())) {
                            radioButton.isChecked = true
                        }
                    }

                    "Body part" -> {
                        if (currOption == getBodyPart(exerciseVm.getCurrBodyPart().toString())) {
                            radioButton.isChecked = true
                        }
                    }
                }

                radioGroup.addView(radioButton)
            }

            radioGroup.setOnCheckedChangeListener { _, index ->
                val radioButton = radioGroup.findViewById<RadioButton>(index)
                val selectedOption = radioButton.text.toString()
                // FIXME replace with listener notification
                exerciseVm.buildExercise(title, selectedOption)
            }

            val scaleUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_up)
            popupView.startAnimation(scaleUpAnimation)

            val popupWindow = PopupWindow(
                popupView,
                resources.getDimension(R.dimen.popup_width).toInt(),
                (radioOptions.size * resources.getDimension(R.dimen.popup_min_height).toInt()),
                true
            )
            popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0)
        }

    private fun getBodyPart(bodyPart: String): String {
        return BodyPart.fromKey(bodyPart)
    }

    private fun getCategory(category: String): String {
        return Category.fromKey(category)
}

