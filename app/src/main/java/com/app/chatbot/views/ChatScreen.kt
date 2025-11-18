package com.app.chatbot.views

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.chatbot.R
import com.app.chatbot.utils.SafeArea
import com.app.chatbot.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScren(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val message = remember { mutableStateOf("") }
    val chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val chatMessages = chatViewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.value.size) {
        if (chatMessages.value.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Log.d("ChatScreen", "ChatScreen: ${chatMessages.value} messages")

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            navHostController.popBackStack()
                        }) {
                            Image(
                                painter = painterResource(R.drawable.back),
                                contentDescription = "Back Button"
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Chatbot", color = Color.White, fontSize = 24.sp)
                    }
                }
            )
        }
    ) { _ ->
        SafeArea {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    state = listState,
                    reverseLayout = true
                ) {
                    items(chatMessages.value.size) { index ->
                        val msg = chatMessages.value[chatMessages.value.size - 1 - index]
                        if (msg.side == "right") {
                            RightMessage(text = msg.text, timestamp = msg.timeStamp)
                        } else {
                            LeftMessage(text = msg.text, isLoading = msg.isLoading, timestamp = msg.timeStamp)
                        }
                    }
                }
                Box(modifier = Modifier.padding(horizontal = 5.dp,)) {
                    BasicTextField(
                        value = message.value,
                        onValueChange = { message.value = it },
                        maxLines = 1,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(
                                shape = MaterialTheme.shapes.extraLarge,
                                width = 1.dp,
                                color = Color.Black
                            )
                            .background(
                                color = Color.Black,
                                shape = MaterialTheme.shapes.extraLarge
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if(message.value.isNotEmpty()) {
                                    chatViewModel.sendMessage(message.value)
                                    message.value = ""
                                }
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween

                            ) {
                                if (message.value.isEmpty()) {
                                    Text(
                                        text = "Write message here..",
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                                IconButton(onClick = {
                                    if(message.value.isNotEmpty()) {
                                        chatViewModel.sendMessage(message.value)
                                        message.value = ""
                                    }
                                },modifier= Modifier.size(30.dp)) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    )
                }

            }
        }
    }
}


@Composable
fun RightMessage(text: String,timestamp: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Column (
            modifier = Modifier
                .background(Color(0xFF005C4B), MaterialTheme.shapes.medium)
                .padding(12.dp)
                .fillMaxWidth(0.75f)
        ) {
            Text(text = text, color = Color.White)
            Text(text = formatTimestamp(timestamp), color = Color.White, fontSize = 10.sp)
        }
    }
}

@Composable
fun LeftMessage(text: String, isLoading: Boolean,timestamp: Long) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .background(Color(0xFF1F1F1F), MaterialTheme.shapes.medium)
                .padding(12.dp)
                .fillMaxWidth(0.75f)
        ) {
            Text(text = if(isLoading) "Analyzing..." else text, color = Color.White)
            if(!isLoading)
            Text(text = formatTimestamp(timestamp), color = Color.White, fontSize = 10.sp)
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

