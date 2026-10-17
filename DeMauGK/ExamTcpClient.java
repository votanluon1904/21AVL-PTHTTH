
package DeMauGK; // Khai báo package chứa lớp ExamTcpClient

import java.io.BufferedReader; // Đọc dữ liệu theo từng dòng
import java.io.IOException; // Xử lý các lỗi nhập xuất
import java.io.InputStreamReader; // Chuyển luồng byte thành luồng ký tự
import java.io.OutputStreamWriter; // Chuyển ký tự thành byte để gửi qua mạng
import java.io.PrintWriter; // Gửi dữ liệu dạng văn bản
import java.net.Socket; // Tạo kết nối TCP giữa Client và Server
import java.nio.charset.StandardCharsets; // Sử dụng bảng mã UTF-8

public class ExamTcpClient { // Khai báo lớp Client TCP

    private static final String SERVER_IP = "127.0.0.1";
    // Địa chỉ IP của Server; 127.0.0.1 là địa chỉ máy tính hiện tại

    private static final int PORT = 7000;
    // Cổng mà Server đang lắng nghe kết nối

    public static void main(String[] args) { // Hàm main, điểm bắt đầu chương trình

        // Tạo kết nối TCP và các luồng đọc ghi dữ liệu
        // try-with-resources tự động đóng các tài nguyên khi kết thúc
        try (Socket socket = new Socket(SERVER_IP, PORT);

                // Tạo luồng đọc dữ liệu người dùng nhập từ bàn phím
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8));

                // Tạo luồng đọc dữ liệu Server gửi về Client
                BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // Tạo luồng gửi dữ liệu từ Client đến Server
                // Tham số true giúp tự động flush sau mỗi lần println()
                PrintWriter serverOut = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // Thông báo kết nối tới Server thành công
            System.out.println("Da ket noi den server " + SERVER_IP + ":" + PORT);

            // 1. Nhận menu dịch vụ do Server gửi đến
            String serverLine; // Biến lưu từng dòng dữ liệu nhận được từ Server

            // Đọc lần lượt từng dòng Server gửi
            while ((serverLine = serverIn.readLine()) != null) {

                // Nếu nhận được END_MENU thì menu đã kết thúc
                if (serverLine.equals("END_MENU")) {
                    break; // Thoát vòng lặp nhận menu
                }

                // Hiển thị từng dòng menu lên màn hình Client
                System.out.println(serverLine);
            }

            // 2. Đọc lựa chọn dịch vụ do người dùng nhập
            String choice = consoleInput.readLine();

            // Gửi lựa chọn dịch vụ đến Server
            serverOut.println(choice);

            // Nhận thông báo xác nhận lựa chọn từ Server
            String confirmation = serverIn.readLine();

            // Hiển thị thông báo xác nhận lên màn hình
            System.out.println("Server: " + confirmation);

            // 3. Bắt đầu nhập dữ liệu và duy trì kết nối với Server
            while (true) {

                // Đọc một dòng văn bản do người dùng nhập
                String userInput = consoleInput.readLine();

                // Nếu không còn dữ liệu đầu vào thì thoát vòng lặp
                if (userInput == null) {
                    break;
                }

                // Gửi dòng vừa nhập đến Server
                serverOut.println(userInput);

                // Nếu người dùng nhập EXIT. thì yêu cầu thoát
                if (userInput.equals("EXIT.")) {
                    // Thông báo đã gửi yêu cầu đóng kết nối
                    System.out.println("Da gui yeu cau dong ket noi.");

                    // Thoát vòng lặp nhập liệu
                    break;
                }

                // Nếu nhập dấu chấm thì kết thúc đợt nhập hiện tại
                if (userInput.equals(".")) {

                    // Chờ nhận kết quả tổng hợp hoặc thông báo hoàn thành từ Server
                    String finalResult = serverIn.readLine();

                    // Kiểm tra Server có trả về dữ liệu hay không
                    if (finalResult != null) {

                        // Hiển thị kết quả cuối đợt nhập
                        System.out.println("Server: " + finalResult);
                    }

                    // Không đóng kết nối; Client có thể tiếp tục nhập dữ liệu
                } else {

                    // Với các dòng thông thường, chờ Server trả kết quả xử lý
                    String response = serverIn.readLine();

                    // Kiểm tra phản hồi nhận được có khác null hay không
                    if (response != null) {

                        // Hiển thị kết quả xử lý từ Server
                        System.out.println("Server: " + response);
                    }
                }
            }

        } catch (IOException e) { // Bắt lỗi khi kết nối hoặc đọc ghi dữ liệu
            // Hiển thị nội dung lỗi để người dùng biết nguyên nhân
            System.err.println("Loi ket noi: " + e.getMessage());
        }

        // Khi thoát khối try, Socket và các luồng sẽ tự động được đóng
    }
}
