package com.f0x1d.dnsmanager.ui.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.f0x1d.dnsmanager.R
import com.f0x1d.dnsmanager.database.entity.DNSItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.DNSItem(
    item: DNSItem,
    selectedHost: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit
) {
    val color = if (item.host == selectedHost)
        MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondaryContainer

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .animateItemPlacement(),
        border = BorderStroke(2.dp, color)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)
            ) {
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = item.host,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.size(10.dp))

            Column(modifier = Modifier.padding(end = 5.dp)) {
                Spacer(modifier = Modifier.size(5.dp))

                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.size(5.dp))
            }
        }
    }
}