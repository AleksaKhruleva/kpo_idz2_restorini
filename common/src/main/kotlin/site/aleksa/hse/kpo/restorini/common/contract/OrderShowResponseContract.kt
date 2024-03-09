package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable
import site.aleksa.hse.kpo.restorini.common.item.MenuItem

/**
 * Order Show Response Client-Server communication contract
 */
@Serializable
class OrderShowResponseContract : Contract() {
    override var contract = OrderShowResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var orderId: Int = -1
    var price: Int = -1
    var cotime: Int = -1
    var timele: Int = -1
    var created: String = ""
    var started: String = ""
    var finished: String = ""
    var payed: String = ""
    var menuItems: MutableList<MenuItem> = mutableListOf()
}