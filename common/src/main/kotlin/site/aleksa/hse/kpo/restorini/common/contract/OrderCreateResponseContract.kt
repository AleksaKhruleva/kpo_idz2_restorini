package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Create Response Client-Server communication contract
 */
@Serializable
class OrderCreateResponseContract : Contract() {
    override var contract = OrderCreateResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var orderId: Int = -1
    var menuItemId: Int = -1
}