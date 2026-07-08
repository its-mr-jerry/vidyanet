package com.kastack.vidyanet.commonUi.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Domain
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.commonUi.viewModels.SplashViewModel
import com.kastack.vidyanet.core.AppConstants
import com.kastack.vidyanet.navigations.MainDestination
import com.kastack.vidyanet.utils.AdaptiveLayout
import com.kastack.vidyanet.utils.WindowSizeClass
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(
    onNavigate: (MainDestination) -> Unit,
    onLoginRequired: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isSuperAdmin by viewModel.isSuperAdmin.collectAsState()

    LaunchedEffect(isLoggedIn, isSuperAdmin) {
        if (isLoggedIn != null) {
            delay(3.seconds) // Match the 3s loading animation
            when (isLoggedIn) {
                true -> {
                    if (isSuperAdmin) {
                        onNavigate(MainDestination.SuperAdmin)
                    } else {
                        onLoginRequired()
                    }
                }
                false -> onLoginRequired()
                else -> {}
            }
        }
    }

    AdaptiveLayout { windowSize ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            // 1. Background Patterns
            BackgroundPatterns()

            // 2. Main Content
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Centralized main content area
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SplashMainContent(windowSize)
                }

                // Footer Section at the bottom
                SplashFooter(windowSize)
            }
        }
    }
}

@Composable
fun BackgroundPatterns() {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Simple particle animation logic
    val particles = remember {
        List(40) {
            ParticleData(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 2f + 1f,
                alpha = Random.nextFloat() * 0.3f + 0.1f,
                speedX = (Random.nextFloat() - 0.5f) * 0.001f,
                speedY = (Random.nextFloat() - 0.5f) * 0.001f
            )
        }
    }

    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val patternColor = MaterialTheme.colorScheme.onPrimary
    val topGradientColor = MaterialTheme.colorScheme.primaryContainer

    Canvas(modifier = Modifier.fillMaxSize()) {
        // 1. Line Pattern (recreating the CSS multi-gradient line-pattern)
        val color = patternColor.copy(alpha = 0.08f)
        val strokeWidth = 1.2.dp.toPx()
        
        val stepX = 60.dp.toPx()
        val stepY = 100.dp.toPx()
        
        for (x in -stepX.toInt()..size.width.toInt() step stepX.toInt()) {
            for (y in -stepY.toInt()..size.height.toInt() step stepY.toInt()) {
                // 30 degree lines
                drawLine(
                    color = color,
                    start = Offset(x.toFloat(), y.toFloat()),
                    end = Offset(x + stepX, y + stepY * 0.5f),
                    strokeWidth = strokeWidth
                )
                // 150 degree lines
                drawLine(
                    color = color,
                    start = Offset(x + stepX, y.toFloat()),
                    end = Offset(x.toFloat(), y + stepY * 0.5f),
                    strokeWidth = strokeWidth
                )
            }
        }

        // 2. Radial Dots Pattern (bg-pattern)
        val dotSpacing = 32.dp.toPx()
        for (x in 0..size.width.toInt() step dotSpacing.toInt()) {
            for (y in 0..size.height.toInt() step dotSpacing.toInt()) {
                drawCircle(
                    color = patternColor.copy(alpha = 0.12f),
                    radius = 1.2.dp.toPx(),
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }

        // 3. Particles
        particles.forEach { p ->
            val currentX = (p.x + p.speedX * animProgress * 1000) % 1f
            val currentY = (p.y + p.speedY * animProgress * 1000) % 1f
            drawCircle(
                color = patternColor.copy(alpha = p.alpha),
                radius = p.size,
                center = Offset(currentX * size.width, currentY * size.height)
            )
        }

        // 4. Top Gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(topGradientColor.copy(alpha = 0.2f), Color.Transparent),
                startY = 0f,
                endY = size.height / 3
            )
        )
    }
}

