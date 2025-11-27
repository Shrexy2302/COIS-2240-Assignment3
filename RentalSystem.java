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

    private RentalSystem() {
        loadData();
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getLicensePlate() == null) {
            System.out.println("Invalid vehicle.");
            return false;
        }
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle with this plate already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (customer == null) {
            System.out.println("Invalid customer.");
            return false;
        }
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Customer with this ID already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle == null || customer == null) {
            System.out.println("Vehicle or customer is null.");
            return;
        }
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord r = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(r);
            saveRecord(r);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle == null || customer == null) {
            System.out.println("Vehicle or customer is null.");
            return;
        }
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord r = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(r);
            saveRecord(r);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
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

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                String type = p[0];
                String plate = p[1];
                String make = p[2];
                String model = p[3];
                int year = Integer.parseInt(p[4]);

                Vehicle v = null;
                if (type.equals("Car") && p.length >= 6) {
                    int seats = Integer.parseInt(p[5]);
                    v = new Car(make, model, year, seats);
                } else if (type.equals("Minibus") && p.length >= 6) {
                    boolean isAccessible = Boolean.parseBoolean(p[5]);
                    v = new Minibus(make, model, year, isAccessible);
                } else if (type.equals("PickupTruck") && p.length >= 7) {
                    double cargoSize = Double.parseDouble(p[5]);
                    boolean hasTrailer = Boolean.parseBoolean(p[6]);
                    v = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                }

                if (v != null) {
                    v.setLicensePlate(plate);
                    vehicles.add(v);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {}

        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                if (findCustomerById(id) == null) {
                    customers.add(new Customer(id, name));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {}

        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(",");
                String type = p[0];
                String plate = p[1];
                String customerName = p[2];
                LocalDate date = LocalDate.parse(p[3]);
                double amount = Double.parseDouble(p[4]);

                Vehicle v = findVehicleByPlate(plate);
                Customer c = null;
                for (Customer cu : customers) {
                    if (cu.getCustomerName().equalsIgnoreCase(customerName)) {
                        c = cu;
                        break;
                    }
                }
                if (c == null) {
                    c = new Customer(0, customerName);
                    customers.add(c);
                }

                RentalRecord r = new RentalRecord(v, c, date, amount, type);
                rentalHistory.addRecord(r);
            }
        } catch (FileNotFoundException e) {
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

    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null) System.out.println("\n=== All Vehicles ===");
        else System.out.println("\n=== " + status + " Vehicles ===");

        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n",
                " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");

        boolean found = false;
        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {
                found = true;
                String t = (v instanceof Car) ? "Car" :
                           (v instanceof Minibus) ? "Minibus" :
                           (v instanceof PickupTruck) ? "PickupTruck" : "Unknown";
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n",
                        t, v.getLicensePlate(), v.getMake(), v.getModel(), v.getYear(), v.getStatus());
            }
        }
        if (!found) System.out.println("No vehicles match.\n");
    }

    public void displayAllCustomers() {
        for (Customer c : customers) System.out.println("  " + c.toString());
    }

    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("No rental history found.");
            return;
        }

        System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n",
                "Type", "Plate", "Customer", "Date", "Amount");
        System.out.println("|-------------------------------------------------------------------------------|");

        for (RentalRecord r : rentalHistory.getRentalHistory()) {
            System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n",
                    r.getRecordType(), r.getVehicle().getLicensePlate(),
                    r.getCustomer().getCustomerName(), r.getRecordDate(),
                    r.getTotalAmount());
        }
    }
}



