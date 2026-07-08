package com.kastack.vidyanet.commonUi.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.theme.*

@Composable
fun AppPagination(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPages: Int,
    pageSize: Int,
    onPageChange: (Int) -> Unit,
    onPageSizeChange: (Int) -> Unit,
    pageSizeOptions: List<Int> = listOf(5, 10, 20, 50)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rows per page selector
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Rows per page:", fontSize = 12.sp, color = AdminTextSecondary)
            var showSizeMenu by remember { mutableStateOf(false) }
            Box {
                TextButton(onClick = { showSizeMenu = true }) {
                    Text(pageSize.toString(), fontSize = 12.sp, color = AdminTextPrimary)
                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp))
                }
                
                if (showSizeMenu) {
                    Box(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .background(Background, RoundedCornerShape(8.dp))
                            .border(1.dp, AdminBorder, RoundedCornerShape(8.dp))
                            .width(60.dp)
                    ) {
                        Column {
                            pageSizeOptions.forEach { size ->
                                Text(
                                    text = size.toString(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onPageSizeChange(size)
                                            showSizeMenu = false
                                        }
                                        .padding(8.dp),
                                    fontSize = 12.sp,
                                    color = AdminTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Page $currentPage of $totalPages",
                fontSize = 12.sp,
                color = AdminTextSecondary,
                modifier = Modifier.padding(end = 16.dp)
            )

            IconButton(
                onClick = { onPageChange(currentPage - 1) },
                enabled = currentPage > 1,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    null,
                    tint = if (currentPage > 1) AdminTextPrimary else AdminTextSecondary
                )
            }

            // Page Numbers
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                val pagesToShow = remember(currentPage, totalPages) {
                    val start = (currentPage - 2).coerceAtLeast(1)
                    val end = (start + 4).coerceAtMost(totalPages)
                    val adjustedStart = (end - 4).coerceAtLeast(1)
                    (adjustedStart..end).toList()
                }

                pagesToShow.forEach { page ->
                    val isSelected = page == currentPage
                    Surface(
                        onClick = { onPageChange(page) },
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) Primary else Color.Transparent,
                        border = if (isSelected) null else BorderStroke(1.dp, AdminBorder),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = page.toString(),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AdminTextPrimary
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = { onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    null,
                    tint = if (currentPage < totalPages) AdminTextPrimary else AdminTextSecondary
                )
            }
        }
    }
}
