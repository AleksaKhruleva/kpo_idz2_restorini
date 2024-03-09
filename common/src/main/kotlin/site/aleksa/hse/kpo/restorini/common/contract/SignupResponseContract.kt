package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

@Serializable
class SignupResponseContract : Contract() {
    override var contract = SignupResponseContract::class.simpleName.toString()
    var completionCode: Int = -1
    var visitorId: Int = -1
}