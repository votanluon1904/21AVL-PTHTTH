package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MessageLogClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5008);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Da ket noi Server. Vui long chao bang cu phap: HELLO <clientId> (VD: HELLO User-01)");

            String input;
            while ((input = console.readLine()) != null) {
                out.println(input);

                if (input.trim().equalsIgnoreCase("QUIT")) {
                    System.out.println("Dang dong ket noi...");
                    break;
                }

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server da tu choi/dong ket noi.");
                    break;
                }
                System.out.println("Server: " + response);
            }
        } catch (IOException e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
}