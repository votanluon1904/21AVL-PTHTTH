package Lab.Lab4socket.src.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class ProtocolBenchmark {
    private static final int TCP_PORT = 5006;
    private static final int UDP_PORT = 5007;
    private static final int MSG_COUNT = 1000;
    private static final int RUNS = 5;
    private static final int PAYLOAD_SIZE = 1024; // 1KB
    private static final int UDP_TIMEOUT_MS = 200; // Timeout 200ms cho mỗi gói UDP

    public static void main(String[] args) throws Exception {
        System.out.println("=== THIET LAP THUC NGHIEM ===");
        System.out.println("- Moi truong: Localhost (127.0.0.1)");
        System.out.println("- Kich thuoc thong diep: " + PAYLOAD_SIZE + " bytes");
        System.out.println("- So luong thong diep / luot: " + MSG_COUNT);
        System.out.println("- So luot chay: " + RUNS);
        System.out.println("- UDP Timeout: " + UDP_TIMEOUT_MS + " ms");
        System.out.println("- Phuong phap do: System.nanoTime() do thoi gian gui va nhan tuan tu (Ping-Pong).\n");

        // Khởi chạy các Server chạy ngầm
        startTcpServer();
        startUdpServer();
        Thread.sleep(1000); // Đợi server khởi động hoàn tất

        System.out.printf("%-10s %-15s %-15s %-15s%n", "Giao thuc", "Luot chay", "Thoi gian (ms)",
                "Phan hoi nhan/Tong");
        System.out.println("---------------------------------------------------------------");

        for (int i = 1; i <= RUNS; i++) {
            benchmarkTcp(i);
            benchmarkUdp(i);
            System.out.println("---------------------------------------------------------------");
        }

        System.exit(0); // Kết thúc chương trình và tắt các thread ngầm
    }

    private static void benchmarkTcp(int runIndex) {
        byte[] payload = new byte[PAYLOAD_SIZE];
        Arrays.fill(payload, (byte) 1);
        byte[] buffer = new byte[PAYLOAD_SIZE];
        int received = 0;

        long startTime = System.nanoTime();
        try (Socket socket = new Socket("127.0.0.1", TCP_PORT);
                OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream()) {

            for (int i = 0; i < MSG_COUNT; i++) {
                out.write(payload);
                out.flush();

                int bytesRead = 0;
                while (bytesRead < PAYLOAD_SIZE) {
                    int count = in.read(buffer, bytesRead, PAYLOAD_SIZE - bytesRead);
                    if (count == -1)
                        break;
                    bytesRead += count;
                }
                if (bytesRead == PAYLOAD_SIZE) {
                    received++;
                }
            }
        } catch (Exception e) {
            System.err.println("TCP Error: " + e.getMessage());
        }
        long endTime = System.nanoTime();
        double totalTimeMs = (endTime - startTime) / 1_000_000.0;

        System.out.printf("%-10s %-15d %-15.2f %d/%d%n", "TCP", runIndex, totalTimeMs, received, MSG_COUNT);
    }

    private static void benchmarkUdp(int runIndex) {
        byte[] payload = new byte[PAYLOAD_SIZE];
        Arrays.fill(payload, (byte) 1);
        byte[] buffer = new byte[PAYLOAD_SIZE];
        int received = 0;

        long startTime = System.nanoTime();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(UDP_TIMEOUT_MS);
            InetAddress address = InetAddress.getByName("127.0.0.1");

            for (int i = 0; i < MSG_COUNT; i++) {
                DatagramPacket sendPacket = new DatagramPacket(payload, payload.length, address, UDP_PORT);
                socket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(receivePacket);
                    received++;
                } catch (SocketTimeoutException e) {
                    // Mất gói tin, bỏ qua và gửi tiếp
                }
            }
        } catch (Exception e) {
            System.err.println("UDP Error: " + e.getMessage());
        }
        long endTime = System.nanoTime();
        double totalTimeMs = (endTime - startTime) / 1_000_000.0;

        System.out.printf("%-10s %-15d %-15.2f %d/%d%n", "UDP", runIndex, totalTimeMs, received, MSG_COUNT);
    }

    private static void startTcpServer() {
        Thread thread = new Thread(() -> {
            try (ServerSocket server = new ServerSocket(TCP_PORT)) {
                while (true) {
                    Socket client = server.accept();
                    new Thread(() -> {
                        try (InputStream in = client.getInputStream();
                                OutputStream out = client.getOutputStream()) {
                            byte[] buffer = new byte[PAYLOAD_SIZE];
                            int count;
                            while ((count = in.read(buffer)) != -1) {
                                out.write(buffer, 0, count);
                            }
                        } catch (Exception ignored) {
                        }
                    }).start();
                }
            } catch (Exception ignored) {
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private static void startUdpServer() {
        Thread thread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                byte[] buffer = new byte[PAYLOAD_SIZE];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    DatagramPacket reply = new DatagramPacket(packet.getData(), packet.getLength(), packet.getAddress(),
                            packet.getPort());
                    socket.send(reply);
                }
            } catch (Exception ignored) {
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}