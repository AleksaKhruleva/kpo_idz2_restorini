package site.aleksa.hse.kpo.restorini.visitorini.tools

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Client UDP socket functions
 */
object VisitoriniSocket {
    private val clientSocket: DatagramSocket = DatagramSocket()
    private var remotePort: Int = 0

    /**
     * Initialize Client Socket
     */
    fun initializeClientSocket() {
        val serveriniDefaultPort = 50107
        print("Enter 'Serverini' UDP service port number (empty for use default $serveriniDefaultPort): ")
        val portStr = readln()
        val serverPortNumber = portStr.toIntOrNull()
        remotePort = serverPortNumber ?: serveriniDefaultPort
        println("'Serverini' remote port is: $remotePort")
    }

    /**
     * Send data
     */
    fun sendString(data: String) {
        try {
            val jsonBytes = data.toByteArray(Charsets.UTF_8)
            val datagramPacket = DatagramPacket(jsonBytes, jsonBytes.size, InetAddress.getLocalHost(), remotePort)
            clientSocket.send(datagramPacket)
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
    }

    /**
     * Receive data
     */
    fun recvString(timeout: Int = 10000): String {
        try {
            val jsonBytes = ByteArray(65536)
            val datagramPacket = DatagramPacket(jsonBytes, jsonBytes.size)
            clientSocket.soTimeout = timeout
            clientSocket.receive(datagramPacket)
            return jsonBytes.copyOf(datagramPacket.length).toString(Charsets.UTF_8)
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
    }
}