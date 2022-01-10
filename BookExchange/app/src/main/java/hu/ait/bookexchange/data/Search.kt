package hu.ait.bookexchange.data

import java.io.Serializable

data class Search(
    var author: String = "",
    var title: String = "",
    var minPrice: Float = 0f,
    var maxPrice: Float = Float.MAX_VALUE,
    var condition: Int = NONE,

) : Serializable
{
    companion object {
        const val NEW: Int = 0
        const val GOOD: Int = 1
        const val FAIR: Int = 2
        const val BAD: Int = 3
        const val NONE: Int = 4
    }
}


