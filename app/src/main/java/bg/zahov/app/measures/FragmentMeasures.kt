package bg.zahov.app.measures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bg.zahov.fitness.app.databinding.FragmentMeasuresBinding

class FragmentMeasures: Fragment() {
    private var _binding: FragmentMeasuresBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMeasuresBinding.inflate(inflater, container, false)
        return binding.root
    }
}