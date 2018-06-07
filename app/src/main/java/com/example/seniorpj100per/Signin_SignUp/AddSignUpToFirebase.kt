package com.example.seniorpj100per.Signin_SignUp

/**
 * Created by Smew on 28/1/2561.
 */

class AddSignUpToFirebase(val username: String, val password: String,
                           val email: String, val age: String, val gender: String) {
    constructor() : this("", "", "", "", "")
}