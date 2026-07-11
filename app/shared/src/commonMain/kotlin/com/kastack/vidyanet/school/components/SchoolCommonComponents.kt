package com.kastack.vidyanet.school.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kastack.vidyanet.commonUi.components.AppText

@Composable
fun AdaptiveIconButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isMobile: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    contentColor: Color? = null,
    border: BorderStroke? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    if (isMobile) {
        if (containerColor != null) {
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor ?: contentColorFor(containerColor)
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = modifier.size(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = contentColor ?: LocalContentColor.current)
                } else {
                    Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
                }
            }
        } else {
            IconButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                modifier = modifier.border(
                    border ?: BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    RoundedCornerShape(8.dp)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(icon, contentDescription = label, tint = contentColor ?: MaterialTheme.colorScheme.primary)
                }
            }
        }
    } else {
        if (containerColor != null) {
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = contentColor ?: contentColorFor(containerColor)
                ),
                modifier = modifier
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = contentColor ?: LocalContentColor.current)
                    Spacer(Modifier.width(8.dp))
                } else {
                    Icon(icon, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                AppText(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        } else {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                shape = RoundedCornerShape(8.dp),
                border = border ?: BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = modifier
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                } else {
                    Icon(icon, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                }
                AppText(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = CircleShape,
        modifier = modifier
    ) {
        AppText(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}
