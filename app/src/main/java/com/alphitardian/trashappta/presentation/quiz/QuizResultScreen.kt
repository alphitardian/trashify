package com.alphitardian.trashappta.presentation.quiz

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alphitardian.trashappta.R
import com.alphitardian.trashappta.presentation.home.HomeTopAppBar

@Composable
fun QuizResultScreen(
    viewModel: QuizViewModel? = null,
    navigateBack: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { HomeTopAppBar() },
        content = {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (imageRef, titleRef, descRef, buttonRef) = createRefs()

                Image(
                    painter = if (viewModel?.getQuizScore()!! > 50) painterResource(id = R.drawable.star)
                    else painterResource(id = R.drawable.anxiety),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .constrainAs(imageRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top, margin = 81.dp)
                        }
                )

                Text(
                    text = if (viewModel.getQuizScore() > 50) "Congratulations!"
                    else "Better luck next time!",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.constrainAs(titleRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(imageRef.bottom, margin = 20.dp)
                    }
                )

                Text(
                    text = "You have complete ${viewModel.getQuizScore()}% of the quiz! Good job!",
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.constrainAs(descRef) {
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        top.linkTo(titleRef.bottom, margin = 8.dp)
                        width = Dimension.preferredWrapContent
                    }
                )

                Button(
                    onClick = navigateBack,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .constrainAs(buttonRef) {
                            start.linkTo(parent.start, margin = 20.dp)
                            end.linkTo(parent.end, margin = 20.dp)
                            bottom.linkTo(parent.bottom, margin = 20.dp)
                            width = Dimension.preferredWrapContent
                        }
                ) {
                    Text(text = "Finish")
                }
            }
        }
    )
}

@Preview(device = Devices.PIXEL_2)
@Composable
fun PreviewQuizResult() {
    QuizResultScreen()
}