package com.ilieinc.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilieinc.core.ui.theme.AppTypography

@Composable
fun ThemedCard(
    title: String,
    modifier: Modifier = Modifier,
    titleBar: (@Composable RowScope.() -> Unit)? = null,
    body: @Composable () -> Unit
) {
    Card(
        modifier
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                titleBar?.invoke(this) ?: Text(
                    text = title,
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            body()
        }
    }
}

@Preview
@Composable
fun ActionCardPreview() {
    ThemedCard("Test") {
    }
}