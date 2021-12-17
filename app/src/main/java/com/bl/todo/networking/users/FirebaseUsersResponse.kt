package com.bl.todo.networking.users

data class FirebaseUsersResponse(
    val documents: ArrayList<UserDocument>
)

data class UserDocument(
    val name: String,
    val fields: UserFields,
    val createTime: String,
    val updateTime: String
)

data class UserFields(
    val email: StringField,
    val phone: StringField,
    val userName: StringField
)

data class StringField(
    val stringValue: String
)

/*

{
    "documents": [
        {
            "name": "projects/todoapp-1898d/databases/(default)/documents/users/BPo7CGzI8LS3ctp2Ude73UW3j9n1",
            "fields": {
                "email": {
                    "stringValue": "test14@gmail.com"
                },
                "phone": {
                    "stringValue": "1234567890"
                },
                "userName": {
                    "stringValue": "test13"
                }
            },
            "createTime": "2021-11-18T17:15:08.569707Z",
            "updateTime": "2021-11-18T17:15:08.569707Z"
        },
        {
            "name": "projects/todoapp-1898d/databases/(default)/documents/users/JN6ASKou9UgL2cZ5Jv0Ij4RhJul2",
            "fields": {
                "phone": {
                    "stringValue": "9874563210"
                },
                "email": {
                    "stringValue": "test99@gmail.com"
                },
                "userName": {
                    "stringValue": "test99"
                }
            },
            "createTime": "2021-11-30T09:26:54.523455Z",
            "updateTime": "2021-11-30T09:26:54.523455Z"
        },
        {
            "name": "projects/todoapp-1898d/databases/(default)/documents/users/gQ1CYbk5u5a9Vu23la8NrcD0w7J3",
            "fields": {
                "userName": {
                    "stringValue": "test120"
                },
                "email": {
                    "stringValue": "test120@gmail.com"
                },
                "phone": {
                    "stringValue": "1234567890"
                }
            },
            "createTime": "2021-11-21T07:58:09.531532Z",
            "updateTime": "2021-11-21T07:58:09.531532Z"
        }
    ]
}

 */