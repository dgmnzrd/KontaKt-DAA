package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerIndicator(pagerState: PagerState) {
    HorizontalPagerIndicator(
        pagerState   = pagerState,
        modifier     = Modifier
            .padding(bottom = 8.dp)
            .height(16.dp),
        activeColor   = MaterialTheme.colorScheme.secondary,
        inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .3f),
        indicatorWidth  = 10.dp,
        spacing        = 8.dp
    )
}