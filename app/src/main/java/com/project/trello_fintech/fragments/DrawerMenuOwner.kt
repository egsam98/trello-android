package com.project.trello_fintech.fragments

import com.google.android.material.navigation.NavigationView


/**
 * Любой UI элемент, имеющий свой набор меню на боковой панели
 */
interface DrawerMenuOwner {
    fun NavigationView.setupMenu()
}