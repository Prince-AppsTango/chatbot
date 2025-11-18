package com.app.chatbot.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.chatbot.views.ChatScren
import com.app.chatbot.views.HomeScreen

@Composable
fun MainNavigation(modifier: Modifier = Modifier) {
 val navController = rememberNavController()
 NavHost(
     navController = navController,
     startDestination = "home_screen",
 ) {
  composable(route= "home_screen") {
      HomeScreen(navHostController = navController)
  }
     composable (route= "chat_screen") {
         ChatScren(navHostController = navController)
     }
 }
}