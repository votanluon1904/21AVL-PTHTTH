package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private static final int PORT = 5005;
    // Cấu trúc Thread-safe để quản lý danh sách client: Key là Nickname, Value là
    // luồng ghi (PrintWriter)
    private static final ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(20); // Hỗ trợ tối đa 20 client

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Chat Server dang chay tai port " + PORT);
            while (true) {
                Socket socket = server.accept();
                pool.submit(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("Loi khoi tao server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    // Luồng xử lý riêng cho từng Client
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private String nickname;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

                // 1. Yêu cầu nhập Nickname duy nhất
                while (true) {
                    out.println("Vui long nhap nickname cua ban:");
                    nickname = in.readLine();
                    if (nickname == null)
                        return; // Client ngắt kết nối ngay khi vừa vào

                    nickname = nickname.trim();
                    if (!nickname.isEmpty() && !clients.containsKey(nickname)) {
                        // Thêm vào danh sách an toàn
                        clients.put(nickname, out);
                        break;
                    }
                    out.println("Loi: Nickname da ton tai hoac bo trong.");
                }

                // 2. Chào mừng và thông báo cho người khác
                out.println("Dang nhap thanh cong! Cac lenh ho tro: USERS, MSG noi_dung, QUIT");
                broadcast("+++ " + nickname + " da tham gia phong chat +++", nickname);

                // 3. Vòng lặp nhận và xử lý lệnh
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.equalsIgnoreCase("QUIT")) {
                        break;
                    } else if (line.equalsIgnoreCase("USERS")) {
                        out.println("Danh sach hien tai: " + String.join(", ", clients.keySet()));
                    } else if (line.toUpperCase().startsWith("MSG ")) {
                        String msg = line.substring(4);
                        broadcast(nickname + ": " + msg, nickname);
                    } else {
                        out.println("Loi: Lenh khong hop le. (Dung: USERS, MSG <text>, QUIT)");
                    }
                }
            } catch (IOException e) {
                // Bắt ngoại lệ khi Client bị ngắt mạng, tắt terminal đột ngột
                System.out.println("Client " + (nickname != null ? nickname : socket.getRemoteSocketAddress())
                        + " ngat ket noi bat thuong.");
            } finally {
                // 4. Dọn dẹp khi Client thoát (dù tự nguyện hay mất mạng)
                if (nickname != null && clients.containsKey(nickname)) {
                    clients.remove(nickname);
                    broadcast("--- " + nickname + " da roi khoi phong chat ---", null);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Loi dong socket: " + e.getMessage());
                }
            }
        }
    }

    // Hàm gửi tin nhắn đến tất cả mọi người (trừ người gửi nếu được chỉ định)
    private static void broadcast(String message, String excludeNickname) {
        for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
            if (excludeNickname == null || !entry.getKey().equals(excludeNickname)) {
                entry.getValue().println(message);
            }
        }
    }
}