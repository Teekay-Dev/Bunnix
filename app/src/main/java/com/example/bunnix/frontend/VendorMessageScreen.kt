<<<<<<< HEAD

=======
package com.example.bunnix.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.bunnix.model.ChatSummary
import com.example.bunnix.model.Message
import com.example.bunnix.model.VendorViewModel

@Composable
fun VendorMessageScreen(
    navController: NavController,
    viewModel: VendorViewModel = viewModel()
) {
    val conversations by viewModel.conversations

    Scaffold(
        topBar = {
            Column(Modifier.background(Color.White).padding(16.dp)) {
                Text("Messages", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                // Search bar to find specific customers
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search customers...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF3F3F3))
                )
            }
        },
        bottomBar = { BunnixBottomNavigation(navController) }
    ) { padding ->
        if (conversations.isEmpty()) {
            EmptyStateView("No messages yet")
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(conversations) { chat ->
                    ConversationItem(chat = chat) {
                        navController.navigate("chat_detail/${chat.customerId}")
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    chat: ChatSummary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(Modifier.size(50.dp), shape = CircleShape, color = Color.LightGray) {
            Icon(Icons.Default.Person, null, Modifier.padding(10.dp), tint = Color.White)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                chat.customerName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
            Text(
                chat.lastMessage,
                color = Color.Gray,
                maxLines = 1,
                fontSize = 14.sp)
        }
        Text(
            chat.timestamp,
            color = Color.Gray,
            fontSize = 12.sp)
    }
}

@Composable
fun ChatBubble(message: Message, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) Color(0xFFF2711C) else Color(0xFFE9E9E9),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = if (isMe) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun ChatDetailScreen(customerId: String, viewModel: VendorViewModel) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.chatMessages

    LaunchedEffect(customerId) {
        viewModel.listenForMessages(customerId)
        viewModel.fetchMessages(customerId)
    }

    Column(Modifier.fillMaxSize()) {
        // 1. Messages Area
        LazyColumn(Modifier.weight(1f).padding(16.dp), reverseLayout = true) {
            items(messages) { msg ->
                ChatBubble(msg, isMe = msg.sender_id != customerId)
            }
        }

        // 2. Input Area
        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message") },
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                viewModel.sendMessage(customerId, messageText)
                messageText = ""
            }) {
                Icon(Icons.Default.Send, null, tint = Color(0xFFF2711C))
            }
        }
    }
}
>>>>>>> 3e8a2de235349208f7d0ce387a237c0a485cf30a
