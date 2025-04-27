package com.example.moonrise.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.example.moonrise.R
import java.util.Calendar

class YearFilterDialog(
    private val onApply: (startYear: Int?, endYear: Int?) -> Unit,
    private val minYear: Int?,
    private val maxYear: Int?,
    private val defaultStartText: String = "С",  // Значение по умолчанию для начального текста
    private val defaultEndText: String = "ПО"   // Значение по умолчанию для конечного текста
) : DialogFragment() {

    private lateinit var numberPickerStart: NumberPicker
    private lateinit var numberPickerEnd: NumberPicker
    private var selectedStartText: String = defaultStartText
    private var selectedEndText: String = defaultEndText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_filter_year, container, false)

        numberPickerStart = view.findViewById(R.id.numberPickerStart)
        numberPickerEnd = view.findViewById(R.id.numberPickerEnd)

        // Настройка диапазона лет
        numberPickerStart.minValue = minYear ?: 1917
        numberPickerStart.maxValue = maxYear ?: Calendar.getInstance().get(Calendar.YEAR)
        numberPickerStart.value = minYear ?: 1917 // Значение по умолчанию для начального значения - "С"

        numberPickerEnd.minValue = minYear ?: 1917
        numberPickerEnd.maxValue = maxYear ?: Calendar.getInstance().get(Calendar.YEAR)
        numberPickerEnd.value = maxYear ?: Calendar.getInstance().get(Calendar.YEAR) // Значение по умолчанию для конечного значения - "ПО"

        // Обработчики кнопок
        view.findViewById<AppCompatButton>(R.id.btn_reset).setOnClickListener {
            onApply(null, null) // Сброс значений
            dismiss()
        }

        view.findViewById<AppCompatButton>(R.id.btn_apply).setOnClickListener {
            // Проверяем, если значение picker соответствует "С" или "ПО", то передаем null
            val startYear = if (numberPickerStart.value == minYear) null else numberPickerStart.value
            val endYear = if (numberPickerEnd.value == maxYear) null else numberPickerEnd.value

            onApply(startYear, endYear) // Применяем выбранные годы
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // Устанавливаем размеры окна (90% ширины экрана)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}