package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Order Show Request Client-Server communication contract
 */
@Serializable
class OrderShowRequestContract(val visitorId: Int) : Contract() {
    override var contract = OrderShowRequestContract::class.simpleName.toString()
}