package com.projects.postit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {

    val context = LocalContext.current

    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }


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
        Text(
            color = Color.White,
            text = stringResource(id = R.string.login_title),
            fontSize = 24.sp
        )
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


        ElevatedButton(onClick = {
        }) {
            Text(text = stringResource(id = R.string.login_btn_text))
        }

        Spacer(modifier = modifier.height(4.dp))

        Text(
            color = Color.White,
            text = stringResource(id = R.string.create_account),
            modifier = Modifier
                .clickable(onClick = {
                    navController.navigate("signup_screen")
                })
        )

        Spacer(modifier = modifier.height(16.dp))

        Text(
            color = Color.White,
            text = stringResource(id = R.string.forgot_password_link),
            modifier = Modifier
                .clickable(onClick = {
                    navController.navigate("forgot_password_screen")
                })
        )
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
    Text(color = Color.White, text = label, modifier = Modifier.fillMaxWidth())
    TextField(
        value = value,
        onValueChange = { newValue -> onChange(newValue) },
        placeholder = { Text(text = placeholder) }, modifier = Modifier.fillMaxWidth(),
    )
}