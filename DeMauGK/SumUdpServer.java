package DeMauGK; // Khai báo package chứa mã nguồn

import java.io.IOException; // Import thư viện xử lý các lỗi ngoại lệ I/O
import java.net.DatagramPacket; // Import lớp đại diện cho gói tin UDP
import java.net.DatagramSocket; // Import lớp tạo cổng giao tiếp UDP
import java.net.SocketAddress; // Import lớp lưu trữ kết hợp IP và Port của Client
import java.nio.charset.StandardCharsets; // Import thư viện cung cấp bảng mã UTF-8
import java.util.concurrent.ConcurrentHashMap; // Import cấu trúc Map hỗ trợ an toàn trong đa luồng
import java.util.concurrent.ExecutorService; // Import giao diện quản lý hồ chứa luồng (Thread pool)
import java.util.concurrent.Executors; // Import lớp tiện ích để khởi tạo Thread pool

public class SumUdpServer { // Khai báo lớp Server public
    private static final int PORT = 7000; // Khai báo hằng số PORT 7000 để Server lắng nghe theo đề

    // Khởi tạo bảng băm để lưu trữ trạng thái (số thứ tự dòng) của từng Client độc
    // lập
    private static final ConcurrentHashMap<SocketAddress, ClientSession> sessions = new ConcurrentHashMap<>();

