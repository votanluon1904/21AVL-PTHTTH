
import java.io.*; // Import các lớp xử lý luồng nhập xuất dữ liệu
import java.net.*; // Import các lớp lập trình mạng như ServerSocket, Socket
import java.util.*; // Import các cấu trúc dữ liệu như Map, Set
import java.util.concurrent.*; // Import cấu trúc dữ liệu hỗ trợ đa luồng an toàn

public class ChatServer { // Khai báo lớp ChatServer, chịu trách nhiệm quản lý Server

    // Khai báo cổng mà Server sử dụng để lắng nghe kết nối từ Client
    private static final int PORT = 12345;

    // Lưu danh sách các phòng chat và danh sách Client trong từng phòng
    // Key là tên phòng, ví dụ: "Study", "Game", "TraiNghiem"
    // Value là tập hợp các ClientHandler đang tham gia phòng đó
    // ConcurrentHashMap giúp truy cập Map an toàn khi có nhiều luồng
    protected static final Map<String, Set<ClientHandler>> rooms = new ConcurrentHashMap<>();

    // Hàm main là điểm bắt đầu chạy chương trình Server
    public static void main(String[] args) {

        // Tạo phòng chat Study với tập hợp Client ban đầu rỗng
        // newKeySet() tạo tập hợp an toàn khi nhiều luồng cùng thao tác
        rooms.put("Study", ConcurrentHashMap.newKeySet());

        // Tạo phòng chat Game
        rooms.put("Game", ConcurrentHashMap.newKeySet());

        // Tạo phòng chat TraiNghiem
        rooms.put("TraiNghiem", ConcurrentHashMap.newKeySet());

        // Hiển thị thông báo Server đang chạy trên cổng 12345
        System.out.println("Server dang chay tren cong " + PORT);

        // Tạo ServerSocket để lắng nghe các yêu cầu kết nối TCP
        // try-with-resources tự động đóng ServerSocket khi thoát khối try
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Vòng lặp vô hạn để Server liên tục chấp nhận Client mới
            while (true) {

                // accept() chờ Client kết nối đến Server
                // Khi có kết nối, trả về Socket đại diện cho Client đó
                Socket clientSocket = serverSocket.accept();

                // Hiển thị địa chỉ IP của Client vừa kết nối
                System.out.println(
                        "Co client moi ket noi: "
                                + clientSocket.getInetAddress());

                // Tạo đối tượng ClientHandler để xử lý Client vừa kết nối
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                // Tạo và khởi chạy luồng riêng cho Client này
                // Nhờ vậy Server có thể phục vụ nhiều Client đồng thời
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {

            // In thông tin lỗi nếu Server không thể mở cổng
            // hoặc xảy ra lỗi trong quá trình chấp nhận kết nối
            e.printStackTrace();
        }
    }
}

// Lớp ClientHandler xử lý hoạt động của một Client cụ thể
// implements Runnable cho phép đối tượng chạy trên một luồng riêng
class ClientHandler implements Runnable {

    // Socket dùng để giao tiếp với Client hiện tại
    private Socket socket;

    // Luồng dùng để gửi tin nhắn đến Client
    private PrintWriter out;

    // Luồng dùng để đọc tin nhắn Client gửi đến Server
    private BufferedReader in;

    // Tên do Client nhập khi kết nối đến Server
    private String clientName;

    // Tên phòng chat mà Client đang tham gia
    // Giá trị null nghĩa là Client chưa ở trong phòng nào
    private String currentRoom;

    // Hàm khởi tạo, nhận Socket của Client từ ChatServer
    public ClientHandler(Socket socket) {
        this.socket = socket; // Lưu Socket vào thuộc tính của đối tượng
    }

