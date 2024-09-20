package com.example.bookshelfapp.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.example.bookshelfapp.R
import com.example.bookshelfapp.model.ImageLinks
import com.example.bookshelfapp.model.Item
import com.example.bookshelfapp.model.VolumeInfo


@Composable
fun ResultScreen(
    bookshelfUiState: BookShelfUiState,
    tryAgain: () -> Unit,
    homepage: () -> Unit,
) {
    when(bookshelfUiState){
        is BookShelfUiState.Success -> BookshelfLazyColumn(itemList = bookshelfUiState.item.items)
        is BookShelfUiState.Loading -> LoadingScreen()
        is BookShelfUiState.Error -> ErrorScreen(
            message = bookshelfUiState.message,
            tryAgain = tryAgain,
            homepage = homepage
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(Color.Black), Alignment.Center) {
        Image(
            modifier = modifier.size(200.dp),
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = null
        )
    }
}

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    message: String,
    tryAgain: () -> Unit,
    homepage: () -> Unit
) {
    Box(modifier = modifier
        .fillMaxSize(),
        Alignment.Center) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_connection_error),
                contentDescription = stringResource(id = R.string.loading_failed)
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = modifier.padding(16.dp)
            )

            Spacer(modifier = modifier.padding(18.dp))

            Text(
                text = stringResource(id = R.string.try_again),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier.clickable { tryAgain() }
            )

            Spacer(modifier = modifier.padding(18.dp))

            Text(
                text = stringResource(id = R.string.homepage),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = modifier.clickable { homepage() }
            )
        }
    }
}


@Composable
fun BookshelfCard(
    image: ImageLinks,
    itemInfo: VolumeInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = modifier.padding(10.dp)) {
            Box {
                Column(modifier = modifier.padding(10.dp)) {
                    Text(
                        text = itemInfo.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = modifier.height(16.dp))
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                    .data(image.thumbnail.replace("http", "https"))
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build(),
                    placeholder = painterResource(id = R.drawable.loading_img),
                    error = painterResource(id = R.drawable.ic_broken_image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = itemInfo.authors.joinToString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = if (itemInfo.publishedDate == stringResource(id = R.string.publication_not_available))
                            stringResource(id = R.string.publication_not_available)
                        else itemInfo.publishedDate.take(integerResource(id = R.integer.publish_date_max_shown_chars)),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (itemInfo.description == stringResource(id = R.string.description_not_available))
                            stringResource(id = R.string.description_not_available)
                        else itemInfo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = integerResource(id = R.integer.description_max_lines),
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun BookshelfLazyColumn(
    itemList: List<Item>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 15.dp,
            vertical = 15.dp
        ),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        items(items = itemList, key = { item -> item.id }) {

            Log.d("Image URL", "Thumbnail URL: ${it.volumeInfo.imageLinks}")
            BookshelfCard(
                image = it.volumeInfo.imageLinks,
                itemInfo = it.volumeInfo
            )
        }
    }
}





@Composable
@Preview
fun BookshelfLazyPreview() {
    BookshelfLazyColumn(
        itemList = listOf(
            Item(
                id = 1.toString(),
                volumeInfo = VolumeInfo(
                    title = "The Lord of the Rings",
                    authors = listOf("J. R. R. Tolkien"),
                    publishedDate = "1954-08-08",
                    imageLinks = ImageLinks(thumbnail = "https://via.placeholder.com/150")
                )
            ),
            Item(
                id = 2.toString(),
                volumeInfo = VolumeInfo(
                    title = "Pride and Prejudice",
                    authors = listOf("Jane Austen"),
                    publishedDate = "1813-01-28",
                    imageLinks = ImageLinks(thumbnail = "https://via.placeholder.com/150/00FFFF")
                )
            )
        )
    )
}

@Composable
@Preview
fun BookCardPreview() {
    BookshelfCard(
        image = ImageLinks(thumbnail = "https://via.placeholder.com/150"), // Placeholder image
        itemInfo = VolumeInfo(
            title = "The Lord of the Rings",
            authors = listOf("J. R. R. Tolkien"),
            publishedDate = "1954-08-08",
            description = "An epic high-fantasy trilogy written by English philologist and University of Oxford professor J. R. R. Tolkien. The story began as a sequel to Tolkien's 1937 fantasy novel The Hobbit, but eventually developed into a much larger work."
        )
    )
}

@Composable
@Preview
fun LoadingScreenPreview() {
    LoadingScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Add a background color
    )
}

@Composable
@Preview
fun ErrorScreenPreview() {
    ErrorScreen(
        modifier = Modifier
            .background(Color(0xFF000000)), // Add a background color
        message = "Error message",
        tryAgain = {}, // Empty lambda for preview
        homepage = {}  // Empty lambda for preview
    )
}