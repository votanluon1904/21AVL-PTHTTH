package DeMauGK; // Khai báo package chứa mã nguồn

import java.io.BufferedReader; // Import thư viện tạo bộ đệm đọc luồng văn bản
import java.io.IOException; // Import thư viện xử lý các lỗi ngoại lệ I/O
import java.io.InputStreamReader; // Import thư viện chuyển đổi luồng byte thành ký tự
import java.io.OutputStreamWriter; // Import thư viện chuyển đổi ký tự thành luồng byte
import java.io.PrintWriter; // Import thư viện ghi văn bản theo định dạng dễ dàng
import java.net.ServerSocket; // Import lớp khởi tạo cổng dịch vụ cho server
import java.net.Socket; // Import lớp tạo đường ống giao tiếp mạng
import java.nio.charset.StandardCharsets; // Import thư viện cung cấp bảng mã UTF-8
import java.util.concurrent.ExecutorService; // Import giao diện quản lý hồ chứa luồng (Thread pool)
import java.util.concurrent.Executors; // Import lớp tiện ích để khởi tạo Thread pool

public class SumTcpServer { // Khai báo lớp Server public
    private static final int PORT = 7000; // Khai báo hằng số PORT 7000 để Server lắng nghe

    public static void main(String[] args) { // Hàm main thực thi điểm bắt đầu của chương trình
        ExecutorService pool = Executors.newFixedThreadPool(10); // Khởi tạo Thread pool hỗ trợ xử lý 10 Client cùng lúc
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Mở ServerSocket tại cổng PORT đã định
            System.out.println("Server Tinh Tong dang chay tai port " + PORT); // In thông báo Server đã bật thành công
            while (true) { // Vòng lặp vô tận giúp Server luôn ở trạng thái chờ đón Client
                Socket clientSocket = serverSocket.accept(); // Đứng chờ và chấp nhận kết nối khi có Client gọi tới
                pool.submit(new ClientHandler(clientSocket)); // Phân công luồng riêng biệt cho Client mới vào Thread
                                                              // pool
            } // Kết thúc vòng lặp while
        } catch (IOException e) { // Bắt các lỗi xảy ra nếu cổng mạng bị chiếm dụng
            System.err.println("Loi server: " + e.getMessage()); // In thông báo lỗi ra màn hình
        } finally { // Khối lệnh luôn được thi hành vào cuối vòng đời chương trình
            pool.shutdown(); // Đóng Thread pool, ngừng tiếp nhận thêm tác vụ mới
        } // Kết thúc try-catch
    } // Kết thúc hàm main

    private static class ClientHandler implements Runnable { // Khai báo lớp con đảm nhận việc xử lý từng Client (Đa
                                                             // luồng)
        private final Socket socket; // Biến hằng lưu trữ Socket của Client hiện tại

        public ClientHandler(Socket socket) { // Constructor nhận tham số socket từ hàm main truyền vào
            this.socket = socket; // Gán tham số vào biến của lớp
        } // Kết thúc constructor

        @Override // Cú pháp ghi đè phương thức run của giao diện Runnable
        public void run() { // Khai báo hàm run chứa logic nghiệp vụ chính
            // Khởi tạo luồng đọc (in) và luồng ghi (out) có hỗ trợ tiếng Việt (UTF-8)
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)); // Tạo bộ đệm đọc dữ liệu
                                                                                             // do Client gửi
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) { // Tạo bộ
                                                                                                               // đệm
                                                                                                               // ghi
                                                                                                               // trả về
                                                                                                               // Client
                                                                                                               // (auto-flush)

                out.println(
                        "Da ket noi! Nhap cac so nguyen, ket thuc bang '.' (VD: 12 13 0 25 0.) hoac 'exit' de thoat."); // Gửi
                                                                                                                        // lời
                                                                                                                        // chào
                                                                                                                        // và
                                                                                                                        // hướng
                                                                                                                        // dẫn

                int lineCount = 1; // Khởi tạo biến đếm để theo dõi số thứ tự của dòng (dòng 1, dòng 2...)
                String line; // Khai báo biến lưu chuỗi văn bản nhận được

                while ((line = in.readLine()) != null) { // Liên tục đọc từng dòng tin nhắn từ Client cho đến khi mất
                                                         // kết nối
                    line = line.trim(); // Cắt bỏ các khoảng trắng thừa ở đầu và cuối chuỗi

                    if (line.equalsIgnoreCase("exit")) { // Kiểm tra xem Client có gửi lệnh "exit" (không phân biệt hoa
                                                         // thường) hay không
                        out.println("Tam biet!"); // Trả lời xác nhận tạm biệt
                        break; // Chủ động bẻ gãy vòng lặp để đóng kết nối theo đúng yêu cầu đề bài
                    } // Kết thúc lệnh if

                    boolean hasDot = line.contains("."); // Quét xem trong dòng Client gửi có chứa dấu chấm "." không
                    line = line.replace(".", ""); // Tạm thời xóa dấu chấm khỏi chuỗi để các hàm ép kiểu số nguyên không
                                                  // bị lỗi

                    if (line.isEmpty() && hasDot) { // Trường hợp người dùng chỉ gõ đúng một dấu chấm rồi Enter
                        out.println("Da nhan dau '.'. Hay nhap 'exit' de dong ket noi."); // Báo hiệu đã nhận lệnh dừng
                        continue; // Bỏ qua đoạn tính tổng bên dưới và quay lại đầu vòng lặp
                    } // Kết thúc if

                    String[] numbers = line.split("\\s+"); // Tách chuỗi văn bản thành mảng các số, ngăn cách nhau bằng
                                                           // khoảng trắng
                    long sum = 0; // Khởi tạo biến lưu tổng (Dùng long thay vì int để đề phòng tổng quá lớn gây
                                  // tràn bộ nhớ)

                    try { // Mở khối xử lý ép kiểu số
                        for (String numStr : numbers) { // Vòng lặp duyệt qua từng chuỗi con trong mảng numbers
                            if (!numStr.isEmpty()) { // Kiểm tra đảm bảo chuỗi con không bị rỗng
                                sum += Long.parseLong(numStr); // Ép chuỗi thành kiểu số nguyên và cộng dồn vào tổng
                                                               // chung
                            } // Kết thúc if
                        } // Kết thúc vòng lặp for

                        out.println("Tổng dòng " + lineCount + ": " + sum); // Định dạng đúng theo đề bài và trả kết quả
                                                                            // tổng về cho Client
                        lineCount++; // Tăng biến đếm số thứ tự dòng lên 1 đơn vị
                    } catch (NumberFormatException e) { // Bắt lỗi nếu Client cố tình nhập chữ cái thay vì số
                        out.println("Loi: Vui long chi nhap so nguyen."); // Cảnh báo lỗi
                    } // Kết thúc try-catch

                    if (hasDot) { // Nếu ở trên phát hiện dòng này có chứa dấu chấm (kết thúc quá trình nhập)
                        out.println("-> Da nhan dau '.'. Qua trinh nhap lieu dung lai. Hay go 'exit' de dong ket noi."); // Phản
                                                                                                                         // hồi
                                                                                                                         // yêu
                                                                                                                         // cầu
                                                                                                                         // Client
                                                                                                                         // gõ
                                                                                                                         // lệnh
                                                                                                                         // thoát
                    } // Kết thúc if
                } // Kết thúc vòng lặp while lắng nghe
            } catch (IOException e) { // Bắt các lỗi I/O rớt mạng giữa chừng
                System.err.println("Client ngat ket noi dot ngot."); // Ghi log ra màn hình Server
            } finally { // Khối lệnh dọn dẹp
                try {
                    socket.close();
                } catch (IOException e) {
                } // Bảo đảm Socket của Client luôn được đóng an toàn để tránh rò rỉ tài nguyên
            } // Kết thúc try-catch-finally
        } // Kết thúc hàm run
    } // Kết thúc lớp ClientHandler
} // Kết thúc lớp SumTcpServer