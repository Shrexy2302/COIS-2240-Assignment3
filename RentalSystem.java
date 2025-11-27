import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.io.*;

public class RentalSystem {

    private static RentalSystem instance = null;

    public static RentalSystem getInstance() {
        if (instance == null) instance = new RentalSystem();
        return instance;
    }

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() {}

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord r = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(r);
            saveRecord(r);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else System.out.println("Vehicle is not available.");
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord r = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(r);
            saveRecord(r);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else System.out.println("Vehicle is not rented.");
    }

    private void saveVehicle(Vehicle v) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("vehicles.txt", true))) {
            String t = (v instanceof Car) ? "Car" :
                       (v instanceof Minibus) ? "Minibus" :
                       "PickupTruck";
            String extra = "";
            if (v instanceof Car) extra = "," + ((Car)v).getNumSeats();
            if (v instanceof Minibus) extra = "," + ((Minibus)v).getInfo().contains("Yes");
            if (v instanceof PickupTruck) extra = "," + ((PickupTruck)v).getCargoSize() + "," + ((PickupTruck)v).hasTrailer();

            bw.write(t + "," + v.getLicensePlate() + "," + v.getMake() + "," + v.getModel() + "," + v.getYear() + extra);
            bw.newLine();
        } catch (Exception e) {}
    }

    private void saveCustomer(Customer c) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("customers.txt", true))) {
            bw.write(c.getCustomerId() + "," + c.getCustomerName());
            bw.newLine();
        } catch (Exception e) {}
    }

    private void saveRecord(RentalRecord r) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("rental_records.txt", true))) {
            bw.write(r.getRecordType() + "," + r.getVehicle().getLicensePlate() + "," +
                     r.getCustomer().getCustomerName() + "," + r.getRecordDate() + "," +
                     r.getTotalAmount());
            bw.newLine();
        } catch (Exception e) {}
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles)
            if (v.getLicensePlate().equalsIgnoreCase(plate)) return v;
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id) return c;
        return null;
    }
}

