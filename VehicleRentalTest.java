import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class VehicleRentalTest {

    @Test
    public void testLicensePlate() {
        Vehicle v1 = new Car("Toyota", "Corolla", 2019, 5);
        Vehicle v2 = new Car("Honda", "Civic", 2020, 5);
        Vehicle v3 = new Car("Ford", "Focus", 2021, 5);

        assertDoesNotThrow(() -> v1.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> v2.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> v3.setLicensePlate("ZZZ999"));

        assertEquals("AAA100", v1.getLicensePlate());
        assertEquals("ABC567", v2.getLicensePlate());
        assertEquals("ZZZ999", v3.getLicensePlate());

        Vehicle invalid = new Car("Test", "Car", 2022, 4);
        assertThrows(IllegalArgumentException.class, () -> invalid.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> invalid.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> invalid.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> invalid.setLicensePlate("ZZZ99"));
    }

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

    @Test
    public void testSingletonRentalSystem() throws Exception {
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        int modifiers = constructor.getModifiers();
        assertEquals(Modifier.PRIVATE, modifiers);

        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance);
    }
}
