package com.example.recipe_app_compose.app

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import com.example.recipe_app_compose.shared.generated.resources.Res
import com.example.recipe_app_compose.shared.generated.resources.explore
import com.example.recipe_app_compose.shared.generated.resources.nearby
import com.example.recipe_app_compose.shared.generated.resources.nav_favorite_outline
import com.example.recipe_app_compose.shared.generated.resources.nav_home
import com.example.recipe_app_compose.shared.generated.resources.nav_home_outline
import com.example.recipe_app_compose.shared.generated.resources.nav_search
import com.example.recipe_app_compose.shared.generated.resources.nav_storefront
import com.example.recipe_app_compose.shared.generated.resources.onboarding_save
import com.example.recipe_app_compose.shared.generated.resources.saved
import com.example.recipe_app_compose.shared.generated.resources.search_dishes
import org.jetbrains.compose.resources.stringResource

internal enum class SharedDestination { Explore, Search, Nearby, Saved }
internal enum class SharedDrawerDestination { Home, Info }

@Composable
internal fun SharedBottomNavigation(
    selected: SharedDestination,
    onSelected: (SharedDestination) -> Unit,
) {
    NavigationBar {
        SharedDestination.entries.forEach { destination ->
            val label = when (destination) {
                SharedDestination.Explore -> stringResource(Res.string.explore)
                SharedDestination.Search -> stringResource(Res.string.search_dishes)
                SharedDestination.Nearby -> stringResource(Res.string.nearby)
                SharedDestination.Saved -> stringResource(Res.string.saved)
            }
            val isSelected = selected == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelected(destination) },
                icon = {
                    Icon(
                        painter = painterResource(destination.icon(isSelected)),
                        contentDescription = label,
                    )
                },
                label = { Text(label) },
            )
        }
    }
}

private fun SharedDestination.icon(selected: Boolean): DrawableResource = when (this) {
    SharedDestination.Explore -> if (selected) {
        Res.drawable.nav_home
    } else {
        Res.drawable.nav_home_outline
    }
    SharedDestination.Search -> Res.drawable.nav_search
    SharedDestination.Nearby -> Res.drawable.nav_storefront
    SharedDestination.Saved -> if (selected) {
        Res.drawable.onboarding_save
    } else {
        Res.drawable.nav_favorite_outline
    }
}
