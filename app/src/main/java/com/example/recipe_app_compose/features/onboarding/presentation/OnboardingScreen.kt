package com.example.recipe_app_compose.features.onboarding.presentation

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.ui.theme.AppControlShape
import com.example.recipe_app_compose.ui.theme.AppSizes
import com.example.recipe_app_compose.ui.theme.AppSpacing
import kotlinx.coroutines.launch

private data class OnboardingPage(
    @param:StringRes val eyebrow: Int,
    @param:StringRes val title: Int,
    @param:StringRes val description: Int,
    val icon: ImageVector,
    val colorRole: OnboardingColorRole,
)

private enum class OnboardingColorRole {
    Primary,
    Secondary,
    Tertiary,
}

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = listOf(
        OnboardingPage(
            eyebrow = R.string.onboarding_discover_label,
            title = R.string.onboarding_discover_title,
            description = R.string.onboarding_discover_description,
            icon = Icons.Default.RestaurantMenu,
            colorRole = OnboardingColorRole.Primary,
        ),
        OnboardingPage(
            eyebrow = R.string.onboarding_save_label,
            title = R.string.onboarding_save_title,
            description = R.string.onboarding_save_description,
            icon = Icons.Default.Favorite,
            colorRole = OnboardingColorRole.Secondary,
        ),
        OnboardingPage(
            eyebrow = R.string.onboarding_nearby_label,
            title = R.string.onboarding_nearby_title,
            description = R.string.onboarding_nearby_description,
            icon = Icons.Default.LocationOn,
            colorRole = OnboardingColorRole.Tertiary,
        ),
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.lastIndex

    BackHandler(enabled = pagerState.currentPage > 0) {
        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = AppSpacing.Medium),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!isLastPage) {
                    TextButton(onClick = onFinished) {
                        Text(stringResource(R.string.skip))
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = AppSpacing.Large),
                pageSpacing = AppSpacing.Large,
                verticalAlignment = Alignment.Top,
            ) { pageIndex ->
                OnboardingPageContent(
                    page = pages[pageIndex],
                    pageNumber = pageIndex + 1,
                    pageCount = pages.size,
                )
            }

            Column(
                modifier = Modifier
                    .widthIn(max = AppSizes.MaximumReadableWidth)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        start = AppSpacing.ExtraLarge,
                        end = AppSpacing.ExtraLarge,
                        top = AppSpacing.Medium,
                        bottom = AppSpacing.Large,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppSpacing.Large),
            ) {
                PageIndicator(
                    pageCount = pages.size,
                    selectedPage = pagerState.currentPage,
                )
                Button(
                    onClick = {
                        if (isLastPage) {
                            onFinished()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    shape = AppControlShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AppSizes.MinimumTouchTarget),
                ) {
                    Text(
                        stringResource(
                            if (isLastPage) R.string.get_started else R.string.next
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageNumber: Int,
    pageCount: Int,
) {
    val listState = rememberLazyListState()
    val pageDescription = stringResource(
        R.string.onboarding_page_description,
        pageNumber,
        pageCount,
    )

    androidx.compose.foundation.lazy.LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = AppSizes.MaximumReadableWidth)
            .semantics { stateDescription = pageDescription },
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = AppSpacing.Large),
    ) {
        item {
            OnboardingArtwork(
                icon = page.icon,
                colorRole = page.colorRole,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.12f),
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.ExtraLarge),
            ) {
                Text(
                    text = stringResource(page.eyebrow).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(AppSpacing.Small))
                Text(
                    text = stringResource(page.title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(AppSpacing.Medium))
                Text(
                    text = stringResource(page.description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun OnboardingArtwork(
    icon: ImageVector,
    colorRole: OnboardingColorRole,
    modifier: Modifier = Modifier,
) {
    val containerColor = when (colorRole) {
        OnboardingColorRole.Primary -> MaterialTheme.colorScheme.primaryContainer
        OnboardingColorRole.Secondary -> MaterialTheme.colorScheme.secondaryContainer
        OnboardingColorRole.Tertiary -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = when (colorRole) {
        OnboardingColorRole.Primary -> MaterialTheme.colorScheme.onPrimaryContainer
        OnboardingColorRole.Secondary -> MaterialTheme.colorScheme.onSecondaryContainer
        OnboardingColorRole.Tertiary -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .background(containerColor),
    ) {
        ArtworkCircle(
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.36f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 30.dp, end = 22.dp)
                .size(82.dp),
        )
        ArtworkCircle(
            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.46f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 30.dp, bottom = 34.dp)
                .size(112.dp),
        )
        ArtworkCircle(
            color = contentColor.copy(alpha = 0.13f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(236.dp),
        )
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(144.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
            contentColor = contentColor,
            shadowElevation = 4.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(68.dp),
                )
            }
        }
    }
}

@Composable
private fun ArtworkCircle(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun PageIndicator(pageCount: Int, selectedPage: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == selectedPage
            val width by animateDpAsState(
                targetValue = if (selected) 24.dp else 8.dp,
                label = "Onboarding indicator width",
            )
            val color by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                },
                label = "Onboarding indicator color",
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}
