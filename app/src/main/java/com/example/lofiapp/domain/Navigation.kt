package com.example.lofiapp.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lofiapp.R
import com.example.lofiapp.data.ScreenRoutes
import com.example.lofiapp.screens.CreatePlaylistScreen
import com.example.lofiapp.screens.EditPlaylistScreen
import com.example.lofiapp.screens.HomeScreen
import com.example.lofiapp.screens.SearchScreen
import com.example.lofiapp.screens.VideoScreen
import com.example.lofiapp.screens.PlaylistScreen
import com.example.lofiapp.screens.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.SplashScreen.route
    ) {
        composable(route = ScreenRoutes.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(route = ScreenRoutes.HomeScreen.route) { // home screen
            HomeScreen(navController)
        }
        composable(route = ScreenRoutes.SearchScreen.route) {// search screen
            SearchScreen(navController = navController)
        }
        composable( // video screen
            route = "video_screen/{video_id}",
            arguments = listOf(
                navArgument("video_id") {type = NavType.StringType}
            )) { backStackEntry ->
                val video_id = backStackEntry.arguments?.getString("video_id").toString()
            VideoScreen(navController = navController, video_id = video_id)
        }
        composable(route = ScreenRoutes.PlaylistScreen.route) {// Playlist screen
            PlaylistScreen(navController = navController)
        }
        composable(route = ScreenRoutes.EditPlaylistScreen.route) {// Edit playlist screen
            EditPlaylistScreen(navController = navController)
        }
        composable(route = ScreenRoutes.CreatePlaylistScreen.route) {// Edit playlist screen
            CreatePlaylistScreen(navController = navController)
        }
    }
}

