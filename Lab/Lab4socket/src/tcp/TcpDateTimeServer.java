/* Bài tập đề xuất 3 */
package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
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

public class TcpDateTimeServer {
    private static final int PORT = 5002;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("TCP DateTime Server chay tai port " + PORT);
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

            String cmd;
            while ((cmd = in.readLine()) != null) {
                cmd = cmd.trim().toUpperCase();
                if (cmd.equals("QUIT")) {
                    break;
                }

                LocalDateTime now = LocalDateTime.now();
                switch (cmd) {
                    case "DATE":
                        out.println(now.format(DATE_FMT));
                        break;
                    case "TIME":
                        out.println(now.format(TIME_FMT));
                        break;
                    case "DATETIME":
                        out.println(now.format(DATETIME_FMT));
                        break;
                    default:
                        out.println("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Client ngat ket noi dot ngot.");
        }
    }
}