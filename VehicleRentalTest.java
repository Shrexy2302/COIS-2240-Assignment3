import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleRentalTest {

    @Test
    public void testRentAndReturnVehicle() {
        RentalSystem system = RentalSystem.getInstance();

        Vehicle car = new Car("Toyota", "Corolla", 2019, 5);
        car.setLicensePlate("ABC123");
        Customer customer = new Customer(1, "George");

        system.addVehicle(car);
        system.addCustomer(customer);

        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        assertTrue(system.rentVehicle(car, customer, java.time.LocalDate.now(), 120.0));
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());

        assertFalse(system.rentVehicle(car, customer, java.time.LocalDate.now(), 120.0));

        assertTrue(system.returnVehicle(car, customer, java.time.LocalDate.now(), 0.0));
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        assertFalse(system.returnVehicle(car, customer, java.time.LocalDate.now(), 0.0));
    }
}
