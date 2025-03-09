package com.example.moonrise

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.example.moonrise.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var indicator: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        indicator = binding.indicator

        // Используем post для перемещения индикатора после завершения раскладки
        navView.post {
            moveIndicatorTo(navView.selectedItemId)
        }

        navView.setOnItemSelectedListener { item ->
            moveIndicatorTo(item.itemId)
            item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment_activity_main)) // Переход безопасным способом
            animateButtonScale(item.itemId) // Анимация увеличения и уменьшения
            true
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

        // Анимация увеличения и уменьшения
        val scaleXAnimator = ObjectAnimator.ofFloat(menuItemView, "scaleX", 1f, 1.2f, 1f).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        }

        val scaleYAnimator = ObjectAnimator.ofFloat(menuItemView, "scaleY", 1f, 1.2f, 1f).apply {
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
        }

        scaleXAnimator.start()
        scaleYAnimator.start()
    }
}