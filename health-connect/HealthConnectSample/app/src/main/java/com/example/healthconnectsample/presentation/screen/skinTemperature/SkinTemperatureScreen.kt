package com.example.healthconnectsample.presentation.screen.skinTemperature

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.example.healthconnectsample.presentation.component.SkinTemperatureRecordRow
import com.example.healthconnectsample.presentation.theme.HealthConnectTheme
import java.time.ZonedDateTime
import java.util.UUID


/**
 * Shows a week's worth of sleep data.
 */
@Composable
fun SkinTemperatureScreen(
    permissions: Set<String>,
    permissionsGranted: Boolean,
    recordList: List<SkinTemperatureRecordData>,
    uiState: SkinTemperatureViewModel.UiState,
    onInsertClick: () -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    onPermissionsLaunch: (Set<String>) -> Unit = {}
) {

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    LaunchedEffect(uiState) {
        // If the initial data load has not taken place, attempt to load the data.
        if (uiState is SkinTemperatureViewModel.UiState.Uninitialized) {
            onPermissionsResult()
        }

        // The [SleepSessionViewModel.UiState] provides details of whether the last action was a
        // success or resulted in an error. Where an error occurred, for example in reading and
        // writing to Health Connect, the user is notified, and where the error is one that can be
        // recovered from, an attempt to do so is made.
        if (uiState is SkinTemperatureViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    if (uiState != SkinTemperatureViewModel.UiState.Uninitialized) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = { onPermissionsLaunch(permissions) }
                    ) {
                        Text(text = stringResource(R.string.permissions_button_label))
                    }
                }
            } else {
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(4.dp),
                        onClick = {
                            onInsertClick()
                        }
                    ) {
                        Text(stringResource(R.string.generate_skin_temperature_data))
                    }
                }

                items(recordList) { record ->
                    SkinTemperatureRecordRow(record)
                }
            }
        }
    }
}

@Preview
@Composable
fun SkinTemperaturePreview() {
    HealthConnectTheme {
        val endTime = ZonedDateTime.now()
        val startTime = endTime.minusHours(1)
        SkinTemperatureScreen(
            permissions = setOf(),
            permissionsGranted = true,
            recordList = listOf(
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
            ),
            uiState = SkinTemperatureViewModel.UiState.Done
        )
    }
}
