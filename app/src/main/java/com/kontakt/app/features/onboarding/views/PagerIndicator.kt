package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier   = modifier
            .fillMaxWidth()
            .padding(16.dp),
        activeColor   = MaterialTheme.colorScheme.primary,
        inactiveColor = MaterialTheme.colorScheme.surfaceVariant
    )
}