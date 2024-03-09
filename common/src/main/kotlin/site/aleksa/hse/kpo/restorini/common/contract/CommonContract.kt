package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Common Client-Server communication contract
 */
@Serializable
class CommonContract : Contract() {
    override var contract: String = CommonContract::class.simpleName.toString()
}