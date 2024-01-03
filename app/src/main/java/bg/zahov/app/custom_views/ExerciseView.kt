package bg.zahov.app.custom_views

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
import bg.zahov.app.data.BodyPart
import bg.zahov.app.data.Category
import bg.zahov.app.exercise.addExercises.AddExerciseViewModel
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

        radioOptions.forEachIndexed { index, _ ->
            val radioButton = RadioButton(context)
            val currOption = radioOptions[index]
            radioButton.text = currOption
            radioButton.layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            radioButton.setPadding(16, 16, 16, 16)
            radioButton.setTextColor(ContextCompat.getColor(context, R.color.white))
            radioButton.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
            radioButton.layoutParams = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )

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
        return when (bodyPart) {
            "Core" -> BodyPart.Core.name
            "Arms" -> BodyPart.Arms.name
            "Back" -> BodyPart.Back.name
            "Chest" -> BodyPart.Chest.name
            "Legs" -> BodyPart.Legs.name
            "Shoulders" -> BodyPart.Shoulders.name
            "Other" -> BodyPart.Other.name
            "Olympic" -> BodyPart.Olympic.name
            else -> ""
        }
    }

    private fun getCategory(category: String): String {
        return when (category) {
            "Barbell" -> Category.Barbell.name
            "Dumbbell" -> Category.Dumbbell.name
            "Machine" -> Category.Machine.name
            "Additional weight" -> Category.AdditionalWeight.name
            "Assisted weight" -> Category.AssistedWeight.name
            "Reps only" -> Category.RepsOnly.name
            "Cardio" -> Category.Cardio.name
            "Timed" -> Category.Timed.name
            else -> Category.None.name
        }
    }
}

