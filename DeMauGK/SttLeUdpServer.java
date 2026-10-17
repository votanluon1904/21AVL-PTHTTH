package DeMauGK; // Khai báo package chứa file mã nguồn

import java.io.IOException; // Thư viện xử lý ngoại lệ liên quan đến luồng vào/ra
import java.net.DatagramPacket; // Lớp đại diện cho một gói tin UDP (Datagram)
import java.net.DatagramSocket; // Lớp tạo socket để gửi/nhận gói tin UDP
import java.net.SocketAddress; // Lớp lưu trữ địa chỉ IP và Port của máy trạm (Client)
import java.nio.charset.StandardCharsets; // Thư viện hỗ trợ bảng mã UTF-8
import java.util.concurrent.ConcurrentHashMap; // Cấu trúc dữ liệu Map hỗ trợ đa luồng an toàn (Thread-safe)
import java.util.concurrent.ExecutorService; // Giao diện quản lý tập hợp các luồng (Thread pool)
import java.util.concurrent.Executors; // Lớp tiện ích để tạo ra Thread pool

public class SttLeUdpServer { // Khai báo lớp public cho Server UDP
    private static final int PORT = 7000; // Khai báo hằng số lưu cổng mạng 7000 theo đề bài

    // Tạo bảng băm lưu trạng thái của từng Client riêng biệt (phân biệt qua
    // IP:Port) vì UDP không có kết nối cố định
    private static final ConcurrentHashMap<SocketAddress, ClientSession> clientSessions = new ConcurrentHashMap<>();

