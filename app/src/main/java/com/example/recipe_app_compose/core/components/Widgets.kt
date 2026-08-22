package com.example.recipe_app_compose.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recipe_app_compose.R
import com.example.recipe_app_compose.ui.theme.AppCardShape
import com.example.recipe_app_compose.ui.theme.AppSpacing

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = stringResource(R.string.confirm),
    dismissLabel: String = stringResource(R.string.dismiss),
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = message) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissLabel)
            }
        },
    )
}

@Composable
fun AppMediaCard(
    painter: Painter,
    imageDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageAspectRatio: Float = 1f,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = AppCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Image(
            painter = painter,
            contentDescription = imageDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(imageAspectRatio),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.Medium),
            content = content,
        )
    }
}

@Composable
fun AppHorizontalMediaCard(
    painter: Painter,
    imageDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 112.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = AppCardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painter,
                contentDescription = imageDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(imageSize),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(AppSpacing.Medium),
                content = content,
            )
        }
    }
}

@Composable
fun ExternalLinkText(
    text: String,
    url: String,
    modifier: Modifier = Modifier,
    linkTextColor: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    val uriHandler = LocalUriHandler.current
    val annotatedString = buildAnnotatedString {
        val link = LinkAnnotation.Url(
            url = url,
            styles = TextLinkStyles(SpanStyle(color = linkTextColor)),
        ) { annotation ->
            uriHandler.openUri((annotation as LinkAnnotation.Url).url)
        }
        withLink(link) { append(text) }
    }
    Text(text = annotatedString, modifier = modifier, style = style)
}
