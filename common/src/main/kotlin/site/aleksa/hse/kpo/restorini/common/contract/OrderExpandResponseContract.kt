package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Expand Response Client-Server communication contract
 */
@Serializable
class OrderExpandResponseContract : Contract() {
    override var contract = OrderExpandResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var orderId: Int = -1
    var menuItemId: Int = -1
}