package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Create Request Client-Server communication contract
 */
@Serializable
class OrderCreateRequestContract(val visitorId: Int, val menuItemIds: MutableList<Int>) : Contract() {
    override var contract = OrderCreateRequestContract::class.simpleName.toString()
//    var visitorId: Int = visitorId
//    var orderItems: MutableList<Int> = mutableListOf()
}