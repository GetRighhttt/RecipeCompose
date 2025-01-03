package com.example.recipe_app_compose.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.recipe_app_compose.features.categories.presentation.view.RandomMealPage

@Composable
fun FullScreenDialog(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            RandomMealPage(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun ReusableFullScreenDialog(content: @Composable () -> Unit, onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        Surface(
            modifier = Modifier
                .height(800.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            content()
        }
    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun MinimalDialog(text: String, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun DialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    painter: Painter,
    imageDescription: String,
    text: String,
    modifier: Modifier
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painter,
                    contentDescription = imageDescription,
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .height(300.dp)
                )
                Text(
                    text = text, fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = modifier.padding(5.dp),
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ElevatedButton(
                        onClick = { onDismissRequest() },
                        modifier = modifier.padding(5.dp),
                        elevation = ButtonDefaults.buttonElevation(15.dp)
                    ) {
                        Text("Dismiss")
                    }
                    ElevatedButton(
                        onClick = { onConfirmation() },
                        modifier = modifier.padding(5.dp),
                        elevation = ButtonDefaults.buttonElevation(15.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}

@Composable
fun DatabaseDialogWithImage(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    painter: Painter,
    imageDescription: String,
    text: String,
    source: List<String>,
    youtube: List<String>,
    modifier: Modifier
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        ),
    ) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(650.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.padding(15.dp),
                )
                Image(
                    painter = painter,
                    contentDescription = imageDescription,
                    contentScale = ContentScale.Crop,
                    modifier = modifier
                        .height(400.dp)
                )
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    HyperlinkText(
                        modifier = Modifier.padding(end = 60.dp),
                        text = "",
                        linkText = listOf("Source"),
                        hyperlinks = source
                    )
                    HyperlinkText(
                        modifier = Modifier,
                        text = "",
                        linkText = listOf("Youtube"),
                        hyperlinks = youtube
                    )
                }
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    ElevatedButton(
                        onClick = { onDismissRequest() },
                        modifier = modifier.padding(15.dp),
                        elevation = ButtonDefaults.buttonElevation(15.dp)
                    ) {
                        Text("Delete Meal", style = MaterialTheme.typography.bodyMedium)
                    }
                    ElevatedButton(
                        onClick = { onConfirmation() },
                        modifier = modifier.padding(15.dp),
                        elevation = ButtonDefaults.buttonElevation(15.dp)
                    ) {
                        Text("Save Meal", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun HyperlinkText(
    modifier: Modifier = Modifier,
    text: String,
    linkText: List<String>,
    hyperlinks: List<String>,
    linkTextColor: Color = MaterialTheme.colorScheme.primary,
    linkStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontFamily: FontFamily = FontFamily.Monospace
) {
    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        linkText.forEachIndexed { index, link ->
            val startIndex = text.indexOf(link, lastIndex)
            val endIndex = startIndex + link.length

            if (startIndex > lastIndex) append(text.substring(lastIndex, startIndex))

            val linkUrL = LinkAnnotation.Url(
                hyperlinks[index], TextLinkStyles(
                    SpanStyle(
                        color = linkTextColor,
                        fontSize = fontSize,
                        fontFamily = fontFamily
                    )
                )
            ) {
                val url = (it as LinkAnnotation.Url).url
                uriHandler.openUri(url)
            }
            withLink(linkUrL) { append(link) }
            append(" ")
            lastIndex = endIndex + 1
        }
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
        addStyle(
            style = SpanStyle(
                fontSize = fontSize, fontFamily = fontFamily
            ), start = 0, end = text.length
        )
    }
    Text(text = annotatedString, modifier = modifier, style = linkStyle)
}


@Composable
fun MyBottomAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = BottomAppBarDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomAppBarDefaults.ContainerElevation,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    content: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shape = RoundedCornerShape(40.dp),
        modifier = modifier
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(windowInsets)
                .height(70.0.dp)
                .padding(contentPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
fun VerticalScrollingWithFixedHeightTextDemo(
    randomText: String,
    height: Dp? = null,
    size: TextUnit? = null
) {
    Text(
        text = randomText,
        style = MaterialTheme.typography.bodyLarge,
        fontSize = size ?: 14.sp,
        modifier = Modifier
            .height(height ?: 100.dp)
            .verticalScroll(rememberScrollState())
            .padding(top = 5.dp)
    )
}

@Composable
fun MessageCard(msg: String) {
    Row(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
        Column {
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun LoginField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Login",
    placeholder: String = "Enter your Login"
) {

    val focusManager = LocalFocusManager.current
    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }

    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Password",
    placeholder: String = "Enter your Password"
) {

    val isPasswordVisible by remember { mutableStateOf(false) }

    val leadingIcon = @Composable {
        Icon(
            Icons.Default.Lock,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(20.dp)
    )
}