package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable
import site.aleksa.hse.kpo.restorini.common.item.ReviewItem

/**
 * Review Request Client-Server communication contract
 */
@Serializable
class ReviewRequestContract : Contract() {
    override var contract: String = ReviewRequestContract::class.simpleName.toString()
    var reviewItems: MutableList<ReviewItem> = mutableListOf()
}