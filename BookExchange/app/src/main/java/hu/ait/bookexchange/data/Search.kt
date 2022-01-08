package hu.ait.bookexchange.data

data class Search(
    var author: String? = null,
    var title: String? = null,
    var minPrice: Float = 0f,
    var maxPrice: Float = Float.MAX_VALUE,
    var condition: Int = NONE,

){
    companion object {
        const val NEW: Int = 0
        const val GOOD: Int = 1
        const val FAIR: Int = 2
        const val BAD: Int = 3
        const val NONE: Int = 4
    }
}


