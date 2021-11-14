package com.bl.todo.data.wrapper

import com.bl.todo.authService.Authentication

data class UserDetails
    (var userName : String,
     var email : String,
     var phone : String,
     var loginStatus : Boolean = Authentication.getCurrentUser() != null,
     var uid : Long = 0L,
     var fUid : String?,
     ){

}
