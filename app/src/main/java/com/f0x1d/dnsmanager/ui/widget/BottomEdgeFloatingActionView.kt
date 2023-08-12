package com.f0x1d.dnsmanager.ui.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.BottomEdgeFloatingActionView(
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    FloatingActionButton(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .navigationBarsPadding(),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null
        )
    }
}