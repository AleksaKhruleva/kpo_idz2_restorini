package site.aleksa.hse.kpo.restorini.serverini.tools

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import site.aleksa.hse.kpo.restorini.serverini.model.OrderTable
import java.time.Duration
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Kitchen object
 */
object Kitchen {
    fun cookingStartTimeout() {
        val cookingStartTimeoutDefault = 15
        print("Enter cooking start timeout (non-less 5 sec.) (or press Enter to use default $cookingStartTimeoutDefault sec.): ")
        val str = readln()
        if (str.isEmpty()) {
            ServeriniState.cookingStartTimeout = cookingStartTimeoutDefault
        } else {
            val cookingStartTimeout = str.toIntOrNull()
            try {
                if (cookingStartTimeout == null) throw Exception()
                if (cookingStartTimeout < 5) throw Exception()
            } catch (ex: Exception) {
                println("Invalid input. Default value (15 sec.) will be used.")
                ServeriniState.cookingStartTimeout = cookingStartTimeoutDefault
            }
        }
    }

    /**
     * Restart interrupted cooking thread for non-finished orders (only on startup 'Serverini' instance)
     */
    fun restartInterruptedCookingThread() {
        try {
            println("Restart interrupted Cooking Thread is in progress...")
            transaction {
                OrderTable.selectAll().where { OrderTable.finished.isNull() }.forEach {
                    thread {
                        cookingThread(it[OrderTable.id], false)
                    }
                    println("Interrupted order id=${it[OrderTable.id]} started.")
                }
            }
            println("Restart interrupted Cooking Threads - okay!")
        } catch (ex: Exception) {
            println("Restart interrupted Cooking Threads - fail!")
            println(ex.message)
            exitProcess(0)
        }
    }

    /**
     * Cooking thread function - imitation cooking process
     */
    fun cookingThread(orderId: Int, isNew: Boolean = true) {
        thread {
            var cookingExits = false
            synchronized(ServeriniState) {
                ServeriniState.cookingThreadsStarted += 1
            }
            if (ServeriniState.trace) println("${{}.javaClass.enclosingMethod.name}(thread-id=${Thread.currentThread().id}) started (order-id=$orderId is-new=$isNew)")
            try {
                var i = 1
                if (isNew) {
                    Thread.sleep(ServeriniState.cookingStartTimeout.toLong() * 1000)
                    transaction {
                        i = OrderTable.update({ OrderTable.id.eq(orderId) and OrderTable.started.isNull() }) {
                            it[started] = LocalDateTime.now()
                        }
                    }
                }
                if (i > 0) {
                    synchronized(ServeriniState) {
                        ServeriniState.cookingThreadsCooking += 1
                    }
                    cookingExits = true
                    while (true) {
                        var pro = 0
                        var cot = 0
                        var t1 = LocalDateTime.now()
                        val t2 = LocalDateTime.now()
                        var p1 = ""
                        transaction {
                            OrderTable.selectAll().where { OrderTable.id.eq(orderId) }.forEach {
                                cot = it[OrderTable.cotime]
                                t1 = it[OrderTable.started]
                                p1 = it[OrderTable.payed].toString().replace("null", "")
                            }
                            if (t1 == null) {
                                t1 = LocalDateTime.now()
                                OrderTable.update({ OrderTable.id.eq(orderId) and OrderTable.started.isNull() }) {
                                    it[started] = t1
                                }
                            }
                            val dur = Duration.between(t1, t2).toSeconds().toInt()
                            pro = cot - dur
                            if (pro < 1) pro = 0
                            OrderTable.update({ OrderTable.id.eq(orderId) }) {
                                it[timele] = pro
                            }
                        }
                        if (p1.isNotEmpty()) break
                        if (pro < 1) {
                            transaction {
                                OrderTable.update({ OrderTable.id.eq(orderId) }) {
                                    it[finished] = LocalDateTime.now()
                                }
                            }
                            break
                        }
                        if (ServeriniState.trace) println("${{}.javaClass.enclosingMethod.name}(thread-id=${Thread.currentThread().id}) cooking (progress=${cot - pro})")
                        Thread.sleep(1000)
                    }
                }
            } catch (ex: Exception) {
                println("Cooking Thread (id=${Thread.currentThread().id}) for order id=${orderId} - broken!!!")
                println(ex.message)
            }
            if (ServeriniState.trace) println("${{}.javaClass.enclosingMethod.name}(thread-id=${Thread.currentThread().id}) finished (order-id=$orderId is-new=$isNew)")
            synchronized(ServeriniState) {
                ServeriniState.cookingThreadsStarted -= 1
                if (cookingExits) ServeriniState.cookingThreadsCooking -= 1
            }
        }
    }
}
