package com.projects.postit


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projects.postit.ui.theme.PostItTheme


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    private val drawingViewModel by viewModels<DrawingViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PostItTheme {
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
                       AppNavigator()
                    }
                }
            }
        }
    }

    @Composable
    fun AppNavigator() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "signup_screen") {
            composable("signup_screen") {
                SignUp(
                    modifier = Modifier.padding(top = 16.dp),
                    navController = navController
                )
            }
            composable("login_screen") {
                Login(
                    modifier = Modifier.padding(top = 16.dp),
                    navController = navController
                )
            }
            composable("drawing_screen") {

                DrawingScreen(navController, drawingViewModel)
            }
            composable("profile_screen") {

                ProfileScreen(navController = navController)
            }
        }
    }
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


/*
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
}*/