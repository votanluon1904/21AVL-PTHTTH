
package Lab.Lab4socket.src.tcp;
// Khai báo package chứa lớp MessageLogClient

// Package giúp tổ chức các lớp Java theo thư mục/chức năng

import java.io.BufferedReader;
// Hỗ trợ đọc dữ liệu theo từng dòng

import java.io.IOException;
// Cung cấp lớp xử lý các lỗi nhập xuất dữ liệu

import java.io.InputStreamReader;
// Chuyển đổi luồng byte thành luồng ký tự

import java.io.OutputStreamWriter;
// Chuyển đổi ký tự thành byte để gửi qua mạng

import java.io.PrintWriter;
// Hỗ trợ gửi dữ liệu dạng văn bản qua luồng xuất

import java.net.Socket;
// Đại diện cho kết nối TCP giữa Client và Server

import java.nio.charset.StandardCharsets;
// Cung cấp bộ mã ký tự chuẩn, ở đây sử dụng UTF-8

public class MessageLogClient {

    // Hàm main là điểm bắt đầu chạy chương trình Client
    public static void main(String[] args) {

        // Tạo Socket kết nối đến Server tại địa chỉ localhost, cổng 5008
        // localhost nghĩa là Server chạy trên cùng máy tính
        //
        // BufferedReader console: đọc dữ liệu người dùng nhập từ bàn phím
        // BufferedReader in: đọc phản hồi do Server gửi về
        // PrintWriter out: gửi tin nhắn từ Client đến Server
        //
        // StandardCharsets.UTF_8 đảm bảo dữ liệu văn bản được mã hóa
        // và giải mã bằng UTF-8
        //
        // Tham số true trong PrintWriter giúp tự động flush
        // sau mỗi lần gọi println()
        //
        // try-with-resources tự động đóng Socket và các luồng
        // khi thoát khỏi khối try
        try (
                Socket socket = new Socket("localhost", 5008);

                // Tạo luồng đọc dữ liệu nhập từ bàn phím
                // System.in là luồng nhập chuẩn của chương trình
                // InputStreamReader chuyển byte thành ký tự UTF-8
                BufferedReader console = new BufferedReader(
                        new InputStreamReader(
                                System.in, StandardCharsets.UTF_8));

                // Lấy luồng nhận dữ liệu từ Server thông qua Socket
                // getInputStream() lấy luồng byte đầu vào của kết nối
                // InputStreamReader giải mã byte theo UTF-8
                // BufferedReader cho phép đọc từng dòng bằng readLine()
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream(), StandardCharsets.UTF_8));

                // Lấy luồng gửi dữ liệu đến Server thông qua Socket
                // getOutputStream() lấy luồng byte đầu ra của kết nối
                // OutputStreamWriter mã hóa ký tự thành byte UTF-8
                // PrintWriter hỗ trợ gửi văn bản bằng println()
                // true giúp tự động đẩy dữ liệu ra luồng sau println()
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream(), StandardCharsets.UTF_8),
                        true)) {

            // Thông báo kết nối thành công và hướng dẫn cách chào Server
            // HELLO <clientId> là cú pháp gửi mã định danh Client
            // Ví dụ: HELLO User-01
            System.out.println(
                    "Da ket noi Server. Vui long chao bang cu phap: "
                            + "HELLO <clientId> (VD: HELLO User-01)");

            // Khai báo biến lưu nội dung người dùng nhập từ bàn phím
            String input;

            // Đọc từng dòng từ bàn phím
            // Vòng lặp tiếp tục khi readLine() còn trả về dữ liệu
            // Nếu readLine() trả về null thì kết thúc vòng lặp
            while ((input = console.readLine()) != null) {

                // Gửi nội dung người dùng vừa nhập đến Server
                // println() thêm ký tự xuống dòng để Server có thể
                // đọc dữ liệu bằng BufferedReader.readLine()
                out.println(input);

                // Kiểm tra người dùng có nhập lệnh QUIT hay không
                // trim() loại bỏ khoảng trắng ở đầu và cuối chuỗi
                // equalsIgnoreCase() không phân biệt chữ hoa/chữ thường
                if (input.trim().equalsIgnoreCase("QUIT")) {

                    // Thông báo Client đang chuẩn bị đóng kết nối
                    System.out.println("Dang dong ket noi...");

                    // Thoát vòng lặp, sau đó đóng tài nguyên trong try
                    break;
                }

                // Chờ đọc một dòng phản hồi từ Server
                // readLine() có thể chờ cho đến khi Server gửi dữ liệu
                // hoặc đóng kết nối
                String response = in.readLine();

                // Nếu nhận được null, nghĩa là Server đã đóng luồng
                // hoặc kết nối đã kết thúc
                if (response == null) {

                    // Thông báo không nhận được phản hồi từ Server
                    System.out.println(
                            "Server da tu choi/dong ket noi.");

                    // Thoát vòng lặp nhận và gửi dữ liệu
                    break;
                }

                // Hiển thị nội dung phản hồi nhận được từ Server
                System.out.println("Server: " + response);
            }

            // Khi thoát khối try, Java tự động đóng:
            // 1. Socket
            // 2. Luồng console
            // 3. Luồng nhận dữ liệu từ Server
            // 4. Luồng gửi dữ liệu đến Server

        } catch (IOException e) {

            // Xử lý lỗi kết nối hoặc lỗi nhập xuất dữ liệu
            // Ví dụ: Server chưa chạy, sai cổng hoặc kết nối bị gián đoạn
            // e.getMessage() lấy nội dung mô tả lỗi
            System.err.println(
                    "Loi ket noi: " + e.getMessage());
        }
    }
}
