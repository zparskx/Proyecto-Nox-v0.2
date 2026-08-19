package com.nox.ai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nox.ai.ui.theme.CyanAccent
import com.nox.ai.ui.theme.EmeraldGlow
import com.nox.ai.ui.theme.GoogleBlue
import com.nox.ai.ui.theme.GoogleRed
import com.nox.ai.ui.theme.GoogleYellow

@Composable
fun GoogleAiNetworkBadge(
    modifier: Modifier = Modifier,
    isProcessing: Boolean = false
) {
    Surface(
        modifier = modifier
            .clip(CircleShape)
            .border(
                1.dp,
                if (isProcessing) CyanAccent.copy(alpha = 0.6f) else EmeraldGlow.copy(alpha = 0.4f),
                CircleShape
            ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isProcessing) CyanAccent else EmeraldGlow)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isProcessing) "Nox Procesando..." else "Nox Local Activa",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isProcessing) CyanAccent else EmeraldGlow
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = if (isProcessing) CyanAccent else EmeraldGlow
            )
        }
    }
}

@Composable
fun ApiKeyWarningBanner(
    errorMessage: String? = null,
    onDismissError: (() -> Unit)? = null
) {
    val isKeyMissing = false

    AnimatedVisibility(visible = isKeyMissing || errorMessage != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("api_key_warning_card"),
            colors = CardDefaults.cardColors(
                containerColor = GoogleRed.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoogleRed.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = GoogleRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isKeyMissing) "Modelo local no instalado" else "Error del modelo local",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = errorMessage ?: "Instala un modelo local compatible (.task) para activar el cerebro de Nox.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (onDismissError != null && errorMessage != null) {
                    IconButton(onClick = onDismissError) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Cerrar aviso",
                            tint = GoogleRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudioMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = accentColor
            )
        }
    }
}

@Composable
fun ParameterSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    unitText: String = "",
    description: String,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = String.format("%.2f %s", value, unitText),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = GoogleBlue
            )
        }
        Text(
            text = description,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = GoogleBlue,
                activeTrackColor = GoogleBlue,
                inactiveTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
fun EmptyStateCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector = Icons.Default.AutoAwesome
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = GoogleBlue.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getIconForName(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "code" -> Icons.Default.Code
        "school" -> Icons.Default.School
        "payments" -> Icons.Default.Payments
        "fitness_center" -> Icons.Default.FitnessCenter
        "headset_mic" -> Icons.Default.HeadsetMic
        "business" -> Icons.Default.Business
        else -> Icons.Default.Psychology
    }
}

fun getColorForHex(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        GoogleBlue
    }
}
