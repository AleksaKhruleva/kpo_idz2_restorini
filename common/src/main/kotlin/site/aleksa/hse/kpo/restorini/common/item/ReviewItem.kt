package site.aleksa.hse.kpo.restorini.common.item

import kotlinx.serialization.Serializable

/**
 * Review Item data class
 */
@Serializable
data class ReviewItem(
    var id: Int,
    val orderId: Int,
    val menuItemId: Int,
    val rating: Int,
    val comment: String
)
