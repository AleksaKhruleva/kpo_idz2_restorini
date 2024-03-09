package site.aleksa.hse.kpo.restorini.utilini

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import site.aleksa.hse.kpo.restorini.serverini.model.*
import java.time.*
import kotlin.system.exitProcess

/**
 * Delete existing Manager login
 */
fun main() {
    System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "OFF")

    try {
        Database.connect("jdbc:h2:file:./restorini", driver = "org.h2.Driver", user = "", password = "")

        println("Delete admin in progress...")

        print("Input login: ")
        val pass1 = readln()
        if (pass1.trim().isEmpty()) exitProcess(0)

        print("Repeat login: ")
        val pass2 = readln()
        if (pass2.trim().isEmpty()) exitProcess(0)

        if (pass1 != pass2) {
            println("\tLogins do not match!!!")
            exitProcess(0)
        }

        transaction {
            val i = AdminTable.update({AdminTable.login.eq(pass1)}) {
                it[toDate] = LocalDate.now()
            }
            println("Rows updated: $i")
        }
    } catch (ex: Exception) {
        println(ex.message)
    }
}
