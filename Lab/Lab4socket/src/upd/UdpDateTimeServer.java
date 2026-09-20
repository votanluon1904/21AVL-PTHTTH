/* Bài tập đề xuất 3 */
package Lab.Lab4socket.src.upd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UdpDateTimeServer {
    private static final int PORT = 5003;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP DateTime Server chay tai port " + PORT);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String cmd = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8)
                        .trim().toUpperCase();
                LocalDateTime now = LocalDateTime.now();
                String response;

                switch (cmd) {
                    case "DATE":
                        response = now.format(DATE_FMT);
                        break;
                    case "TIME":
                        response = now.format(TIME_FMT);
                        break;
                    case "DATETIME":
                        response = now.format(DATETIME_FMT);
                        break;
                    default:
                        response = "ERR UNKNOWN_COMMAND";
                }

                byte[] sendData = response.getBytes(StandardCharsets.UTF_8);
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.err.println("Loi UDP server: " + e.getMessage());
        }
    }
}