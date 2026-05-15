package com.app.dopp.ui_project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.dopp.ui.theme.DarkBg
import com.app.dopp.ui.theme.DarkSurface
import com.app.dopp.ui.theme.Sky500
import com.app.dopp.ui.theme.Violet600
import com.app.dopp.ui.theme.Violet800
import androidx.compose.ui.window.Dialog

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val error     by viewModel.error.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onLoginSuccess()
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val focusManager = LocalFocusManager.current

    var loginEmail    by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPassVis  by remember { mutableStateOf(false) }

    var regName     by remember { mutableStateOf("") }
    var regEmail    by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPassVis  by remember { mutableStateOf(false) }

    var showServerDialog by remember { mutableStateOf(false) }
    var serverUrlInput   by remember { mutableStateOf(viewModel.getServerUrl()) }

    if (showServerDialog) {
        Dialog(onDismissRequest = { showServerDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = null,
                            tint = Violet800,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Адрес сервера",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Введите URL вашего сервера DoPP",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = serverUrlInput,
                        onValueChange = { serverUrlInput = it },
                        label = { Text("URL сервера") },
                        placeholder = { Text("http://85.198.67.191:5000/") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Violet800,
                            focusedLabelColor = Violet800
                        )
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showServerDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) { Text("Отмена") }
                        Button(
                            onClick = {
                                viewModel.saveServerUrl(serverUrlInput)
                                showServerDialog = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Violet800)
                        ) { Text("Сохранить") }
                    }
                }
            }
        }
    }

    LaunchedEffect(selectedTab) { viewModel.clearError() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DarkBg, DarkSurface, Color(0xFF1A0E3D)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(listOf(Violet800, Violet600, Sky500))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Science,
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "DoPP",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Физика в дополненной реальности",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(Modifier.height(44.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF3F0FF)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Войти", "Регистрация").forEachIndexed { idx, title ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(
                                        if (selectedTab == idx)
                                            Brush.horizontalGradient(listOf(Violet800, Violet600))
                                        else
                                            Brush.horizontalGradient(
                                                listOf(Color.Transparent, Color.Transparent)
                                            )
                                    )
                                    .clickable { selectedTab = idx }
                                    .padding(vertical = 11.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == idx) Color.White else Color(0xFF6B7280),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    AnimatedVisibility(visible = error != null, enter = fadeIn(), exit = fadeOut()) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFFE4E6))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = null,
                                        tint = Color(0xFFE11D48),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = error ?: "",
                                        color = Color(0xFF881337),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }

                    if (selectedTab == 0) {
                        AuthField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = "Email",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        Spacer(Modifier.height(12.dp))
                        AuthPasswordField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = "Пароль",
                            visible = loginPassVis,
                            onToggle = { loginPassVis = !loginPassVis },
                            imeAction = ImeAction.Done,
                            onIme = {
                                focusManager.clearFocus()
                                viewModel.login(loginEmail, loginPassword)
                            }
                        )
                        Spacer(Modifier.height(24.dp))
                        GradientButton(
                            text = "Войти",
                            onClick = { viewModel.login(loginEmail, loginPassword) },
                            isLoading = isLoading,
                            enabled = !isLoading && loginEmail.isNotBlank() && loginPassword.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        AuthField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = "Имя",
                            icon = Icons.Default.Person,
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        Spacer(Modifier.height(12.dp))
                        AuthField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = "Email",
                            icon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            onIme = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                        Spacer(Modifier.height(12.dp))
                        AuthPasswordField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = "Пароль",
                            visible = regPassVis,
                            onToggle = { regPassVis = !regPassVis },
                            imeAction = ImeAction.Done,
                            onIme = {
                                focusManager.clearFocus()
                                viewModel.register(regName, regEmail, regPassword)
                            }
                        )
                        Spacer(Modifier.height(24.dp))
                        GradientButton(
                            text = "Зарегистрироваться",
                            onClick = { viewModel.register(regName, regEmail, regPassword) },
                            isLoading = isLoading,
                            enabled = !isLoading && regName.isNotBlank() && regEmail.isNotBlank() && regPassword.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showServerDialog = true }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Настройки сервера",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onIme: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = KeyboardActions(onNext = { onIme() }, onDone = { onIme() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor     = Violet800,
            focusedLabelColor      = Violet800,
            focusedLeadingIconColor = Violet800
        )
    )
}

@Composable
private fun AuthPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggle: () -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    onIme: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = { onIme() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = Violet800,
            focusedLabelColor       = Violet800,
            focusedLeadingIconColor = Violet800
        )
    )
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled)
                    Brush.horizontalGradient(listOf(Violet800, Violet600, Sky500))
                else
                    Brush.horizontalGradient(listOf(Color(0xFFD1D5DB), Color(0xFFD1D5DB)))
            )
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp,
                color = Color.White
            )
        } else {
            Text(
                text = text,
                color = if (enabled) Color.White else Color(0xFF9CA3AF),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}
