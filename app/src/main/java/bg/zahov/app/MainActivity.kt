package bg.zahov.app

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.map
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import bg.zahov.app.data.model.AuthUiModelMapper
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)

        binding.bottomNavigation.setupWithNavController(navController)

        authViewModel.state.map { AuthUiModelMapper.map(it) }.observe(this) {
            if (it.isAuthenticated) navController.navigate(R.id.welcome_to_loading)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

fun FragmentActivity.hideBottomNav() {
    findViewById<BottomNavigationView>(R.id.bottom_navigation)?.apply {
        visibility = View.GONE
    }
}

fun FragmentActivity.showBottomNav() {
    findViewById<BottomNavigationView>(R.id.bottom_navigation)?.apply {
        visibility = View.VISIBLE
    }
}