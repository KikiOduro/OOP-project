import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Main demonstration class for the GardenMate Reservation System.
 * Shows how all the classes work together in an end-to-end scenario.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     GARDENMATE - Community Garden Reservation System  ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");

        // ============================================================
        // 1. CREATE THE GARDEN SYSTEM (Facade Pattern)
        // ============================================================
        System.out.println("▶ Initializing GardenMate System...\n");
        GardenSystem system = new GardenSystem();

        // ============================================================
        // 2. ADD GARDEN PLOTS
        // ============================================================
        System.out.println("▶ Setting up Garden Plots...");
        
        GardenPlot plot1 = new GardenPlot("P001", "Sunny Corner", 25.0, "North Section");
        GardenPlot plot2 = new GardenPlot("P002", "Shady Grove", 30.0, "East Section");
        GardenPlot plot3 = new GardenPlot("P003", "Herb Garden", 15.0, "South Section");
        
        // Add crop restrictions to Herb Garden
        plot3.addAllowedCrop("basil");
        plot3.addAllowedCrop("mint");
        plot3.addAllowedCrop("rosemary");
        plot3.addAllowedCrop("thyme");
        
        system.addPlot(plot1);
        system.addPlot(plot2);
        system.addPlot(plot3);
        
        System.out.println("  Added: " + plot1.getName() + " (" + plot1.getSizeSqMeters() + " m²)");
        System.out.println("  Added: " + plot2.getName() + " (" + plot2.getSizeSqMeters() + " m²)");
        System.out.println("  Added: " + plot3.getName() + " (Restricted to herbs only)");
        System.out.println();

        // ============================================================
        // 3. REGISTER GARDENERS
        // ============================================================
        System.out.println("▶ Registering Gardeners...");
        
        Gardener alice = new Gardener("G001", "Alice Johnson", "alice@email.com", "555-1234");
        Gardener bob = new Gardener("G002", "Bob Smith", "bob@email.com", "555-5678");
        Gardener carol = new Gardener("G003", "Carol Davis");
        
        system.registerGardener(alice);
        system.registerGardener(bob);
        system.registerGardener(carol);
        
        System.out.println("  Registered: " + alice.getName());
        System.out.println("  Registered: " + bob.getName());
        System.out.println("  Registered: " + carol.getName());
        System.out.println();

        // ============================================================
        // 4. CREATE CROPS (Value Objects with Immutability)
        // ============================================================
        System.out.println("▶ Defining Available Crops...");
        
        Crop tomatoes = new Crop("Tomatoes", 90, 
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.SUMMER)),
            "Requires full sun and regular watering");
        Crop lettuce = new Crop("Lettuce", 45,
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.FALL)),
            "Cool weather crop");
        Crop basil = new Crop("Basil", 60,
            new HashSet<>(Arrays.asList(Crop.Season.SUMMER)),
            "Aromatic herb");
        Crop carrots = new Crop("Carrots", 70);
        
        System.out.println("  Defined: " + tomatoes);
        System.out.println("  Defined: " + lettuce);
        System.out.println("  Defined: " + basil);
        System.out.println("  Defined: " + carrots);
        System.out.println();

        // ============================================================
        // 5. CREATE DATE RANGES (Immutable Value Objects)
        // ============================================================
        System.out.println("▶ Setting up Reservation Periods...");
        
        DateRange springPeriod = new DateRange(
            LocalDate.of(2025, 3, 1),
            LocalDate.of(2025, 5, 31)
        );
        DateRange summerPeriod = new DateRange(
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 8, 31)
        );
        DateRange overlappingPeriod = new DateRange(
            LocalDate.of(2025, 4, 15),
            LocalDate.of(2025, 6, 15)
        );
        
        System.out.println("  Spring Period: " + springPeriod + " (" + springPeriod.lengthInDays() + " days)");
        System.out.println("  Summer Period: " + summerPeriod + " (" + summerPeriod.lengthInDays() + " days)");
        System.out.println("  Overlapping Period: " + overlappingPeriod);
        System.out.println("  Do Spring and Summer overlap? " + springPeriod.overlaps(summerPeriod));
        System.out.println("  Do Spring and Overlapping overlap? " + springPeriod.overlaps(overlappingPeriod));
        System.out.println();

        // ============================================================
        // 6. CREATE RESERVATIONS (State Pattern for Status)
        // ============================================================
        System.out.println("▶ Creating Reservations...\n");
        
        // Alice reserves Sunny Corner for Spring
        List<Crop> aliceCrops = Arrays.asList(tomatoes, lettuce);
        Reservation res1 = system.createReservation("P001", "G001", springPeriod, aliceCrops);
        if (res1 != null) {
            System.out.println("  ✓ Created: " + res1.getReservationID() + " for " + alice.getName());
            System.out.println("    Plot: " + res1.getPlot().getName());
            System.out.println("    Status: " + res1.getStatus());
        }

        // Bob reserves Shady Grove for Summer
        List<Crop> bobCrops = Arrays.asList(carrots);
        Reservation res2 = system.createReservation("P002", "G002", summerPeriod, bobCrops);
        if (res2 != null) {
            System.out.println("  ✓ Created: " + res2.getReservationID() + " for " + bob.getName());
            System.out.println("    Status: " + res2.getStatus());
        }

        // Carol tries to reserve Herb Garden with non-herb crop (should fail)
        System.out.println("\n  Attempting to book Herb Garden with Tomatoes...");
        Reservation res3 = system.createReservation("P003", "G003", springPeriod, Arrays.asList(tomatoes));
        if (res3 == null) {
            System.out.println("  ✗ Failed: Tomatoes not allowed in Herb Garden");
        }

        // Carol reserves Herb Garden with herbs (should succeed)
        List<Crop> herbCrops = Arrays.asList(basil);
        Reservation res4 = system.createReservation("P003", "G003", springPeriod, herbCrops);
        if (res4 != null) {
            System.out.println("  ✓ Created: " + res4.getReservationID() + " for " + carol.getName());
        }
        System.out.println();

        // ============================================================
        // 7. CONFIRM RESERVATIONS (Status Transitions)
        // ============================================================
        System.out.println("▶ Confirming Reservations...\n");
        
        system.confirmReservation(res1.getReservationID());
        System.out.println("  ✓ Confirmed: " + res1.getReservationID() + " -> " + res1.getStatus());
        
        system.confirmReservation(res2.getReservationID());
        System.out.println("  ✓ Confirmed: " + res2.getReservationID() + " -> " + res2.getStatus());
        
        System.out.println();

        // ============================================================
        // 8. TEST CONFLICT DETECTION
        // ============================================================
        System.out.println("▶ Testing Conflict Detection...\n");
        
        System.out.println("  Attempting to book Sunny Corner during overlapping period...");
        Reservation conflictRes = system.createReservation("P001", "G002", overlappingPeriod, null);
        if (conflictRes == null) {
            System.out.println("  ✗ Failed: Correctly detected overlap with existing reservation");
        }
        System.out.println();

        // ============================================================
        // 9. CHECK AVAILABILITY
        // ============================================================
        System.out.println("▶ Checking Plot Availability...\n");
        
        List<GardenPlot> availableForSummer = system.findAvailablePlots(summerPeriod);
        System.out.println("  Available plots for Summer 2025:");
        for (GardenPlot plot : availableForSummer) {
            System.out.println("    - " + plot.getName() + " (" + plot.getPlotID() + ")");
        }
        System.out.println();

        // ============================================================
        // 10. CANCEL A RESERVATION
        // ============================================================
        System.out.println("▶ Cancelling a Reservation...\n");
        
        System.out.println("  Before cancellation: " + res4.getStatus());
        system.cancelReservation(res4.getReservationID());
        System.out.println("  After cancellation: " + res4.getStatus());
        System.out.println();

        // ============================================================
        // 11. COMPLETE A RESERVATION
        // ============================================================
        System.out.println("▶ Completing a Reservation...\n");
        
        System.out.println("  Before completion: " + res2.getStatus());
        system.completeReservation(res2.getReservationID());
        System.out.println("  After completion: " + res2.getStatus());
        System.out.println();

        // ============================================================
        // 12. GENERATE REPORTS
        // ============================================================
        System.out.println("▶ Generating Reports...\n");
        System.out.println(system.generatePlantingReport());
        System.out.println(system.generateAvailabilityReport());

        // ============================================================
        // 13. DEMONSTRATE OOP TECHNIQUES
        // ============================================================
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║              OOP TECHNIQUES DEMONSTRATED              ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝\n");
        
        System.out.println("1. ENCAPSULATION:");
        System.out.println("   - All fields are private with getters/setters");
        System.out.println("   - Internal lists return unmodifiable views");
        System.out.println("   - Package-private methods for internal operations");
        System.out.println();
        
        System.out.println("2. IMMUTABILITY (Value Objects):");
        System.out.println("   - DateRange: immutable with final fields");
        System.out.println("   - Crop: immutable with defensive copying of collections");
        System.out.println();
        
        System.out.println("3. ENUM with Behavior:");
        System.out.println("   - ReservationStatus has canTransitionTo(), isActive(), occupiesPlot()");
        System.out.println("   - Encapsulates state machine logic");
        System.out.println();
        
        System.out.println("4. FACADE Pattern:");
        System.out.println("   - GardenSystem provides simplified interface to complex subsystem");
        System.out.println("   - Coordinates Gardeners, Plots, and Reservations");
        System.out.println();
        
        System.out.println("5. STATE Pattern (via enum):");
        System.out.println("   - ReservationStatus controls valid transitions");
        System.out.println("   - Reservation.confirm(), cancel(), complete() methods");
        System.out.println();
        
        System.out.println("6. COMPOSITION:");
        System.out.println("   - Reservation contains Gardener, GardenPlot, DateRange, List<Crop>");
        System.out.println("   - GardenPlot manages its own reservations list");
        System.out.println();
        
        System.out.println("7. VALIDATION & DEFENSIVE PROGRAMMING:");
        System.out.println("   - Constructor validation with IllegalArgumentException");
        System.out.println("   - Null checks throughout");
        System.out.println("   - Business rule enforcement (crop restrictions, overlaps)");
        System.out.println();
        
        System.out.println("8. equals() / hashCode() / toString():");
        System.out.println("   - Proper implementations for object comparison");
        System.out.println("   - Identity based on IDs for entities");
        System.out.println("   - Value-based for value objects (DateRange, Crop)");
        System.out.println();

        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("                    DEMO COMPLETE!                      ");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}
