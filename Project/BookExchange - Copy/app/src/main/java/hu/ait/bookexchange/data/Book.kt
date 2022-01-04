package hu.ait.bookexchange.data

import java.io.Serializable
import java.util.*

data class Book(
    var user_id: String = "",
    var author: String = "",
    var title: String = "",
//    var subject: String = "",
    var condition: Int = 0,
    var price: Float = 0f,
    var imgUrl: String = "",
    var isClaimed: Boolean = false,
    var claimedBy: String = "",
) : Serializable {

    companion object {
        const val NEW: Int = 0
        const val GOOD: Int = 1
        const val FAIR: Int = 2
        const val BAD: Int = 3
    }
}
