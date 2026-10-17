package DeMauGK;

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

public class SttChanTcpServer {
    private static final int PORT = 7000; // Cổng 7000 theo yêu cầu đề bài

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server (STT Chan) chay tai port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Loi server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

                // Gửi danh sách dịch vụ STT Chẵn (2 và 4)
                out.println("=== DANH SACH DICH VU (STT CHAN) ===");
                out.println("2: Dao nguoc tu va in hoa ky tu dau, giu nguyen vi tri");
                out.println("4: Dem so luong tu tung dong");
                out.println("Chon dich vu (2 hoac 4):");
                out.println("END_MENU");

                String serviceChoiceStr = in.readLine();
                if (serviceChoiceStr == null || serviceChoiceStr.equals("EXIT."))
                    return;

                int serviceChoice;
                try {
                    serviceChoice = Integer.parseInt(serviceChoiceStr.trim());
                    if (serviceChoice != 2 && serviceChoice != 4) {
                        out.println("Loi: STT Chan chi duoc ho tro dich vu 2 va 4.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    out.println("Loi: Lua chon khong hop le.");
                    return;
                }

                out.println("Da chon dich vu " + serviceChoice + ". Nhap tung dong chuoi (nhap '.' de ngung nhap):");

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();

                    // Client yêu cầu đóng kết nối
                    if (line.equals("EXIT.")) {
                        break;
                    }

                    // Dấu "." đánh dấu kết thúc một đợt nhập liệu
                    if (line.equals(".")) {
                        out.println("Hoan thanh xu ly. Ban co the nhap tiep hoac nhap 'EXIT.' de thoat.");
                        continue;
                    }

                    if (line.isEmpty()) {
                        out.println("Loi: Dong trong. Vui long nhap chuoi.");
                        continue;
                    }

                    // Xử lý dịch vụ
                    String result = "";
                    if (serviceChoice == 2) {
                        result = reverseWordsKeepPosition(line);
                    } else if (serviceChoice == 4) {
                        result = "So tu trong dong: " + countWords(line);
                    }

                    out.println(result);
                }
            } catch (IOException e) {
                System.err.println("Client ngat ket noi.");
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        // Dịch vụ 2
        private String reverseWordsKeepPosition(String text) {
            String[] words = text.split("\\s+");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    String reversedWord = new StringBuilder(word).reverse().toString();
                    result.append(capitalizeFirstLetter(reversedWord)).append(" ");
                }
            }
            return result.toString().trim();
        }

        private String capitalizeFirstLetter(String word) {
            if (word == null || word.isEmpty())
                return word;
            return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        }

        // Dịch vụ 4
        private int countWords(String text) {
            if (text == null || text.trim().isEmpty())
                return 0;
            return text.trim().split("\\s+").length;
        }
    }
}