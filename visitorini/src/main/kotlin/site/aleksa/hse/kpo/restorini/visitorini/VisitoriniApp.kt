package site.aleksa.hse.kpo.restorini.visitorini

import site.aleksa.hse.kpo.restorini.visitorini.tools.VisitoriniSocket
import site.aleksa.hse.kpo.restorini.visitorini.tools.Visitorini

/**
 * 'Visitorini' application main function
 */
fun main() {
    println("This is 'Visitorini' program instance. PID = ${ProcessHandle.current().pid()}")
    VisitoriniSocket.initializeClientSocket()

    print("y-Register, N-(or empty)-Login? (y/N): ")
    val s = readln()
    if (s.uppercase() == "Y") {
        Visitorini.performSignup()
    } else {
        Visitorini.performSignin()
    }

    Visitorini.run()
}