package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteCalcServer {
    private static final int PORT = 5004;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("May tinh tu xa (Server) chay tai port " + PORT);
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Loi server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        try (socket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equalsIgnoreCase("QUIT")) {
                    break;
                }

                // Tách chuỗi lệnh bằng khoảng trắng
                String[] parts = line.split("\\s+");

                // Kiểm tra định dạng cơ bản: Phải có đúng 4 phần và bắt đầu bằng CALC
                if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
                    out.println("ERR INVALID_FORMAT");
                    continue;
                }

                String operator = parts[1];
                double op1, op2;

                // Kiểm tra lỗi ép kiểu (Toán hạng không phải là số)
                try {
                    op1 = Double.parseDouble(parts[2]);
                    op2 = Double.parseDouble(parts[3]);
                } catch (NumberFormatException e) {
                    out.println("ERR INVALID_NUMBER");
                    continue;
                }

                double result = 0;
                boolean isError = false;

                // Xử lý các phép toán
                switch (operator) {
                    case "+":
                        result = op1 + op2;
                        break;
                    case "-":
                        result = op1 - op2;
                        break;
                    case "*":
                        result = op1 * op2;
                        break;
                    case "/":
                        if (op2 == 0) {
                            out.println("ERR DIVIDE_BY_ZERO");
                            isError = true;
                        } else {
                            result = op1 / op2;
                        }
                        break;
                    default:
                        out.println("ERR UNSUPPORTED_OPERATOR");
                        isError = true;
                        break;
                }

                // Nếu không có lỗi trong quá trình tính toán, trả về OK
                if (!isError) {
                    // Loại bỏ đuôi .0 nếu kết quả là số nguyên để hiển thị đẹp hơn
                    if (result == (long) result) {
                        out.println("OK " + (long) result);
                    } else {
                        out.println("OK " + result);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Client ngat ket noi dot ngot.");
        }
    }
}