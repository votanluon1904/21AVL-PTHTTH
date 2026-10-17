package DeMauGK; // Khai báo đường dẫn package chứa file

import java.io.BufferedReader; // Import thư viện tạo bộ đệm đọc
import java.io.IOException; // Import thư viện xử lý ngoại lệ I/O
import java.io.InputStreamReader; // Import thư viện chuyển đổi byte thành ký tự văn bản
import java.io.OutputStreamWriter; // Import thư viện chuyển đổi văn bản thành byte để truyền đi
import java.io.PrintWriter; // Import thư viện ghi dữ liệu ra luồng với định dạng chuẩn
import java.net.Socket; // Import thư viện kết nối mạng TCP
import java.nio.charset.StandardCharsets; // Import thư viện quy định mã hóa UTF-8 (dùng tiếng Việt)

public class SumTcpClient { // Khai báo lớp khởi tạo Client
    private static final String SERVER_IP = "127.0.0.1"; // Định nghĩa hằng số IP của Server (127.0.0.1 là máy tính hiện
                                                         // tại)
    private static final int PORT = 7000; // Định nghĩa hằng số cổng giao tiếp tương ứng với Server

    public static void main(String[] args) { // Hàm Main khởi chạy ứng dụng Client
        // Dùng cú pháp try-with-resources để khởi tạo Socket và các luồng nhập xuất (sẽ
        // tự đóng khi xong)
        try (Socket socket = new Socket(SERVER_IP, PORT); // Mở kết nối TCP thông qua IP và Port
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8)); // Tạo luồng đọc dữ liệu từ bàn phím
                                                                                   // người dùng gõ
                BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)); // Tạo luồng hứng dữ
                                                                                                 // liệu gửi về từ
                                                                                                 // Server
                PrintWriter serverOut = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) { // Tạo luồng
                                                                                                           // đẩy dữ
                                                                                                           // liệu lên
                                                                                                           // Server (tự
                                                                                                           // động xả
                                                                                                           // đệm -
                                                                                                           // true)

            System.out.println("Da ket noi den Server Tinh Tong."); // In thông báo thành công ra Terminal của Client

            String welcomeMsg = serverIn.readLine(); // Đọc dòng hướng dẫn đầu tiên Server gửi về
            System.out.println("Server: " + welcomeMsg); // In dòng hướng dẫn đó ra màn hình

            while (true) { // Vòng lặp giao tiếp vô tận giúp Client có thể nhập nhiều dòng
                String userInput = consoleInput.readLine(); // Dừng màn hình chờ người dùng gõ phím và bấm Enter
                if (userInput == null)
                    break; // Nếu luồng bàn phím bị ngắt đột ngột (VD: nhấn Ctrl+C), thoát vòng lặp

                serverOut.println(userInput); // Gửi nguyên bản dòng dữ liệu người dùng vừa gõ lên cho Server

                if (userInput.trim().equalsIgnoreCase("exit")) { // Kiểm tra nếu người dùng gõ "exit" (không phân biệt
                                                                 // chữ hoa thường)
                    System.out.println("Da dong ket noi."); // In ra thông báo ngắt kết nối
                    break; // Bẻ gãy vòng lặp, chạy xuống cuối hàm main để tự động đóng Socket
                } // Kết thúc if

                String response = serverIn.readLine(); // Đứng chờ và đọc dòng trả lời đầu tiên của Server (chính là
                                                       // dòng in kết quả Tổng)
                if (response != null) { // Nếu Server chưa sập và có gửi về phản hồi
                    System.out.println("Server phản hồi: \n" + response); // In dòng kết quả đó lên màn hình
                } // Kết thúc if

                if (userInput.contains(".")) { // Nếu phát hiện trong chuỗi người dùng gõ có chứa dấu chấm (báo hiệu
                                               // dừng nhập)
                    String alert = serverIn.readLine(); // Cần đọc thêm một dòng nữa từ Server (dòng thông báo nhắc nhở
                                                        // hãy gõ exit)
                    if (alert != null) { // Nếu Server có gửi dòng nhắc nhở
                        System.out.println("Server: " + alert); // In nhắc nhở đó ra màn hình cho người dùng đọc
                    } // Kết thúc if
                } // Kết thúc if
            } // Kết thúc vòng lặp while

        } catch (IOException e) { // Bắt lỗi không thể mở kết nối mạng (Do sai IP, sai cổng, hoặc Server chưa bật)
            System.err.println("Loi ket noi: " + e.getMessage()); // In chi tiết lỗi ra màn hình
        } // Kết thúc try-catch
    } // Kết thúc hàm main
} // Kết thúc lớp SumTcpClient