package Lab.Lab4socket.src.network;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class DiscoveryServer {
    private static final int UDP_PORT = 9999;
    private static final int TCP_PORT = 8888;
    private static final String SERVICE_INFO = "SERVICE FileServer " + TCP_PORT + " v1.0";

    public static void main(String[] args) {
        // Luồng 1: Chạy TCP Server thực sự
        new Thread(() -> {
            try (ServerSocket tcpServer = new ServerSocket(TCP_PORT)) {
                System.out.println("TCP Service chay tai port " + TCP_PORT);
                while (true) {
                    Socket client = tcpServer.accept();
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println("Chao mung ban da den voi dich vu qua TCP!");
                    client.close();
                }
            } catch (Exception e) {
            }
        }).start();

        // Luồng 2: Chạy UDP Responder (Lắng nghe Broadcast)
        try (DatagramSocket udpSocket = new DatagramSocket(UDP_PORT)) {
            System.out.println("UDP Discovery dang lang nghe Broadcast port " + UDP_PORT);
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                udpSocket.receive(packet);
                String req = new String(packet.getData(), 0, packet.getLength()).trim();

                if ("DISCOVER_SERVICE".equals(req)) {
                    byte[] sendData = SERVICE_INFO.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(),
                            packet.getPort());
                    udpSocket.send(sendPacket);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}