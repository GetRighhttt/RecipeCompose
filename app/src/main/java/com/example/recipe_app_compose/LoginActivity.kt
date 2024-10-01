package com.example.recipe_app_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.recipe_app_compose.features.categories.presentation.view.LoginForm
import com.example.recipe_app_compose.ui.theme.MyLoginApplicationTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            MyLoginApplicationTheme {
                LoginForm()
            }
        }
    }
}