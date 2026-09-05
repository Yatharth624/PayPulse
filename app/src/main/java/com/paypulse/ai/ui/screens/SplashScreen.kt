package com.paypulse.ai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }
    var statusText by remember { mutableStateOf("INITIALIZING SECURE SESSION...") }

    // Ambient Pulse & Glow Drift Animations
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_ambient")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "animated_progress"
    )

    // Splash Loader Timeline (2.4s total)
    LaunchedEffect(Unit) {
        progress = 0.20f
        delay(400)
        progress = 0.55f
        statusText = "CONNECTING REVENUE ENGINE..."
        delay(600)
        progress = 0.85f
        statusText = "SYNCHRONIZING AI AGENTS..."
        delay(700)
        progress = 1.0f
        statusText = "SYSTEM READY"
        delay(350)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06080F)), // Obsidian Cyber Dark
        contentAlignment = Alignment.Center
    ) {
        // ─── 1. CYBERNETIC WAVEFORM AURA BACKGROUND ────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.minDimension * 0.38f

            // Outer Glowing Cyber Rings
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00D2FF).copy(alpha = glowAlpha * 0.18f),
                        Color(0xFF0284C7).copy(alpha = glowAlpha * 0.08f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = baseRadius * 1.4f
                ),
                center = centerOffset,
                radius = baseRadius * 1.4f
            )

            // Dynamic Concentric Oscillating Rings
            drawCircle(
                color = Color(0xFF22D3EE).copy(alpha = glowAlpha * 0.25f),
                center = centerOffset,
                radius = baseRadius * pulseScale,
                style = Stroke(width = 1.5.dp.toPx())
            )

            drawCircle(
                color = Color(0xFF0284C7).copy(alpha = 0.20f),
                center = centerOffset,
                radius = baseRadius * 0.78f,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Center Ambient Glow Behind Logo
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(pulseScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF06B6D4).copy(alpha = 0.22f),
                            Color(0xFF0284C7).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 36.dp, horizontal = 24.dp)
        ) {
            // Top Status Bar Spacer
            Spacer(modifier = Modifier.height(24.dp))

            // ─── 2. HERO BRAND EMBLEM & TYPOGRAPHY ─────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                // Glass-Pill Logo Badge Container
                Surface(
                    color = Color(0xFF0C1830).copy(alpha = 0.75f),
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .size(88.dp)
                        .scale(pulseScale)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = 0.45f),
                                    Color(0xFF00D2FF).copy(alpha = 0.20f),
                                    Color(0xFF0284C7).copy(alpha = 0.10f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Stylized 'P' Intersected with Electric Heartbeat Waveform Vector
                        Canvas(modifier = Modifier.size(52.dp)) {
                            val width = size.width
                            val height = size.height

                            // 1. Geometric 'P' Stem & Loop
                            val pPath = Path().apply {
                                moveTo(width * 0.25f, height * 0.15f)
                                lineTo(width * 0.58f, height * 0.15f)
                                cubicTo(
                                    width * 0.78f, height * 0.15f,
                                    width * 0.78f, height * 0.50f,
                                    width * 0.58f, height * 0.50f
                                )
                                lineTo(width * 0.40f, height * 0.50f)
                                lineTo(width * 0.40f, height * 0.85f)
                                lineTo(width * 0.25f, height * 0.85f)
                                close()
                            }

                            drawPath(
                                path = pPath,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFFFFF),
                                        Color(0xFF38BDF8),
                                        Color(0xFF00D2FF)
                                    )
                                ),
                                alpha = 0.25f
                            )

                            drawPath(
                                path = pPath,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF0284C7)
                                    )
                                ),
                                style = Stroke(width = 2.dp.toPx(), join = StrokeJoin.Round)
                            )

                            // Inner P loop fill
                            val pInnerLoop = Path().apply {
                                moveTo(width * 0.40f, height * 0.28f)
                                lineTo(width * 0.54f, height * 0.28f)
                                cubicTo(
                                    width * 0.65f, height * 0.28f,
                                    width * 0.65f, height * 0.40f,
                                    width * 0.54f, height * 0.40f
                                )
                                lineTo(width * 0.40f, height * 0.40f)
                                close()
                            }

                            drawPath(
                                path = pInnerLoop,
                                color = Color(0xFF22D3EE).copy(alpha = 0.85f)
                            )

                            // 2. Vibrant Electric Pulse Waveform traversing through the emblem
                            val wavePath = Path().apply {
                                moveTo(width * 0.05f, height * 0.58f)
                                lineTo(width * 0.22f, height * 0.58f)
                                lineTo(width * 0.32f, height * 0.42f)
                                lineTo(width * 0.42f, height * 0.72f)
                                lineTo(width * 0.52f, height * 0.30f)
                                lineTo(width * 0.62f, height * 0.64f)
                                lineTo(width * 0.72f, height * 0.52f)
                                lineTo(width * 0.80f, height * 0.58f)
                                lineTo(width * 0.95f, height * 0.58f)
                            }

                            // Glow effect under stroke
                            drawPath(
                                path = wavePath,
                                color = Color(0xFF00D2FF).copy(alpha = 0.5f),
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )

                            // Neon Cyan Sharp Line
                            drawPath(
                                path = wavePath,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0284C7),
                                        Color(0xFF22D3EE),
                                        Color(0xFFFFFFFF),
                                        Color(0xFF38BDF8)
                                    )
                                ),
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Brand Name Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "PayPulse",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF22D3EE),
                        letterSpacing = (-0.5).sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Monospace Cyber Tagline
                Text(
                    text = "AUTONOMOUS REVENUE RECOVERY",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF67E8F9).copy(alpha = 0.9f),
                    letterSpacing = 3.5.sp
                )
            }

            // ─── 3. MINIMAL ANIMATED LOADING SECTION ───────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                // Progress Bar Container with Outer Glow Border
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(
                            width = 0.8.dp,
                            color = Color(0xFF06B6D4).copy(alpha = 0.25f),
                            shape = RoundedCornerShape(10.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF0284C7),
                                        Color(0xFF00D2FF),
                                        Color(0xFF67E8F9),
                                        Color(0xFF00D2FF)
                                    )
                                )
                            )
                    )
                }

                // Monospace Telemetry Status Text
                Text(
                    text = statusText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF22D3EE).copy(alpha = 0.75f),
                    letterSpacing = 2.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Minimal iOS Home Indicator Bar
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF64748B).copy(alpha = 0.4f))
                )
            }
        }
    }
}
