package DeMauGK; // Khai báo thư mục chứa file mã nguồn theo đúng cấu trúc dự án

import java.io.IOException; // Thư viện bắt các lỗi liên quan đến luồng vào/ra dữ liệu
import java.net.DatagramPacket; // Lớp đại diện cho gói tin UDP (Datagram)
import java.net.DatagramSocket; // Lớp tạo cổng mạng để gửi và nhận gói tin UDP
import java.net.SocketAddress; // Lớp lưu trữ thông tin kết hợp giữa IP và Port của Client
import java.nio.charset.StandardCharsets; // Thư viện hỗ trợ mã hóa chuỗi sang chuẩn UTF-8
import java.util.concurrent.ConcurrentHashMap; // Cấu trúc từ điển (Map) hỗ trợ chạy đa luồng an toàn
import java.util.concurrent.ExecutorService; // Giao diện quản lý các luồng (Thread pool)
import java.util.concurrent.Executors; // Lớp hỗ trợ tạo ra Thread pool nhanh chóng

public class SttChanUdpServer { // Khởi tạo lớp public cho Server UDP
    private static final int PORT = 7000; // Khai báo hằng số cổng mạng 7000 theo yêu cầu đề bài

    // Bảng băm lưu trạng thái làm việc của từng Client (vì UDP không tự duy trì kết
    // nối như TCP)
    private static final ConcurrentHashMap<SocketAddress, ClientSession> clientSessions = new ConcurrentHashMap<>();

