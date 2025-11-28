public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return null;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private boolean isValidPlate(String plate) {
        if (plate == null) return false;
        String p = plate.trim();
        if (p.isEmpty()) return false;
        return p.matches("[A-Za-z]{3}\\d{3}");
    }

    public void setLicensePlate(String plate) {
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Invalid license plate");
        }
        this.licensePlate = plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
}
