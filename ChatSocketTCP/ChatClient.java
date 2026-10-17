
import java.io.*; // Import các lớp xử lý luồng nhập xuất dữ liệu
import java.net.*; // Import các lớp lập trình mạng như Socket
import java.util.Scanner; // Import Scanner để đọc dữ liệu từ bàn phím

public class ChatClient { // Khai báo lớp ChatClient, đại diện cho chương trình Client

    // Địa chỉ của máy chủ Chat
    // localhost nghĩa là máy chủ chạy trên chính máy tính hiện tại
    private static final String SERVER_ADDRESS = "localhost";

    // Cổng mà Server Chat đang lắng nghe kết nối
    // Client phải sử dụng đúng cổng của Server
    private static final int SERVER_PORT = 12345;

    // Hàm main: điểm bắt đầu chạy chương trình Java
    public static void main(String[] args) {

        // Khởi tạo Socket để kết nối đến Server
        // BufferedReader dùng để nhận dữ liệu dạng văn bản từ Server
        // PrintWriter dùng để gửi dữ liệu văn bản đến Server
        // Scanner dùng để đọc tin nhắn người dùng nhập từ bàn phím
        //
        // try-with-resources tự động đóng các tài nguyên
        // khi chương trình thoát khỏi khối try
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

                // Lấy luồng nhận dữ liệu từ Server
                // InputStreamReader chuyển byte nhận được thành ký tự
                // BufferedReader hỗ trợ đọc dữ liệu theo từng dòng
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                // Lấy luồng gửi dữ liệu đến Server
                // true giúp tự động flush dữ liệu khi gọi println()
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(), true);

                // Tạo Scanner để đọc nội dung người dùng nhập
                Scanner scanner = new Scanner(System.in)) {

            // Thông báo kết nối đến Server thành công
            System.out.println("Da ket noi toi Server Chat.");

            // Tạo một luồng riêng để nhận tin nhắn từ Server
            // Nhờ vậy Client vẫn có thể nhập tin nhắn trong lúc chờ nhận
            Thread readThread = new Thread(() -> {

                try {
                    String serverMessage; // Biến lưu tin nhắn nhận được

                    // Đọc liên tục từng dòng dữ liệu từ Server
                    // readLine() trả về null khi luồng kết nối kết thúc
                    while ((serverMessage = in.readLine()) != null) {

                        // Hiển thị tin nhắn Server gửi đến màn hình
                        System.out.println(serverMessage);
                    }

                } catch (IOException e) {

                    // Thông báo khi xảy ra lỗi trong quá trình nhận dữ liệu
                    System.out.println("\nDa ngat ket noi voi Server.");
                }
            });

            // Khởi chạy luồng nhận tin nhắn
            // Phương thức start() cho phép luồng chạy độc lập
            readThread.start();

            // Luồng chính liên tục đọc tin nhắn người dùng nhập
            // hasNextLine() kiểm tra xem còn dòng nhập nào hay không
            while (scanner.hasNextLine()) {

                // Đọc một dòng văn bản do người dùng nhập
                String message = scanner.nextLine();

                // Gửi tin nhắn đến Server
                // println() thêm ký tự xuống dòng để Server đọc bằng readLine()
                out.println(message);

                // Nếu người dùng nhập /exit thì thoát vòng lặp gửi tin nhắn
                // equalsIgnoreCase() không phân biệt chữ hoa và chữ thường
                if (message.equalsIgnoreCase("/exit")) {
                    break; // Kết thúc vòng lặp
                }
            }

            // Khi thoát khối try, socket và các luồng liên quan
            // sẽ được tự động đóng bởi try-with-resources

        } catch (IOException e) {

            // Xử lý lỗi nếu không thể kết nối hoặc có lỗi nhập xuất
            // Ví dụ: Server chưa chạy hoặc địa chỉ/cổng không chính xác
            System.out.println(
                    "Khong the ket noi den Server: " + e.getMessage());
        }
    }
}
