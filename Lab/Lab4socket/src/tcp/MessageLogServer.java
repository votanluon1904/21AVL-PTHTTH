package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class MessageLogServer {
    private static final int PORT = 5008;
    private static final String LOG_DIR = "data_logs"; // Thư mục lưu file log
    // Biểu thức chính quy: Chỉ cho phép chữ (a-z, A-Z), số (0-9), gạch dưới (_) và
    // gạch ngang (-)
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Tự động tạo thư mục data_logs nếu chưa tồn tại
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Message Log Server dang chay tai port " + PORT);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Loi khoi tao server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        String remoteAddress = socket.getRemoteSocketAddress().toString();
        System.out.println("Co ket noi moi tu: " + remoteAddress);

        try (socket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // Bắt buộc dòng đầu tiên phải là HELLO clientId
            String firstLine = in.readLine();
            if (firstLine == null || !firstLine.toUpperCase().startsWith("HELLO ")) {
                out.println("ERR INVALID_HANDSHAKE (Ban phai bat dau bang lenh: HELLO clientId)");
                return;
            }

            // Tách và kiểm tra tính hợp lệ của clientId
            String clientId = firstLine.substring(6).trim();
            if (!CLIENT_ID_PATTERN.matcher(clientId).matches()) {
                out.println("ERR INVALID_CLIENT_ID (Chi chap nhan chu, so, gach ngang, gach duoi)");
                return;
            }

            out.println("OK Xin chao " + clientId + ". He thong bat dau ghi log, nhap QUIT de ket thuc.");

            // Mở file của client đó ra để ghi nối (append = true)
            File logFile = new File(LOG_DIR, clientId + ".txt");
            try (PrintWriter fileOut = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8), true)) {
                String msg;
                // Vòng lặp nhận tin nhắn
                while ((msg = in.readLine()) != null) {
                    if (msg.trim().equalsIgnoreCase("QUIT")) {
                        break;
                    }

                    // Tạo log entry: [Thời gian] Địa_chỉ_IP: Nội dung
                    String timestamp = LocalDateTime.now().format(FMT);
                    String logEntry = String.format("[%s] %s: %s", timestamp, remoteAddress, msg);

                    // Ghi vào file và báo thành công cho client
                    fileOut.println(logEntry);
                    out.println("OK Da luu: " + msg);
                }
            }
        } catch (IOException e) {
            System.err.println("Client " + remoteAddress + " ngat ket noi.");
        }
    }
}