    public static void main(String[] args) { // Phương thức chạy chính của chương trình
        ExecutorService pool = Executors.newFixedThreadPool(10); // Tạo một Thread pool chứa tối đa 10 luồng chạy song
                                                                 // song

        try (DatagramSocket socket = new DatagramSocket(PORT)) { // Khởi tạo UDP Socket lắng nghe tại cổng 7000
            System.out.println("UDP Server (STT Le) dang chay tai port " + PORT); // In thông báo Server đã bật thành
                                                                                  // công
            byte[] receiveBuffer = new byte[2048]; // Tạo mảng byte làm bộ đệm để chứa dữ liệu nhận được (tối đa 2KB)

            while (true) { // Vòng lặp vô tận để Server luôn ở trạng thái chờ gói tin tới
                // Tạo một DatagramPacket rỗng để hứng dữ liệu từ Client gửi tới
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket); // Lệnh chặn (block) để chờ cho đến khi có gói tin bay vào socket

                // Chuyển đổi mảng byte nhận được thành chuỗi String, dùng bảng mã UTF-8 và xóa
                // khoảng trắng thừa 2 đầu
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                        StandardCharsets.UTF_8).trim();
                SocketAddress clientAddress = receivePacket.getSocketAddress(); // Lấy địa chỉ IP và Cổng của Client vừa
                                                                                // gửi gói tin

                // Đẩy công việc xử lý tin nhắn vào Thread pool để Server có thể quay lại hứng
                // tin nhắn khác ngay lập tức
                pool.submit(() -> handleClientMessage(socket, clientAddress, message));
            }
        } catch (IOException e) { // Bắt lỗi nếu không thể mở cổng hoặc lỗi mạng
            System.err.println("Loi server: " + e.getMessage()); // In ra thông báo lỗi
        } finally { // Khối lệnh luôn chạy ở cuối
            pool.shutdown(); // Đóng Thread pool để giải phóng tài nguyên hệ thống
        }
    }

    // Hàm xử lý logic tin nhắn gửi từ Client
    private static void handleClientMessage(DatagramSocket socket, SocketAddress clientAddress, String message) {
        // Tìm phiên làm việc (Session) của Client này, nếu chưa có thì tạo mới một
        // ClientSession
        ClientSession session = clientSessions.computeIfAbsent(clientAddress, k -> new ClientSession());
        String response; // Biến lưu trữ câu trả lời sẽ gửi về cho Client

        if (message.equals("HELLO_SERVER")) { // Nếu Client vừa gửi thông điệp chào hỏi đầu tiên
            // Tạo chuỗi Menu chứa danh sách dịch vụ STT Lẻ (1 và 3)
            response = "=== DANH SACH DICH VU (STT LE) ===\n"
                    + "1: Dao nguoc toan bo chuoi va in hoa ky tu dau\n"
                    + "3: Dem so luong tu cua tat ca cac dong\n"
                    + "Chon dich vu (1 hoac 3):";
            session.reset(); // Reset lại toàn bộ trạng thái của Client này về ban đầu
        } else if (message.equals("EXIT.")) { // Nếu Client muốn thoát
            response = "Da dong phien lam viec. Tam biet!"; // Gửi lời chào tạm biệt
            clientSessions.remove(clientAddress); // Xóa thông tin Client này khỏi bộ nhớ Server
        } else if (!session.hasChosenService) { // Nếu Client chưa chọn dịch vụ nào
            try { // Thử chuyển đổi chuỗi tin nhắn thành số nguyên
                int choice = Integer.parseInt(message); // Ép kiểu String sang int
                if (choice == 1 || choice == 3) { // Nếu lựa chọn hợp lệ (1 hoặc 3)
                    session.serviceChoice = choice; // Lưu lại lựa chọn vào Session của Client
                    session.hasChosenService = true; // Đánh dấu là đã chọn dịch vụ xong
                    response = "Da chon dich vu " + choice + ". Nhap cac dong chuoi (nhap '.' de ket thuc):"; // Báo
                                                                                                              // thành
                                                                                                              // công
                } else { // Nếu chọn số khác 1 và 3
                    response = "Loi: STT Le chi duoc ho tro dich vu 1 va 3."; // Báo lỗi sai dịch vụ
                }
            } catch (NumberFormatException e) { // Bắt lỗi nếu Client nhập chữ thay vì nhập số
                response = "Loi: Vui long nhap so 1 hoac 3."; // Yêu cầu nhập lại số
            }
        } else { // Nếu Client đã chọn dịch vụ và đang gửi dữ liệu văn bản
            if (message.isEmpty()) { // Nếu Client gửi một chuỗi rỗng (chỉ nhấn Enter)
                response = "Loi: Ban vua gui dong trong. Vui long nhap chuoi hoac dau '.'"; // Báo lỗi
            } else if (message.equals(".")) { // Nếu Client gửi dấu chấm để chốt dữ liệu
                if (session.serviceChoice == 3) { // Nếu đang dùng Dịch vụ 3 (đếm từ)
                    // Trả về tổng số từ đã cộng dồn qua nhiều lần gửi
                    response = "Tong so tu cua tat ca cac dong: " + session.totalWords
                            + " --- (Nhap tiep hoac 'EXIT.' de thoat)";
                    session.totalWords = 0; // Đặt lại bộ đếm về 0 cho lượt nhập tiếp theo
                } else { // Nếu đang dùng Dịch vụ 1
                    response = "Hoan thanh xu ly. Ban co the nhap tiep hoac 'EXIT.' de thoat."; // Thông báo hoàn tất
                }
            } else { // Nếu Client gửi văn bản bình thường cần xử lý
                switch (session.serviceChoice) { // Kiểm tra xem Client đang dùng dịch vụ số mấy
                    case 1: // Nếu là Dịch vụ 1
                        response = reverseEntireStringAndCapitalize(message); // Gọi hàm đảo chuỗi và in hoa
                        break; // Thoát switch
                    case 3: // Nếu là Dịch vụ 3
                        session.totalWords += countWords(message); // Đếm số từ của dòng này và cộng dồn vào Session
                        response = "-> Da ghi nhan. Dang cho dong tiep theo hoac dau '.'"; // Gửi phản hồi tránh treo
                                                                                           // Client
                        break; // Thoát switch
                    default: // Trường hợp không xác định
                        response = "Loi dich vu."; // Báo lỗi
                }
            }
        }
        sendToClient(socket, clientAddress, response); // Gọi hàm để gửi chuỗi `response` về lại cho Client
    }

    // Hàm phụ trợ dùng để đóng gói và gửi tin nhắn UDP đi
    private static void sendToClient(DatagramSocket socket, SocketAddress clientAddress, String response) {
        try { // Bắt đầu khối xử lý có khả năng sinh lỗi
            byte[] sendData = response.getBytes(StandardCharsets.UTF_8); // Chuyển chuỗi trả lời thành mảng byte UTF-8
            // Tạo gói tin UDP chứa dữ liệu, độ dài dữ liệu, và địa chỉ đích (Client)
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress);
            socket.send(sendPacket); // Bắn gói tin UDP qua mạng
        } catch (IOException e) { // Bắt lỗi mạng nếu có
            System.err.println("Loi gui phan hoi UDP: " + e.getMessage()); // In ra lỗi
        }
    }

    // Lớp nội bộ để lưu trữ trạng thái của từng Client
    private static class ClientSession {
        boolean hasChosenService = false; // Cờ đánh dấu Client đã chọn số dịch vụ hay chưa
        int serviceChoice = -1; // Lưu số dịch vụ (1 hoặc 3)
        int totalWords = 0; // Biến lưu tổng số từ (dành riêng cho DV3)

        void reset() { // Hàm để cài đặt lại trạng thái về mặc định
            hasChosenService = false; // Trả cờ về false
            serviceChoice = -1; // Xóa số dịch vụ
            totalWords = 0; // Đưa bộ đếm từ về 0
        }
    }

    // Dịch vụ 1: Đảo ngược toàn bộ chuỗi và in hoa chữ đầu
    private static String reverseEntireStringAndCapitalize(String text) {
        // Dùng StringBuilder để lật ngược toàn bộ chuỗi văn bản (VD: "ABC DEF" -> "FED
        // CBA")
        String reversedString = new StringBuilder(text).reverse().toString();
        // Cắt chuỗi đã lật ngược thành các từ riêng biệt dựa trên khoảng trắng
        String[] words = reversedString.split("\\s+");
        StringBuilder result = new StringBuilder(); // Tạo biến để ghép kết quả cuối cùng
        for (String word : words) { // Duyệt qua từng từ trong mảng
            if (!word.isEmpty()) { // Đảm bảo từ không bị rỗng
                result.append(capitalizeFirstLetter(word)).append(" "); // In hoa chữ đầu, ghép vào kết quả và thêm dấu
                                                                        // cách
            }
        }
        return result.toString().trim(); // Trả về chuỗi kết quả và xóa dấu cách dư ở cuối
    }

    // Hàm phụ trợ: In hoa chữ cái đầu tiên của một từ
    private static String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty())
            return word; // Nếu từ rỗng thì trả về nguyên bản
        // Lấy ký tự đầu tiên viết hoa, cộng với phần còn lại của từ viết thường
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    // Dịch vụ 3: Đếm số lượng từ trong một dòng văn bản
    private static int countWords(String text) {
        if (text == null || text.trim().isEmpty())
            return 0; // Nếu chuỗi rỗng thì không có từ nào (trả về 0)
        return text.trim().split("\\s+").length; // Cắt chuỗi bằng khoảng trắng và trả về kích thước mảng (số từ)
    }
}