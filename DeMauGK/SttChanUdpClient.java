package DeMauGK; // Chỉ định thư mục logic chứa file mã nguồn

import java.io.BufferedReader; // Hỗ trợ đọc dữ liệu văn bản theo từng dòng
import java.io.IOException; // Bắt các ngoại lệ Input/Output
import java.io.InputStreamReader; // Dịch luồng byte từ bàn phím thành văn bản
import java.net.DatagramPacket; // Đại diện cho gói tin UDP
import java.net.DatagramSocket; // Tạo cổng giao tiếp UDP
import java.net.InetAddress; // Đại diện và phân giải địa chỉ IP
import java.net.SocketTimeoutException; // Xử lý lỗi treo mạng khi chờ quá lâu
import java.nio.charset.StandardCharsets; // Cung cấp bảng mã UTF-8 để gõ tiếng Việt

public class SttChanUdpClient { // Khai báo lớp Client
    private static final String SERVER_IP = "127.0.0.1"; // Địa chỉ IP của Server (chạy cùng máy)
    private static final int PORT = 7000; // Cổng kết nối mà Server đang chờ

    public static void main(String[] args) { // Điểm bắt đầu của Client
        // Khởi tạo DatagramSocket (cổng ngẫu nhiên) và luồng đọc bàn phím
        // (consoleInput)
        try (DatagramSocket socket = new DatagramSocket();
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // Yêu cầu Socket chỉ chờ phản hồi từ Server tối đa 5 giây, tránh treo Client
            // mãi mãi
            socket.setSoTimeout(5000);
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP); // Tìm kiếm địa chỉ IP của Server

            System.out.println("Da khoi tao UDP Client (STT Chan)."); // In thông báo

            // Gửi một chuỗi "mồi" để Server biết IP/Port của mình, từ đó Server gửi lại
            // Menu
            sendPacket(socket, serverAddress, "HELLO_SERVER");
            System.out.println(receivePacket(socket)); // Hứng Menu trả về từ Server và in ra màn hình

            while (true) { // Vòng lặp cho phép người dùng gõ phím liên tục
                String userInput = consoleInput.readLine(); // Hệ thống đứng chờ người dùng gõ và nhấn Enter
                if (userInput == null) { // Xử lý trường hợp luồng nhập bị cưỡng bức đóng
                    break; // Dừng vòng lặp
                }

                sendPacket(socket, serverAddress, userInput); // Gửi chuỗi người dùng vừa gõ sang Server

                if (userInput.equals("EXIT.")) { // Kiểm tra xem người dùng có gõ lệnh thoát không
                    System.out.println("Da gui yeu cau thoat."); // Xác nhận việc thoát
                    break; // Thoát khỏi vòng lặp vô tận, tự động đóng chương trình
                }

                String response = receivePacket(socket); // Chờ Server xử lý và trả kết quả về
                if (response != null) { // Nếu nhận được tin nhắn hợp lệ
                    System.out.println("Server: " + response); // In kết quả ra màn hình cho người dùng xem
                }
            }

        } catch (IOException e) { // Bắt tất cả các lỗi xảy ra trong khối Try
            System.err.println("Loi UDP Client: " + e.getMessage()); // In ra nguyên nhân gây lỗi
        }
    }

    // Hàm phụ trợ giúp mã hóa văn bản thành mảng byte và bắn UDP đi
    private static void sendPacket(DatagramSocket socket, InetAddress address, String message) throws IOException {
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8); // Chuyển String thành byte array
        // Đóng gói mảng byte, độ dài, IP đích và Port đích
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
        socket.send(sendPacket); // Bắn đi
    }

    // Hàm phụ trợ giúp hứng gói tin UDP và dịch ngược thành văn bản String
    private static String receivePacket(DatagramSocket socket) {
        try {
            byte[] receiveBuffer = new byte[2048]; // Tạo một mảng rỗng 2KB làm bộ hứng
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length); // Khai báo gói tin
                                                                                                    // nhận
            socket.receive(receivePacket); // Đứng chờ gói tin bay tới (tối đa 5 giây như đã set)
            // Cắt phần byte có dữ liệu, dịch thành String UTF-8 và bỏ đi khoảng trắng dư
            // thừa
            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
        } catch (SocketTimeoutException e) { // Nếu đợi quá 5 giây mà không có tin nhắn
            return "[Loi] Khong nhan duoc phan hoi tu Server (Timeout)."; // Trả về cảnh báo mất mạng
        } catch (IOException e) { // Nếu xảy ra lỗi đọc ghi khác
            return "[Loi] Doc phan hoi that bai: " + e.getMessage(); // Báo lỗi
        }
    }
}