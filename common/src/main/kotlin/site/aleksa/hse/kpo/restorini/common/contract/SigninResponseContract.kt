package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Signin Response Client-Server communication contract
 */
@Serializable
class SigninResponseContract : Contract() {
    override var contract = SigninResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var visitorId: Int = -1
    var orderId: Int = -1
}