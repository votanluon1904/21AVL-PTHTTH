package Lab.Lab4socket.src.network;

import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class DiscoveryClient {
    public static void main(String[] args) {
        Set<String> discoveredServers = new HashSet<>();
        String targetIp = null;
        int targetTcpPort = -1;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true); // Cho phép gửi Broadcast
            socket.setSoTimeout(2000); // Timeout 2s

            byte[] data = "DISCOVER_SERVICE".getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),
                    9999);
            socket.send(packet);
            System.out.println("Dang tim kiem Server...");

            long endTime = System.currentTimeMillis() + 2000;
            byte[] buf = new byte[1024];

            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                    socket.receive(receivePacket);

                    String ip = receivePacket.getAddress().getHostAddress();
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    // Loại bỏ server bị trùng
                    if (!discoveredServers.contains(ip)) {
                        discoveredServers.add(ip);
                        System.out.println("Tim thay server: " + ip + " -> " + response);

                        // Parse lấy Port TCP (Cấu trúc: SERVICE Tên Port Version)
                        if (response.startsWith("SERVICE")) {
                            targetIp = ip;
                            targetTcpPort = Integer.parseInt(response.split(" ")[2]);
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break;
                }
            }
        } catch (Exception e) {
        }

        // Nếu tìm thấy, thực hiện kết nối TCP
        if (targetIp != null && targetTcpPort != -1) {
            System.out.println("\nKet noi vao TCP Server tai " + targetIp + ":" + targetTcpPort + "...");
            try (Socket tcpSocket = new Socket(targetIp, targetTcpPort);
                    BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()))) {
                System.out.println("Server noi: " + in.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Khong tim thay server nao.");
        }
    }
}