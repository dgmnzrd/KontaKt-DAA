package com.kontakt.app.features.onboarding.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.kontakt.app.R
import com.kontakt.app.core.navigation.NavRoutes
import com.kontakt.app.data.PageData
import com.kontakt.app.data.local.datastore.StoreBoarding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

// … imports sin cambios …
@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainOnBoarding(
    nav: NavController,
    store: StoreBoarding
) {
    val pages = listOf(
        PageData(R.raw.agenda  , "Agenda"     , "Organiza tus citas"),
        PageData(R.raw.contact , "Contacto"   , "Crea contactos fácilmente"),
        PageData(R.raw.contacts, "Directorio" , "Accede a tus contactos"),
        PageData(R.raw.search  , "Búsqueda"   , "Encuentra a quien necesites")
    )

    /* 🔄  rememberPagerState ya no recibe pageCount */
    val pagerState = rememberPagerState(initialPage = 0)
    val scope      = rememberCoroutineScope()

    OnBoardingPager(
        items      = pages,
        pagerState = pagerState,
        modifier   = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5FA)),
        onFinish = {
            scope.launch { store.setDone(nav.context) }
            nav.navigate(NavRoutes.HOME) {
                popUpTo(NavRoutes.ONBOARDING) { inclusive = true }
            }
        }
    )
}