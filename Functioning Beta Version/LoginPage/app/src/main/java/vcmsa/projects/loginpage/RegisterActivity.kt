package vcmsa.projects.loginpage

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import vcmsa.projects.loginpage.ui.theme.LoginPageTheme
import androidx.compose.foundation.clickable
import vcmsa.projects.loginpage.user.UserList

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginPageTheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    val userDatabase = UserDatabase.getDatabase(context)
    val userDao = userDatabase.userDao()
    val coroutineScope = rememberCoroutineScope()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFF994F31)
    val textFieldColor = Color(0xFFEEBA86)

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        focusedIndicatorColor = textFieldColor,
        unfocusedIndicatorColor = textFieldColor,
        focusedLabelColor = textFieldColor,
        unfocusedLabelColor = textFieldColor,
        cursorColor = textFieldColor,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
    ) {
        // Top Left Text Back to Login
        Text(
            text = "Back to Login",
            style = TextStyle(fontSize = 25.sp, color = Color.White),
            modifier = Modifier
                .clickable {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                .padding(bottom = 30.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Register", fontSize = 32.sp, color = Color.White)

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors
            )

            // Displaying error message if any
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = TextStyle(fontSize = 14.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Check if email already exists
                    coroutineScope.launch {
                        val existingUser = userDao.getUserByEmail(email)
                        if (existingUser != null) {
                            errorMessage = "Email already exists."
                        } else {
                            // Proceed with registration if email does not exist
                            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                                errorMessage = "All fields are required."
                            } else if (password != confirmPassword) {
                                errorMessage = "Passwords do not match."
                            } else {
                                val user = User(fullName = fullName, email = email, password = password)
                                userDao.insert(user)
                                errorMessage = "" // Clear any previous errors
                                context.startActivity(Intent(context, MainActivity::class.java)) // Redirect to login
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Clickable text at the bottom
            ClickableText(
                text = AnnotatedString("Already have an account? Login"),
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                style = TextStyle(color = Color.White)
            )
            ClickableText(
                text = AnnotatedString("View users"),
                onClick = {
                    context.startActivity(Intent(context, UserList::class.java)) // Open register page
                },
                style = TextStyle(color = Color.White)
            )
        }
    }
}
