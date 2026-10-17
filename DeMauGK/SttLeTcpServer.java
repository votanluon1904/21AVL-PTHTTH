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

public class SttLeTcpServer {
    private static final int PORT = 7000;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server (STT Le) chay tai port " + PORT);
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

                // Gửi danh sách dịch vụ cho STT Lẻ
                out.println("=== DANH SACH DICH VU (STT LE) ===");
                out.println("1: Dao nguoc toan bo chuoi va in hoa ky tu dau");
                out.println("3: Dem so luong tu cua tat ca cac dong");
                out.println("Chon dich vu (1 hoac 3):");
                out.println("END_MENU");

                String serviceChoiceStr = in.readLine();
                if (serviceChoiceStr == null || serviceChoiceStr.equals("EXIT."))
                    return;

                int serviceChoice;
                try {
                    serviceChoice = Integer.parseInt(serviceChoiceStr.trim());
                    if (serviceChoice != 1 && serviceChoice != 3) {
                        out.println("Loi: STT Le chi duoc ho tro dich vu 1 va 3.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    out.println("Loi: Lua chon khong hop le.");
                    return;
                }

                out.println("Da chon dich vu " + serviceChoice + ". Nhap tung dong chuoi (nhap '.' de ngung nhap):");

                int totalWordsAllLines = 0;
                String line;

                while ((line = in.readLine()) != null) {
                    line = line.trim();

                    if (line.equals("EXIT.")) {
                        break;
                    }

                    if (line.equals(".")) {
                        if (serviceChoice == 3) {
                            out.println("Tong so tu cua tat ca cac dong: " + totalWordsAllLines
                                    + " --- (Nhap tiep hoac 'EXIT.' de thoat)");
                            totalWordsAllLines = 0; // Reset đếm lại từ đầu nếu client nhập tiếp
                        } else {
                            out.println("Hoan thanh xu ly. Ban co the nhap tiep hoac 'EXIT.' de thoat.");
                        }
                        continue;
                    }

                    if (line.isEmpty()) {
                        out.println("Loi: Dong trong. Vui long nhap chuoi hoac dau '.'");
                        continue;
                    }

                    String result = "";
                    if (serviceChoice == 1) {
                        result = reverseEntireStringAndCapitalize(line);
                    } else if (serviceChoice == 3) {
                        totalWordsAllLines += countWords(line);
                        result = "-> Da ghi nhan. Dang cho dong tiep theo hoac dau '.'";
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

        // Dịch vụ 1: Đảo ngược TOÀN BỘ chuỗi và in hoa ký tự đầu
        private String reverseEntireStringAndCapitalize(String text) {
            String reversedString = new StringBuilder(text).reverse().toString();
            String[] words = reversedString.split("\\s+");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (!word.isEmpty()) {
                    result.append(capitalizeFirstLetter(word)).append(" ");
                }
            }
            return result.toString().trim();
        }

        private String capitalizeFirstLetter(String word) {
            if (word == null || word.isEmpty())
                return word;
            return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        }

        // Dịch vụ 3: Đếm số lượng từ
        private int countWords(String text) {
            if (text == null || text.trim().isEmpty())
                return 0;
            return text.trim().split("\\s+").length;
        }
    }
}