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

public class ExamTcpServer {
    private static final int PORT = 7000;

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                pool.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
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

                out.println("=== DANH SACH DICH VU ===");
                out.println("1: Dao nguoc toan bo chuoi va in hoa ky tu dau (STT LE)");
                out.println("2: Dao nguoc tu va in hoa ky tu dau, giu nguyen vi tri (STT CHAN)");
                out.println("3: Dem so luong tu cua tat ca cac dong (STT LE)");
                out.println("4: Dem so luong tu tung dong (STT CHAN)");
                out.println("Chon dich vu (1-4):");
                out.println("END_MENU");

                String serviceChoiceStr = in.readLine();
                if (serviceChoiceStr == null || serviceChoiceStr.equalsIgnoreCase("EXIT.")) {
                    return;
                }
                int serviceChoice;
                try {
                    serviceChoice = Integer.parseInt(serviceChoiceStr.trim());
                } catch (NumberFormatException e) {
                    out.println("Loi: Lua chon dich vu khong hop le.");
                    return;
                }

                out.println("Da chon dich vu " + serviceChoice
                        + ". Nhap cac dong chuoi (nhap '.' de ket thuc qua trinh nhap lieu):");

                int totalWordsAllLines = 0;
                String line;

                while ((line = in.readLine()) != null) {
                    line = line.trim();

                    if (line.equals("EXIT.")) {
                        System.out.println("Client requested to exit.");
                        break;
                    }

                    // XỬ LÝ LỆNH CHỐT KẾT QUẢ CỦA CLIENT
                    if (line.equals(".")) {
                        if (serviceChoice == 3) {
                            // Gộp chung kết quả thành 1 dòng phản hồi duy nhất để đồng bộ với Client
                            out.println("Tong so tu cua tat ca cac dong: " + totalWordsAllLines
                                    + " --- (Nhap tiep hoac 'EXIT.' de thoat)");
                            totalWordsAllLines = 0;
                        } else {
                            out.println("Hoan thanh xu ly. Ban co the nhap tiep hoac 'EXIT.' de thoat.");
                        }
                        continue;
                    }

                    // SỬA LỖI TREO KHI GỬI DÒNG TRỐNG
                    if (line.isEmpty()) {
                        out.println("Loi: Ban vua gui dong trong. Vui long nhap chuoi hoac dau '.'");
                        continue;
                    }

                    String result = "";
                    switch (serviceChoice) {
                        case 1:
                            result = reverseEntireStringAndCapitalize(line);
                            break;
                        case 2:
                            result = reverseWordsAndCapitalizeKeepPosition(line);
                            break;
                        case 3:
                            totalWordsAllLines += countWords(line);
                            // TRẢ VỀ PHẢN HỒI TẠM THỜI ĐỂ CLIENT KHÔNG BỊ TREO
                            result = "-> Da ghi nhan. Dang cho dong tiep theo hoac dau '.'";
                            break;
                        case 4:
                            int wordCount = countWords(line);
                            result = "So tu trong dong: " + wordCount;
                            break;
                        default:
                            result = "Dich vu khong ton tai.";
                    }

                    // BẮT BUỘC TRẢ PHẢN HỒI VỀ CHO MỌI LỆNH
                    out.println(result);
                }

            } catch (IOException e) {
                System.err.println("Client handling error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }

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

        private String reverseWordsAndCapitalizeKeepPosition(String text) {
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
            if (word == null || word.isEmpty()) {
                return word;
            }
            return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
        }

        private int countWords(String text) {
            if (text == null || text.trim().isEmpty()) {
                return 0;
            }
            return text.trim().split("\\s+").length;
        }
    }
}