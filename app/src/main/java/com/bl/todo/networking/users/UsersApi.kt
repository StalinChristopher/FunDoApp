package com.bl.todo.networking.users

import retrofit2.http.*

interface UsersApi {

    @GET("projects/todoapp-1898d/databases/(default)/documents/users")
    suspend fun getUsers(): FirebaseUsersResponse

    @GET("projects/todoapp-1898d/databases/(default)/documents/users/{fUid}")
    suspend fun getUser(@Path("fUid") fUid: String) : UserDocument

    @POST("projects/todoapp-1898d/databases/(default)/documents/users")
    suspend fun addUser(@Query("documentId") userId: String, @Body userDocument: AddUserDocument)
}