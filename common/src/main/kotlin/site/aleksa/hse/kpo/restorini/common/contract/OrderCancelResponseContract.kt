package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order cancel Response Client-Server communication contract
 */
@Serializable
class OrderCancelResponseContract : Contract() {
    override var contract = OrderCancelResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
}