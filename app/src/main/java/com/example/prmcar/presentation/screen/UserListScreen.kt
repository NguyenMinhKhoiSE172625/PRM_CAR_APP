package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prmcar.data.model.UserRequest
import com.example.prmcar.presentation.viewmodel.UserUiState
import com.example.prmcar.presentation.viewmodel.UserViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    userUiState: UserUiState,
    userViewModel: UserViewModel,
    onEditUser: (Int) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, Int?>>(false to null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Users Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add User")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (userUiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userUiState.users) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            userViewModel.getUserById(user.userId)
                                            showDetailDialog = true
                                        }
                                ) {
                                    Text(
                                        text = user.username,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = user.fullName ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = user.email ?: "",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(onClick = { showEditDialog = true to user.userId }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { userViewModel.deleteUser(user.userId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    if (showDetailDialog && userUiState.selectedUser != null) {
        UserDetailDialog(
            user = userUiState.selectedUser,
            onDismiss = {
                showDetailDialog = false
                userViewModel.clearSelectedUser()
            }
        )
    }
    // Add Dialog
    if (showAddDialog) {
        UserDialog(
            title = "Add User",
            initialUsername = "",
            initialPassword = "",
            initialFullName = "",
            initialEmail = "",
            initialPhone = "",
            initialAddress = "",
            initialRegDate = "",
            onConfirm = { username, password, fullName, email, phone, address, regDate ->
                userViewModel.createUser(
                    UserRequest(
                        username.orEmpty(),
                        password.orEmpty(),
                        fullName.orEmpty(),
                        email.orEmpty(),
                        phone,
                        address,
                        regDate
                    )
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
    // Edit Dialog
    if (showEditDialog.first && showEditDialog.second != null) {
        val editId = showEditDialog.second!!
        val editUser = userUiState.users.find { it.userId == editId }
        UserDialog(
            title = "Edit User",
            initialUsername = editUser?.username.orEmpty(),
            initialPassword = "", // Không show password cũ
            initialFullName = editUser?.fullName.orEmpty(),
            initialEmail = editUser?.email.orEmpty(),
            initialPhone = editUser?.phoneNumber.orEmpty(),
            initialAddress = editUser?.address.orEmpty(),
            initialRegDate = editUser?.registrationDate.orEmpty(),
            onConfirm = { username, password, fullName, email, phone, address, regDate ->
                userViewModel.updateUser(
                    editId,
                    UserRequest(
                        username.orEmpty(),
                        password.orEmpty(),
                        fullName.orEmpty(),
                        email.orEmpty(),
                        phone,
                        address,
                        regDate
                    )
                )
                showEditDialog = false to null
            },
            onDismiss = { showEditDialog = false to null }
        )
    }
}

@Composable
fun UserDialog(
    title: String,
    initialUsername: String = "",
    initialPassword: String = "",
    initialFullName: String = "",
    initialEmail: String = "",
    initialPhone: String = "",
    initialAddress: String = "",
    initialRegDate: String = "",
    onConfirm: (String, String, String?, String?, String?, String?, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }
    var password by remember { mutableStateOf(initialPassword) }
    var fullName by remember { mutableStateOf(initialFullName) }
    var email by remember { mutableStateOf(initialEmail) }
    var phone by remember { mutableStateOf(initialPhone) }
    var address by remember { mutableStateOf(initialAddress) }
    var regDate by remember { mutableStateOf(initialRegDate) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password Hash") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = regDate,
                    onValueChange = { regDate = it },
                    label = { Text("Registration Date (yyyy-MM-dd)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank())
                        onConfirm(
                            username,
                            password,
                            fullName.ifBlank { null },
                            email.ifBlank { null },
                            phone.ifBlank { null },
                            address.ifBlank { null },
                            regDate.ifBlank { null }
                        )
                },
                enabled = username.isNotBlank() && password.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun UserDetailDialog(user: com.example.prmcar.data.model.UserResponse, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("User Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ID: ${user.userId}")
                Text("Username: ${user.username}")
                Text("Full Name: ${user.fullName ?: ""}")
                Text("Email: ${user.email ?: ""}")
                Text("Phone: ${user.phoneNumber ?: ""}")
                Text("Address: ${user.address ?: ""}")
                Text("User Type: ${user.userType ?: ""}")
                Text("Registration Date: ${user.registrationDate ?: ""}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
} 