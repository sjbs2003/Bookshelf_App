package com.example.bookshelfapp.ui.screen.searchScreen

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bookshelfapp.R
import com.example.bookshelfapp.model.Item
import com.example.bookshelfapp.ui.screen.BookShelfViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() } // disabling animation when button is clicked

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { focusManager.clearFocus() },
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = modifier.padding(top = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SearchBar(
                placeholder = R.string.search_books,
                value = viewModel.userInput,
                onValueChange = { viewModel.updateUserInput(it) },
                onSearch = onSearch,
                clearUserInput = { viewModel.clearUserInput() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Search Type: ${viewModel.searchType}")
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { viewModel.searchType = "intitle" }) {
                    Text("Title")
                }
                Button(onClick = { viewModel.searchType = "inauthor" }) {
                    Text("Author")
                }
                Button(onClick = { viewModel.searchType = "subject" }) {
                    Text("Subject")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display search results
            SearchResults(viewModel.searchResults)
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
fun SearchResults(items: List<Item>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            SearchResultItem(item)
        }
    }
}

@Composable
fun SearchResultItem(item: Item) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = item.volumeInfo.imageLinks.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(end = 16.dp)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = item.volumeInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = item.volumeInfo.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = item.volumeInfo.publishedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    // Use the actual BookShelfViewModel with the factory
    val viewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory)

    SearchScreen(
        viewModel = viewModel,
        onSearch = { viewModel.searchBooks() },
        modifier = Modifier
    )
}
