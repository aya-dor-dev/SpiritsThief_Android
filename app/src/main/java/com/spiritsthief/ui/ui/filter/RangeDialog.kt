package com.spiritsthief.ui.ui.filter

import android.content.DialogInterface
import androidx.fragment.app.FragmentActivity
import androidx.appcompat.app.AlertDialog
import android.widget.NumberPicker
import com.spiritsthief.R
import com.spiritsthief.api.model.SearchRequest
import java.util.*

class RangeDialog(activity: FragmentActivity,
                  title: Int,
                  min: Int?,
                  max: Int?,
                  val values: MutableList<Int>,
                  prefix: String,
                  suffix: String,
                  val onRangeChanged: (Int, Int) -> Unit) : DialogInterface.OnClickListener, NumberPicker.OnValueChangeListener {
    private val pickerMin: NumberPicker
    private val pickerMax: NumberPicker
    private var timer: Timer = Timer()
    private var timerTask: TimerTask? = null

    val dialog: AlertDialog

    init {
        dialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setPositiveButton("OK", this)
                .setNegativeButton("CANCEL", this)
                .setView(R.layout.filter_dialog_range_2)
                .show()
        pickerMin = dialog.findViewById(R.id.min)!!
        pickerMax = dialog.findViewById(R.id.max)!!
        pickerMin.minValue = 0
        pickerMin.maxValue = values.size
        pickerMax.minValue = 0
        pickerMax.maxValue = values.size

        pickerMin.wrapSelectorWheel = false
        pickerMax.wrapSelectorWheel = false

        val displayedValues = mutableListOf<String>()
        displayedValues.add("Any")
        displayedValues.addAll(values.map { "$prefix$it$suffix" })
        pickerMin.displayedValues = displayedValues.toTypedArray()
        pickerMax.displayedValues = displayedValues.toTypedArray()

        pickerMin.value = when(min) {
            SearchRequest.DEFAULT_VAL -> 0
            else -> values.indexOf(min) + 1
        }

        pickerMax.value = when(max) {
            SearchRequest.DEFAULT_VAL -> 0
            else -> values.indexOf(max) + 1
        }

        pickerMin.setOnValueChangedListener(this)
        pickerMax.setOnValueChangedListener(this)
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        timerTask?.cancel()
        timer.cancel()
        timer.purge()

        timerTask = kotlin.concurrent.timerTask {
            if (picker == pickerMin) {
                if (newVal != 0 && newVal > pickerMax.value && pickerMax.value != 0) {
                    pickerMax.value = newVal
                }
            } else {
                if (newVal != 0 && newVal < pickerMin.value && pickerMin.value != 0) {
                    pickerMin.value = newVal
                }
            }
        }

        timer = Timer()
        timer.schedule(timerTask!!, 1500L)
    }

    private fun validatePickers() {
        val min = pickerMin.value
        val max = pickerMax.value
        if (min != 0 && max != 0 && min > max) {
            pickerMin.value = pickerMax.value
        }
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            validatePickers()
            onRangeChanged(
                    if (pickerMin.value == 0) SearchRequest.DEFAULT_VAL else values[pickerMin.value - 1],
                    if (pickerMax.value == 0) SearchRequest.DEFAULT_VAL else values[pickerMax.value - 1]
            )
        }
    }

}

