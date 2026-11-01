package Lab.Lab6.Bai7;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GeometryInterface extends Remote {
    double rectangleArea(double width, double height) throws RemoteException; // Ham hinh chu nhat

    double circleArea(double radius) throws RemoteException; // Ham hinh tron

    double triangleArea(double base, double height) throws RemoteException; // Ham tam giac
}
