package Lab.Lab6.Bai8;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class FlightServer extends UnicastRemoteObject implements FlightInterface {
    private HashMap<String, Integer> flights;

    protected FlightServer() throws RemoteException {
        super();
        flights = new HashMap<>();
        flights.put("VN123", 100);
        flights.put("VJ456", 50);
    }

    @Override
    public synchronized boolean bookTicket(String flightNumber, int seats) throws RemoteException {
        if (flights.containsKey(flightNumber) && flights.get(flightNumber) >= seats) {
            flights.put(flightNumber, flights.get(flightNumber) - seats);
            System.out.println("Dat " + seats + " ghe tren " + flightNumber + " - Thanh cong");
            return true;
        }
        return false;
    }

    @Override
    public int availableSeats(String flightNumber) throws RemoteException {
        return flights.getOrDefault(flightNumber, -1);
    }

    @Override
    public synchronized boolean cancelBooking(String flightNumber, int seats) throws RemoteException {
        if (flights.containsKey(flightNumber)) {
            flights.put(flightNumber, flights.get(flightNumber) + seats);
            System.out.println("Huy " + seats + " ghe tren " + flightNumber + " - Thanh cong");
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://localhost/Flight", new FlightServer());
            System.out.println("May chu Quan Ly Ve May Bay da chay.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}