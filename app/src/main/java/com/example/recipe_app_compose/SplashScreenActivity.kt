package com.example.recipe_app_compose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.recipe_app_compose.ui.theme.Recipe_App_ComposeTheme

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Recipe_App_ComposeTheme {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val alpha = remember {
        Animatable(0F)
    }

    // Coroutine Launcher that initiates when composables are composed
    LaunchedEffect(key1 = true, block = {
        alpha.animateTo(1F, animationSpec = tween(1000))
        context.startActivity(Intent(context, LoginActivity::class.java))
    })
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.alpha(alpha = alpha.value),
            painter = painterResource(R.drawable.dining_two),
            contentDescription = stringResource(R.string.image)
        )
    }
}