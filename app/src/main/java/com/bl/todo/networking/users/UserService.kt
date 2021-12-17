package com.bl.todo.networking.users

import com.bl.todo.data.models.FirebaseUser
import com.bl.todo.networking.RetroFitClient
import com.bl.todo.ui.wrapper.UserDetails
import com.google.firebase.firestore.auth.User

object UserService {
    private val retrofit = RetroFitClient.createRetroFit()
    private val usersApi = retrofit.create(UsersApi::class.java)

    suspend fun getUsers(): ArrayList<FirebaseUser> {
        val usersApi = retrofit.create(UsersApi::class.java)
        val usersResponse = usersApi.getUsers()
        val userList: ArrayList<FirebaseUser> = ArrayList(usersResponse.documents.map {
            FirebaseUser(
                it.fields.userName.stringValue,
                it.fields.email.stringValue,
                it.fields.phone.stringValue)
        })
        return userList
    }

    suspend fun getUser(fUid: String): UserDetails {

        val userResponse = usersApi.getUser(fUid)
        val fields = userResponse.fields
        return UserDetails(
            userName = fields.userName.stringValue,
            phone = fields.phone.stringValue,
            email = fields.email.stringValue,
            fUid = fUid
        )
    }

    suspend fun addUser(user: UserDetails) {
        val userFields = UserFields(
            userName = StringField(user.userName),
            email = StringField(user.email),
            phone = StringField(user.phone)
        )
        val userDocument = AddUserDocument(
            userFields
        )
        user.fUid?.let { usersApi.addUser(it, userDocument) }
    }
}