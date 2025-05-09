package com.example.moonrise

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.moonrise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray111)


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

    // Функция для получения нужного элемента меню по ID
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
}