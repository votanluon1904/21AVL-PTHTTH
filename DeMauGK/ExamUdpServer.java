
package DeMauGK; // Khai báo package chứa lớp ExamUdpServer

import java.io.IOException; // Hỗ trợ xử lý lỗi nhập xuất
import java.net.DatagramPacket; // Đóng gói dữ liệu UDP để gửi và nhận
import java.net.DatagramSocket; // Tạo socket giao tiếp bằng UDP
import java.net.InetSocketAddress; // Biểu diễn địa chỉ IP và cổng mạng
import java.net.SocketAddress; // Biểu diễn địa chỉ của Client
import java.nio.charset.StandardCharsets; // Sử dụng bảng mã UTF-8
import java.util.concurrent.ConcurrentHashMap; // Lưu trạng thái nhiều Client an toàn giữa các luồng
import java.util.concurrent.ExecutorService; // Quản lý nhóm luồng xử lý công việc
import java.util.concurrent.Executors; // Tạo ThreadPool

public class ExamUdpServer { // Khai báo lớp UDP Server

    private static final int PORT = 7000;
    // Cổng UDP mà Server sử dụng để nhận dữ liệu từ Client

    // Lưu trạng thái làm việc riêng của từng Client
    // Key: địa chỉ IP và cổng nguồn của Client
    // Value: đối tượng ClientSession chứa thông tin phiên làm việc
    // ConcurrentHashMap hỗ trợ nhiều luồng truy cập bản đồ an toàn hơn
    private static final ConcurrentHashMap<SocketAddress, ClientSession> clientSessions = new ConcurrentHashMap<>();

    public static void main(String[] args) { // Hàm main, điểm bắt đầu chương trình

        // Tạo ThreadPool có tối đa 10 luồng xử lý công việc
        ExecutorService pool = Executors.newFixedThreadPool(10);

        // Tạo DatagramSocket và gắn với cổng 7000
        // try-with-resources tự động đóng socket khi thoát khối try
        try (DatagramSocket socket = new DatagramSocket(PORT)) {

            // Thông báo Server đã khởi động thành công
            System.out.println("UDP Server is running on port " + PORT);

            // Tạo bộ đệm 2048 byte để nhận dữ liệu UDP
            byte[] receiveBuffer = new byte[2048];

            // Vòng lặp nhận dữ liệu liên tục từ các Client
            while (true) {

                // Tạo gói tin nhận, sử dụng bộ đệm đã tạo
                DatagramPacket receivePacket = new DatagramPacket(
                        receiveBuffer, receiveBuffer.length);

                // Chờ nhận một gói UDP
                // Lệnh này chờ cho đến khi có gói tin được gửi đến
                socket.receive(receivePacket);

                // Trích xuất nội dung gói tin và giải mã bằng UTF-8
                // getData(): lấy mảng byte chứa dữ liệu
                // 0: vị trí bắt đầu đọc trong mảng
                // getLength(): số byte thực tế nhận được
                // trim(): loại bỏ khoảng trắng ở đầu và cuối chuỗi
                String message = new String(
                        receivePacket.getData(),
                        0,
                        receivePacket.getLength(),
                        StandardCharsets.UTF_8).trim();

                // Lấy địa chỉ IP và cổng nguồn của Client gửi gói tin
                SocketAddress clientAddress = receivePacket.getSocketAddress();

                // Gửi công việc xử lý gói tin vào ThreadPool
                // Luồng nhận chính có thể tiếp tục nhận gói tin tiếp theo
                // Lambda gọi hàm xử lý với socket, địa chỉ Client và nội dung tin nhắn
                pool.submit(() -> handleClientMessage(
                        socket, clientAddress, message));
            }

        } catch (IOException e) { // Xử lý lỗi khi khởi tạo hoặc nhận dữ liệu UDP

            // Hiển thị thông báo lỗi của Server
            System.err.println("Server error: " + e.getMessage());

        } finally {

            // Yêu cầu ThreadPool ngừng nhận công việc mới
            // Các công việc đã được gửi vào có thể tiếp tục hoàn thành
            pool.shutdown();
        }
    }

