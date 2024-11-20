package bg.zahov.app.ui.workout.finish

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.fitness.app.R

class FragmentWorkoutFinish : Fragment() {

    private val workoutFinishViewModel: WorkoutFinishViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.workout_finish)
        activity?.hideTopBar()
        activity?.hideBottomNav()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                WorkoutFinishScreen(
                    finishWorkoutViewModel = workoutFinishViewModel,
                    onClose = {
                        findNavController().navigateUp()
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mediaPlayer?.start()
//        ValueAnimator.ofFloat(1f, 1.2f).apply {
//            duration = 500
//            repeatCount = 1
//            repeatMode = ValueAnimator.REVERSE
//            addUpdateListener {
//                binding.star.scaleX = it.animatedValue as Float
//                binding.star.scaleY = it.animatedValue as Float
//            }
//        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}