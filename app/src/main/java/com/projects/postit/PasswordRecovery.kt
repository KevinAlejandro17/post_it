package com.projects.postit

import android.content.Intent
import android.net.MailTo
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordRecovery(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: Authentication = viewModel(),
) {
    val context = LocalContext.current

    var userEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.isUserLoggedIn(
            context,
        )
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(color = Color.White, text = stringResource(id = R.string.forgot_password_title), fontSize = 24.sp)

        Spacer(modifier = modifier.height(4.dp))

        FormTextField(
            label = stringResource(id = R.string.email_label),
            placeholder = stringResource(id = R.string.email_placeholder),
            value = userEmail,
            onChange = { newValue ->
                userEmail = newValue
            })

        Spacer(modifier = modifier.height(4.dp))

        ElevatedButton(onClick = {
            navController.navigate("change_pass_screen?mailTo=$userEmail")
        }) {
            Text(text = stringResource(id = R.string.forgot_pass_btn))
        }

        Spacer(modifier = modifier.height(4.dp))

        ElevatedButton(onClick = {
            navController.navigate("login_screen")
        }) {
            Text(text = stringResource(id = R.string.return_btn))
        }
    }
}

