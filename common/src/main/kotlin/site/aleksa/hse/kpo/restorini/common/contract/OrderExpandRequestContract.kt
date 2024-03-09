package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Expand Request Client-Server communication contract
 */
@Serializable
class OrderExpandRequestContract(val orderId: Int, val menuItemIds: MutableList<Int>) : Contract() {
    override var contract = OrderExpandRequestContract::class.simpleName.toString()
}