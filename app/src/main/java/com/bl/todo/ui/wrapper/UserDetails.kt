package com.bl.todo.ui.wrapper

import com.bl.todo.auth.service.FirebaseAuthentication

data class UserDetails
    (
    var userName: String,
    var email: String,
    var phone: String,
    var loginStatus: Boolean = FirebaseAuthentication.getCurrentUser() != null,
    var uid: Long = 0L,
    var fUid: String?,
)
