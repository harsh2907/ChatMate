package com.example.signalclone.ui.screens.nav_screen

sealed class Screens(val route:String){

    object SignInScreen:Screens("Signin Screen")
    object SignUpScreen:Screens("Signup Screen")
    object MainScreen:Screens("Main Screen")

}


sealed class BottomNavScreens(val route:String){

    object PostsScreen:Screens("Posts Screen")
    object ProfileScreen:Screens("Profile Screen")

}
