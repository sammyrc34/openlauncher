package com.openlauncher.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.openlauncher.app.ui.theme.colorPresetLabels
import com.openlauncher.app.ui.theme.colorPresets

@Composable
fun ColorPickerDialog(
    title: String,
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    // Seed HSV state from the initial color synchronously — initializing in a
    // LaunchedEffect made the sliders flash wrong positions on the first frame
    val initialHsv = remember(initialColor) {
        FloatArray(3).also { android.graphics.Color.colorToHSV(initialColor.toArgb(), it) }
    }
    val initialAlpha = remember { android.graphics.Color.alpha(initialColor.toArgb()) }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var hue   by remember { mutableStateOf(initialHsv[0]) }
    var sat   by remember { mutableStateOf(initialHsv[1]) }
    var value by remember { mutableStateOf(initialHsv[2]) }
    var alpha by remember { mutableStateOf(initialAlpha) }

    fun rebuildColor() {
        selectedColor = Color(android.graphics.Color.HSVToColor( alpha, floatArrayOf(hue, sat, value)))
    }

    fun syncFrom(color: Color) {
        selectedColor = color
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hue = hsv[0]; sat = hsv[1]; value = hsv[2]
        alpha = android.graphics.Color.alpha(color.toArgb())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        // Always-dark surface: pin light content colors so day mode stays legible
        containerColor    = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor  = Color(0xFFCCCCCC),
        title = { Text(title) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Preset swatches
                Text("Presets", style = MaterialTheme.typography.labelMedium, color = Color(0xFF888888))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorPresets.forEachIndexed { i, color ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = 1.dp,
                                        color = if (selectedColor == color) Color.White else Color(0xFF333333),
                                        shape = CircleShape
                                    )
                                    .clickable { syncFrom(color) }
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(colorPresetLabels[i], style = MaterialTheme.typography.labelSmall, color = Color(0xFF555555), fontSize = androidx.compose.ui.unit.TextUnit(8f, androidx.compose.ui.unit.TextUnitType.Sp))
                        }
                    }
                }

                Divider(color = Color(0xFF2A2A2A))

                // Hue slider
                Text("Hue", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
                Slider(
                    value = hue / 360f,
                    onValueChange = { hue = it * 360f; rebuildColor() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(
                            colors = (0..6).map { i ->
                                Color(android.graphics.Color.HSVToColor(floatArrayOf(i * 60f, 1f, 1f)))
                            }
                        )),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                // Saturation slider
                Text("Saturation", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
                Slider(
                    value = sat,
                    onValueChange = { sat = it; rebuildColor() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(
                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 0f, value))),
                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, value)))
                        ))),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                // Brightness slider
                Text("Brightness", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
                Slider(
                    value = value,
                    onValueChange = { value = it; rebuildColor() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Brush.horizontalGradient(listOf(
                            Color.Black,
                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, 1f)))
                        ))),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )

                // Transparency
                Text("Transparency", style = MaterialTheme.typography.labelSmall, color = Color(0xFF666666))
                Slider(
                    value = alpha.toFloat(),
                    onValueChange = { alpha = it.toInt(); rebuildColor() },
                    valueRange = 0f..255f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .drawBehind {
                            drawRect( Color.White )
                            drawRect( Color.Black, topLeft = Offset( 0f, 24.dp.toPx() ) )
                        }
                        .background(Brush.horizontalGradient(listOf(
                            Color.Transparent,
                            Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, sat, 1f)))
                        ))),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            // Filled with the chosen color + auto-contrast label, so Apply stays
            // visible no matter how dark or light the selection is
            Button(
                onClick = { onColorSelected(selectedColor); onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor,
                    contentColor   = if (selectedColor.luminance() > 0.5f) Color.Black else Color.White
                )
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFFAAAAAA)) }
        }
    )
}
