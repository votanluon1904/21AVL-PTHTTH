package Lab.Lab4socket.src.upd;

import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastReceiver {
    public static void main(String[] args) {
        String groupIp = "239.255.0.1";
        int port = 9000;

        try {
            InetAddress group = InetAddress.getByName(groupIp);
            MulticastSocket socket = new MulticastSocket(port);

            // Tìm kiếm NetworkInterface hợp lệ hỗ trợ Multicast (hoặc fallback về Localhost
            // để test)
            NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            if (netIf == null || !netIf.supportsMulticast()) {
                netIf = NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
            }

            SocketAddress groupAddress = new InetSocketAddress(group, port);
            socket.joinGroup(groupAddress, netIf);
            System.out.println("Tham gia nhom " + groupIp + " qua giao dien " + netIf.getName());

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);
            String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            System.out.println("Nhan duoc: " + msg);

            // Rời group trước khi đóng
            socket.leaveGroup(groupAddress, netIf);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}