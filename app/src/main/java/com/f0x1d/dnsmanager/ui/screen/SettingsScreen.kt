package com.f0x1d.dnsmanager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.store.datastore.SettingsDataStore
import com.f0x1d.dnsmanager.ui.widget.NavigationBackIcon
import com.f0x1d.dnsmanager.viewmodel.SettingsViewModel
import com.jamal.composeprefs3.ui.PrefsScreen
import com.jamal.composeprefs3.ui.prefs.SwitchPref

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val viewModel = hiltViewModel<SettingsViewModel>()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Column {
        LargeTopAppBar(
            title = { Text(text = stringResource(id = R.string.settings)) },
            navigationIcon = { NavigationBackIcon(navController = navController) },
            scrollBehavior = scrollBehavior
        )

        Box(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current

            PrefsScreen(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                dataStore = viewModel.settingsDataStore.dataStore
            ) {
                prefsGroup(title = context.getString(R.string.dns_mode)) {
                    prefsItem {
                        SwitchPref(
                            key = SettingsDataStore.SKIP_AUTO_MODE_KEY.name,
                            title = stringResource(id = R.string.skip_auto_mode)
                        )
                    }
                }
            }
        }
    }
}