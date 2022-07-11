package com.felipe.appExample.client

import com.soywiz.klock.Time
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class Structures {

    @Serializable
    data class User(
        @SerialName("id")
        val id: UserIdentifier?,
        @SerialName("name")
        val name: String?,
        @SerialName("birthdate")
        val birthdate: String?
    )
}

typealias UserIdentifier = Int?