@file:OptIn(ExperimentalMaterial3Api::class)

package pk.taxreturn.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pk.taxreturn.app.model.UserData
import pk.taxreturn.app.network.SheetsService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoggedIn: (UserData) -> Unit) {
    val ctx = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Login state
    var loginCnic by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }

    // Register state
    var regName by remember { mutableStateOf("") }
    var regCnic by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirm by remember { mutableStateOf("") }
    var regError by remember { mutableStateOf("") }
    var registering by remember { mutableStateOf(false) }

    var showPassword by remember { mutableStateOf(false) }

    val gradientGreen = Brush.verticalGradient(
        listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
    )

    Column(Modifier.fillMaxSize()) {
        // ââ Header ââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        Box(
            Modifier
                .fillMaxWidth()
                .background(gradientGreen)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.AccountBalance,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Tax Return PK",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Tax Year 2026 â¢ Income Tax Ordinance 2001",
                    color = Color(0xFFB9F6CA),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ââ Tabs âââââââââââââââââââââââââââââââââââââââââââââââââââââââââââââ
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Login", modifier = Modifier.padding(vertical = 14.dp))
            }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                Text("Register", modifier = Modifier.padding(vertical = 14.dp))
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            if (selectedTab == 0) {
                // ââ LOGIN ââââââââââââââââââââââââââââââââââââââââââââââââââ
                Spacer(Modifier.height(12.dp))
                Text("Welcome back!", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                Text("Sign in with your CNIC and password",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = loginCnic,
                    onValueChange = { loginCnic = it.filter(Char::isDigit).take(13) },
                    label = { Text("CNIC (13 digits, no dashes)") },
                    leadingIcon = { Icon(Icons.Filled.Badge, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = loginPassword,
                    onValueChange = { loginPassword = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Filled.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(loginError.isNotEmpty()) {
                    Text(loginError, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp))
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        loginError = ""
                        val saved = UserData.load(ctx)
                        when {
                            loginCnic.length != 13 -> loginError = "Enter a valid 13-digit CNIC."
                            loginPassword.isBlank() -> loginError = "Password cannot be empty."
                            saved == null || saved.cnic != loginCnic ->
                                loginError = "No account found for this CNIC. Please register."
                            saved.passwordHash != UserData.hashPassword(loginPassword) ->
                                loginError = "Incorrect password."
                            else -> {
                                SheetsService.logLogin(saved.cnic, saved.fullName)
                                onLoggedIn(saved)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't have an account? Register here")
                }

            } else {
                // ââ REGISTER âââââââââââââââââââââââââââââââââââââââââââââââ
                Spacer(Modifier.height(12.dp))
                Text("Create account", style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold)
                Text("Your details are stored locally and logged securely.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(20.dp))

                listOf(
                    Triple(regName, "Full Name (as per CNIC)", Icons.Filled.Person) to
                            { v: String -> regName = v },
                    Triple(regCnic, "CNIC (13 digits, no dashes)", Icons.Filled.Badge) to
                            { v: String -> regCnic = v.filter(Char::isDigit).take(13) },
                    Triple(regEmail, "Email Address", Icons.Filled.Email) to
                            { v: String -> regEmail = v },
                    Triple(regPhone, "Phone Number", Icons.Filled.Phone) to
                            { v: String -> regPhone = v.filter { it.isDigit() || it == '+' }.take(15) }
                ).forEach { (triple, setter) ->
                    val (value, label, icon) = triple
                    OutlinedTextField(
                        value = value,
                        onValueChange = setter,
                        label = { Text(label) },
                        leadingIcon = { Icon(icon, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        keyboardOptions = when {
                            label.contains("CNIC") || label.contains("Phone") ->
                                KeyboardOptions(keyboardType = KeyboardType.Number)
                            label.contains("Email") ->
                                KeyboardOptions(keyboardType = KeyboardType.Email)
                            else -> KeyboardOptions.Default
                        }
                    )
                }

                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    label = { Text("Password (min 6 characters)") },
                    leadingIcon = { Icon(Icons.Filled.Lock, null) },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, null)
                        }
                    },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                )
                OutlinedTextField(
                    value = regConfirm,
                    onValueChange = { regConfirm = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Filled.LockOpen, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(regError.isNotEmpty()) {
                    Text(regError, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 6.dp))
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        regError = ""
                        when {
                            regName.isBlank() -> regError = "Full name is required."
                            regCnic.length != 13 -> regError = "Enter a valid 13-digit CNIC."
                            regEmail.isBlank() || !regEmail.contains("@") ->
                                regError = "Enter a valid email address."
                            regPassword.length < 6 -> regError = "Password must be at least 6 characters."
                            regPassword != regConfirm -> regError = "Passwords do not match."
                            else -> {
                                registering = true
                                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
                                val user = UserData(
                                    cnic = regCnic,
                                    fullName = regName,
                                    email = regEmail,
                                    phone = regPhone,
                                    passwordHash = UserData.hashPassword(regPassword),
                                    registeredAt = now
                                )
                                UserData.save(ctx, user)
                                SheetsService.registerUser(user) { /* silent */ }
                                registering = false
                                onLoggedIn(user)
                            }
                        }
                    },
                    enabled = !registering,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (registering) {
                        CircularProgressIndicator(
                            Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(12.dp))
                TextButton(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already have an account? Sign in")
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Your data is stored locally on your device. Filing is done manually on iris.fbr.gov.pk.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
