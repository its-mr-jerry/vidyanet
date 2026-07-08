package com.kastack.vidyanet.commonUi.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kastack.vidyanet.theme.AuditGreen

enum class AppDialogType {
    INFO, LOADING, ERROR, SUCCESS
}

data class AppDialogState(
    val isVisible: Boolean = false,
    val type: AppDialogType = AppDialogType.INFO,
    val title: String = "",
    val message: String = "",
    val confirmLabel: String? = null,
    val dismissLabel: String? = null,
    val onConfirm: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null
)

@Composable
fun AppDialog(
    state: AppDialogState,
    onDismissRequest: () -> Unit = { state.onDismiss?.invoke() }
) {
    if (!state.isVisible) return

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = state.type != AppDialogType.LOADING,
            dismissOnClickOutside = state.type != AppDialogType.LOADING
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon / Progress Section
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = when (state.type) {
                                AppDialogType.INFO -> Color(0xFFE3F2FD)
                                AppDialogType.SUCCESS -> Color(0xFFE8F5E9)
                                AppDialogType.ERROR -> Color(0xFFFFEBEE)
                                AppDialogType.LOADING -> Color.Transparent
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (state.type) {
                        AppDialogType.LOADING -> {
                            CircularProgressIndicator(
                                color = AuditGreen,
                                strokeWidth = 3.dp
                            )
                        }
                        AppDialogType.SUCCESS -> {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF43A047),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        AppDialogType.ERROR -> {
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        AppDialogType.INFO -> {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF1E88E5),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                if (state.title.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                if (state.message.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }

                if (state.type != AppDialogType.LOADING) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (state.onDismiss != null) {
                            TextButton(
                                onClick = { 
                                    state.onDismiss.invoke()
                                    onDismissRequest()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(state.dismissLabel ?: "Cancel")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        
                        Button(
                            onClick = { 
                                state.onConfirm?.invoke()
                                onDismissRequest()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when(state.type) {
                                    AppDialogType.ERROR -> Color(0xFFE53935)
                                    AppDialogType.SUCCESS -> AuditGreen
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        ) {
                            Text(
                                state.confirmLabel ?: when (state.type) {
                                    AppDialogType.ERROR -> "Close"
                                    AppDialogType.LOADING -> "Wait"
                                    else -> "OK"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
