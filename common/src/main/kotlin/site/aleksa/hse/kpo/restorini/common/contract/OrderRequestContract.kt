package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Request Client-Server communication contract
 */
@Serializable
class OrderRequestContract(val orderId: Int) : Contract() {
    override var contract = OrderRequestContract::class.simpleName.toString()
}