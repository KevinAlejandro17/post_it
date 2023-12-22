package com.projects.postit


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseUser
import com.projects.postit.ui.theme.PostItTheme
import com.projects.postit.utils.AuthManager
import com.projects.postit.utils.CloudStorageManager


@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    private val drawingViewModel by viewModels<DrawingViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PostItTheme {
                Column {

                    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF333333),
                        titleContentColor = Color.White,
                    ),
                        title = {
                            Icon(
                                painter = painterResource(id = R.drawable.post_it_logo),
                                contentDescription = "logo"
                            )
                        }
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF333333)
                    ) {
                        AppNavigator()
                    }
                }
            }
        }
    }

    @Composable
    fun AppNavigator(
    ) {
        val navController = rememberNavController()

        val context = LocalContext.current
        val authManager: AuthManager = AuthManager(context)

        val user: FirebaseUser? = authManager.getCurrentUser()
        val storage = CloudStorageManager(context)

        NavHost(
            navController = navController,
            startDestination = if(user == null) "login_screen" else "drawing_screen"
        ) {
            composable("drawing_screen") {
                DrawingScreen(navController, drawingViewModel)
            }

            composable("posts_screen") {
                CloudStorageScreen(storage)
            }

            composable("signup_screen") {
                SignUp(
                    modifier = Modifier.padding(top = 16.dp),
                    navController = navController,
                    auth = authManager
                )
            }
            composable("login_screen") {
                Login(
                    modifier = Modifier.padding(top = 16.dp),
                    navController = navController
                )
            }
            composable("forgot_password_screen") {
                PasswordRecovery(
                    modifier = Modifier.padding(top = 16.dp),
                    navController = navController
                )
            }
            composable(
                route = "change_pass_screen?mailTo={mailTo}",
                arguments = listOf(
                    navArgument(name = "mailTo"){
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ){ backstackEntry ->
                ChangePass(
                    modifier = Modifier.padding(top = 16.dp),
                    navController = navController,
                    mailTo = backstackEntry.arguments?.getString("mailTo") ,
                )
            }
            
            composable("change_user_info_screen") {
                ChangeUserInfoScreen(navController)
            }

            composable("profile_screen") {
                ProfileScreen(navController=navController, auth=authManager)
            }

            composable("terms_conditions_screen") {
                TermsConditionsScreen(navController = navController)
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