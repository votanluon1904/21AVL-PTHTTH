
package DeMauGK; // Khai báo package chứa lớp ExamUdpClient

import java.io.BufferedReader; // Đọc dữ liệu theo từng dòng
import java.io.IOException; // Xử lý các lỗi nhập xuất
import java.io.InputStreamReader; // Chuyển luồng byte thành luồng ký tự
import java.net.DatagramPacket; // Đóng gói dữ liệu để gửi và nhận qua UDP
import java.net.DatagramSocket; // Tạo socket để giao tiếp bằng giao thức UDP
import java.net.InetAddress; // Biểu diễn địa chỉ IP của Server
import java.net.SocketTimeoutException; // Xử lý lỗi khi chờ phản hồi quá thời gian quy định
import java.nio.charset.StandardCharsets; // Sử dụng bảng mã UTF-8 để mã hóa và giải mã dữ liệu

public class ExamUdpClient { // Khai báo lớp Client sử dụng giao thức UDP

    private static final String SERVER_IP = "127.0.0.1";
    // Địa chỉ IP của Server; 127.0.0.1 là địa chỉ máy tính hiện tại

    private static final int PORT = 7000;
    // Cổng UDP mà Server sử dụng để nhận dữ liệu

    public static void main(String[] args) { // Hàm main, điểm bắt đầu chương trình

        // Tạo DatagramSocket để gửi và nhận dữ liệu UDP
        // Không chỉ định cổng nên hệ điều hành tự chọn một cổng nguồn
        // BufferedReader dùng để đọc dữ liệu người dùng nhập từ bàn phím
        try (DatagramSocket socket = new DatagramSocket();
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // Thiết lập thời gian chờ nhận dữ liệu tối đa là 5000 mili giây (5 giây)
            // Nếu không nhận được phản hồi trong thời gian này,
            // socket.receive() sẽ phát sinh SocketTimeoutException
            socket.setSoTimeout(5000);

            // Chuyển địa chỉ IP dạng chuỗi thành đối tượng InetAddress
            // Đối tượng này được dùng để xác định máy chủ cần gửi dữ liệu đến
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            // Thông báo Client UDP đã được khởi tạo
            System.out.println("Da khoi tao UDP Client.");

            // Bước khởi đầu: gửi thông điệp HELLO_SERVER đến Server
            // Server có thể dựa vào IP và cổng nguồn của gói tin này
            // để biết nơi gửi phản hồi về
            sendPacket(socket, serverAddress, "HELLO_SERVER");

            // Chờ nhận menu hoặc thông điệp đầu tiên do Server gửi về
            // Nếu hết thời gian chờ, receivePacket() trả về thông báo timeout
            System.out.println(receivePacket(socket));

            // Vòng lặp giao tiếp chính giữa Client và Server
            while (true) {

                // Đọc một dòng dữ liệu do người dùng nhập từ bàn phím
                String userInput = consoleInput.readLine();

                // Nếu không còn dữ liệu đầu vào thì thoát vòng lặp
                if (userInput == null) {
                    break;
                }

                // Gửi dữ liệu người dùng vừa nhập đến Server qua UDP
                sendPacket(socket, serverAddress, userInput);

                // Kiểm tra người dùng có yêu cầu thoát hay không
                if (userInput.equals("EXIT.")) {

                    // Thông báo đã gửi yêu cầu thoát đến Server
                    System.out.println("Da gui yeu cau thoat.");

                    // Thoát vòng lặp giao tiếp
                    break;
                }

                // Chờ nhận một gói tin phản hồi từ Server
                String response = receivePacket(socket);

                // Kiểm tra kết quả nhận được có khác null hay không
                if (response != null) {

                    // Hiển thị nội dung phản hồi lên màn hình
                    System.out.println("Server: " + response);
                }
            }

        } catch (IOException e) { // Bắt lỗi khởi tạo socket, đọc dữ liệu hoặc kết nối
            // Hiển thị thông báo lỗi nếu có sự cố xảy ra
            System.err.println("Loi UDP Client: " + e.getMessage());
        }

        // Khi kết thúc khối try, DatagramSocket và BufferedReader
        // được tự động đóng nhờ cơ chế try-with-resources
    }

    // Phương thức gửi một thông điệp đến địa chỉ IP của Server
    private static void sendPacket(
            DatagramSocket socket, // Socket UDP dùng để gửi dữ liệu
            InetAddress address, // Địa chỉ IP của máy nhận
            String message // Nội dung thông điệp cần gửi
    ) throws IOException { // Có thể phát sinh IOException khi gửi dữ liệu

        // Chuyển nội dung chuỗi thành mảng byte theo bảng mã UTF-8
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8);

        // Tạo gói tin UDP chứa dữ liệu cần gửi
        // sendData: mảng byte chứa nội dung thông điệp
        // sendData.length: số byte dữ liệu cần gửi
        // address: địa chỉ IP của Server
        // PORT: cổng UDP của Server
        DatagramPacket sendPacket = new DatagramPacket(
                sendData, sendData.length, address, PORT);

        // Gửi gói tin UDP đến Server
        socket.send(sendPacket);

        // UDP không yêu cầu thiết lập kết nối trước khi gửi dữ liệu
    }

    // Phương thức nhận một gói tin UDP từ Server và chuyển thành chuỗi
    private static String receivePacket(DatagramSocket socket) {

        try {

            // Tạo bộ đệm 2048 byte để chứa dữ liệu nhận được
            // Nếu gói tin lớn hơn bộ đệm, phần dữ liệu vượt quá có thể bị cắt
            byte[] receiveBuffer = new byte[2048];

            // Tạo DatagramPacket để chứa dữ liệu nhận vào bộ đệm
            // Ban đầu, độ dài tối đa có thể nhận là receiveBuffer.length
            DatagramPacket receivePacket = new DatagramPacket(
                    receiveBuffer, receiveBuffer.length);

            // Chờ nhận một gói tin UDP
            // Lệnh này chờ cho đến khi nhận được gói tin hoặc hết timeout
            socket.receive(receivePacket);

            // Giải mã chính xác phần dữ liệu thực tế đã nhận thành chuỗi UTF-8
            // getData(): lấy mảng byte chứa dữ liệu
            // 0: vị trí bắt đầu đọc trong mảng byte
            // getLength(): số byte thực tế nhận được trong gói tin
            // UTF_8: bảng mã dùng để giải mã
            // trim(): loại bỏ khoảng trắng ở đầu và cuối chuỗi
            return new String(
                    receivePacket.getData(),
                    0,
                    receivePacket.getLength(),
                    StandardCharsets.UTF_8).trim();

        } catch (SocketTimeoutException e) {

            // Xảy ra khi không nhận được gói tin trong vòng 5 giây
            // Trả về thông báo để Client không bị dừng đột ngột
            return "[Loi] Khong nhan duoc phan hoi tu Server (Timeout).";

        } catch (IOException e) {

            // Xử lý các lỗi nhập xuất khác trong quá trình nhận dữ liệu
            // Trả về thông báo lỗi kèm nội dung chi tiết
            return "[Loi] Doc phan hoi that bai: " + e.getMessage();
        }
    }
}
