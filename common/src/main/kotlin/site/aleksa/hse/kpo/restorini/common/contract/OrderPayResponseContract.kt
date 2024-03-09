package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Pay Response Client-Server communication contract
 */
@Serializable
class OrderPayResponseContract : Contract() {
    override var contract = OrderPayResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var orderId: Int = -1
}