    public static void main(String[] args) { // Hàm main thực thi điểm bắt đầu của chương trình
        ExecutorService pool = Executors.newFixedThreadPool(10); // Khởi tạo Thread pool hỗ trợ xử lý 10 gói tin cùng
                                                                 // lúc

        try (DatagramSocket socket = new DatagramSocket(PORT)) { // Mở UDP Socket tại cổng PORT đã định
            System.out.println("UDP Server Tinh Tong dang chay tai port " + PORT); // In thông báo Server đã bật thành
                                                                                   // công
            byte[] buffer = new byte[2048]; // Tạo mảng byte 2KB làm bộ đệm để hứng dữ liệu đến

            while (true) { // Vòng lặp vô tận giúp Server luôn ở trạng thái chờ đón gói tin
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length); // Khởi tạo gói tin rỗng để
                                                                                          // hứng dữ liệu
                socket.receive(receivePacket); // Đứng đợi (chặn) cho đến khi có gói tin bay vào cổng

                // Giải mã dữ liệu mảng byte thành chuỗi String UTF-8 và cắt khoảng trắng 2 đầu
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                        StandardCharsets.UTF_8).trim();
                SocketAddress clientAddress = receivePacket.getSocketAddress(); // Trích xuất IP và Port của người gửi

                // Đẩy logic tính toán vào Thread pool để Server rảnh tay hứng gói tin tiếp theo
                // ngay lập tức
                pool.submit(() -> handleClientRequest(socket, clientAddress, message));
            } // Kết thúc vòng lặp while
        } catch (IOException e) { // Bắt các lỗi xảy ra nếu cổng mạng bị chiếm dụng
            System.err.println("Loi server: " + e.getMessage()); // In thông báo lỗi ra màn hình
        } finally { // Khối lệnh luôn được thi hành vào cuối vòng đời
            pool.shutdown(); // Đóng Thread pool, ngừng tiếp nhận thêm tác vụ mới
        } // Kết thúc try-catch
    } // Kết thúc hàm main

    // Hàm chứa logic xử lý nghiệp vụ cho từng gói tin nhận được
    private static void handleClientRequest(DatagramSocket socket, SocketAddress clientAddress, String message) {
        // Lấy thông tin phiên làm việc của Client, nếu là người mới thì tạo một
        // ClientSession mới
        ClientSession session = sessions.computeIfAbsent(clientAddress, k -> new ClientSession());
        String response = ""; // Biến chứa câu trả lời sẽ gửi lại cho Client

        if (message.equalsIgnoreCase("exit")) { // Kiểm tra xem Client có gửi lệnh "exit" không
            response = "Tam biet! Da dong ket noi."; // Soạn lời chia tay
            sessions.remove(clientAddress); // Xóa thông tin Client này khỏi bộ nhớ tạm
        } else { // Nếu không phải lệnh thoát
            boolean hasDot = message.contains("."); // Kiểm tra xem trong dòng có dấu chấm "." không
            message = message.replace(".", ""); // Xóa dấu chấm khỏi chuỗi để các hàm ép kiểu số không bị lỗi

            if (message.isEmpty() && hasDot) { // Nếu người dùng chỉ gõ đúng một dấu chấm rồi Enter
                response = "Da nhan dau '.'. Hay nhap 'exit' de thoat."; // Báo hiệu đã nhận lệnh dừng
            } else { // Nếu có chứa số liệu để tính toán
                String[] numbers = message.split("\\s+"); // Tách chuỗi thành mảng các số bằng khoảng trắng
                long sum = 0; // Khởi tạo biến lưu tổng (Dùng long để đề phòng tổng quá lớn)
                boolean hasError = false; // Cờ đánh dấu xem có lỗi ép kiểu không

                for (String numStr : numbers) { // Lặp qua từng phần tử trong mảng chữ
                    if (!numStr.isEmpty()) { // Đảm bảo chuỗi con không bị rỗng
                        try { // Bắt đầu khối xử lý ép kiểu
                            sum += Long.parseLong(numStr); // Ép chuỗi thành kiểu số nguyên và cộng dồn
                        } catch (NumberFormatException e) { // Bắt lỗi nếu gõ chữ cái
                            hasError = true; // Bật cờ lỗi
                            break; // Dừng vòng lặp tính toán
                        } // Kết thúc try-catch
                    } // Kết thúc if
                } // Kết thúc for

                if (hasError) { // Nếu phát hiện lỗi gõ chữ
                    response = "Loi: Vui long chi nhap so nguyen."; // Soạn câu báo lỗi
                } else { // Nếu tính tổng thành công
                    response = "Tổng dòng " + session.lineCount + ": " + sum; // Soạn kết quả tổng đúng format đề bài
                    session.lineCount++; // Tăng số thứ tự dòng lên 1 đơn vị

                    if (hasDot) { // Nếu dòng vừa tính có chứa dấu chấm đi kèm
                        response += "\nServer: -> Da nhan dau '.'. Qua trinh nhap lieu dung lai. Hay go 'exit' de dong ket noi."; // Nối
                                                                                                                                  // thêm
                                                                                                                                  // câu
                                                                                                                                  // nhắc
                                                                                                                                  // nhở
                                                                                                                                  // thoát
                    } // Kết thúc if
                } // Kết thúc if-else
            } // Kết thúc if-else
        } // Kết thúc if-else tổng

        sendToClient(socket, clientAddress, response); // Gọi hàm phụ trợ để gửi kết quả về mạng
    } // Kết thúc hàm

    // Hàm phụ trợ đóng gói String và gửi qua mạng UDP
    private static void sendToClient(DatagramSocket socket, SocketAddress clientAddress, String response) {
        try { // Dùng try-catch để phòng ngừa lỗi rớt mạng khi gửi
            byte[] sendData = response.getBytes(StandardCharsets.UTF_8); // Dịch chuỗi thành mảng byte UTF-8
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress); // Đóng gói mảng
                                                                                                      // byte và địa chỉ
                                                                                                      // đích
            socket.send(sendPacket); // Bắn gói tin UDP qua mạng
        } catch (IOException e) { // Bắt lỗi gửi
            System.err.println("Loi gui phan hoi UDP: " + e.getMessage()); // In log báo lỗi
        } // Kết thúc try-catch
    } // Kết thúc hàm

    // Lớp chứa dữ liệu trạng thái của mỗi người dùng (dùng để đếm dòng)
    private static class ClientSession { // Khai báo lớp nội bộ
        int lineCount = 1; // Khởi tạo số đếm dòng bắt đầu từ 1
    } // Kết thúc lớp
} // Kết thúc lớp SumUdpServer