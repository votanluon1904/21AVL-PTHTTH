package Lab.Lab4socket.src.upd;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastSender {
    public static void main(String[] args) {
        String groupIp = "239.255.0.1";
        int port = 9000;
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(groupIp);
            String message = "Thong bao khan: He thong se bao tri sau 5 phut!";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);
            socket.send(packet);
            System.out.println("Da gui multicast message.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}