package DeMauGK; // Khai báo đường dẫn package chứa file

import java.io.BufferedReader; // Import thư viện tạo bộ đệm đọc
import java.io.IOException; // Import thư viện xử lý ngoại lệ I/O
import java.io.InputStreamReader; // Import thư viện chuyển đổi luồng byte thành ký tự
import java.net.DatagramPacket; // Import lớp xử lý gói tin UDP
import java.net.DatagramSocket; // Import lớp tạo kết nối mạng UDP
import java.net.InetAddress; // Import lớp đại diện cho địa chỉ IP
import java.net.SocketTimeoutException; // Import lỗi xảy ra khi chờ mạng quá lâu
import java.nio.charset.StandardCharsets; // Import thư viện quy định mã hóa UTF-8

public class SumUdpClient { // Khai báo lớp khởi tạo Client
    private static final String SERVER_IP = "127.0.0.1"; // Định nghĩa hằng số IP của Server (localhost)
    private static final int PORT = 7000; // Định nghĩa hằng số cổng giao tiếp

    public static void main(String[] args) { // Hàm Main khởi chạy ứng dụng Client
        // Dùng cú pháp try-with-resources để tự động đóng Socket và luồng bàn phím khi
        // kết thúc
        try (DatagramSocket socket = new DatagramSocket(); // Mở cổng UDP ngẫu nhiên tại máy Client
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8))) { // Tạo luồng đọc dữ liệu bàn phím

            socket.setSoTimeout(5000); // Cài đặt thời gian chờ tin nhắn phản hồi tối đa là 5 giây để không bị treo
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP); // Phân giải địa chỉ IP của Server

            System.out.println(
                    "Da khoi tao UDP Client. Nhap so nguyen de tinh tong (nhap '.' de dung, 'exit' de thoat):"); // In
                                                                                                                 // thông
                                                                                                                 // báo
                                                                                                                 // hướng
                                                                                                                 // dẫn
                                                                                                                 // ra
                                                                                                                 // màn
                                                                                                                 // hình

            while (true) { // Vòng lặp giao tiếp vô tận giúp Client nhập liệu liên tục
                String userInput = consoleInput.readLine(); // Dừng màn hình chờ người dùng gõ phím và bấm Enter
                if (userInput == null)
                    break; // Thoát vòng lặp nếu luồng dữ liệu bàn phím bị ngắt (VD: Ctrl+C)

                sendPacket(socket, serverAddress, userInput); // Đóng gói chuỗi vừa nhập và gửi lên Server

                if (userInput.trim().equalsIgnoreCase("exit")) { // Kiểm tra nếu người dùng gõ lệnh "exit"
                    System.out.println("Da gui lenh dong ket noi."); // In thông báo xác nhận
                    break; // Bẻ gãy vòng lặp để đóng chương trình
                } // Kết thúc if

                String response = receivePacket(socket); // Gọi hàm đứng chờ và nhận câu trả lời của Server
                if (response != null) { // Nếu nhận được phản hồi hợp lệ (không bị timeout)
                    System.out.println("Server phản hồi: \n" + response); // In dòng kết quả đó lên màn hình
                } // Kết thúc if
            } // Kết thúc vòng lặp while

        } catch (IOException e) { // Bắt các lỗi khởi tạo cổng hoặc phân giải IP
            System.err.println("Loi UDP Client: " + e.getMessage()); // In chi tiết lỗi
        } // Kết thúc try-catch
    } // Kết thúc hàm main

    // Hàm phụ trợ đóng gói String thành DatagramPacket và gửi đi
    private static void sendPacket(DatagramSocket socket, InetAddress address, String message) throws IOException {
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8); // Chuyển chuỗi văn bản thành mảng byte
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT); // Đóng gói mảng byte
                                                                                                  // và thông tin đích
        socket.send(sendPacket); // Đẩy gói tin ra mạng
    } // Kết thúc hàm

    // Hàm phụ trợ chờ hứng DatagramPacket và dịch ngược thành String
    private static String receivePacket(DatagramSocket socket) {
        try { // Bắt đầu khối kiểm tra lỗi mạng
            byte[] receiveBuffer = new byte[2048]; // Tạo mảng rỗng 2KB làm bộ hứng dữ liệu
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length); // Khai báo gói tin
                                                                                                    // nhận
            socket.receive(receivePacket); // Đứng chờ gói tin bay tới (tối đa 5 giây)
            // Lấy phần dữ liệu thực tế từ gói tin, chuyển sang chuỗi UTF-8 và cắt khoảng
            // trắng
            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
        } catch (SocketTimeoutException e) { // Bắt lỗi nếu đợi 5 giây mà không có phản hồi
            return "[Loi] Khong nhan duoc phan hoi (Timeout)."; // Trả về câu cảnh báo thay vì làm treo máy
        } catch (IOException e) { // Bắt các lỗi đọc mạng khác
            return "[Loi] Doc phan hoi that bai: " + e.getMessage(); // Trả về thông báo lỗi
        } // Kết thúc try-catch
    } // Kết thúc hàm
} // Kết thúc lớp SumUdpClient