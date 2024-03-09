package site.aleksa.hse.kpo.restorini.common.contract

import kotlinx.serialization.Serializable

/**
 * Abstract Client-Server communication contract
 */
@Serializable
abstract class Contract {
    abstract var contract: String
}