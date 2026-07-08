package com.kastack.vidyanet.commonUi.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppButton
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.commonUi.viewModels.LoginUiState
import com.kastack.vidyanet.commonUi.viewModels.LoginViewModel
import com.kastack.vidyanet.core.AppConstants
import com.kastack.vidyanet.navigations.MainDestination
import com.kastack.vidyanet.utils.AdaptiveLayout
import com.kastack.vidyanet.utils.WindowSizeClass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onAuthenticated: (MainDestination) -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val otp by viewModel.otp.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onAuthenticated(MainDestination.SuperAdmin)
        }
    }

    AdaptiveLayout { windowSize ->
        val isCompact = windowSize == WindowSizeClass.COMPACT

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Background Image with Overlay
            Image(
                imageVector = Icons.Default.School, // Placeholder for campus background
                contentDescription = null,
                modifier = Modifier.fillMaxSize().blur(if (isCompact) 0.dp else 2.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.1f
            )

            Surface(
                modifier = Modifier
                    .fillMaxSize(if (isCompact) 1f else 0.9f)
                    .widthIn(max = 1200.dp)
                    .heightIn(max = 800.dp)
                    .padding(if (isCompact) 0.dp else 24.dp),
                shape = if (isCompact) RoundedCornerShape(0.dp) else RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Left Side: Branding (Hidden on mobile)
                    if (!isCompact) {
                        BrandingSection(modifier = Modifier.weight(1f))
                    }

                    // Right Side: Login Form
                    LoginFormSection(
                        modifier = Modifier.weight(if (isCompact) 1f else 1f),
                        viewModel = viewModel,
                        uiState = uiState,
                        isLoading = isLoading,
                        error = error,
                        phone = phone,
                        otp = otp,
                        isCompact = isCompact
                    )
                }
            }
        }
    }
}

@Composable
fun BrandingSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.primary)
            .padding(48.dp)
    ) {
        // Decorative Circles
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.1f)) {
            val center = Offset(size.width, 0f)
            drawCircle(Color.White, radius = 400.dp.toPx(), center = center)
            drawCircle(Color.White, radius = 300.dp.toPx(), center = center, style = androidx.compose.ui.graphics.drawscope.Stroke(2.dp.toPx()))
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.School, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    AppText(
                        text = AppConstants.BRANDING_NAME,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                AppText(
                    text = "Empowering Educational Excellence through Precision.",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 44.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                AppText(
                    text = "The all-in-one administrative hub for institutional growth, financial oversight, and student success tracking.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.VerifiedUser, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                AppText(
                    text = "ENTERPRISE GRADE SECURITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f),
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun LoginFormSection(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
    uiState: LoginUiState,
    isLoading: Boolean,
    error: String?,
    phone: String,
    otp: String,
    isCompact: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(if (isCompact) 24.dp else 64.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.widthIn(max = 400.dp)) {
            // Mobile Logo
            if (isCompact) {
                Row(
                    modifier = Modifier.padding(bottom = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    AppText(
                        text = AppConstants.BRANDING_NAME,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            AppText(
                text = "SUPER ADMIN PORTAL",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            AppText(
                text = "Welcome Back",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            AppText(
                text = if (uiState is LoginUiState.InputPhone) "Please enter your phone number to access the portal."
                       else "Please enter the 6-digit code sent to your phone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form Fields
            if (uiState is LoginUiState.InputPhone) {
                LoginInputField(
                    value = phone,
                    onValueChange = viewModel::onPhoneChanged,
                    label = "Phone Number",
                    placeholder = "Enter 10 digit number",
                    icon = Icons.Outlined.Mail,
                    keyboardType = KeyboardType.Phone,
                    enabled = !isLoading,
                    prefix = "+91 "
                )
            } else {
                LoginInputField(
                    value = otp,
                    onValueChange = viewModel::onOtpChanged,
                    label = "OTP Code",
                    placeholder = "123456",
                    icon = Icons.Outlined.Info,
                    keyboardType = KeyboardType.Phone,
                    enabled = !isLoading,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = {},
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                    )
                    AppText("Keep me logged in", style = MaterialTheme.typography.bodySmall)
                }
                if (uiState is LoginUiState.InputOtp){
                    TextButton(onClick = {viewModel.backToPhone()}) {
                        AppText("Change Number", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                onClick = { if (uiState is LoginUiState.InputPhone) viewModel.sendOtp() else viewModel.verifyOtp() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading && (if (uiState is LoginUiState.InputPhone) phone.length == 10 else otp.length == 6),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppText(if (uiState is LoginUiState.InputPhone) "Get OTP" else "Verify & Sign In", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (error != null) {
                AppText(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp), selectable = true)
            }

            // Support Box
            Spacer(modifier = Modifier.height(40.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    AppText(
                        text = "Having trouble signing in? Contact your system administrator or visit our Support Center.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Footer
            Spacer(modifier = Modifier.height(48.dp))
            HorizontalDivider(modifier = Modifier.fillMaxWidth().alpha(0.1f))
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText("Copyright © 2023 ${AppConstants.BRANDING_NAME}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppText("Privacy", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    AppText("Terms", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    prefix: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppText(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { AppText(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline) },
            leadingIcon = { Icon(icon, null, tint = MaterialTheme.colorScheme.outline) },
            trailingIcon = {
                if (isPassword && onPasswordToggle != null) {
                    IconButton(onClick = onPasswordToggle) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = MaterialTheme.colorScheme.outline)
                    }
                }
            },
            prefix = prefix?.let { { AppText(it) } },
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}
