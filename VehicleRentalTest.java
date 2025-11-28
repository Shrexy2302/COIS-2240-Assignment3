import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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

        assertFalse("AAA1000".matches("[A-Z]{3}\\d{3}"));
        assertFalse("ZZZ99".matches("[A-Z]{3}\\d{3}"));

        Vehicle invalidVehicle = new Car("Test", "Car", 2022, 4);

        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> invalidVehicle.setLicensePlate("ZZZ99"));
    }
}
