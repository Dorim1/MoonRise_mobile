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
    private val maxYear: Int?
) : DialogFragment() {

    private lateinit var numberPickerStart: NumberPicker
    private lateinit var numberPickerEnd: NumberPicker

    private lateinit var yearValues: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_filter_year, container, false)

        numberPickerStart = view.findViewById(R.id.numberPickerStart)
        numberPickerEnd = view.findViewById(R.id.numberPickerEnd)

        val min = minYear ?: 1917
        val max = maxYear ?: Calendar.getInstance().get(Calendar.YEAR)

        // Создаем список строк для годов от min до max
        yearValues = Array(max - min + 1) { index -> (min + index).toString() }

        // Настройка NumberPicker для start
        numberPickerStart.minValue = 0
        numberPickerStart.maxValue = yearValues.size - 1
        numberPickerStart.displayedValues = yearValues
        numberPickerStart.value = 0 // по умолчанию выбираем первый год
        numberPickerStart.wrapSelectorWheel = true  // Включаем цикличность

        // Настройка NumberPicker для end
        numberPickerEnd.minValue = 0
        numberPickerEnd.maxValue = yearValues.size - 1
        numberPickerEnd.displayedValues = yearValues
        numberPickerEnd.value = yearValues.size - 1 // по умолчанию выбираем последний год
        numberPickerEnd.wrapSelectorWheel = true  // Включаем цикличность

        // Обработчики кнопок
        view.findViewById<AppCompatButton>(R.id.btn_reset).setOnClickListener {
            onApply(null, null)
            dismiss()
        }

        view.findViewById<AppCompatButton>(R.id.btn_apply).setOnClickListener {
            val startYear = yearValues.getOrNull(numberPickerStart.value)?.toInt()
            val endYear = yearValues.getOrNull(numberPickerEnd.value)?.toInt()
            onApply(startYear, endYear)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}