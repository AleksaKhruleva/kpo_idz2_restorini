package site.aleksa.hse.kpo.restorini.serverini.tools

/**
 * Server State object - contain statistics
 */
object ServeriniState {
    /**
     * Total cooking thread count started
     */
    var cookingThreadsStarted: Int = 0

    /**
     * Total cooking thread in 'cooking'-state count
     */
    var cookingThreadsCooking: Int = 0

    /**
     * Trace flag
     */
    var trace: Boolean = false

    /**
     * Cooking start timeout
     */
    var cookingStartTimeout: Int = 0
}
