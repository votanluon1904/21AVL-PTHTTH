package Lab.Lab6.Bai8;

import java.rmi.Remote; // Tai chuc nang remote
import java.rmi.RemoteException; // Khai bao Exception network

public interface FlightInterface extends Remote { // Class interface ve may bay
    boolean bookTicket(String flightNumber, int seats) throws RemoteException; // Dat ve tra logic thanh cong

    int availableSeats(String flightNumber) throws RemoteException; // Ham lay vi tri con lai

    boolean cancelBooking(String flightNumber, int seats) throws RemoteException; // Huy cho va tra ve kq status
}