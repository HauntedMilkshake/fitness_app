package bg.zahov.app.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bg.zahov.fitness.app.databinding.FragmentExercisesBinding

class FragmentExercises: Fragment() {
    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            searchIcon.setOnClickListener {
                exerciseText.visibility = View.GONE
                searchIcon.visibility = View.GONE
                settingsDots.visibility = View.GONE
                removeSearchBar.visibility = View.VISIBLE
                searchBar.visibility = View.VISIBLE
            }
            removeSearchBar.setOnClickListener {
                if(searchBar.visibility == View.VISIBLE){
                    exerciseText.visibility = View.VISIBLE
                    searchIcon.visibility = View.VISIBLE
                    settingsDots.visibility = View.VISIBLE
                    searchBar.visibility = View.INVISIBLE
                    removeSearchBar.visibility = View.GONE
                }
            }
        }
    }
}