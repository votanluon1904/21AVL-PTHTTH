import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleReaderDemo {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        int count = 0;
        System.out.println("Nhap van ban; nhap q de ket thuc:");
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null || line.equalsIgnoreCase("q")) {
                    break;
                }
                count++;
                System.out.printf("Dong %d: %s%n", count, line);
            }

        } catch (IOException e) {
            System.err.println("Khong the doc du lieu: " + e.getMessage());
        }
        System.out.println("Tong so dong da nhap: " + count);
    }
}
// chay truoc chcp 65001
// sau do chay javac -encoding UTF-8 ConsoleReaderDemo.java
// va chay java -Dfile.encoding=UTF-8 ConsoleReaderDemo
/*
 * 1. Vì sao cần InputStreamReader giữa System.in và BufferedReader?
 * Bởi vì chúng làm việc với các loại dữ liệu khác nhau. InputStreamReader đóng
 * vai trò là cầu nối (bridge) chuyển đổi dữ liệu:
 * 
 * System.in là một luồng byte (Byte Stream), nó chỉ đọc các byte thô (0 và 1)
 * từ hệ điều hành.
 * 
 * BufferedReader là một luồng ký tự (Character Stream), nó cần nhận vào các ký
 * tự (text) để xử lý.
 * 
 * InputStreamReader đứng ở giữa để lấy các byte từ System.in và "dịch" chúng
 * thành các ký tự có nghĩa dựa trên một bảng mã nhất định (trong code của bạn
 * là UTF-8). Nếu không có nó, BufferedReader sẽ không thể hiểu được dữ liệu từ
 * bàn phím.
 * 
 * 2. readLine() trả về giá trị nào khi gặp EOF?
 * Hàm readLine() sẽ trả về null khi luồng dữ liệu kết thúc (End of File - EOF)
 * và không còn gì để đọc nữa.
 * 
 * Lưu ý: Khi chạy trên console, bạn có thể tạo tín hiệu EOF bằng cách nhấn tổ
 * hợp phím Ctrl + D (trên Linux/Mac) hoặc Ctrl + Z rồi Enter (trên Windows). Đó
 * là lý do trong code có câu lệnh kiểm tra if (line == null).
 * 
 * 3. Vì sao ví dụ không đóng reader gắn với System.in?
 * Nếu bạn gọi lệnh reader.close(), nó sẽ đóng luôn luồng dữ liệu gốc bên dưới
 * là System.in.
 * Khi System.in đã bị đóng, bạn sẽ vĩnh viễn không thể đọc thêm bất kỳ dữ liệu
 * nào từ bàn phím trong suốt phần còn lại của chương trình (cho đến khi khởi
 * động lại ứng dụng). Các luồng tiêu chuẩn của hệ thống như System.in,
 * System.out, và System.err được quản lý bởi máy ảo Java (JVM) và thường không
 * bao giờ nên được đóng thủ công.
 */