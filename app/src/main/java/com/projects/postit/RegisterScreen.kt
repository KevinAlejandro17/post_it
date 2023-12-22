package com.projects.postit

import android.content.Context
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.projects.postit.utils.AuthManager
import com.projects.postit.utils.AuthRes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    auth: AuthManager,
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }


    var isPasswordVisible by rememberSaveable {
        mutableStateOf(false)
    }


    var isChecked by remember { mutableStateOf(false) }

    Column(
        modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            color = Color.White,
            text = stringResource(id = R.string.sign_up), fontSize = 24.sp
        )

        Spacer(modifier = modifier.height(4.dp))

        FormTextField(
            label = stringResource(id = R.string.nickname_label),
            placeholder = stringResource(id = R.string.nickname_placeholder),
            value = nickName,
            onChange = { newValue ->
                nickName = newValue
            })

        Spacer(modifier = modifier.height(4.dp))

        FormTextField(
            label = stringResource(id = R.string.email_label),
            placeholder = stringResource(id = R.string.email_placeholder),
            value = userEmail,
            onChange = { newValue ->
                userEmail = newValue
            })

        Spacer(modifier = modifier.height(4.dp))

        Text(
            color = Color.White,
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

        val annotatedString = buildAnnotatedString {
            append("Al pulsar en ${stringResource(R.string.register_btn_text)}, aceptas los")

            pushStringAnnotation(tag = "policy", annotation = "https://google.com/policy")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(" términos y condiciones")
            }
            pop()

            append(" y la ")

            pushStringAnnotation(tag = "terms", annotation = "https://google.com/terms")
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append("política de privacidad")
            }
            pop()
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
            )
            ClickableText(
                text = annotatedString,
                style = TextStyle(color = Color.White, fontSize = 16.sp), modifier = Modifier.fillMaxWidth(),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "policy",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        navController.navigate("terms_conditions_screen")
                    }

                    annotatedString.getStringAnnotations(
                        tag = "terms",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        navController.navigate("terms_conditions_screen")
                    }
                },


                )
        }

        ElevatedButton(onClick = {
            scope.launch {
                signUp(userEmail, userPassword, nickName, auth, context, navController)
            }
        }) {
            Text(text = stringResource(id = R.string.register_btn_text))
        }

        Spacer(modifier = modifier.height(4.dp))

        Text(
            color = Color.White,
            text = stringResource(id = R.string.user_registered),
            modifier = Modifier
                .clickable(onClick = {
                    navController.navigate("login_screen")
                })
        )

    }

}

private suspend fun signUp(email: String, password: String, nickname: String, auth: AuthManager, context: Context, navigation: NavHostController) {
    if(email.isNotEmpty() && password.isNotEmpty()) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password, nickname)
        } catch (e: Exception) {
            Log.d(TAG,"Error al crear el usuario. ${e.message}")
        }

    } else {
        Toast.makeText(context, "Existen campos vacios", Toast.LENGTH_SHORT).show()
    }
}
