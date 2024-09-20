package com.example.bookshelfapp.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.bookshelfapp.R

@Composable
fun SearchScreen(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    clearUserInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember {  MutableInteractionSource() } // disabling animation when button is clicked

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() },
        Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchScreenDescription(
                modifier = modifier.padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 24.dp
                )
            )

            SearchBar(
                placeholder = R.string.search_books,
                value = value,
                onValueChange = onValueChange,
                onSearch = onSearch,
                clearUserInput = clearUserInput)
        }
    }
}

@Composable
fun SearchBar(
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    clearUserInput: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(stringResource(placeholder)) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = null,
                    modifier = modifier.clickable { onSearch() }
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.clear_icon),
                    contentDescription = null,
                    modifier = modifier.clickable { clearUserInput() }
                )
            },
            keyboardActions = KeyboardActions { onSearch() },
            modifier = modifier.widthIn(max = 280.dp)
        )
    }
}

@Composable
fun SearchScreenDescription(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.home_search_description),
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}