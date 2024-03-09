package site.aleksa.hse.kpo.restorini.serverini

import org.jetbrains.exposed.sql.Database
import site.aleksa.hse.kpo.restorini.common.util.CommonUtils
import site.aleksa.hse.kpo.restorini.serverini.tools.*
import site.aleksa.hse.kpo.restorini.serverini.utils.SampleDatabase
import java.io.File
import kotlin.system.exitProcess

/**
 * 'Serverini' application main function
 */
fun main() {
    System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "OFF")

    println("This is 'Serverini' program instance. PID = ${ProcessHandle.current().pid()}")
    try {
        val dbFile = File("./restorini.mv.db")
        if (!dbFile.exists()) {
            println("Database file does not exist.")
            println("Create new sample file.")
            println("Administrator login/password: q/q")
            SampleDatabase.create()
        }
        Database.connect("jdbc:h2:file:./restorini", driver = "org.h2.Driver", user = "", password = "")
    } catch (ex: Exception) {
        println("Database connection error: ${ex.message}")
        exitProcess(0)
    }

    ServeriniSocket.initializeServerSocket()

    Kitchen.cookingStartTimeout()
    Kitchen.restartInterruptedCookingThread()

    while (true) {
        CommonUtils.printSeparator()
        println("1. Statistics")
        println("2. Menu items Popularity")
        println("3. Menu items Rating")
        println("4. View last 5 reviews for menu items")
        println("t. Toggle trace output")
        println("q. Quit")
        println("0. Administrator space")
        print("Make your choice: ")
        when (readln().uppercase()) {
            "1" -> Serverini.statistics()
            "2" -> Serverini.popularity()
            "3" -> Serverini.rating()
            "4" -> Serverini.lastNreview(5)
            "T" -> ServeriniState.trace = !ServeriniState.trace
            "Q" -> break
            "0" -> Manager.login()
            else -> {
                CommonUtils.printSeparator()
                println("Wrong choice :(")
            }
        }
    }

    exitProcess(0)
}