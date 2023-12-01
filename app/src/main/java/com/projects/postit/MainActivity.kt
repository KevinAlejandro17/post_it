package com.projects.postit


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projects.postit.data.model.UserState
import com.projects.postit.ui.theme.PostItTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import java.io.IOException


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    private val drawingViewModel by viewModels<DrawingViewModel>()
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PostItTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF333333)
                ) {
                    Scaffold(topBar = {
                        TopAppBar(title = {
                            Icon(
                                painter = painterResource(id = R.drawable.post_it_logo),
                                contentDescription = "logo"
                            )
                        })
                    }) {
                        NavHost(
                            navController = navController,
                            startDestination = "login_screen"
                        ) {
                            composable("drawing_screen") {

                                DrawingScreen(navController, drawingViewModel)
                            }
                            composable("profile_screen") {

                                ProfileScreen(navController = navController)
                            }
                            composable("login_screen") {
                                Login(
                                    modifier = Modifier.padding(top = 16.dp),
                                    navController = navController
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}


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

@Serializable
data class User(
    val id: Int = 0,
    val nickname: String = "",
    val created_at: String = "",
    val password: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    viewModel: Authentication = viewModel(),
) {

    val context = LocalContext.current
    val userState by viewModel.userState

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    var currentUserState by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(
            context,
        )
    }

    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = stringResource(id = R.string.sign_up), fontSize = 24.sp)
        FormTextField(
            label = stringResource(id = R.string.email_label),
            placeholder = stringResource(id = R.string.email_placeholder),
            value = userEmail,
            onChange = { newValue ->
                userEmail = newValue
            })

        Spacer(modifier = modifier.height(4.dp))

        Text(
            text = stringResource(id = R.string.password_label),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = userPassword,
            onValueChange = { userPassword = it },
            placeholder = { Text(text = stringResource(id = R.string.password_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isPasswordVisible)
                    Icons.Default.Visibility
                else Icons.Default.VisibilityOff

                val description = if (isPasswordVisible) "Hide password" else "Show password"

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )

        Spacer(modifier = modifier.height(4.dp))

        val coroutineScope = remember { CoroutineScope(Dispatchers.Default) }

        ElevatedButton(onClick = {
            viewModel.signUp(
                context,
                userEmail,
                userPassword,
            );
        }) {
            Text(text = stringResource(id = R.string.register_btn_text))
        }

        ElevatedButton(onClick = {
            navController?.navigate("drawing_screen")
        }) {
            Text(text = stringResource(id = R.string.register_btn_text))
        }

        when (userState) {
            is UserState.Loading -> {
                LoadingComponent()
            }

            is UserState.Success -> {
                val message = (userState as UserState.Success).message
                currentUserState = message
            }

            is UserState.Error -> {
                val message = (userState as UserState.Error).message
                currentUserState = message
            }
        }

        if (currentUserState.isNotEmpty()) {
            Text(text = currentUserState)
        }
    }
}


@ExperimentalMaterial3Api
@Composable
fun FormTextField(
    label: String,
    placeholder: String,
    value: String,
    onChange: (String) -> Unit,
) {
    Text(text = label, modifier = Modifier.fillMaxWidth())
    TextField(
        value = value,
        onValueChange = { newValue -> onChange(newValue) },
        placeholder = { Text(text = placeholder) }, modifier = Modifier.fillMaxWidth(),
    )
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


/*private fun saveCanvasAsImage(lines: List<Line>, canvasSize: Int): Uri? {
    val bitmap =
        Bitmap.createBitmap(canvasSize * 11 / 4, canvasSize * 11 / 4, Bitmap.Config.ARGB_8888)
    val androidCanvas = android.graphics.Canvas(bitmap)

    // Clear the canvas with a white background
    androidCanvas.drawColor(Color.White.toArgb())

    lines.forEach { line ->
        androidCanvas.drawLine(
            line.start.x,
            line.start.y,
            line.end.x,
            line.end.y,
            Paint().apply {
                color = line.color.toArgb()
                strokeWidth = line.strokeWidth.value
                style = Paint.Style.STROKE
            }
        )
    }

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "canvas_image.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    }

    val resolver = contentResolver
    val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val imageUri = resolver.insert(collection, contentValues)

    try {
        imageUri?.let { uri ->
            val outputStream = resolver.openOutputStream(uri)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream?.close()
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }

    return imageUri
} */


@Composable
fun ProfileScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .background(Color.Gray, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.patricio),
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            Text(
                text = stringResource(id = R.string.temp_profile),
                fontSize = 22.sp,
                color = Color.White
            )
        }

        item {
            Text(
                text = stringResource(id = R.string.my_posts),
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                for (index in 0 until 3) {
                    ImageCard(imageResource = R.drawable.casa)
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                for (index in 3 until 6) {
                    ImageCard(imageResource = R.drawable.casa)
                }
            }
        }

        item {
            Button(
                onClick = {
                    navController.navigate("drawing_screen")
                },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.log_out),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}


@Composable
fun ImageCard(imageResource: Int) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
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


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@ExperimentalMaterial3Api
@Preview(showBackground = true, widthDp = 400, heightDp = 740, backgroundColor = 0xFF333333)
@Composable
fun PostItPreview() {
    PostItTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                Icon(
                    painter = painterResource(id = R.drawable.post_it_logo),
                    contentDescription = "logo"
                )
            })
        }) {
            Login(modifier = Modifier.padding(top = 16.dp))
        }
    }
}