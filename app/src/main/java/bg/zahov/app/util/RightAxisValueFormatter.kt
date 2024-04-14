package bg.zahov.app.util

import com.github.mikephil.charting.formatter.ValueFormatter

class RightAxisValueFormatter(private val suffix: String) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()} $suffix"
    }
}