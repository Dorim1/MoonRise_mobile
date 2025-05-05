package com.example.moonrise

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.moonrise.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var indicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        indicator = binding.indicator

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navView = binding.navView

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_list,
                R.id.navigation_dashboard,
                R.id.navigation_profile -> {
                    moveIndicatorTo(destination.id)
                }
            }
        }

        navView.post {
            moveIndicatorTo(navView.selectedItemId)
        }

        navView.setOnItemSelectedListener { item ->
            val handled = item.onNavDestinationSelected(navController)
            if (handled) {
                moveIndicatorTo(item.itemId)
                animateButtonScale(item.itemId)
            }
            handled
        }
    }

    // Функция для получения нужного элемента меню по ID
    private fun getMenuItemView(menuItemId: Int): View? {
        val navView = binding.navView
        val menuView = navView.getChildAt(0) // Получаем контейнер меню

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

    // Функция для перемещения индикатора
    private fun moveIndicatorTo(menuItemId: Int) {
        val menuItemView = getMenuItemView(menuItemId) ?: return
        val targetX = menuItemView.x + (menuItemView.width / 2) - (indicator.width / 2)

        // Используем ObjectAnimator для анимации перемещения
        ObjectAnimator.ofFloat(indicator, "x", targetX).apply {
            duration = 300
            start()
        }
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