    // Phương thức run() chứa các công việc xử lý Client
    // Được thực thi khi gọi new Thread(clientHandler).start()
    @Override
    public void run() {

        try {
            // Lấy luồng nhận dữ liệu từ Client
            // InputStreamReader chuyển byte thành ký tự
            // BufferedReader hỗ trợ đọc dữ liệu theo từng dòng
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            // Lấy luồng gửi dữ liệu đến Client
            // true giúp tự động đẩy dữ liệu ra luồng khi gọi println()
            out = new PrintWriter(socket.getOutputStream(), true);

            // ===== 1. YÊU CẦU CLIENT NHẬP TÊN =====

            // Gửi yêu cầu nhập tên đến Client
            out.println("Vui long nhap ten cua ban: ");

            // Đọc tên Client gửi lên Server
            clientName = in.readLine();

            // Vòng lặp cho phép Client chọn phòng nhiều lần
            // Sau khi rời phòng bằng /quit, Client có thể chọn phòng khác
            while (true) {

                // ===== 2. HIỂN THỊ DANH SÁCH PHÒNG CHAT =====

                // Gửi tiêu đề danh sách phòng đến Client
                out.println("\n--- DANH SACH PHONG ---");

                // Duyệt qua tên tất cả các phòng trong Map
                for (String roomName : ChatServer.rooms.keySet()) {

                    // Gửi tên từng phòng đến Client
                    out.println("- " + roomName);
                }

                // Yêu cầu Client nhập tên phòng muốn tham gia
                // /exit là lệnh thoát khỏi Server
                out.println(
                        "Nhap ten phong de vao, hoac /exit de thoat: ");

                // Đọc lựa chọn phòng do Client gửi đến
                String choice = in.readLine();

                // Nếu Client ngắt kết nối hoặc nhập /exit thì thoát vòng lặp
                if (choice == null || choice.equalsIgnoreCase("/exit")) {
                    break;
                }

                // Kiểm tra tên phòng có tồn tại trong Map hay không
                if (ChatServer.rooms.containsKey(choice)) {

                    // Lưu tên phòng mà Client vừa chọn
                    currentRoom = choice;

                    // Thêm Client hiện tại vào danh sách thành viên phòng
                    ChatServer.rooms.get(currentRoom).add(this);

                    // Thông báo Client đã vào phòng thành công
                    // Đồng thời hướng dẫn các lệnh /quit và /exit
                    out.println(
                            "=> Ban da vao phong [" + currentRoom
                                    + "]. Go /quit de roi phong, /exit de thoat server.");

                    // Thông báo cho các thành viên khác trong phòng
                    // biết rằng Client này vừa tham gia
                    broadcast(
                            "--- " + clientName + " da tham gia phong ---");

                    // ===== 3. BẮT ĐẦU CHAT TRONG PHÒNG =====

                    // Xử lý việc nhận và chuyển tiếp tin nhắn trong phòng
                    // Phương thức này kết thúc khi Client nhập lệnh thoát
                    chatLoop();

                    // Kiểm tra Socket đã đóng chưa
                    // Nếu đóng thì thoát vòng lặp chọn phòng
                    if (socket.isClosed()) {
                        break;
                    }

                } else {

                    // Thông báo nếu Client nhập tên phòng không tồn tại
                    out.println(
                            "! Phong khong ton tai. Vui long nhap dung ten phong.");
                }
            }

        } catch (IOException e) {

            // Thông báo khi kết nối của Client xảy ra lỗi hoặc bị đóng
            System.out.println(
                    "Ket noi toi " + clientName + " bi loi/dong.");

        } finally {

            // Luôn thực hiện dọn dẹp tài nguyên khi phương thức run kết thúc
            // Kể cả khi xảy ra lỗi hoặc Client thoát chương trình
            closeConnections();
        }
    }

    // Phương thức xử lý quá trình trò chuyện trong phòng hiện tại
    private void chatLoop() throws IOException {

        // Biến lưu nội dung tin nhắn nhận được từ Client
        String message;

        // Đọc liên tục từng dòng tin nhắn từ Client
        // Vòng lặp kết thúc nếu Client ngắt kết nối hoặc có lệnh thoát
        while ((message = in.readLine()) != null) {

            // Nếu Client nhập /quit thì chỉ rời phòng chat hiện tại
            if (message.equalsIgnoreCase("/quit")) {

                // Xóa Client khỏi phòng và thông báo cho thành viên khác
                leaveRoom();

                // Thoát vòng lặp chat để quay lại màn hình chọn phòng
                break;

                // Nếu Client nhập /exit thì thoát khỏi Server
            } else if (message.equalsIgnoreCase("/exit")) {

                // Xóa Client khỏi phòng hiện tại
                leaveRoom();

                // Đóng các luồng và Socket của Client
                closeConnections();

                // Thoát vòng lặp chat
                break;

            } else {

                // Nếu là tin nhắn thông thường, gửi đến các thành viên khác
                // trong phòng hiện tại
                broadcast(clientName + ": " + message);
            }
        }
    }

    // Phương thức cho Client rời khỏi phòng chat hiện tại
    private void leaveRoom() {

        // Kiểm tra Client có phòng hiện tại và phòng vẫn tồn tại hay không
        if (currentRoom != null
                && ChatServer.rooms.containsKey(currentRoom)) {

            // Xóa Client hiện tại khỏi danh sách thành viên của phòng
            ChatServer.rooms.get(currentRoom).remove(this);

            // Thông báo cho những thành viên còn lại biết Client đã rời phòng
            broadcast(
                    "--- " + clientName + " da roi phong ---");

            // Đặt phòng hiện tại thành null vì Client không còn ở phòng nào
            currentRoom = null;
        }
    }

    // Phương thức gửi một tin nhắn đến Client này
    public void sendMessage(String message) {

        // Gửi nội dung tin nhắn qua luồng PrintWriter
        // println() thêm ký tự xuống dòng để bên nhận dùng readLine()
        out.println(message);
    }

    // Phương thức gửi tin nhắn đến các Client khác trong cùng phòng
    private void broadcast(String message) {

        // Chỉ gửi tin nhắn khi Client đang thuộc một phòng chat
        if (currentRoom != null) {

            // Duyệt qua tất cả Client đang tham gia phòng hiện tại
            for (ClientHandler client : ChatServer.rooms.get(currentRoom)) {

                // Không gửi lại tin nhắn cho chính Client đã gửi
                if (client != this) {

                    // Gọi phương thức gửi tin nhắn của Client nhận
                    client.sendMessage(message);
                }
            }
        }
    }

    // Phương thức dọn dẹp và đóng kết nối của Client
    private void closeConnections() {

        // Đảm bảo Client được xóa khỏi phòng nếu vẫn còn tham gia
        leaveRoom();

        try {
            // Đóng luồng gửi dữ liệu nếu đã được khởi tạo
            if (out != null)
                out.close();

            // Đóng luồng nhận dữ liệu nếu đã được khởi tạo
            if (in != null)
                in.close();

            // Đóng Socket nếu Socket tồn tại và chưa đóng
            if (socket != null && !socket.isClosed())
                socket.close();

        } catch (IOException e) {

            // In thông tin lỗi nếu không thể đóng tài nguyên
            e.printStackTrace();
        }
    }
}
