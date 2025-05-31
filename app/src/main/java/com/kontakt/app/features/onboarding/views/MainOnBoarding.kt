package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kontakt.app.R
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.data.PageData
import com.kontakt.app.data.local.datastore.StoreBoarding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainOnBoarding(
    nav: NavController,
    store: StoreBoarding
) {
    val pages = listOf(
        PageData(R.raw.agenda  , "Agenda"     , "Organize your agenda"),
        PageData(R.raw.contact , "Contact"   , "Create contacts easily"),
        PageData(R.raw.contacts, "Directory" , "Access your contacts"),
        PageData(R.raw.search  , "Search"   , "Find whoever you need")
    )

    val pagerState = rememberPagerState()
    val scope      = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        /* Nombre de la app */
        Text(
            "KontaKt",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 48.dp)
        )

        /* Páginas */
        OnBoardingPager(
            items      = pages,
            pagerState = pagerState,
            modifier   = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 16.dp),
            onFinish = {
                scope.launch { store.setDone(nav.context) }
                nav.navigate(NavRoutes.HOME) {
                    popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
                }
            }
        )
    }
}