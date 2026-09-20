package Lab.Lab4socket.src.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostInspector {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java network.HostInspector <hostname>");
            return;
        }
        try {
            InetAddress[] addresses = InetAddress.getAllByName(args[0]);
            System.out.println("Host: " + args[0]);
            for (InetAddress address : addresses) {
                System.out.println("IP Address: " + address.getHostAddress());
                System.out.println("Canonical Name: " + address.getCanonicalHostName());
                System.out.println("Loopback Address: " + address.isLoopbackAddress());
                System.out.println("Site Local Address: " + address.isSiteLocalAddress());
            }
        } catch (UnknownHostException e) {
            System.out.println("Khong phan giai duoc host: " + args[0]);
        }

    }
}
