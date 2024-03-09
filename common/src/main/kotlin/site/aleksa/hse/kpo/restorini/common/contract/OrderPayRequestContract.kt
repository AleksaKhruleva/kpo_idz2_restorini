package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Pay Request Client-Server communication contract
 */
@Serializable
class OrderPayRequestContract(val orderId: Int) : Contract() {
    override var contract = OrderPayRequestContract::class.simpleName.toString()
}