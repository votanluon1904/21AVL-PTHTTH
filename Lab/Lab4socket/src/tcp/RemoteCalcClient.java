package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RemoteCalcClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5004);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Ket noi thanh cong. Nhap lenh theo mau: CALC + 100 200 (Hoac QUIT de thoat)");
            String request;

            while ((request = console.readLine()) != null) {
                out.println(request);
                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server da dong ket noi.");
                    break;
                }
                System.out.println("Server phan hoi: " + response);
            }
        } catch (IOException e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
}