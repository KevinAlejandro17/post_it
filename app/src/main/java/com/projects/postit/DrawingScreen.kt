package com.projects.postit

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Dp = 3.dp
)

class DrawingViewModel : ViewModel() {
    @OptIn(ExperimentalMaterial3Api::class)
    val lines = mutableStateListOf<Line>()
}

@Composable
fun DrawingScreen(navController: NavHostController, drawingViewModel: DrawingViewModel) {
    val lines = drawingViewModel.lines
    val canvasSize = 340.dp
    val canvasOffset = remember { mutableStateOf(Offset(0f, 0f)) }
    val canvasRect = Rect(
        canvasOffset.value.x + 8,
        canvasOffset.value.y + 8,
        canvasOffset.value.x + 2 * canvasSize.value + 230,
        canvasOffset.value.y + 2 * canvasSize.value + 230
    )
    val selectedStrokeWidth = remember { mutableStateOf(3.dp) }
    val selectedColor = remember { mutableStateOf(Color.Black) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = stringResource(id = R.string.new_drawing),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(30.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .size(canvasSize)
                .background(Color.White)
                .pointerInput(true)
                {
                    detectDragGestures { change, dragAmount ->
                        change.consume()

                        val newStart = change.position - dragAmount
                        val newEnd = change.position

                        if (canvasRect.contains(newStart) && canvasRect.contains(newEnd)) {
                            val line = Line(
                                start = newStart,
                                end = newEnd,
                                color = selectedColor.value,
                                strokeWidth = selectedStrokeWidth.value
                            )

                            lines.add(line)
                        }
                    }
                }
        ) {
            lines.forEach { line ->
                drawLine(
                    color = line.color,
                    start = line.start,
                    end = line.end,
                    strokeWidth = line.strokeWidth.value,
                    cap = StrokeCap.Round
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Square(Color.Black) {
                selectedColor.value = Color.Black
            }
            Square(Color.Red) {
                selectedColor.value = Color.Red
            }
            Square(Color.Blue) {
                selectedColor.value = Color.Blue
            }
            Square(Color.Yellow) {
                selectedColor.value = Color.Yellow
            }
        }


        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            CircularButtonWithDot(10.dp) {
                selectedStrokeWidth.value = 3.dp
            }
            CircularButtonWithDot(15.dp) {
                selectedStrokeWidth.value = 8.dp
            }
            CircularButtonWithDot(25.dp) {
                selectedStrokeWidth.value = 15.dp
            }
        }

        Button(
            onClick = {

            },
            modifier = Modifier
                .padding(15.dp)
                .size(150.dp, 60.dp)
        ) {
            Text(
                text = stringResource(id = R.string.post_button),
                color = Color.White,
                fontSize = 18.sp
            )
        }
        Button(
            onClick = {
                navController.navigate("profile_screen")
            },
            modifier = Modifier
                .padding(15.dp)
                .size(150.dp, 60.dp)
        ) {
            Text(
                text = stringResource(id = R.string.profile_button),
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}


@Composable
fun CircularButtonWithDot(dotSize: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(Color.White, shape = CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .background(Color.Black, shape = CircleShape)
        )
    }
}


@Composable
fun Square(color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(color)
            .clickable(onClick = onClick)
    ) { }
}

@Composable
fun GridItem(imageResource: String) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .background(Color.LightGray),
    ) {
        Image(
            painter = painterResource(id = R.drawable.casa),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}