/* Bài tập đề xuất 3 */
package Lab.Lab4socket.src.upd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UdpDateTimeClient {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // Thiết lập timeout 2 giây để tránh treo client nếu UDP Server chết
            socket.setSoTimeout(2000);
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 5003;

            System.out.println("Da khoi tao UDP Client. Nhap DATE, TIME, DATETIME hoac QUIT:");
            String request;

            while ((request = console.readLine()) != null) {
                if (request.equalsIgnoreCase("QUIT")) {
                    System.out.println("Client tu thoat (UDP khong can gui QUIT len server).");
                    break;
                }

                byte[] sendData = request.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                socket.send(sendPacket);

                try {
                    byte[] receiveBuffer = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    socket.receive(receivePacket);

                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                            StandardCharsets.UTF_8);
                    System.out.println("Server: " + response);
                } catch (SocketTimeoutException e) {
                    System.out.println("Loi: Khong nhan duoc phan hoi. Server co the da dong hoac rớt mang.");
                }
            }
        } catch (IOException e) {
            System.err.println("Loi UDP client: " + e.getMessage());
        }
    }
}