    // Xử lý một thông điệp nhận được từ một Client
    private static void handleClientMessage(
            DatagramSocket socket, // Socket UDP dùng để gửi phản hồi
            SocketAddress clientAddress, // Địa chỉ IP và cổng của Client
            String message // Nội dung Client gửi đến
    ) {

        // Tìm phiên làm việc của Client
        // Nếu chưa có thì tạo ClientSession mới và lưu vào bản đồ
        ClientSession session = clientSessions.computeIfAbsent(
                clientAddress, k -> new ClientSession());

        // Biến chứa nội dung phản hồi sẽ gửi lại cho Client
        String response;

        // Kiểm tra Client có gửi thông điệp khởi tạo hay không
        if (message.equals("HELLO_SERVER")) {

            // Tạo menu dịch vụ thành một chuỗi duy nhất
            // Ký tự \n giúp hiển thị từng mục trên một dòng
            response = "=== DANH SACH DICH VU ===\n"
                    + "1: Dao nguoc toan bo chuoi va in hoa ky tu dau (STT LE)\n"
                    + "2: Dao nguoc tu va in hoa ky tu dau, giu nguyen vi tri (STT CHAN)\n"
                    + "3: Dem so luong tu cua tat ca cac dong (STT LE)\n"
                    + "4: Dem so luong tu tung dong (STT CHAN)\n"
                    + "Chon dich vu (1-4):";

            // Đặt lại trạng thái phiên làm việc khi Client gửi HELLO_SERVER
            session.reset();

        } else if (message.equals("EXIT.")) {

            // Tạo thông báo xác nhận kết thúc phiên làm việc
            response = "Da dong phien lam viec. Tam biet!";

            // Xóa phiên của Client khỏi bản đồ để giải phóng dữ liệu
            clientSessions.remove(clientAddress);

        } else if (!session.hasChosenService) {

            // Nếu Client chưa chọn dịch vụ thì xem thông điệp là lựa chọn
            try {

                // Chuyển chuỗi lựa chọn thành số nguyên
                int choice = Integer.parseInt(message);

                // Chỉ chấp nhận các lựa chọn từ 1 đến 4
                if (choice >= 1 && choice <= 4) {

                    // Lưu dịch vụ mà Client đã chọn
                    session.serviceChoice = choice;

                    // Đánh dấu Client đã chọn dịch vụ
                    session.hasChosenService = true;

                    // Thông báo xác nhận và yêu cầu Client nhập dữ liệu
                    response = "Da chon dich vu " + choice
                            + ". Nhap cac dong chuoi (nhap '.' de ket thuc):";

                } else {

                    // Báo lỗi nếu số được nhập nằm ngoài khoảng 1 đến 4
                    response = "Loi: Lua chon dich vu khong hop le. Vui long chon 1-4.";
                }

            } catch (NumberFormatException e) {

                // Báo lỗi nếu thông điệp không thể chuyển thành số nguyên
                response = "Loi: Vui long nhap mot so tu 1 den 4.";
            }

        } else {

            // Client đã chọn dịch vụ, bắt đầu xử lý dữ liệu đầu vào

            // Kiểm tra nội dung có rỗng hay không
            if (message.isEmpty()) {

                // Yêu cầu Client nhập chuỗi hoặc dấu chấm
                response = "Loi: Ban vua gui dong trong. Vui long nhap chuoi hoac dau '.'";

            } else if (message.equals(".")) {

                // Dấu chấm dùng để kết thúc một đợt nhập dữ liệu

                // Nếu Client chọn dịch vụ 3 thì trả tổng số từ đã đếm
                if (session.serviceChoice == 3) {

                    // Tạo thông báo tổng số từ của tất cả các dòng
                    response = "Tong so tu cua tat ca cac dong: "
                            + session.totalWords
                            + " --- (Nhap tiep hoac 'EXIT.' de thoat)";

                    // Đặt lại bộ đếm để bắt đầu đợt nhập mới
                    session.totalWords = 0;

                } else {

                    // Với các dịch vụ còn lại, thông báo hoàn thành xử lý
                    response = "Hoan thanh xu ly. Ban co the nhap tiep hoac 'EXIT.' de thoat.";
                }

            } else {

                // Chọn cách xử lý dựa trên dịch vụ mà Client đã đăng ký
                switch (session.serviceChoice) {

                    case 1:
                        // Dịch vụ 1: đảo ngược toàn bộ chuỗi
                        // Sau đó viết hoa chữ cái đầu của từng từ
                        response = reverseEntireStringAndCapitalize(message);
                        break; // Kết thúc case 1

                    case 2:
                        // Dịch vụ 2: đảo ngược từng từ
                        // Giữ nguyên thứ tự vị trí của các từ trong câu
                        response = reverseWordsAndCapitalizeKeepPosition(message);
                        break; // Kết thúc case 2

                    case 3:
                        // Dịch vụ 3: cộng số từ của dòng hiện tại vào tổng
                        session.totalWords += countWords(message);

                        // Phản hồi tạm thời để Client biết Server đã nhận dòng
                        response = "-> Da ghi nhan. Dang cho dong tiep theo hoac dau '.'";
                        break; // Kết thúc case 3

                    case 4:
                        // Dịch vụ 4: đếm số từ trong dòng hiện tại
                        response = "So tu trong dong: " + countWords(message);
                        break; // Kết thúc case 4

                    default:
                        // Trường hợp dịch vụ không hợp lệ
                        response = "Loi dich vu.";
                }
            }
        }

        // Gửi kết quả xử lý về đúng địa chỉ của Client gửi yêu cầu
        sendToClient(socket, clientAddress, response);
    }

