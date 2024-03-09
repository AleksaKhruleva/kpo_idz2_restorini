package site.aleksa.hse.kpo.restorini.serverini.tools

import kotlinx.serialization.json.Json
import site.aleksa.hse.kpo.restorini.common.contract.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * Server socket functions
 */
object ServeriniSocket {
    fun initializeServerSocket() {
        val serveriniDefaultPort = 50107
        print("Enter UDP service port number (or press Enter to use default $serveriniDefaultPort): ")
        val portStr = readln()
        val serverPortNumber = portStr.toIntOrNull()
        try {
            val serverSocket = DatagramSocket(serverPortNumber ?: serveriniDefaultPort)
            println("Bound to UDP port: ${serverSocket.localPort}")
            serverSocketThread(serverSocket)
        } catch (ex: Exception) {
            println(ex.message ?: "")
            exitProcess(0)
        }
    }

    private fun serverSocketThread(serverSocket: DatagramSocket) {
        thread {
            println("Server port thread started...")
            while (true) {
                val dataArray = ByteArray(65536)
                val datagramPacket = DatagramPacket(dataArray, dataArray.size)
                serverSocket.receive(datagramPacket)
                clientSocketHandler(serverSocket, datagramPacket, datagramPacket.data.copyOf(datagramPacket.length))
            }
        }
    }

    private fun clientSocketHandler(
        serverSocket: DatagramSocket,
        datagramPacket: DatagramPacket,
        dataArray: ByteArray
    ) {
        thread {
            val json = Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            }
            if (ServeriniState.trace) println("${{}.javaClass.enclosingMethod.name}(thread-id=${Thread.currentThread().id}) started")
            try {
                val jsonString = dataArray.toString(Charsets.UTF_8)
                val request = json.decodeFromString<CommonContract>(jsonString)
                if (ServeriniState.trace) println("${{}.javaClass.enclosingMethod.name}(thread-id=${Thread.currentThread().id}) contract=${request.contract}")
                when (request.contract) {
                    MenuRequestContract::class.simpleName.toString() -> Serverini.handleMenuRequest(
                        serverSocket,
                        datagramPacket
                    )

                    SignupRequestContract::class.simpleName.toString() -> Serverini.handleSignupRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    SigninRequestContract::class.simpleName.toString() -> Serverini.handleSigninRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    OrderCreateRequestContract::class.simpleName.toString() -> Serverini.handleOrderCreateRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    OrderShowRequestContract::class.simpleName.toString() -> Serverini.handleOrderShowRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    OrderExpandRequestContract::class.simpleName.toString() -> Serverini.handleOrderExpandRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    OrderPayRequestContract::class.simpleName.toString() -> Serverini.handleOrderPayRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    OrderCancelRequestContract::class.simpleName.toString() -> Serverini.handleOrderCancelRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    ReviewRequestContract::class.simpleName.toString() -> Serverini.handleReviewRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )

                    OrderRequestContract::class.simpleName.toString() -> Serverini.handleOrderRequest(
                        serverSocket,
                        datagramPacket,
                        dataArray
                    )
                }
            } catch (ex: Exception) {
                println("Client Thread (id=${Thread.currentThread().id}) - broken!!!")
                println(ex.message ?: "")
            }
            if (ServeriniState.trace) println("${{}.javaClass.enclosingMethod.name}(thread-id=${Thread.currentThread().id}) finished")
        }
    }
}
