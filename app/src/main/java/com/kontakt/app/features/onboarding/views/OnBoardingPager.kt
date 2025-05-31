package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        /* ---------- Páginas ---------- */
        HorizontalPager(
            count = items.size,          // 👈 nuevo parámetro obligatorio
            state = pagerState
        ) { index ->
            val page = items[index]

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                /* Animación Lottie */
                val compositionResult = rememberLottieComposition(
                    LottieCompositionSpec.RawRes(page.animRes)
                )
                LottieAnimation(
                    compositionResult.value,      // 👈 se usa .value
                    iterations = LottieConstants.IterateForever,
                    modifier   = Modifier.size(180.dp)
                )
                Spacer(Modifier.height(32.dp))

                /* Texto */
                Text(page.titulo, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text(
                    page.descripcion,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        /* Indicadores */
        PagerIndicator(pagerState, Modifier.padding(8.dp))

        /* Botón Final */
        if (pagerState.currentPage == items.lastIndex) {
            ButtonFinish(onFinish, Modifier.padding(bottom = 24.dp))
        } else {
            Spacer(Modifier.height(56.dp)) // reserva espacio
        }
    }
}