    // Phương thức gửi phản hồi UDP về Client
    private static void sendToClient(
            DatagramSocket socket, // Socket UDP dùng để gửi dữ liệu
            SocketAddress clientAddress, // Địa chỉ nhận gồm IP và cổng nguồn của Client
            String response // Nội dung phản hồi cần gửi
    ) {

        try {

            // Mã hóa nội dung phản hồi thành mảng byte UTF-8
            byte[] sendData = response.getBytes(StandardCharsets.UTF_8);

            // Tạo gói UDP chứa dữ liệu và địa chỉ nhận
            DatagramPacket sendPacket = new DatagramPacket(
                    sendData, sendData.length, clientAddress);

            // Gửi gói tin đến Client
            socket.send(sendPacket);

        } catch (IOException e) {

            // Hiển thị lỗi nếu không gửi được phản hồi
            System.err.println("Loi gui phan hoi UDP: " + e.getMessage());
        }
    }

    // Lớp quản lý trạng thái riêng của từng Client
    private static class ClientSession {

        // Cho biết Client đã chọn dịch vụ hay chưa
        boolean hasChosenService = false;

        // Lưu mã dịch vụ được chọn; -1 nghĩa là chưa chọn
        int serviceChoice = -1;

        // Lưu tổng số từ đã đếm trong dịch vụ 3
        int totalWords = 0;

        // Phương thức đưa trạng thái phiên về giá trị ban đầu
        void reset() {

            // Đánh dấu Client chưa chọn dịch vụ
            hasChosenService = false;

            // Đặt lại mã dịch vụ
            serviceChoice = -1;

            // Đặt lại tổng số từ
            totalWords = 0;
        }
    }

    // =========================================================
    // CÁC HÀM XỬ LÝ NGHIỆP VỤ
    // =========================================================

    // Dịch vụ 1: đảo ngược toàn bộ chuỗi rồi viết hoa chữ cái đầu mỗi từ
    private static String reverseEntireStringAndCapitalize(String text) {

        // Đảo ngược toàn bộ chuỗi bằng StringBuilder
        String reversedString = new StringBuilder(text).reverse().toString();

        // Tách chuỗi thành các từ dựa trên khoảng trắng
        // Lưu ý: trong Java nên viết "\\s+" để biểu diễn regex khoảng trắng
        String[] words = reversedString.split("\\s+");

        // Dùng StringBuilder để ghép kết quả
        StringBuilder result = new StringBuilder();

        // Duyệt qua từng từ trong chuỗi đã đảo ngược
        for (String word : words) {

            // Bỏ qua phần tử rỗng nếu có
            if (!word.isEmpty()) {

                // Viết hoa chữ cái đầu của từ rồi thêm vào kết quả
                result.append(capitalizeFirstLetter(word)).append(" ");
            }
        }

        // Chuyển kết quả thành chuỗi và xóa khoảng trắng cuối
        return result.toString().trim();
    }

    // Dịch vụ 2: đảo ngược từng từ nhưng giữ nguyên thứ tự các từ
    private static String reverseWordsAndCapitalizeKeepPosition(String text) {

        // Tách chuỗi thành các từ dựa trên khoảng trắng
        String[] words = text.split("\\s+");

        // Tạo đối tượng để ghép các từ sau khi xử lý
        StringBuilder result = new StringBuilder();

        // Duyệt lần lượt qua từng từ
        for (String word : words) {

            // Bỏ qua từ rỗng
            if (!word.isEmpty()) {

                // Đảo ngược các ký tự bên trong từ hiện tại
                String reversedWord = new StringBuilder(word).reverse().toString();

                // Viết hoa chữ cái đầu rồi thêm từ vào kết quả
                result.append(capitalizeFirstLetter(reversedWord)).append(" ");
            }
        }

        // Trả về chuỗi kết quả và loại bỏ khoảng trắng cuối
        return result.toString().trim();
    }

    // Phương thức viết hoa ký tự đầu và chuyển các ký tự còn lại thành chữ thường
    private static String capitalizeFirstLetter(String word) {

        // Nếu chuỗi null hoặc rỗng thì trả về nguyên giá trị
        if (word == null || word.isEmpty()) {
            return word;
        }

        // Ghép ký tự đầu viết hoa với phần còn lại viết thường
        return word.substring(0, 1).toUpperCase()
                + word.substring(1).toLowerCase();
    }

    // Phương thức đếm số từ trong một chuỗi
    private static int countWords(String text) {

        // Nếu chuỗi null hoặc chỉ chứa khoảng trắng thì có 0 từ
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        // Loại bỏ khoảng trắng đầu/cuối, tách theo khoảng trắng
        // rồi trả về số phần tử trong mảng
        return text.trim().split("\\s+").length;
    }
}
