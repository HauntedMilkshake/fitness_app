package bg.zahov.app.util

import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate

class MonthValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return "${value.toInt()} ${
            LocalDate.now().month.name.substring(0, 3).lowercase()
        }"
    }
}
