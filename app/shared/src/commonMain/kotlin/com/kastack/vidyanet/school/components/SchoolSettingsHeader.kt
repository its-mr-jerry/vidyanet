package com.kastack.vidyanet.school.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kastack.vidyanet.commonUi.components.AppText

data class HeaderAction(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val color: Color? = null
)

@Composable
fun SchoolSettingsHeader(
    title: String,
    subtitle: String? = null,
    breadcrumbs: List<String> = emptyList(),
    primaryAction: HeaderAction? = null,
    secondaryAction: HeaderAction? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        BoxWithConstraints {
            val isMobile = maxWidth < 600.dp
            
            Column(
                modifier = Modifier.padding(
                    horizontal = if (isMobile) 16.dp else 24.dp,
                    vertical = if (isMobile) 12.dp else 20.dp
                )
            ) {
                if (breadcrumbs.isNotEmpty() && !isMobile) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        breadcrumbs.forEachIndexed { index, item ->
                            AppText(
                                text = item,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (index == breadcrumbs.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (index == breadcrumbs.lastIndex) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                            if (index < breadcrumbs.lastIndex) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        AppText(
                            text = title,
                            style = if (isMobile) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            softWrap = true
                        )
                        if (!isMobile && subtitle != null) {
                            AppText(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.padding(start = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(if (isMobile) 8.dp else 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        secondaryAction?.let { action ->
                            AdaptiveIconButton(
                                label = action.label,
                                icon = action.icon ?: Icons.Default.Circle, // Fallback if no icon provided
                                onClick = action.onClick,
                                isMobile = isMobile && action.icon != null,
                                enabled = action.enabled,
                                isLoading = action.isLoading
                            )
                        }

                        primaryAction?.let { action ->
                            AdaptiveIconButton(
                                label = action.label,
                                icon = action.icon ?: Icons.Default.Check,
                                onClick = action.onClick,
                                isMobile = isMobile && action.icon != null,
                                enabled = action.enabled,
                                isLoading = action.isLoading,
                                containerColor = action.color ?: MaterialTheme.colorScheme.primary,
                                contentColor = if (action.color != null) Color.White else MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
