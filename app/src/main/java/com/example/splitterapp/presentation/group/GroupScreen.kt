package com.example.splitterapp.presentation.group

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun GroupScreen(viewModel: GroupViewModel) {
    val groupList by viewModel.groups.collectAsState(initial = emptyList())

    var groupName by remember { mutableStateOf("") }
    var participantsText by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var amountPaidText by remember { mutableStateOf("") }
    var expandedGroupId by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val paidBy = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val discount = remember { mutableStateOf("") }
    val gst = remember { mutableStateOf("") }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showAddGroupForm by remember { mutableStateOf(true) }

    // Flags for showing animations
    var showCreatedIcon by remember { mutableStateOf(false) }
    var showDeletedIcon by remember { mutableStateOf(false) }

    // Snackbar & coroutine scope
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Build a DatePickerDialog whose maxDate is “today”
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                date.value = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()  // no future dates
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // ─── Toggle “Create” vs “View” ───
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showAddGroupForm) "Creating New Group" else "Viewing All Groups",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    TextButton(
                        onClick = { showAddGroupForm = !showAddGroupForm },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(if (showAddGroupForm) "View Groups" else "Create Group")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (showAddGroupForm) {
                    // ─── Create Group Form ───
                    Text("Create Group", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    Column {
                        // 1) Group Name
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("Group Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // 2) Participants (comma-separated)
                        OutlinedTextField(
                            value = participantsText,
                            onValueChange = { participantsText = it },
                            label = { Text("Participants (comma-separated)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // 3) Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // 4) Amount Paid (must be ≥ 5.0)
                        OutlinedTextField(
                            value = amountPaidText,
                            onValueChange = { amountPaidText = it },
                            label = { Text("Amount Paid (min ₹5)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions =
                                KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))

                        // 5) Paid By
                        OutlinedTextField(
                            value = paidBy.value,
                            onValueChange = { paidBy.value = it },
                            label = { Text("Paid By") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))

                        // 6) Date (no future dates)
                        Text(
                            text = if (date.value.isEmpty()) "Select Date" else date.value,
                            modifier = Modifier
                                .clickable { datePickerDialog.show() }
                                .padding(8.dp),
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))

                        // 7) Discount % (optional; if provided, 0.0..99.99)
                        OutlinedTextField(
                            value = discount.value,
                            onValueChange = { discount.value = it },
                            label = { Text("Discount (%) – optional") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions =
                                KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(8.dp))

                        // 8) GST % (optional; if provided, 0.1..99.99)
                        OutlinedTextField(
                            value = gst.value,
                            onValueChange = { gst.value = it },
                            label = { Text("GST (%)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions =
                                KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        Spacer(Modifier.height(12.dp))

                        // Show any validation error
                        errorMessage?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(8.dp))
                        }

                        // “Add Group” Button
                        Button(
                            onClick = {
                                // Parse inputs
                                val amount = amountPaidText.toDoubleOrNull()
                                val discountValue = discount.value.toDoubleOrNull() ?: 0.0
                                val gstValue = gst.value.toDoubleOrNull() ?: 0.0
                                val participants = participantsText
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() }

                                // Validate:
                                when {
                                    groupName.isBlank() ||
                                            title.isBlank() ||
                                            paidBy.value.isBlank() ||
                                            date.value.isBlank() -> {
                                        errorMessage = "Please fill all required fields"
                                    }
                                    amount == null -> {
                                        errorMessage = "Amount must be a valid number"
                                    }
                                    amount < 5.0 -> {
                                        errorMessage = "Amount Paid must be at least ₹5"
                                    }
                                    participants.isEmpty() -> {
                                        errorMessage = "At least one participant is required"
                                    }
                                    // Discount if non-blank must be between 0.0 and 99.99
                                    discount.value.isNotBlank() &&
                                            (discountValue < 0.0 || discountValue > 99.99) -> {
                                        errorMessage = "Discount must be between 0.0% and 99.99%"
                                    }
                                    // GST if non-blank must be between 0.1 and 99.99
                                    gst.value.isNotBlank() &&
                                            (gstValue < 0.1 || gstValue > 99.99) -> {
                                        errorMessage = "GST must be between 0.1% and 99.99%"
                                    }
                                    else -> {
                                        // Passed all validation!
                                        val discounted = amount - (amount * discountValue / 100)
                                        val final = discounted + (discounted * gstValue / 100)

                                        viewModel.addGroup(
                                            name = groupName.trim(),
                                            participants = participants,
                                            title = title.trim(),
                                            amountPaid = final,
                                            paidBy = paidBy.value.trim()
                                                .ifEmpty { participants.firstOrNull() ?: "Unknown" },
                                            date = date.value.trim(),
                                            discount = discountValue,
                                            gst = gstValue
                                        )

                                        // Clear form
                                        groupName = ""
                                        participantsText = ""
                                        title = ""
                                        amountPaidText = ""
                                        paidBy.value = ""
                                        date.value = ""
                                        discount.value = ""
                                        gst.value = ""
                                        errorMessage = null

                                        // Show “Group created” Snackbar
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Group created")
                                        }

                                        // Animate green checkmark at CENTER
                                        showCreatedIcon = true
                                        scope.launch {
                                            delay(1500L)
                                            showCreatedIcon = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Add Group")
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                } else {
                    // ─── View Groups List ───
                    Text("All Groups", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    LazyColumn {
                        items(groupList) { group ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(group.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                                        Row {
                                            IconButton(onClick = {
                                                expandedGroupId =
                                                    if (expandedGroupId == group.id) null else group.id
                                            }) {
                                                Icon(
                                                    imageVector = if (expandedGroupId == group.id)
                                                        Icons.Filled.KeyboardArrowUp
                                                    else
                                                        Icons.Filled.KeyboardArrowDown,
                                                    contentDescription = "Toggle Details"
                                                )
                                            }
                                            IconButton(onClick = {
                                                viewModel.deleteGroup(group)

                                                // Show “Group deleted” Snackbar
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Group deleted")
                                                }

                                                // Animate red cross at CENTER
                                                showDeletedIcon = true
                                                scope.launch {
                                                    delay(1500L)
                                                    showDeletedIcon = false
                                                }
                                            }) {
                                                Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = "Delete Group"
                                                )
                                            }
                                        }
                                    }

                                    if (expandedGroupId == group.id) {
                                        Spacer(Modifier.height(8.dp))
                                        Text("Title: ${group.title}", fontWeight = FontWeight.SemiBold)
                                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                                        Spacer(Modifier.height(4.dp))
                                        Text("Participants:", fontWeight = FontWeight.SemiBold)
                                        group.participants.forEach { participant ->
                                            Text(
                                                text = "- $participant",
                                                fontWeight = if (participant == group.paidBy)
                                                    FontWeight.Bold else FontWeight.Normal,
                                                color = if (participant == group.paidBy)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.error
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Divider(color = Color.Gray.copy(alpha = 0.3f))
                                        Spacer(Modifier.height(8.dp))

                                        Text("Payment Info:", fontWeight = FontWeight.SemiBold)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Paid By: ${group.paidBy}",
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text("Date: ${group.date}")
                                        Text("Discount: ${group.discount}%")
                                        Text("GST: ${group.gst}%")
                                        Spacer(Modifier.height(4.dp))
                                        Divider(color = Color.Gray.copy(alpha = 0.3f))

                                        Text(
                                            "Final Amount (After Discount & GST): ₹%.2f".format(group.amountPaid),
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (group.participants.isNotEmpty()) {
                                            val split = group.amountPaid / group.participants.size
                                            Text("Split per person: ₹%.2f".format(split))
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Divider(color = Color.Gray.copy(alpha = 0.3f))
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }

            // ─── Centered Animated Icons ───
            AnimatedVisibility(
                visible = showCreatedIcon,
                modifier = Modifier.align(Alignment.Center),
                enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                exit = scaleOut(animationSpec = tween(300)) + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Created",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(96.dp)
                )
            }

            AnimatedVisibility(
                visible = showDeletedIcon,
                modifier = Modifier.align(Alignment.Center),
                enter = scaleIn(animationSpec = tween(300)) + fadeIn(),
                exit = scaleOut(animationSpec = tween(300)) + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Deleted",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(96.dp)
                )
            }
        }
    }
}
