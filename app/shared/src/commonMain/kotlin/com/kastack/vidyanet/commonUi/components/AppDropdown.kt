package com.kastack.vidyanet.commonUi.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.theme.*

@Composable
fun <T> AppDropdown(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    allLabel: String? = null,
    onClear: (() -> Unit)? = null,
    maxHeight: Dp = 250.dp
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.height(52.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, if (selectedItem != null) Primary else AdminBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (selectedItem != null) Primary else AdminTextPrimary
            )
        ) {
            Text(
                text = if (selectedItem != null) itemLabel(selectedItem) else label,
                fontSize = 14.sp,
                maxLines = 1
            )
            Icon(
                if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 180.dp)
                .heightIn(max = maxHeight)
                .background(Background),
            offset = DpOffset(0.dp, 4.dp)
        ) {
            if (allLabel != null && onClear != null) {
                DropdownMenuItem(
                    text = { Text(allLabel, fontSize = 14.sp) },
                    onClick = {
                        onClear()
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (selectedItem == null) Primary else AdminTextPrimary
                    )
                )
                HorizontalDivider(color = AdminDivider)
            }

            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item), fontSize = 14.sp) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (item == selectedItem) Primary else AdminTextPrimary
                    )
                )
                HorizontalDivider(color = AdminDivider)
            }
        }
    }
}
