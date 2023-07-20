package com.example.signalclone.ui.screens.main_screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.signalclone.ui.screens.nav_screen.BottomNavScreens
import com.example.signalclone.ui.theme.ThemeColor

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {

    object Home : BottomNavItem("Feed", Icons.Outlined.DynamicFeed, BottomNavScreens.PostsScreen.route)
    object Favourite : BottomNavItem("Profile", Icons.Outlined.PersonOutline, BottomNavScreens.ProfileScreen.route)

}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Favourite
)


@Composable
fun MainScreenBottomNav(
    navController: NavController
) {
    BottomNavigation(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
//            .padding(20.dp)
//            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(20.dp))
//            .graphicsLayer {
//                shape = RoundedCornerShape(20.dp)
//                clip = true
//            }
        ,
        elevation = 20.dp,
        backgroundColor = MaterialTheme.colorScheme.background
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        tint = if(isSelected) ThemeColor else Color.LightGray,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                selectedContentColor = ThemeColor,
                unselectedContentColor = Color.Gray,
                selected = isSelected,
                alwaysShowLabel = false,
                onClick = {

                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            //used pop up to avoid stack in bottom navigation
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}