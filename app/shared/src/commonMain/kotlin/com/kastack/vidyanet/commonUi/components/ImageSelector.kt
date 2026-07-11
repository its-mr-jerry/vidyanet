package com.kastack.vidyanet.commonUi.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.kastack.vidyanet.theme.*

@Composable
fun ImageSelector(
    imageUrl: String,
    onClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
    label: String = "Select"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AdminBorder),
        colors = CardDefaults.cardColors(containerColor = AdminBackground)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (imageUrl.isBlank()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(24.dp), tint = Primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(label, fontSize = 10.sp, color = AdminTextSecondary)
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp).padding(4.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = HealthRed, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        "Selected",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp),
                        fontSize = 10.sp,
                        color = StatusActive,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
