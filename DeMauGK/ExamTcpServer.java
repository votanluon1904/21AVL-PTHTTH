
package DeMauGK;
// Khai báo package chứa lớp ExamTcpServer

import java.io.BufferedReader;
// Đọc dữ liệu văn bản theo từng dòng

import java.io.IOException;
// Xử lý các lỗi nhập xuất dữ liệu

import java.io.InputStreamReader;
// Chuyển luồng byte thành luồng ký tự

import java.io.OutputStreamWriter;
// Chuyển ký tự thành byte để gửi qua mạng

import java.io.PrintWriter;
// Gửi dữ liệu dạng văn bản qua luồng xuất

import java.net.ServerSocket;
// Tạo Server TCP và lắng nghe kết nối từ Client

import java.net.Socket;
// Đại diện cho kết nối TCP với một Client

import java.nio.charset.StandardCharsets;
// Cung cấp bộ mã ký tự UTF-8

import java.util.concurrent.ExecutorService;
// Quản lý nhóm luồng thực thi các tác vụ

import java.util.concurrent.Executors;
// Cung cấp phương thức tạo Thread Pool

public class ExamTcpServer {

    // Cổng mà Server sử dụng để chờ Client kết nối
    private static final int PORT = 7000;

    // Hàm main là điểm bắt đầu chạy chương trình
    public static void main(String[] args) {

        // Tạo Thread Pool gồm tối đa 10 luồng xử lý tác vụ đồng thời
        // Các Client được gửi vào pool để xử lý thay vì tạo Thread thủ công
        ExecutorService pool = Executors.newFixedThreadPool(10);

        // Tạo ServerSocket để lắng nghe kết nối TCP tại cổng 7000
        // try-with-resources tự động đóng ServerSocket khi thoát khối try
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Thông báo Server đã khởi động thành công
            System.out.println("Server is running on port " + PORT);

            // Vòng lặp vô hạn để liên tục tiếp nhận Client mới
            while (true) {

                // accept() chờ cho đến khi có Client kết nối
                // Sau khi kết nối thành công, trả về Socket của Client
                Socket clientSocket = serverSocket.accept();

                // In địa chỉ IP của Client vừa kết nối
                System.out.println(
                        "New client connected: "
                                + clientSocket.getInetAddress());

                // Tạo đối tượng xử lý riêng cho Client này
                ClientHandler handler = new ClientHandler(clientSocket);

                // Đưa tác vụ xử lý Client vào Thread Pool
                // Pool sẽ sử dụng một luồng có sẵn để chạy handler
                pool.submit(handler);
            }

        } catch (IOException e) {

            // Hiển thị lỗi nếu Server không thể mở cổng
            // hoặc xảy ra lỗi khi chấp nhận kết nối
            System.err.println("Server error: " + e.getMessage());

        } finally {

            // Yêu cầu Thread Pool ngừng nhận tác vụ mới
            // Các tác vụ đã được gửi vào pool vẫn được phép hoàn thành
            pool.shutdown();
        }
    }

    // Lớp ClientHandler xử lý yêu cầu của một Client
    // implements Runnable để có thể chạy trong Thread Pool
    private static class ClientHandler implements Runnable {

        // Socket dùng để trao đổi dữ liệu với Client
        private final Socket socket;

        // Hàm khởi tạo, nhận Socket của Client
        public ClientHandler(Socket socket) {
            this.socket = socket; // Lưu Socket vào thuộc tính của đối tượng
        }

        // Phương thức run() chứa toàn bộ quy trình xử lý Client
        @Override
        public void run() {

            // Khởi tạo luồng nhận và gửi dữ liệu
            // Cả hai luồng sử dụng UTF-8 để mã hóa và giải mã văn bản
            //
            // BufferedReader in: đọc dữ liệu Client gửi đến
            // PrintWriter out: gửi dữ liệu từ Server đến Client
            //
            // try-with-resources tự động đóng in và out khi kết thúc
            try (
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream(),
                                    StandardCharsets.UTF_8));

                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream(),
                                    StandardCharsets.UTF_8),
                            true // Tự động flush sau mỗi lần println()
                    )) {

                // ===== 1. GỬI DANH SÁCH DỊCH VỤ =====

                // Gửi tiêu đề menu đến Client
                out.println("=== DANH SACH DICH VU ===");

                // Dịch vụ 1: đảo ngược toàn bộ chuỗi,
                // sau đó viết hoa chữ cái đầu của mỗi từ
                out.println(
                        "1: Dao nguoc toan bo chuoi va in hoa ky tu dau (STT LE)");

                // Dịch vụ 2: đảo ngược từng từ nhưng giữ thứ tự các từ
                out.println(
                        "2: Dao nguoc tu va in hoa ky tu dau, giu nguyen vi tri (STT CHAN)");

                // Dịch vụ 3: đếm tổng số từ của tất cả các dòng nhập
                out.println(
                        "3: Dem so luong tu cua tat ca cac dong (STT LE)");

                // Dịch vụ 4: đếm số từ riêng biệt trên từng dòng
                out.println(
                        "4: Dem so luong tu tung dong (STT CHAN)");

                // Yêu cầu Client chọn một dịch vụ từ 1 đến 4
                out.println("Chon dich vu (1-4):");

                // Gửi dấu kết thúc menu để Client biết menu đã hoàn tất
                // Client có thể dựa vào dòng này để ngừng đọc menu
                out.println("END_MENU");

                // ===== 2. NHẬN LỰA CHỌN DỊCH VỤ =====

                // Đọc lựa chọn dịch vụ do Client gửi đến
                String serviceChoiceStr = in.readLine();

                // Nếu Client đã ngắt kết nối hoặc gửi EXIT.
                // thì kết thúc xử lý Client
                if (serviceChoiceStr == null
                        || serviceChoiceStr.equalsIgnoreCase("EXIT.")) {
                    return; // Kết thúc phương thức run()
                }

                // Biến lưu lựa chọn dịch vụ dưới dạng số nguyên
                int serviceChoice;

                try {

                    // Loại bỏ khoảng trắng ở hai đầu chuỗi
                    // Sau đó chuyển chuỗi thành số nguyên
                    serviceChoice = Integer.parseInt(
                            serviceChoiceStr.trim());

                } catch (NumberFormatException e) {

                    // Thông báo nếu lựa chọn không phải số nguyên hợp lệ
                    out.println("Loi: Lua chon dich vu khong hop le.");

                    // Dừng xử lý Client khi lựa chọn không hợp lệ
                    return;
                }

                // ===== 3. YÊU CẦU NHẬP CÁC DÒNG CHUỖI =====

                // Thông báo dịch vụ đã chọn và hướng dẫn nhập dữ liệu
                // Dấu chấm "." dùng để chốt một đợt nhập liệu
                out.println(
                        "Da chon dich vu " + serviceChoice
                                + ". Nhap cac dong chuoi "
                                + "(nhap '.' de ket thuc qua trinh nhap lieu):");

                // Lưu tổng số từ của tất cả các dòng đối với dịch vụ 3
                int totalWordsAllLines = 0;

                // Biến lưu từng dòng Client gửi lên
                String line;

                // Đọc liên tục các dòng dữ liệu từ Client
                // Kết thúc nếu Client đóng kết nối hoặc luồng đọc trả về null
                while ((line = in.readLine()) != null) {

                    // Loại bỏ khoảng trắng ở đầu và cuối dòng
                    line = line.trim();

                    // Kiểm tra lệnh EXIT. để thoát khỏi phiên làm việc
                    if (line.equals("EXIT.")) {

                        // Ghi thông báo Client muốn thoát lên màn hình Server
                        System.out.println("Client requested to exit.");

                        // Kết thúc vòng lặp xử lý dữ liệu
                        break;
                    }

                    // ===== 4. XỬ LÝ DẤU CHẤM ĐỂ CHỐT KẾT QUẢ =====

                    // Nếu Client gửi đúng một dấu chấm,
                    // xử lý kết thúc đợt nhập liệu hiện tại
                    if (line.equals(".")) {

                        // Với dịch vụ 3, trả về tổng số từ đã tích lũy
                        if (serviceChoice == 3) {

                            // Gửi tổng số từ của tất cả các dòng đã nhập
                            out.println(
                                    "Tong so tu cua tat ca cac dong: "
                                            + totalWordsAllLines
                                            + " --- (Nhap tiep hoac 'EXIT.' de thoat)");

                            // Đặt lại bộ đếm để bắt đầu đợt nhập tiếp theo
                            totalWordsAllLines = 0;

                        } else {

                            // Với các dịch vụ còn lại, thông báo đã hoàn thành
                            // Client có thể nhập thêm hoặc gửi EXIT.
                            out.println(
                                    "Hoan thanh xu ly. Ban co the nhap tiep "
                                            + "hoac 'EXIT.' de thoat.");
                        }

                        // Quay lại đầu vòng lặp để chờ dòng tiếp theo
                        // Không xử lý dấu chấm như một chuỗi thông thường
                        continue;
                    }

                    // ===== 5. KIỂM TRA DÒNG TRỐNG =====

                    // Nếu dòng sau khi trim() không còn ký tự nào
                    if (line.isEmpty()) {

                        // Gửi thông báo yêu cầu nhập lại dữ liệu
                        out.println(
                                "Loi: Ban vua gui dong trong. "
                                        + "Vui long nhap chuoi hoac dau '.'");

                        // Bỏ qua dòng trống và đọc dòng tiếp theo
                        continue;
                    }

                    // Biến lưu kết quả xử lý của dịch vụ được chọn
                    String result = "";

                    // ===== 6. THỰC HIỆN DỊCH VỤ =====

                    // Chọn cách xử lý dựa vào serviceChoice
                    switch (serviceChoice) {

                        // Dịch vụ 1: đảo ngược toàn bộ chuỗi
                        case 1:

                            // Gọi phương thức đảo ngược chuỗi và chuẩn hóa chữ
                            result = reverseEntireStringAndCapitalize(line);

                            // Kết thúc nhánh case 1
                            break;

                        // Dịch vụ 2: đảo ngược từng từ, giữ vị trí từ
                        case 2:

                            // Gọi phương thức đảo ngược từng từ
                            result = reverseWordsAndCapitalizeKeepPosition(line);

                            // Kết thúc nhánh case 2
                            break;

                        // Dịch vụ 3: đếm tổng số từ của nhiều dòng
                        case 3:

                            // Đếm số từ trên dòng hiện tại
                            // Sau đó cộng vào tổng số từ của các dòng
                            totalWordsAllLines += countWords(line);

                            // Gửi phản hồi tạm thời để Client biết Server
                            // đã nhận và xử lý dòng này
                            result = "-> Da ghi nhan. Dang cho dong tiep theo "
                                    + "hoac dau '.'";

                            // Kết thúc nhánh case 3
                            break;

                        // Dịch vụ 4: đếm số từ trên dòng hiện tại
                        case 4:

                            // Đếm số từ trong dòng hiện tại
                            int wordCount = countWords(line);

                            // Tạo thông báo chứa số từ vừa đếm được
                            result = "So tu trong dong: " + wordCount;

                            // Kết thúc nhánh case 4
                            break;

                        // Nếu lựa chọn không nằm trong các trường hợp trên
                        default:

                            // Thông báo dịch vụ không tồn tại
                            result = "Dich vu khong ton tai.";
                    }

                    // ===== 7. GỬI KẾT QUẢ VỀ CLIENT =====

                    // Gửi phản hồi cho dòng dữ liệu vừa xử lý
                    // Với dịch vụ 3, đây là thông báo tạm thời;
                    // tổng kết quả chỉ được gửi khi Client nhập "."
                    out.println(result);
                }

            } catch (IOException e) {

                // Xử lý lỗi khi đọc hoặc gửi dữ liệu cho Client
                System.err.println(
                        "Client handling error: " + e.getMessage());

            } finally {

                // Đảm bảo Socket được đóng khi xử lý Client kết thúc
                try {

                    // Đóng kết nối TCP với Client
                    socket.close();

                } catch (IOException e) {

                    // Hiển thị lỗi nếu không thể đóng Socket
                    System.err.println(
                            "Error closing socket: " + e.getMessage());
                }
            }
        }

        // ===== PHƯƠNG THỨC DỊCH VỤ 1 =====

        // Đảo ngược toàn bộ chuỗi, sau đó viết hoa chữ cái đầu mỗi từ
        private String reverseEntireStringAndCapitalize(String text) {

            // Dùng StringBuilder.reverse() để đảo ngược toàn bộ chuỗi
            // Ví dụ: "hello world" -> "dlrow olleh"
            String reversedString = new StringBuilder(text).reverse().toString();

            // Tách chuỗi đã đảo ngược thành các từ
            // \\s+ là biểu thức chính quy đại diện cho một hoặc nhiều
            // ký tự khoảng trắng
            String[] words = reversedString.split("\\s+");

            // Dùng StringBuilder để ghép các từ thành kết quả
            StringBuilder result = new StringBuilder();

            // Duyệt qua từng từ sau khi tách
            for (String word : words) {

                // Bỏ qua phần tử rỗng nếu có
                if (!word.isEmpty()) {

                    // Viết hoa chữ cái đầu của từ
                    // rồi thêm một dấu cách phía sau
                    result.append(capitalizeFirstLetter(word)).append(" ");
                }
            }

            // Chuyển kết quả thành String và bỏ khoảng trắng cuối
            return result.toString().trim();
        }

        // ===== PHƯƠNG THỨC DỊCH VỤ 2 =====

        // Đảo ngược từng từ nhưng giữ nguyên thứ tự các từ
        private String reverseWordsAndCapitalizeKeepPosition(String text) {

            // Tách chuỗi ban đầu thành các từ theo khoảng trắng
            String[] words = text.split("\\s+");

            // Tạo đối tượng để ghép các từ sau khi xử lý
            StringBuilder result = new StringBuilder();

            // Duyệt lần lượt từng từ trong chuỗi
            for (String word : words) {

                // Bỏ qua từ rỗng
                if (!word.isEmpty()) {

                    // Đảo ngược các ký tự bên trong từ hiện tại
                    String reversedWord = new StringBuilder(word).reverse().toString();

                    // Viết hoa chữ cái đầu của từ đã đảo ngược
                    // rồi nối vào kết quả
                    result.append(
                            capitalizeFirstLetter(reversedWord)).append(" ");
                }
            }

            // Trả về kết quả sau khi loại bỏ khoảng trắng cuối
            return result.toString().trim();
        }

        // ===== PHƯƠNG THỨC VIẾT HOA CHỮ CÁI ĐẦU =====

        // Viết hoa ký tự đầu và chuyển các ký tự còn lại thành chữ thường
        private String capitalizeFirstLetter(String word) {

            // Nếu chuỗi null hoặc rỗng thì trả lại nguyên giá trị
            // để tránh lỗi khi truy cập ký tự đầu
            if (word == null || word.isEmpty()) {
                return word;
            }

            // substring(0, 1): lấy ký tự đầu tiên
            // toUpperCase(): chuyển ký tự đầu thành chữ hoa
            //
            // substring(1): lấy phần còn lại của chuỗi
            // toLowerCase(): chuyển phần còn lại thành chữ thường
            //
            // Ví dụ: "hELLO" -> "Hello"
            return word.substring(0, 1).toUpperCase()
                    + word.substring(1).toLowerCase();
        }

        // ===== PHƯƠNG THỨC ĐẾM SỐ TỪ =====

        // Đếm số từ trong một chuỗi
        private int countWords(String text) {

            // Nếu chuỗi null hoặc chỉ chứa khoảng trắng
            // thì số từ bằng 0
            if (text == null || text.trim().isEmpty()) {
                return 0;
            }

            // trim() loại bỏ khoảng trắng đầu và cuối
            // split("\\s+") tách chuỗi tại một hoặc nhiều khoảng trắng
            // length trả về số phần tử trong mảng từ
            //
            // Ví dụ: "Java TCP Server" -> 3 từ
            return text.trim().split("\\s+").length;
        }
    }
}