    public static void main(String[] args) { // Hàm Main chạy chương trình Server
        ExecutorService pool = Executors.newFixedThreadPool(10); // Tạo Thread pool chứa tối đa 10 luồng chạy cùng lúc

        try (DatagramSocket socket = new DatagramSocket(PORT)) { // Mở UDP Socket tại cổng 7000
            System.out.println("UDP Server (STT Chan) dang chay tai port " + PORT); // Báo hiệu Server đã bật
            byte[] receiveBuffer = new byte[2048]; // Tạo mảng byte 2KB làm bộ đệm để hứng dữ liệu đến

            while (true) { // Vòng lặp vô tận giữ Server luôn lắng nghe
                // Tạo một gói tin rỗng để hứng dữ liệu từ mảng đệm
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket); // Đứng đợi (chặn) cho đến khi có gói tin UDP bay tới

                // Giải mã dữ liệu mảng byte thành chuỗi String UTF-8 và cắt khoảng trắng 2 đầu
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                        StandardCharsets.UTF_8).trim();
                SocketAddress clientAddress = receivePacket.getSocketAddress(); // Trích xuất IP và Port của người gửi

                // Đẩy logic xử lý tin nhắn vào Thread pool để Server rảnh tay hứng tin nhắn
                // tiếp theo ngay lập tức
                pool.submit(() -> handleClientMessage(socket, clientAddress, message));
            }
        } catch (IOException e) { // Bắt các lỗi không thể mở port hoặc lỗi mạng
            System.err.println("Loi server: " + e.getMessage()); // In ra thông báo lỗi
        } finally { // Khối lệnh luôn thực thi khi chương trình kết thúc
            pool.shutdown(); // Đóng Thread pool, giải phóng RAM và CPU
        }
    }

    // Hàm xử lý nghiệp vụ đối với tin nhắn vừa nhận
    private static void handleClientMessage(DatagramSocket socket, SocketAddress clientAddress, String message) {
        // Tìm lịch sử (Session) của Client, nếu là người mới thì tạo một Session mới
        // tinh
        ClientSession session = clientSessions.computeIfAbsent(clientAddress, k -> new ClientSession());
        String response; // Biến chứa câu trả lời sẽ gửi lại cho Client

        if (message.equals("HELLO_SERVER")) { // Nếu Client vừa gửi lời chào mở màn
            // Tạo chuỗi danh sách dịch vụ cho số thứ tự Chẵn (2 và 4)
            response = "=== DANH SACH DICH VU (STT CHAN) ===\n"
                    + "2: Dao nguoc tu va in hoa ky tu dau, giu nguyen vi tri\n"
                    + "4: Dem so luong tu tung dong\n"
                    + "Chon dich vu (2 hoac 4):";
            session.reset(); // Xóa sạch trạng thái cũ nếu Client này vừa khởi động lại
        } else if (message.equals("EXIT.")) { // Nếu Client ra lệnh thoát
            response = "Da dong phien lam viec. Tam biet!"; // Soạn lời chia tay
            clientSessions.remove(clientAddress); // Xóa thông tin Client này khỏi bộ nhớ tạm
        } else if (!session.hasChosenService) { // Nếu Client chưa chọn dịch vụ (đang ở bước nhập menu)
            try { // Thử chuyển đổi chữ Client nhập thành số
                int choice = Integer.parseInt(message); // Ép kiểu từ String sang số nguyên (int)
                if (choice == 2 || choice == 4) { // Kiểm tra xem Client có chọn đúng 2 hoặc 4 không
                    session.serviceChoice = choice; // Ghi nhớ lựa chọn vào Session
                    session.hasChosenService = true; // Bật cờ đánh dấu đã chọn xong
                    response = "Da chon dich vu " + choice + ". Nhap tung dong chuoi (nhap '.' de ket thuc):"; // Báo OK
                } else { // Nếu nhập số khác 2 và 4
                    response = "Loi: STT Chan chi duoc ho tro dich vu 2 va 4."; // Từ chối phục vụ
                }
            } catch (NumberFormatException e) { // Bắt lỗi nếu Client gõ chữ (ví dụ "abc")
                response = "Loi: Vui long nhap so 2 hoac 4."; // Báo lỗi sai định dạng
            }
        } else { // Nếu Client đã chọn xong dịch vụ và đang gửi văn bản để xử lý
            if (message.isEmpty()) { // Nếu Client bấm Enter mà không gõ gì
                response = "Loi: Ban vua gui dong trong. Vui long nhap chuoi hoac dau '.'"; // Yêu cầu nhập lại
            } else if (message.equals(".")) { // Nếu Client gửi dấu chấm đứng 1 mình
                // Thông báo hoàn thành một phiên nhập liệu, nhưng không ngắt kết nối
                response = "Hoan thanh xu ly. Ban co the nhap tiep hoac 'EXIT.' de thoat.";
            } else { // Nếu Client gửi văn bản hợp lệ
                if (session.serviceChoice == 2) { // Nếu đang dùng Dịch vụ 2
                    response = reverseWordsKeepPosition(message); // Gọi hàm xử lý lật ngược từ và in hoa
                } else if (session.serviceChoice == 4) { // Nếu đang dùng Dịch vụ 4
                    response = "So tu trong dong: " + countWords(message); // Gọi hàm đếm số từ trên dòng đó
                } else { // Trường hợp dự phòng lỗi logic
                    response = "Loi dich vu khong xac dinh.";
                }
            }
        }
        sendToClient(socket, clientAddress, response); // Gọi hàm gửi câu trả lời (response) về lại Client
    }

    // Hàm phụ trợ đóng gói String và gửi qua UDP
    private static void sendToClient(DatagramSocket socket, SocketAddress clientAddress, String response) {
        try { // Dùng try-catch để phòng ngừa lỗi rớt mạng
            byte[] sendData = response.getBytes(StandardCharsets.UTF_8); // Dịch chuỗi thành mảng byte UTF-8
            // Nạp mảng byte, độ dài, và địa chỉ đích vào một gói tin UDP
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress);
            socket.send(sendPacket); // Gửi gói tin đi
        } catch (IOException e) { // Nếu việc gửi thất bại
            System.err.println("Loi gui phan hoi UDP: " + e.getMessage()); // In log báo lỗi
        }
    }

    // Lớp chứa dữ liệu trạng thái của mỗi người dùng
    private static class ClientSession {
        boolean hasChosenService = false; // Đánh dấu người dùng đã vượt qua bước Menu chưa
        int serviceChoice = -1; // Lưu mã dịch vụ người dùng đang chạy (2 hoặc 4)

        void reset() { // Hàm khôi phục mọi thứ về như cũ
            hasChosenService = false;
            serviceChoice = -1;
        }
    }

    // Dịch vụ 2: Lật ngược từng từ riêng lẻ và in hoa chữ cái đầu (VD: PHAT -> TAHP
    // -> Tahp)
    private static String reverseWordsKeepPosition(String text) {
        String[] words = text.split("\\s+"); // Chặt câu thành nhiều từ con bằng khoảng trắng
        StringBuilder result = new StringBuilder(); // Tạo bộ nối chuỗi để tăng tốc độ xử lý
        for (String word : words) { // Lặp qua từng từ
            if (!word.isEmpty()) { // Nếu từ không bị rỗng
                // Lật ngược thứ tự ký tự của từ đó (VD: "abc" -> "cba")
                String reversedWord = new StringBuilder(word).reverse().toString();
                // In hoa chữ đầu của từ vừa lật ngược, rồi nối vào kết quả chung kèm 1 dấu cách
                result.append(capitalizeFirstLetter(reversedWord)).append(" ");
            }
        }
        return result.toString().trim(); // Trả về kết quả và cắt đi dấu cách bị thừa ở cuối cùng
    }

    // Hàm phụ trợ: In hoa chữ cái đầu tiên
    private static String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty())
            return word; // Trả lại nếu từ bị rỗng
        // Lấy chữ đầu tiên viết hoa (toUpperCase) ghép với phần còn lại viết thường
        // (toLowerCase)
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    // Dịch vụ 4: Đếm số lượng từ có trong một câu
    private static int countWords(String text) {
        if (text == null || text.trim().isEmpty())
            return 0; // Trả về 0 nếu người dùng nhập toàn dấu cách
        return text.trim().split("\\s+").length; // Cắt chuỗi theo khoảng trắng và đếm xem có bao nhiêu phần tử
    }
}