package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.getValue
import com.airbnb.lottie.compose.*
import com.google.accompanist.pager.*
import com.kontakt.app.data.PageData

@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingPager(
    items: List<PageData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomCenter
    ) {

        /* ----- Páginas animadas ----- */
        HorizontalPager(
            state = pagerState,
            count = items.size
        ) { index ->
            val page = items[index]

            Column(
                Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val comp by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(page.animRes)
                )
                LottieAnimation(
                    composition = comp,
                    iterations  = LottieConstants.IterateForever,
                    modifier    = Modifier.size(200.dp)
                )
                Spacer(Modifier.height(32.dp))

                /* ------- Texto de la página ------- */
                Text(
                    page.titulo,
                    style = MaterialTheme.typography.displaySmall
                        .copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    page.descripcion,
                    style = MaterialTheme.typography.titleMedium,      // Montserrat 18 sp
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        /* ----- Indicadores y botón ----- */
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            PagerIndicator(pagerState)
            if (pagerState.currentPage == items.lastIndex) {
                ButtonFinish(onFinish, Modifier
                    .padding(vertical = 20.dp, horizontal = 24.dp))
            }
        }
    }
}