package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Signup Request Client-Server communication contract
 */
@Serializable
class SignupRequestContract : Contract() {
    override var contract = SignupRequestContract::class.simpleName.toString()
    var email: String = ""
    var password: String = ""
}