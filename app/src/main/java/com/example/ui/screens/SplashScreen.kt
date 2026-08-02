package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PurpleSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }

    fun navigateNext() {
        if (!hasNavigated) {
            hasNavigated = true
            onTimeout()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimations")

    // Dynamic Floating Orbs & Rotation
    val orbOffset1 by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbOffset1"
    )

    val orbOffset2 by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbOffset2"
    )

    val auraRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AuraRotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoPulse"
    )

    val glowRingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowRingScale"
    )

    val tapHintAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TapHintAlpha"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "Alpha"
    )

    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "Scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(7000) // 7 seconds duration
        navigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Dark Slate
                        Color(0xFF1E1B4B), // Deep Indigo
                        Color(0xFF311042), // Rich Deep Purple
                        Color(0xFF0F172A)  // Dark Slate
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navigateNext()
            },
        contentAlignment = Alignment.Center
    ) {
        // Dynamic Glowing Background Particles & Orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Glowing Top-Right Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF6366F1).copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(size.width * 0.85f + orbOffset1, size.height * 0.2f + orbOffset2),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.85f + orbOffset1, size.height * 0.2f + orbOffset2),
                radius = size.width * 0.5f
            )

            // Glowing Bottom-Left Sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEC4899).copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(size.width * 0.15f - orbOffset2, size.height * 0.75f + orbOffset1),
                    radius = size.width * 0.55f
                ),
                center = Offset(size.width * 0.15f - orbOffset2, size.height * 0.75f + orbOffset1),
                radius = size.width * 0.55f
            )

            // Center Cyan Accent Glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF06B6D4).copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.45f),
                    radius = size.width * 0.45f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.45f),
                radius = size.width * 0.45f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .alpha(alphaAnim)
                .scale(scaleAnim)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Welcome Badge with Gradient Border & Glow
            Surface(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF3B82F6))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WELCOME TO STUDYMATE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Logo with Pulsing Outer Glow Ring & Rainbow Rotating Aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                // Outer Pulsing Glow Circle
                Box(
                    modifier = Modifier
                        .scale(glowRingScale)
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6).copy(alpha = 0.5f),
                                    Color(0xFF3B82F6).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rotating Rainbow Border Ring
                Box(
                    modifier = Modifier
                        .rotate(auraRotation)
                        .size(136.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFFEC4899),
                                    Color(0xFFF59E0B),
                                    Color(0xFF10B981),
                                    Color(0xFF06B6D4),
                                    Color(0xFF6366F1)
                                )
                            )
                        )
                )

                // StudyMate App Logo Tile
                Surface(
                    modifier = Modifier
                        .scale(pulseScale)
                        .size(126.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    color = Color.White,
                    shadowElevation = 20.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.studymate_app_icon_1785239825317),
                            contentDescription = "StudyMate Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name with Radiant Gradient Feel
            Text(
                text = "StudyMate",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Smart Student Planner & Study Companion",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Developer Credit Badge
            Surface(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .padding(bottom = 14.dp)
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // Glowing emerald online indicator
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Developed by Siddhant Hurule",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Tap anywhere prompt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .alpha(tapHintAlpha)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Tap anywhere to enter Home",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}


