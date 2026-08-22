package com.example.recipe_app_compose.shared

/** Temporary proof that both platform hosts consume the same common artifact. */
const val SHARED_MODULE_ID = "recipe-compose-shared"

/** Each target supplies this value from its own source set at compile time. */
expect val sharedPlatformName: String
