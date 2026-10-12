import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    // Lưu trữ danh sách các phòng và các client trong phòng đó
    protected static final Map<String, Set<ClientHandler>> rooms = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // Khởi tạo các phòng mặc định
        rooms.put("Study", ConcurrentHashMap.newKeySet());
        rooms.put("Game", ConcurrentHashMap.newKeySet());
        rooms.put("TraiNghiem", ConcurrentHashMap.newKeySet());

        System.out.println("Server dang chay tren cong " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Co client moi ket noi: " + clientSocket.getInetAddress());

                // Mỗi client được xử lý trên một luồng riêng
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private String currentRoom;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 1. Nhập tên
            out.println("Vui long nhap ten cua ban: ");
            clientName = in.readLine();

            while (true) {
                // 2. Chọn phòng
                out.println("\n--- DANH SACH PHONG ---");
                for (String roomName : ChatServer.rooms.keySet()) {
                    out.println("- " + roomName);
                }
                out.println("Nhap ten phong de vao, hoac /exit de thoat: ");

                String choice = in.readLine();
                if (choice == null || choice.equalsIgnoreCase("/exit")) {
                    break;
                }

                if (ChatServer.rooms.containsKey(choice)) {
                    currentRoom = choice;
                    ChatServer.rooms.get(currentRoom).add(this);
                    out.println(
                            "=> Ban da vao phong [" + currentRoom + "]. Go /quit de roi phong, /exit de thoat server.");

                    broadcast("--- " + clientName + " da tham gia phong ---");

                    // 3. Xử lý chat trong phòng
                    chatLoop();

                    // Nếu thoát khỏi chatLoop mà currentRoom bị null nghĩa là client đã gõ /quit
                    if (socket.isClosed()) {
                        break;
                    }
                } else {
                    out.println("! Phong khong ton tai. Vui long nhap dung ten phong.");
                }
            }
        } catch (IOException e) {
            System.out.println("Ket noi toi " + clientName + " bi loi/dong.");
        } finally {
            closeConnections();
        }
    }

    private void chatLoop() throws IOException {
        String message;
        while ((message = in.readLine()) != null) {
            if (message.equalsIgnoreCase("/quit")) {
                leaveRoom();
                break; // Thoát vòng lặp chat, quay lại chọn phòng
            } else if (message.equalsIgnoreCase("/exit")) {
                leaveRoom();
                closeConnections(); // Đóng socket hoàn toàn
                break;
            } else {
                broadcast(clientName + ": " + message);
            }
        }
    }

    private void leaveRoom() {
        if (currentRoom != null && ChatServer.rooms.containsKey(currentRoom)) {
            ChatServer.rooms.get(currentRoom).remove(this);
            broadcast("--- " + clientName + " da roi phong ---");
            currentRoom = null;
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void broadcast(String message) {
        if (currentRoom != null) {
            for (ClientHandler client : ChatServer.rooms.get(currentRoom)) {
                // Không gửi lại tin nhắn cho chính người gửi
                if (client != this) {
                    client.sendMessage(message);
                }
            }
        }
    }

    private void closeConnections() {
        leaveRoom();
        try {
            if (out != null)
                out.close();
            if (in != null)
                in.close();
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}