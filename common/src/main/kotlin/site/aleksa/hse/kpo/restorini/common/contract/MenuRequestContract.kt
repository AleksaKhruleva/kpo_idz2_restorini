package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Menu Request Client-Server communication contract
 */
@Serializable
class MenuRequestContract : Contract() {
    override var contract: String = MenuRequestContract::class.simpleName.toString()
}