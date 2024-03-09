package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Cancel Request Client-Server communication contract
 */
@Serializable
class OrderCancelRequestContract(val orderId: Int) : Contract() {
    override var contract = OrderCancelRequestContract::class.simpleName.toString()
}