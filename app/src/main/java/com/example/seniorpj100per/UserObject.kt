package com.example.seniorpj100per

/**
 * Created by Smew on 3/2/2561.
 */

object UserObject {
    private val user:UserProfile = UserProfile()

    fun getUser(): UserProfile {
        return user
    }
}
