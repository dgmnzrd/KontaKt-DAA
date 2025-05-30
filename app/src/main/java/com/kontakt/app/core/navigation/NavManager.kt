package com.kontakt.app.core.navigation

object NavRoutes {
    const val ONBOARDING = "onboarding"
    const val HOME       = "home"
    const val ADD        = "add"
    const val EDIT       = "edit/{contactId}"
}

fun String.withId(id: Long) = replace("{contactId}", id.toString())