@Composable
fun SplashMainContent(windowSize: WindowSizeClass) {
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(10f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(1f, tween(1200, easing = EaseOut))
        translateY.animateTo(0f, tween(1200, easing = EaseOut))
    }

    val isCompact = windowSize == WindowSizeClass.COMPACT
    val titleSize = if (isCompact) 28.sp else 36.sp
    val subtitleSize = if (isCompact) 13.sp else 16.sp
    val iconBoxSize = if (isCompact) 80.dp else 96.dp
    val iconSize = if (isCompact) 40.dp else 48.dp

    Column(
        modifier = Modifier
            .alpha(alpha.value)
            .offset(y = translateY.value.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Box
        Surface(
            modifier = Modifier.size(iconBoxSize),
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 24.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(iconSize),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AppText(
            text = AppConstants.BRANDING_NAME,
            style = MaterialTheme.typography.displayLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = titleSize,
                letterSpacing = (-0.02).sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppText(
            text = "Empowering Educational Excellence",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                letterSpacing = 2.sp,
                fontSize = subtitleSize,
                fontWeight = FontWeight.Medium
            )
        )
        
        if (!isCompact) {
            Spacer(modifier = Modifier.height(48.dp))
            AbstractNodesVisual()
        }
    }
}

@Composable
fun AbstractNodesVisual() {
    Canvas(modifier = Modifier.size(width = 240.dp, height = 120.dp).alpha(0.2f)) {
        val p1 = Offset(20.dp.toPx(), 60.dp.toPx())
        val p2 = Offset(120.dp.toPx(), 20.dp.toPx())
        val p3 = Offset(120.dp.toPx(), 100.dp.toPx())
        val p4 = Offset(220.dp.toPx(), 60.dp.toPx())

        val stroke = Stroke(
            width = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()), 0f)
        )

        drawLine(Color.White, p1, p2, strokeWidth = stroke.width, pathEffect = stroke.pathEffect)
        drawLine(Color.White, p1, p3, strokeWidth = stroke.width, pathEffect = stroke.pathEffect)
        drawLine(Color.White, p2, p4, strokeWidth = stroke.width, pathEffect = stroke.pathEffect)
        drawLine(Color.White, p3, p4, strokeWidth = stroke.width, pathEffect = stroke.pathEffect)

        drawCircle(Color.White, 4.dp.toPx(), p1)
        drawCircle(Color.White, 4.dp.toPx(), p2)
        drawCircle(Color.White, 4.dp.toPx(), p3)
        drawCircle(Color.White, 4.dp.toPx(), p4)
    }
}


@Composable
fun SplashFooter(windowSize: WindowSizeClass) {
    val isCompact = windowSize == WindowSizeClass.COMPACT
    
    Column(
        modifier = Modifier
            .widthIn(max = 1440.dp)
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = if (isCompact) 40.dp else 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Loading Bar
        val progress = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(3000, easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f))
            )
        }

        val loadingBarWidth = if (isCompact) 0.8f else 0.4f

        Box(
            modifier = Modifier
                .fillMaxWidth(loadingBarWidth)
                .height(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress.value)
                    .background(MaterialTheme.colorScheme.onPrimary)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().alpha(0.1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onPrimary
        )

        val footerContentModifier = Modifier
            .fillMaxWidth()
            .alpha(0.6f)
            .padding(top = 12.dp)

        if (isCompact) {
            Column(
                modifier = footerContentModifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FooterItem(Icons.Outlined.Domain, "Institutional Management Suite")
                FooterItem(Icons.Outlined.Security, "Enterprise Grade Security")
            }
        } else {
            Row(
                modifier = footerContentModifier,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FooterItem(Icons.Outlined.Domain, "Institutional Management Suite")
                FooterItem(Icons.Outlined.Security, "Enterprise Grade Security")
            }
        }
    }
}

@Composable
fun FooterItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.width(12.dp))
        AppText(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 12.sp,
                letterSpacing = 0.05.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

data class ParticleData(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float,
    val speedX: Float,
    val speedY: Float
)
