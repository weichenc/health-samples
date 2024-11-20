package com.example.healthconnectsample.data

import androidx.health.connect.client.records.SkinTemperatureRecord
import androidx.health.connect.client.units.Temperature
import androidx.health.connect.client.units.TemperatureDelta
import java.time.Instant
import java.time.ZoneOffset

/**
 * Represents skin temperature data, raw, aggregated, for a given [SkinTemperatureRecord].
 */
data class SkinTemperatureRecordData (
    val uid: String,
    val startTime: Instant,
    val startZoneOffset: ZoneOffset?,
    val endTime: Instant,
    val endZoneOffset: ZoneOffset?,
    val baseline: Temperature?,
    val deltaAvg: TemperatureDelta?,
    val deltaMax: TemperatureDelta?,
    val deltaMin: TemperatureDelta?,
    val deltas: List<SkinTemperatureRecord.Delta>
)