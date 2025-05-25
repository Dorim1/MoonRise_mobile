package com.example.moonrise

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.moonrise.databinding.ActivityMainBinding
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        val sharedPref = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDark = sharedPref.getBoolean("dark_theme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateStatusBarIcons()


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        val navView = binding.navView

        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            val handled = item.onNavDestinationSelected(navController)
            if (handled) {
                animateButtonScale(item.itemId)
            }
            handled
        }
    }

    private fun getMenuItemView(menuItemId: Int): View? {
        val navView = binding.navView
        val menuView = navView.getChildAt(0)

        if (menuView is ViewGroup) {
            for (i in 0 until menuView.childCount) {
                val itemView = menuView.getChildAt(i)
                if (itemView.id == menuItemId) {
                    return itemView
                }
            }
        }
        return null
    }

    // Функция для анимации кнопки
    private fun animateButtonScale(itemId: Int) {
        val menuItemView = getMenuItemView(itemId) ?: return

        val scaleXAnimator = ObjectAnimator.ofFloat(menuItemView, "scaleX", 1f, 1.2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(menuItemView, "scaleY", 1f, 1.2f, 1f)

        AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun updateStatusBarIcons() {
        val isLightTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightTheme) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = if (isLightTheme)
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            else
                0
        }
    }
}