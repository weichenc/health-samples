package com.example.healthconnectsample.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.SkinTemperatureRecord
import androidx.health.connect.client.units.Temperature
import androidx.health.connect.client.units.TemperatureDelta
import com.example.healthconnectsample.R
import com.example.healthconnectsample.data.SkinTemperatureRecordData
import com.example.healthconnectsample.data.dateTimeWithOffsetOrDefault
import com.example.healthconnectsample.data.formatHoursMinutes
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SkinTemperatureRecordRow(
    skinTemperatureRecord: SkinTemperatureRecordData,
    startExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(startExpanded) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable {
                expanded = !expanded
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val startDatetime = dateTimeWithOffsetOrDefault(skinTemperatureRecord.startTime, skinTemperatureRecord.startZoneOffset)
        val baseline = skinTemperatureRecord.baseline?.inCelsius?: 0.0
        val deltaAvg = skinTemperatureRecord.deltaAvg?.inCelsius?: 0.0


        Text(
            modifier = Modifier
                .weight(0.4f),
            color = MaterialTheme.colors.primary,
            text = startDatetime.format(formatter)
        )
        Text(
            text = "Average: " + String.format("%.1f", baseline + deltaAvg) + "°C"
        )
        IconButton(
            onClick = { expanded = !expanded }
        ) {
            val icon = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown
            Icon(icon, stringResource(R.string.delete_button))
        }
    }
    if (expanded) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val baseline = skinTemperatureRecord.baseline?.inCelsius?: 0.0

        skinTemperatureRecord.deltas.forEach{ delta ->
            val datetime = dateTimeWithOffsetOrDefault(delta.time, skinTemperatureRecord.startZoneOffset)
            Row{
                Text(
                    modifier = Modifier
                        .weight(0.4f),
                    color = MaterialTheme.colors.primary,
                    text = datetime.format(formatter)
                )
                Text(
                    text = String.format("%.1f", baseline + delta.delta.inCelsius) + "°C"
                )
            }

        }
    }
}

@Preview
@Composable
fun SkinTemperatureRecordRowPreview(){
    val endTime = ZonedDateTime.now()
    val startTime = endTime.minusHours(1)

    SkinTemperatureRecordRow(
        SkinTemperatureRecordData(
            uid = "123",
            startTime = startTime.toInstant(),
            startZoneOffset = startTime.offset,
            endTime = endTime.toInstant(),
            endZoneOffset = endTime.offset,
            baseline = Temperature.celsius(36.5),
            deltaAvg =  TemperatureDelta.celsius(0.2),
            deltaMax = TemperatureDelta.celsius(0.4),
            deltaMin = TemperatureDelta.celsius(-0.1),
            deltas = listOf(
                SkinTemperatureRecord.Delta(
                    time = startTime.plusMinutes(5).toInstant(),
                    delta = TemperatureDelta.celsius(0.2)
                ),
                SkinTemperatureRecord.Delta(
                    time = startTime.plusMinutes(10).toInstant(),
                    delta = TemperatureDelta.celsius(0.4)
                ),
                SkinTemperatureRecord.Delta(
                    time = startTime.plusMinutes(15).toInstant(),
                    delta = TemperatureDelta.celsius(-0.1)
                ),
                SkinTemperatureRecord.Delta(
                    time = startTime.plusMinutes(20).toInstant(),
                    delta = TemperatureDelta.celsius(0.3)
                ),
            )
        )
    )
}