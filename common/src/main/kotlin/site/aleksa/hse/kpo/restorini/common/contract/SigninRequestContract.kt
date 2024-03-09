package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Signin Request Client-Server communication contract
 */
@Serializable
class SigninRequestContract : Contract() {
    override var contract = SigninRequestContract::class.simpleName.toString()
    var email: String = ""
    var password: String = ""
}