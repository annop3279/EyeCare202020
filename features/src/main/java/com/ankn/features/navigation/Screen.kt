package com.ankn.features.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.ankn.features.R

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int? = R.string.home,
    @DrawableRes val drawableId: Int? = R.drawable.ic_home,
) {
    object Home : Screen("home_screen")
    object Setting : Screen("setting_screen")

/*    object Shop : Screen("shop", R.string.shop, R.drawable.shop)
    object Explore : Screen("explore", R.string.explore, R.drawable.explore)
    object Cart : Screen("cart", R.string.cart, R.drawable.cart)
    object Favourite : Screen("favourite", R.string.favourite, R.drawable.outline_favorite_border)
    object Account : Screen("account", R.string.account, R.drawable.account)*/

}
