package com.kastack.vidyanet.superAdmin.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kastack.vidyanet.commonUi.components.AppText
import com.kastack.vidyanet.constants.IndiaConstants
import com.kastack.vidyanet.models.schoolUser.CreateSchoolRequest
import com.kastack.vidyanet.models.schoolUser.SchoolType
import com.kastack.vidyanet.validators.SchoolValidator
import com.kastack.vidyanet.validators.ValidationException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSchoolDialog(
    onDismiss: () -> Unit,
    onConfirm: (CreateSchoolRequest) -> Unit,
    isLoading: Boolean = false
) {
    var schoolName by remember { mutableStateOf("") }
    var schoolType by remember { mutableStateOf(SchoolType.PRIVATE) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("Jharkhand") }
    var country by remember { mutableStateOf("India") }
    var postalCode by remember { mutableStateOf("") }

    var expandedType by remember { mutableStateOf(false) }
    var expandedState by remember { mutableStateOf(false) }

    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        "Register New School",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider()

                if (validationError != null) {
                    AppText(
                        validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Basic Information
                    AppText("Basic Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text("School Name") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. St. Xavier's International") }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = expandedType,
                            onExpandedChange = { expandedType = !expandedType },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = schoolType.name.replace("_", " ").lowercase().capitalize(),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("School Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                            )
                            ExposedDropdownMenu(
                                expanded = expandedType,
                                onDismissRequest = { expandedType = false }
                            ) {
                                SchoolType.entries.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.name.replace("_", " ").lowercase().capitalize()) },
                                        onClick = {
                                            schoolType = type
                                            expandedType = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Contact Phone") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website (Optional)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider()

                    // Address Information
                    AppText("Location & Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Street Address") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedState,
                            onExpandedChange = { expandedState = !expandedState },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = state,
                                onValueChange = { state = it },
                                label = { Text("State / Province") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedState) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                readOnly = true
                            )
                            ExposedDropdownMenu(
                                expanded = expandedState,
                                onDismissRequest = { expandedState = false }
                            ) {
                                IndiaConstants.states.forEach { stateName ->
                                    DropdownMenuItem(
                                        text = { Text(stateName) },
                                        onClick = {
                                            state = stateName
                                            expandedState = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = postalCode,
                            onValueChange = { postalCode = it },
                            label = { Text("Postal Code") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider()

                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, enabled = !isLoading) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val request = CreateSchoolRequest(
                                schoolCode = "",
                                schoolName = schoolName,
                                schoolType = schoolType,
                                phone = phone,
                                email = email.takeIf { it.isNotBlank() },
                                website = website.takeIf { it.isNotBlank() },
                                address = address,
                                city = city,
                                state = state,
                                country = country,
                                postalCode = postalCode
                            )
                            try {
                                SchoolValidator.validateCreate(request)
                                validationError = null
                                onConfirm(request)
                            } catch (e: ValidationException) {
                                validationError = e.message
                            }
                        },
                        enabled = !isLoading && schoolName.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Text("Create School")
                        }
                    }
                }
            }
        }
    }
}

fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
