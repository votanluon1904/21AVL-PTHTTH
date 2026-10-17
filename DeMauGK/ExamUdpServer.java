package DeMauGK;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExamUdpServer {
    private static final int PORT = 7000;

    // Cấu trúc lưu trữ trạng thái của từng Client (vì UDP không duy trì kết nối)
    private static final ConcurrentHashMap<SocketAddress, ClientSession> clientSessions = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("UDP Server is running on port " + PORT);
            byte[] receiveBuffer = new byte[2048];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                // Trích xuất dữ liệu, IP và Port của người gửi
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                        StandardCharsets.UTF_8).trim();
                SocketAddress clientAddress = receivePacket.getSocketAddress();

                // Đẩy vào ThreadPool xử lý để không chặn luồng nhận chính
                pool.submit(() -> handleClientMessage(socket, clientAddress, message));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClientMessage(DatagramSocket socket, SocketAddress clientAddress, String message) {
        // Lấy hoặc tạo mới phiên làm việc (Session) cho Client này
        ClientSession session = clientSessions.computeIfAbsent(clientAddress, k -> new ClientSession());
        String response;

        if (message.equals("HELLO_SERVER")) {
            // Client vừa khởi động, gửi Menu gộp trong 1 chuỗi
            response = "=== DANH SACH DICH VU ===\n"
                    + "1: Dao nguoc toan bo chuoi va in hoa ky tu dau (STT LE)\n"
                    + "2: Dao nguoc tu va in hoa ky tu dau, giu nguyen vi tri (STT CHAN)\n"
                    + "3: Dem so luong tu cua tat ca cac dong (STT LE)\n"
                    + "4: Dem so luong tu tung dong (STT CHAN)\n"
                    + "Chon dich vu (1-4):";
            session.reset(); // Reset trạng thái nếu client mở lại
        } else if (message.equals("EXIT.")) {
            response = "Da dong phien lam viec. Tam biet!";
            clientSessions.remove(clientAddress); // Dọn dẹp bộ nhớ
        } else if (!session.hasChosenService) {
            // Bước xử lý chọn dịch vụ
            try {
                int choice = Integer.parseInt(message);
                if (choice >= 1 && choice <= 4) {
                    session.serviceChoice = choice;
                    session.hasChosenService = true;
                    response = "Da chon dich vu " + choice + ". Nhap cac dong chuoi (nhap '.' de ket thuc):";
                } else {
                    response = "Loi: Lua chon dich vu khong hop le. Vui long chon 1-4.";
                }
            } catch (NumberFormatException e) {
                response = "Loi: Vui long nhap mot so tu 1 den 4.";
            }
        } else {
            // Bước xử lý dữ liệu văn bản theo dịch vụ đã chọn
            if (message.isEmpty()) {
                response = "Loi: Ban vua gui dong trong. Vui long nhap chuoi hoac dau '.'";
            } else if (message.equals(".")) {
                if (session.serviceChoice == 3) {
                    response = "Tong so tu cua tat ca cac dong: " + session.totalWords
                            + " --- (Nhap tiep hoac 'EXIT.' de thoat)";
                    session.totalWords = 0;
                } else {
                    response = "Hoan thanh xu ly. Ban co the nhap tiep hoac 'EXIT.' de thoat.";
                }
            } else {
                switch (session.serviceChoice) {
                    case 1:
                        response = reverseEntireStringAndCapitalize(message);
                        break;
                    case 2:
                        response = reverseWordsAndCapitalizeKeepPosition(message);
                        break;
                    case 3:
                        session.totalWords += countWords(message);
                        response = "-> Da ghi nhan. Dang cho dong tiep theo hoac dau '.'";
                        break;
                    case 4:
                        response = "So tu trong dong: " + countWords(message);
                        break;
                    default:
                        response = "Loi dich vu.";
                }
            }
        }

        sendToClient(socket, clientAddress, response);
    }

    private static void sendToClient(DatagramSocket socket, SocketAddress clientAddress, String response) {
        try {
            byte[] sendData = response.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress);
            socket.send(sendPacket);
        } catch (IOException e) {
            System.err.println("Loi gui phan hoi UDP: " + e.getMessage());
        }
    }

    // Lớp quản lý trạng thái của từng Client riêng biệt
    private static class ClientSession {
        boolean hasChosenService = false;
        int serviceChoice = -1;
        int totalWords = 0;

        void reset() {
            hasChosenService = false;
            serviceChoice = -1;
            totalWords = 0;
        }
    }

    // --- CÁC HÀM XỬ LÝ NGHIỆP VỤ (Giữ nguyên như bản TCP) ---
    private static String reverseEntireStringAndCapitalize(String text) {
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

    private static String reverseWordsAndCapitalizeKeepPosition(String text) {
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

    private static String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty())
            return word;
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }

    private static int countWords(String text) {
        if (text == null || text.trim().isEmpty())
            return 0;
        return text.trim().split("\\s+").length;
    }
}