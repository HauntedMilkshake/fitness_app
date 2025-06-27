package bg.zahov.app.data.repository.mock

import bg.zahov.app.data.interfaces.MeasurementRepository
import bg.zahov.app.data.model.Measurement
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.data.model.Measurements
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.random.Random

class MockMeasurementRepository @Inject constructor() : MeasurementRepository {

    private val measurements = generateMeasurementData()

    private fun generateMeasurementData(): MutableMap<MeasurementType, MutableList<Measurement>> {
        val data = mutableMapOf<MeasurementType, MutableList<Measurement>>()
        MeasurementType.entries.forEachIndexed { index, item ->
            data.put(
                item,
                mutableListOf(
                    Measurement(
                        date = LocalDateTime.now().minusDays(Random(index).nextInt(0, 6).toLong()),
                        value = Random(index).nextInt(10, 101).toDouble()
                    )
                )
            )
        }
        return data
    }

    override suspend fun getMeasurement(type: MeasurementType) =
        Measurements(measurements = mapOf(type to (measurements[type] ?: listOf())))

    override suspend fun updateMeasurement(
        measurementType: MeasurementType,
        measurement: Measurement,
    ) {
        measurements[measurementType]?.add(measurement)
    }

}