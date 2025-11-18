package com.app.chatbot.utils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SafeArea(content : @Composable ()-> Unit) {
    Box(
        modifier = Modifier.fillMaxHeight().fillMaxWidth()
            .background(Color.White)
            .systemBarsPadding()
            .imePadding()
    ) {
        content()
    }
}