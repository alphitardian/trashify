package com.alphitardian.trashappta.presentation.solution

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.data.waste.remote.response.WasteDataResponse
import com.alphitardian.trashappta.data.waste.remote.response.WasteResponse
import com.alphitardian.trashappta.utils.Resource
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun SolutionDialog(
    isDialogOpen: MutableState<Boolean>,
    wasteType: String = "",
    waste: WasteDataResponse? = null,
) {
    if (isDialogOpen.value) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    val image = when (wasteType) {
                        "Organic" -> R.drawable.organic_waste
                        "Non-Organic" -> R.drawable.non_organic_waste
                        "Poison" -> R.drawable.poison_waste
                        "Recyclable" -> R.drawable.recyclable_waste
                        else -> R.drawable.placeholder_image
                    }
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = waste?.name.orEmpty(),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.W700
                    )
                    Text(
                        text = waste?.description.orEmpty(),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Solution",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.W700
                    )
                    Text(
                        text = waste?.solution?.description.orEmpty(),
                        style = MaterialTheme.typography.body2
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { isDialogOpen.value = !isDialogOpen.value }) {
                    Text(text = "OK")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview
@Composable
fun PreviewSolutionDialog() {
    val isOpen = remember { mutableStateOf(true) }
    SolutionDialog(isDialogOpen = isOpen)
}