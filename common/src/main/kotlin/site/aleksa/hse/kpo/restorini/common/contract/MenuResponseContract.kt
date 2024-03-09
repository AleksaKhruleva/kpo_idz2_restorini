package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable
import site.aleksa.hse.kpo.restorini.common.item.MenuItem

/**
 * Menu Response Client-Server communication contract
 */
@Serializable
class MenuResponseContract : Contract() {
    override var contract = MenuResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var menuItems: MutableList<MenuItem> = mutableListOf()
}