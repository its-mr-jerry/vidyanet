package com.kastack.vidyanet.commonUi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppPagination(
    totalItems: Int,
    currentPage: Int,
    rowsPerPage: Int,
    onPageChange: (Int) -> Unit,
    onRowsPerPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    rowsPerPageOptions: List<Int> = listOf(10, 20, 50, 100)
) {
    val totalPages = (totalItems + rowsPerPage - 1).coerceAtLeast(0) / rowsPerPage.coerceAtLeast(1)
    
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp)
        ) {
            val isCompact = maxWidth < 700.dp
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isCompact) Arrangement.Center else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isCompact) {
                    val start = ((currentPage - 1) * rowsPerPage) + 1
                    val end = (start + rowsPerPage - 1).coerceAtMost(totalItems)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppText(
                            text = "Showing ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            AppText(
                                text = " $start - $end ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        AppText(
                            text = " of $totalItems entries",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Navigation Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PaginationNavButton(
                            icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            enabled = currentPage > 1,
                            onClick = { onPageChange(currentPage - 1) }
                        )

                        if (!isCompact) {
                            PaginationNumbers(
                                currentPage = currentPage,
                                totalPages = totalPages,
                                onPageChange = onPageChange
                            )
                        } else {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    AppText(
                                        text = currentPage.toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    AppText(
                                        text = "/",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    AppText(
                                        text = totalPages.toString(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        PaginationNavButton(
                            icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            enabled = currentPage < totalPages,
                            onClick = { onPageChange(currentPage + 1) }
                        )
                    }

                    if (!isCompact) {
                        VerticalDivider(modifier = Modifier.height(24.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        
                        // Rows Per Page Selector
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            Surface(
                                onClick = { expanded = true },
                                color = MaterialTheme.colorScheme.surfaceContainerLow,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    AppText(
                                        text = "$rowsPerPage per page",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                offset = DpOffset(0.dp, 4.dp),
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                rowsPerPageOptions.forEach { size ->
                                    DropdownMenuItem(
                                        text = { 
                                            AppText(
                                                "$size per page", 
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (size == rowsPerPage) FontWeight.Bold else FontWeight.Normal
                                            ) 
                                        },
                                        onClick = {
                                            onRowsPerPageChange(size)
                                            expanded = false
                                        },
                                        trailingIcon = if (size == rowsPerPage) {
                                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary) }
                                        } else null,
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaginationNavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        color = if (enabled) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
        contentColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        modifier = Modifier.size(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun PaginationNumbers(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    val visiblePages = 5
    val startPage = when {
        totalPages <= visiblePages -> 1
        currentPage <= 3 -> 1
        currentPage >= totalPages - 2 -> (totalPages - 4).coerceAtLeast(1)
        else -> currentPage - 2
    }
    
    val endPage = (startPage + 4).coerceAtMost(totalPages)

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        if (startPage > 1) {
            PaginationNumberButton("1", active = currentPage == 1, onClick = { onPageChange(1) })
            if (startPage > 2) {
                AppText("...", modifier = Modifier.padding(horizontal = 4.dp), color = MaterialTheme.colorScheme.outline)
            }
        }

        for (i in startPage..endPage) {
            PaginationNumberButton(i.toString(), active = i == currentPage, onClick = { onPageChange(i) })
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                AppText("...", modifier = Modifier.padding(horizontal = 4.dp), color = MaterialTheme.colorScheme.outline)
            }
            PaginationNumberButton(totalPages.toString(), active = currentPage == totalPages, onClick = { onPageChange(totalPages) })
        }
    }
}

@Composable
private fun PaginationNumberButton(text: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable(enabled = !active, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AppText(
            text = text,
            color = if (active) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (active) FontWeight.Black else FontWeight.Bold,
            style = MaterialTheme.typography.labelLarge,
            fontSize = 13.sp
        )
    }
}
