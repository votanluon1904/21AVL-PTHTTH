package Lab.Lab6.Bai7;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class GeometryServer extends UnicastRemoteObject implements GeometryInterface {
    protected GeometryServer() throws RemoteException {
        super();
    }

    @Override
    public double rectangleArea(double width, double height) throws RemoteException {
        return width * height;
    }

    @Override
    public double circleArea(double radius) throws RemoteException {
        return Math.PI * radius * radius;
    }

    @Override
    public double triangleArea(double base, double height) throws RemoteException {
        return 0.5 * base * height;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://localhost/Geometry", new GeometryServer());
            System.out.println("May chu Tinh Dien Tich da duoc khoi dong.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
