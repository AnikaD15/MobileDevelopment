package hu.ait.bookexchange.data

import java.io.Serializable

data class User(
    var user_id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: Int = 0,
    var school: String = ""
):Serializable