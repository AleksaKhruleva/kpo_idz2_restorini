package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Review Response Client-Server communication contract
 */
@Serializable
class ReviewResponseContract : Contract() {
    override var contract = ReviewResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
}