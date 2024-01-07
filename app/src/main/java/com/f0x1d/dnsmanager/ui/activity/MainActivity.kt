package com.f0x1d.dnsmanager.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.f0x1d.dnsmanager.model.navigation.Screen
import com.f0x1d.dnsmanager.ui.screen.CreateDNSItemScreen
import com.f0x1d.dnsmanager.ui.screen.DNSListScreen
import com.f0x1d.dnsmanager.ui.screen.SettingsScreen
import com.f0x1d.dnsmanager.ui.screen.SetupScreen
import com.f0x1d.dnsmanager.ui.theme.DNSManagerTheme
import com.f0x1d.dnsmanager.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            DNSManagerTheme {
                Surface(modifier = Modifier.imePadding()) {
                    val permissionGranted by viewModel.permissionGranted.collectAsStateWithLifecycle(initialValue = true)

                    Crossfade(targetState = permissionGranted, label = "Setup") {
                        if (!it) {
                            SetupScreen()
                        } else {
                            val navController = rememberNavController()

                            NavHost(
                                navController = navController,
                                startDestination = Screen.DNSList.route
                            ) {
                                composable(
                                    route = Screen.DNSList.route,
                                    enterTransition = { fadeIn() },
                                    exitTransition = { fadeOut() }
                                ) {
                                    DNSListScreen(navController = navController)
                                }

                                composable(
                                    route = Screen.CreateDNSItem.route,
                                    enterTransition = { fadeIn() },
                                    exitTransition = { fadeOut() }
                                ) {
                                    CreateDNSItemScreen(navController = navController)
                                }

                                composable(
                                    route = "${Screen.CreateDNSItem.route}/{id}",
                                    arguments = listOf(
                                        navArgument("id") { type = NavType.LongType }
                                    ),
                                    enterTransition = { fadeIn() },
                                    exitTransition = { fadeOut() }
                                ) {
                                    CreateDNSItemScreen(navController = navController)
                                }

                                composable(
                                    route = Screen.Settings.route,
                                    enterTransition = { fadeIn() },
                                    exitTransition = { fadeOut() }
                                ) {
                                    SettingsScreen(navController = navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}