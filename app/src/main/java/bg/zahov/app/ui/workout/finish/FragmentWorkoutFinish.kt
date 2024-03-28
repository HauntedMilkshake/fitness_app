package bg.zahov.app.ui.workout.finish

import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.util.timeToString
import bg.zahov.app.util.toFormattedString
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentWorkoutFinishBinding
import com.google.android.material.textview.MaterialTextView

class FragmentWorkoutFinish : Fragment() {
    private var _binding: FragmentWorkoutFinishBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val workoutFinishViewModel: WorkoutFinishViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWorkoutFinishBinding.inflate(inflater, container, false)
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.workout_finish)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        ValueAnimator.ofFloat(1f, 1.2f).apply {
            duration = 500
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
            addUpdateListener {
                binding.star.scaleX = it.animatedValue as Float
                binding.star.scaleY = it.animatedValue as Float
            }
        }.start()

        mediaPlayer?.start()

        val includedLayout = binding.workout.root

        val titleTextView = includedLayout.findViewById<MaterialTextView>(R.id.workout_name)
        val dateTextView = includedLayout.findViewById<MaterialTextView>(R.id.workout_date)
        val durationTextView = includedLayout.findViewById<MaterialTextView>(R.id.duration)
        val volumeTextView = includedLayout.findViewById<MaterialTextView>(R.id.volume)
        val prTextView = includedLayout.findViewById<MaterialTextView>(R.id.pr_count)
        val exercisesTextView = includedLayout.findViewById<MaterialTextView>(R.id.exercises)
        val bestSetsTextView = includedLayout.findViewById<MaterialTextView>(R.id.best_sets)

        workoutFinishViewModel.workout.observe(viewLifecycleOwner) { item ->
            titleTextView.text = item.name
            dateTextView.text = item.date.toFormattedString()
            durationTextView.text = item.duration?.timeToString()
            volumeTextView.text = "${item.volume ?: 0} kg"
            prTextView.text = item.personalRecords.toString()
            exercisesTextView.text = item.exercises.joinToString("\n") {
                "${if (it.sets.isNotEmpty()) "${it.sets.size} X " else ""}${it.name} "
            }
            bestSetsTextView.text = item.exercises.joinToString("\n") {
                "${it.bestSet.firstMetric ?: 0} X ${it.bestSet.secondMetric ?: 0}"
            }
        }

        workoutFinishViewModel.workoutCount.observe(viewLifecycleOwner) {
            binding.workoutCount.text = it
        }
        binding.close.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}