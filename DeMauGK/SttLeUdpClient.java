package DeMauGK; // Khai báo package chứa mã nguồn UDP

import java.io.BufferedReader; // Thư viện đọc luồng ký tự có bộ đệm
import java.io.IOException; // Thư viện bắt lỗi ngoại lệ I/O
import java.io.InputStreamReader; // Chuyển đổi luồng byte từ bàn phím thành luồng ký tự
import java.net.DatagramPacket; // Lớp xử lý gói tin UDP
import java.net.DatagramSocket; // Lớp tạo kết nối mạng UDP
import java.net.InetAddress; // Lớp đại diện cho địa chỉ IP
import java.net.SocketTimeoutException; // Lỗi xảy ra khi chờ quá thời gian (Timeout)
import java.nio.charset.StandardCharsets; // Thư viện bảng mã UTF-8

public class SttLeUdpClient { // Khai báo lớp public cho Client UDP
    private static final String SERVER_IP = "127.0.0.1"; // Định nghĩa IP của Server (localhost)
    private static final int PORT = 7000; // Định nghĩa Cổng của Server (7000)

    public static void main(String[] args) { // Hàm Main thực thi chương trình
        // Dùng Try-with-resources để tự động đóng Socket và luồng nhập từ bàn phím khi
        // kết thúc
        try (DatagramSocket socket = new DatagramSocket(); // Khởi tạo UDP Socket tại một cổng ngẫu nhiên ở máy Client
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // Cài đặt thời gian chờ tối đa khi nhận dữ liệu là 5000 mili-giây (5 giây) để
            // Client không bị treo
            socket.setSoTimeout(5000);
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP); // Phân giải địa chỉ IP của Server

            System.out.println("Da khoi tao UDP Client."); // Thông báo khởi tạo thành công

            // Gửi một tin nhắn đánh tiếng trước để Server biết IP và Port của Client, từ đó
            // Server gửi lại Menu
            sendPacket(socket, serverAddress, "HELLO_SERVER");
            System.out.println(receivePacket(socket)); // Nhận Menu dịch vụ từ Server và in ra màn hình

            while (true) { // Vòng lặp vô tận để Client liên tục nhập liệu
                String userInput = consoleInput.readLine(); // Dừng màn hình chờ người dùng nhập văn bản từ bàn phím
                if (userInput == null) { // Nếu luồng nhập bị ngắt (nhấn Ctrl+D hoặc Ctrl+C)
                    break; // Thoát vòng lặp
                }

                sendPacket(socket, serverAddress, userInput); // Gửi văn bản người dùng vừa gõ lên Server

                if (userInput.equals("EXIT.")) { // Nếu người dùng gõ lệnh thoát
                    System.out.println("Da gui yeu cau thoat."); // Báo cho người dùng biết
                    break; // Thoát vòng lặp, kết thúc chương trình
                }

                String response = receivePacket(socket); // Gọi hàm chờ nhận câu trả lời từ Server
                if (response != null) { // Nếu nhận được tin nhắn bình thường
                    System.out.println("Server: " + response); // In câu trả lời của Server ra màn hình
                }
            }

        } catch (IOException e) { // Bắt các lỗi liên quan đến đường truyền hoặc đọc phím
            System.err.println("Loi UDP Client: " + e.getMessage()); // In chi tiết lỗi ra Terminal
        }
    }

    // Hàm phụ trợ đóng gói String thành DatagramPacket và gửi đi
    private static void sendPacket(DatagramSocket socket, InetAddress address, String message) throws IOException {
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8); // Chuyển String thành mảng byte
        // Khởi tạo gói tin UDP mang theo dữ liệu và địa chỉ đích đến
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
        socket.send(sendPacket); // Đẩy gói tin ra card mạng
    }

    // Hàm phụ trợ chờ hứng DatagramPacket và dịch nó thành String
    private static String receivePacket(DatagramSocket socket) {
        try { // Bắt đầu khối kiểm tra lỗi
            byte[] receiveBuffer = new byte[2048]; // Tạo mảng rỗng 2KB làm giỏ hứng dữ liệu
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length); // Tạo gói tin rỗng
                                                                                                    // để hứng
            socket.receive(receivePacket); // Đứng đợi nhận gói tin (sẽ ném lỗi nếu quá 5 giây không nhận được)
            // Lấy dữ liệu từ gói tin, chuyển sang chuỗi UTF-8 và cắt bỏ khoảng trắng thừa
            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
        } catch (SocketTimeoutException e) { // Bắt lỗi mạng chập chờn hoặc Server sập (quá 5 giây)
            return "[Loi] Khong nhan duoc phan hoi tu Server (Timeout)."; // Trả về thông báo lỗi thay vì treo máy
        } catch (IOException e) { // Bắt các lỗi I/O khác
            return "[Loi] Doc phan hoi that bai: " + e.getMessage(); // Trả về lỗi
        }
    }
}