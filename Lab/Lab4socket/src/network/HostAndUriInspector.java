/* Bài tập đề xuất 1 */
package Lab.Lab4socket.src.network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostAndUriInspector {
    public static void main(String[] args) {
        // Xử lý lỗi thiếu tham số
        if (args.length < 2) {
            System.out.println("Loi: Thieu tham so.");
            System.out.println("Cu phap: java Lab.Lab4socket.src.network.HostAndUriInspector <hostname> <URI>");
            return;
        }

        // Nhận hostname và URI qua args (2 tham số tách biệt)
        String hostname = args[0];
        String uriString = args[1];

        // -----------------------------------------
        // 1. KIỂM TRA HOSTNAME
        // -----------------------------------------
        System.out.println("=== KET QUA KIEM TRA HOST ===");
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            System.out.println("Host: " + hostname);
            for (InetAddress addr : addresses) {
                System.out.println(" - IP Address: " + addr.getHostAddress());

                // Xác định loại IPv4 hoặc IPv6
                if (addr instanceof Inet4Address) {
                    System.out.println("   Loai: IPv4");
                } else if (addr instanceof Inet6Address) {
                    System.out.println("   Loai: IPv6");
                }

                System.out.println("   Loopback: " + addr.isLoopbackAddress());
                System.out.println("   Site Local: " + addr.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            // Xử lý hostname không phân giải được
            System.out.println("Loi phan giai: Khong tim thay host '" + hostname + "'");
        }

        // -----------------------------------------
        // 2. KIỂM TRA URI
        // -----------------------------------------
        System.out.println("\n=== KET QUA KIEM TRA URI ===");
        try {
            URI uri = new URI(uriString);
            System.out.println("URI truyen vao: " + uriString);
            System.out.println(" - Scheme   : " + uri.getScheme());
            System.out.println(" - Host     : " + uri.getHost());
            System.out.println(" - Port     : " + uri.getPort());
            System.out.println(" - Path     : " + uri.getPath());
            System.out.println(" - Query    : " + uri.getQuery());
            System.out.println(" - Fragment : " + uri.getFragment());
        } catch (URISyntaxException e) {
            // Xử lý lỗi URI sai cú pháp
            System.out.println("Loi cu phap URI: URI khong hop le '" + uriString + "'");
        }
    }
}