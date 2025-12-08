import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import java.util.Set;

/**
 * Test script that checks if each method of all classes works correctly.
 * Prints PASS or FAIL for each test.
 */
public class TestScript {
    
    private static int passed = 0;
    private static int failed = 0;
    
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         GARDENMATE METHOD TEST SCRIPT                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        
        testDateRange();
        testCrop();
        testReservationStatus();
        testGardener();
        testGardenPlot();
        testReservation();
        testGardenSystem();
        
        // Print summary
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    TEST SUMMARY                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("  PASSED: " + passed);
        System.out.println("  FAILED: " + failed);
        System.out.println("  TOTAL:  " + (passed + failed));
        System.out.println("  RESULT: " + (failed == 0 ? "✅ ALL TESTS PASSED!" : "❌ SOME TESTS FAILED"));
    }
    
    //  DATERANGE TESTS 
    
    private static void testDateRange() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing DateRange class");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusDays(7);
        LocalDate lastWeek = today.minusDays(7);
        LocalDate yesterday = today.minusDays(1);
        
        // Constructor test
        DateRange range = new DateRange(today, nextWeek);
        test("DateRange constructor", range != null);
        
        // Test invalid constructor (start after end)
        try {
            new DateRange(nextWeek, today);
            test("DateRange rejects start after end", false);
        } catch (IllegalArgumentException e) {
            test("DateRange rejects start after end", true);
        }
        
        // Test null rejection
        try {
            new DateRange(null, today);
            test("DateRange rejects null dates", false);
        } catch (IllegalArgumentException e) {
            test("DateRange rejects null dates", true);
        }
        
        // Getters
        test("getStartDate()", range.getStartDate().equals(today));
        test("getEndDate()", range.getEndDate().equals(nextWeek));
        
        // isValid()
        test("isValid() returns true for valid range", range.isValid());
        
        // overlaps()
        DateRange overlapping = new DateRange(tomorrow, nextWeek.plusDays(3));
        DateRange nonOverlapping = new DateRange(nextWeek.plusDays(10), nextWeek.plusDays(20));
        test("overlaps() - overlapping ranges", range.overlaps(overlapping));
        test("overlaps() - non-overlapping ranges", !range.overlaps(nonOverlapping));
        test("overlaps() - null returns false", !range.overlaps(null));
        
        // contains(LocalDate)
        test("contains(date) - date inside range", range.contains(tomorrow));
        test("contains(date) - date outside range", !range.contains(nextWeek.plusDays(10)));
        test("contains(date) - null returns false", !range.contains((LocalDate)null));
        
        // contains(DateRange)
        DateRange inner = new DateRange(tomorrow, today.plusDays(5));
        DateRange outer = new DateRange(lastWeek, nextWeek.plusDays(10));
        test("contains(range) - inner range", range.contains(inner));
        test("contains(range) - outer range", !range.contains(outer));
        test("contains(range) - null returns false", !range.contains((DateRange)null));
        
        // lengthInDays()
        DateRange sevenDays = new DateRange(today, today.plusDays(6));
        test("lengthInDays()", sevenDays.lengthInDays() == 7);
        
        // isInPast()
        DateRange pastRange = new DateRange(lastWeek, yesterday);
        test("isInPast() - past range", pastRange.isInPast());
        test("isInPast() - current/future range", !range.isInPast());
        
        // isInFuture()
        DateRange futureRange = new DateRange(tomorrow, nextWeek);
        test("isInFuture() - future range", futureRange.isInFuture());
        test("isInFuture() - includes today", !range.isInFuture());
        
        // isCurrentlyActive()
        DateRange activeRange = new DateRange(yesterday, tomorrow);
        test("isCurrentlyActive() - active range", activeRange.isCurrentlyActive());
        test("isCurrentlyActive() - future range", !futureRange.isCurrentlyActive());
        
        // equals() and hashCode()
        DateRange same = new DateRange(today, nextWeek);
        DateRange different = new DateRange(today, tomorrow);
        test("equals() - same range", range.equals(same));
        test("equals() - different range", !range.equals(different));
        test("hashCode() - same for equal objects", range.hashCode() == same.hashCode());
        
        // toString()
        test("toString() - returns string", range.toString() != null && !range.toString().isEmpty());
        
        System.out.println();
    }
    
    // CROP TESTS 
    
    private static void testCrop() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing Crop class");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        // Test Season enum
        test("Crop.Season.SPRING exists", Crop.Season.SPRING != null);
        test("Crop.Season.SUMMER exists", Crop.Season.SUMMER != null);
        test("Crop.Season.FALL exists", Crop.Season.FALL != null);
        test("Crop.Season.WINTER exists", Crop.Season.WINTER != null);
        
        // Constructor tests
        Set<Crop.Season> seasons = new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.SUMMER));
        Crop fullCrop = new Crop("Tomato", 90, seasons, "A juicy fruit");
        test("Full constructor", fullCrop != null);
        
        Crop simpleCrop = new Crop("Carrot");
        test("Simple constructor (name only)", simpleCrop != null);
        
        Crop mediumCrop = new Crop("Lettuce", 45);
        test("Constructor with name and days", mediumCrop != null);
        
        // Test invalid constructor
        try {
            new Crop(null);
            test("Crop rejects null name", false);
        } catch (IllegalArgumentException e) {
            test("Crop rejects null name", true);
        }
        
        try {
            new Crop("");
            test("Crop rejects empty name", false);
        } catch (IllegalArgumentException e) {
            test("Crop rejects empty name", true);
        }
        
        try {
            new Crop("Test", -5);
            test("Crop rejects negative growing days", false);
        } catch (IllegalArgumentException e) {
            test("Crop rejects negative growing days", true);
        }
        
        // Getters
        test("getName()", fullCrop.getName().equals("Tomato"));
        test("getMinGrowingDays()", fullCrop.getMinGrowingDays() == 90);
        test("getBestSeasons()", fullCrop.getBestSeasons().size() == 2);
        test("getDescription()", fullCrop.getDescription().equals("A juicy fruit"));
        
        // canGrowIn()
        LocalDate today = LocalDate.now();
        DateRange longEnough = new DateRange(today, today.plusDays(100));
        DateRange tooShort = new DateRange(today, today.plusDays(30));
        test("canGrowIn() - long enough period", fullCrop.canGrowIn(longEnough));
        test("canGrowIn() - too short period", !fullCrop.canGrowIn(tooShort));
        test("canGrowIn() - null returns false", !fullCrop.canGrowIn(null));
        
        // equals() and hashCode()
        Crop sameTomato = new Crop("tomato"); // lowercase should still equal
        Crop carrot = new Crop("Carrot");
        test("equals() - same crop (case-insensitive)", fullCrop.equals(sameTomato));
        test("equals() - different crop", !fullCrop.equals(carrot));
        test("hashCode() - same for equal objects", fullCrop.hashCode() == sameTomato.hashCode());
        
        // toString()
        test("toString() - returns string", fullCrop.toString() != null);
        test("toString() - contains name", fullCrop.toString().contains("Tomato"));
        
        System.out.println();
    }
    
    //RESERVATIONSTATUS TESTS
    
    private static void testReservationStatus() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing ReservationStatus enum");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        // Test all enum values exist
        test("REQUESTED exists", ReservationStatus.REQUESTED != null);
        test("CONFIRMED exists", ReservationStatus.CONFIRMED != null);
        test("CANCELLED exists", ReservationStatus.CANCELLED != null);
        test("COMPLETED exists", ReservationStatus.COMPLETED != null);
        
        // getDescription()
        test("getDescription() - returns string", 
             ReservationStatus.REQUESTED.getDescription() != null);
        
        // canTransitionTo() - valid transitions
        test("REQUESTED -> CONFIRMED allowed", 
             ReservationStatus.REQUESTED.canTransitionTo(ReservationStatus.CONFIRMED));
        test("REQUESTED -> CANCELLED allowed", 
             ReservationStatus.REQUESTED.canTransitionTo(ReservationStatus.CANCELLED));
        test("CONFIRMED -> COMPLETED allowed", 
             ReservationStatus.CONFIRMED.canTransitionTo(ReservationStatus.COMPLETED));
        test("CONFIRMED -> CANCELLED allowed", 
             ReservationStatus.CONFIRMED.canTransitionTo(ReservationStatus.CANCELLED));
        
        // canTransitionTo() - invalid transitions
        test("REQUESTED -> COMPLETED not allowed", 
             !ReservationStatus.REQUESTED.canTransitionTo(ReservationStatus.COMPLETED));
        test("CANCELLED -> any not allowed (terminal)", 
             !ReservationStatus.CANCELLED.canTransitionTo(ReservationStatus.REQUESTED));
        test("COMPLETED -> any not allowed (terminal)", 
             !ReservationStatus.COMPLETED.canTransitionTo(ReservationStatus.CANCELLED));
        test("Same status transition not allowed", 
             !ReservationStatus.REQUESTED.canTransitionTo(ReservationStatus.REQUESTED));
        test("Null transition not allowed", 
             !ReservationStatus.REQUESTED.canTransitionTo(null));
        
        // isActive()
        test("isActive() - REQUESTED is active", ReservationStatus.REQUESTED.isActive());
        test("isActive() - CONFIRMED is active", ReservationStatus.CONFIRMED.isActive());
        test("isActive() - CANCELLED is not active", !ReservationStatus.CANCELLED.isActive());
        test("isActive() - COMPLETED is not active", !ReservationStatus.COMPLETED.isActive());
        
        // isTerminal()
        test("isTerminal() - REQUESTED is not terminal", !ReservationStatus.REQUESTED.isTerminal());
        test("isTerminal() - CONFIRMED is not terminal", !ReservationStatus.CONFIRMED.isTerminal());
        test("isTerminal() - CANCELLED is terminal", ReservationStatus.CANCELLED.isTerminal());
        test("isTerminal() - COMPLETED is terminal", ReservationStatus.COMPLETED.isTerminal());
        
        // occupiesPlot()
        test("occupiesPlot() - only CONFIRMED occupies", ReservationStatus.CONFIRMED.occupiesPlot());
        test("occupiesPlot() - REQUESTED does not occupy", !ReservationStatus.REQUESTED.occupiesPlot());
        test("occupiesPlot() - CANCELLED does not occupy", !ReservationStatus.CANCELLED.occupiesPlot());
        test("occupiesPlot() - COMPLETED does not occupy", !ReservationStatus.COMPLETED.occupiesPlot());
        
        System.out.println();
    }
    
    //  GARDENER TESTS 
    
    private static void testGardener() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing Gardener class");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        // Constructor tests
        Gardener fullGardener = new Gardener("G001", "Alice", "alice@email.com", "555-1234");
        test("Full constructor", fullGardener != null);
        
        Gardener simpleGardener = new Gardener("G002", "Bob");
        test("Constructor with ID and name", simpleGardener != null);
        
        Gardener minimalGardener = new Gardener("G003");
        test("Minimal constructor (ID only)", minimalGardener != null);
        
        // Test invalid constructor
        try {
            new Gardener(null);
            test("Gardener rejects null ID", false);
        } catch (IllegalArgumentException e) {
            test("Gardener rejects null ID", true);
        }
        
        try {
            new Gardener("");
            test("Gardener rejects empty ID", false);
        } catch (IllegalArgumentException e) {
            test("Gardener rejects empty ID", true);
        }
        
        // Getters
        test("getGardenerID()", fullGardener.getGardenerID().equals("G001"));
        test("getName()", fullGardener.getName().equals("Alice"));
        test("getGardenerName() - alias", fullGardener.getGardenerName().equals("Alice"));
        test("getEmail()", fullGardener.getEmail().equals("alice@email.com"));
        test("getPhoneNumber()", fullGardener.getPhoneNumber().equals("555-1234"));
        test("getReservations() - initially empty", fullGardener.getReservations().isEmpty());
        
        // Setters
        fullGardener.setName("Alice Smith");
        test("setName()", fullGardener.getName().equals("Alice Smith"));
        
        fullGardener.setEmail("alice.smith@email.com");
        test("setEmail()", fullGardener.getEmail().equals("alice.smith@email.com"));
        
        fullGardener.setPhoneNumber("555-5678");
        test("setPhoneNumber()", fullGardener.getPhoneNumber().equals("555-5678"));
        
        // Reservation management (need to set up reservation first)
        LocalDate today = LocalDate.now();
        DateRange range = new DateRange(today, today.plusDays(30));
        GardenPlot plot = new GardenPlot("P001", "Test Plot");
        Reservation res = new Reservation("R001", plot, fullGardener, range);
        
        fullGardener.addReservation(res);
        test("addReservation()", fullGardener.getReservations().size() == 1);
        
        test("getActiveReservations()", fullGardener.getActiveReservations().size() == 1);
        test("getActiveReservationCount()", fullGardener.getActiveReservationCount() == 1);
        test("hasActiveReservations()", fullGardener.hasActiveReservations());
        test("hasReservationForPlot() - has reservation", fullGardener.hasReservationForPlot("P001"));
        test("hasReservationForPlot() - no reservation", !fullGardener.hasReservationForPlot("P999"));
        
        fullGardener.removeReservation(res);
        test("removeReservation()", fullGardener.getReservations().isEmpty());
        
        // equals() and hashCode()
        Gardener sameId = new Gardener("G001", "Different Name");
        Gardener differentId = new Gardener("G999", "Alice");
        test("equals() - same ID", fullGardener.equals(sameId));
        test("equals() - different ID", !fullGardener.equals(differentId));
        test("hashCode() - same for equal objects", fullGardener.hashCode() == sameId.hashCode());
        
        // toString() and toDetailedString()
        test("toString() - returns string", fullGardener.toString() != null);
        test("toDetailedString() - returns string", fullGardener.toDetailedString() != null);
        
        System.out.println();
    }
    
    // GARDENPLOT TESTS  
    
    private static void testGardenPlot() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing GardenPlot class");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        // Constructor tests
        GardenPlot fullPlot = new GardenPlot("P001", "Sunny Garden", 25.5, "North Corner");
        test("Full constructor", fullPlot != null);
        
        GardenPlot simplePlot = new GardenPlot("P002", "Shady Spot");
        test("Constructor with ID and name", simplePlot != null);
        
        GardenPlot minimalPlot = new GardenPlot("P003");
        test("Minimal constructor (ID only)", minimalPlot != null);
        
        // Test invalid constructor
        try {
            new GardenPlot(null);
            test("GardenPlot rejects null ID", false);
        } catch (IllegalArgumentException e) {
            test("GardenPlot rejects null ID", true);
        }
        
        // Getters
        test("getPlotID()", fullPlot.getPlotID().equals("P001"));
        test("getName()", fullPlot.getName().equals("Sunny Garden"));
        test("getSizeSqMeters()", fullPlot.getSizeSqMeters() == 25.5);
        test("getLocation()", fullPlot.getLocation().equals("North Corner"));
        test("getCurrentGardener() - initially null", fullPlot.getCurrentGardener() == null);
        test("getAllowedCrops() - initially empty", fullPlot.getAllowedCrops().isEmpty());
        test("getReservations() - initially empty", fullPlot.getReservations().isEmpty());
        
        // Setters
        fullPlot.setName("Updated Garden");
        test("setName()", fullPlot.getName().equals("Updated Garden"));
        
        fullPlot.setSizeSqMeters(30.0);
        test("setSizeSqMeters()", fullPlot.getSizeSqMeters() == 30.0);
        
        fullPlot.setLocation("South Corner");
        test("setLocation()", fullPlot.getLocation().equals("South Corner"));
        
        // Crop restrictions
        fullPlot.addAllowedCrop("Tomato");
        fullPlot.addAllowedCrop("Carrot");
        test("addAllowedCrop()", fullPlot.getAllowedCrops().size() == 2);
        
        Crop tomato = new Crop("Tomato");
        Crop pepper = new Crop("Pepper");
        test("isCropAllowed(Crop) - allowed crop", fullPlot.isCropAllowed(tomato));
        test("isCropAllowed(Crop) - not allowed crop", !fullPlot.isCropAllowed(pepper));
        test("isCropAllowed(String) - allowed crop", fullPlot.isCropAllowed("tomato"));
        test("isCropAllowed(String) - not allowed crop", !fullPlot.isCropAllowed("pepper"));
        
        fullPlot.removeAllowedCrop("Carrot");
        test("removeAllowedCrop()", fullPlot.getAllowedCrops().size() == 1);
        
        fullPlot.clearCropRestrictions();
        test("clearCropRestrictions()", fullPlot.getAllowedCrops().isEmpty());
        test("isCropAllowed() - no restrictions means all allowed", fullPlot.isCropAllowed(pepper));
        
        // Availability methods
        LocalDate today = LocalDate.now();
        DateRange range1 = new DateRange(today, today.plusDays(30));
        test("isAvailable() - no reservations", fullPlot.isAvailable(range1));
        test("isCurrentlyOccupied() - no confirmed reservations", !fullPlot.isCurrentlyOccupied());
        
        // Add a reservation and test
        Gardener gardener = new Gardener("G001", "Test Gardener");
        Reservation res = new Reservation("R001", fullPlot, gardener, range1);
        fullPlot.addReservation(res);
        test("addReservation()", fullPlot.getReservations().size() == 1);
        test("isAvailable() - requested reservation doesn't block", fullPlot.isAvailable(range1));
        
        // Confirm reservation and re-test
        res.confirm();
        test("isAvailable() - confirmed reservation blocks", !fullPlot.isAvailable(range1));
        
        DateRange nonOverlapping = new DateRange(today.plusDays(60), today.plusDays(90));
        test("isAvailable() - non-overlapping dates", fullPlot.isAvailable(nonOverlapping));
        
        test("getActiveReservations()", fullPlot.getActiveReservations().size() == 1);
        test("getConflictingReservations()", fullPlot.getConflictingReservations(range1).size() == 1);
        
        // assign() and release()
        fullPlot.assign(gardener);
        test("assign()", fullPlot.getCurrentGardener() != null);
        test("getCurrentGardener()", fullPlot.getCurrentGardener().equals(gardener));
        
        fullPlot.release();
        test("release()", fullPlot.getCurrentGardener() == null);
        
        fullPlot.removeReservation(res);
        test("removeReservation()", fullPlot.getReservations().isEmpty());
        
        // equals() and hashCode()
        GardenPlot sameId = new GardenPlot("P001", "Different Name");
        GardenPlot differentId = new GardenPlot("P999", "Sunny Garden");
        test("equals() - same ID", fullPlot.equals(sameId));
        test("equals() - different ID", !fullPlot.equals(differentId));
        test("hashCode() - same for equal objects", fullPlot.hashCode() == sameId.hashCode());
        
        // toString() and toDetailedString()
        test("toString() - returns string", fullPlot.toString() != null);
        test("toDetailedString() - returns string", fullPlot.toDetailedString() != null);
        
        System.out.println();
    }
    
    
    private static void testReservation() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing Reservation class");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        LocalDate today = LocalDate.now();
        DateRange range = new DateRange(today, today.plusDays(30));
        GardenPlot plot = new GardenPlot("P001", "Test Plot");
        Gardener gardener = new Gardener("G001", "Test Gardener");
        Crop tomato = new Crop("Tomato", 60);
        List<Crop> crops = Arrays.asList(tomato);
        
        // Constructor tests
        Reservation fullRes = new Reservation("R001", plot, gardener, range, crops);
        test("Full constructor", fullRes != null);
        
        Reservation simpleRes = new Reservation("R002", plot, gardener, range);
        test("Constructor without planting plan", simpleRes != null);
        
        // Test invalid constructors
        try {
            new Reservation(null, plot, gardener, range);
            test("Reservation rejects null ID", false);
        } catch (IllegalArgumentException e) {
            test("Reservation rejects null ID", true);
        }
        
        try {
            new Reservation("R003", null, gardener, range);
            test("Reservation rejects null plot", false);
        } catch (IllegalArgumentException e) {
            test("Reservation rejects null plot", true);
        }
        
        try {
            new Reservation("R003", plot, null, range);
            test("Reservation rejects null gardener", false);
        } catch (IllegalArgumentException e) {
            test("Reservation rejects null gardener", true);
        }
        
        try {
            new Reservation("R003", plot, gardener, null);
            test("Reservation rejects null dateRange", false);
        } catch (IllegalArgumentException e) {
            test("Reservation rejects null dateRange", true);
        }
        
        // Getters
        test("getReservationID()", fullRes.getReservationID().equals("R001"));
        test("getPlot()", fullRes.getPlot().equals(plot));
        test("getGardener()", fullRes.getGardener().equals(gardener));
        test("getDateRange()", fullRes.getDateRange().equals(range));
        test("getStatus() - initial is REQUESTED", fullRes.getStatus() == ReservationStatus.REQUESTED);
        test("getPlantingPlan()", fullRes.getPlantingPlan().size() == 1);
        
        // Planting plan management
        Crop carrot = new Crop("Carrot", 30);
        fullRes.addCrop(carrot);
        test("addCrop()", fullRes.getPlantingPlan().size() == 2);
        
        fullRes.removeCrop(carrot);
        test("removeCrop()", fullRes.getPlantingPlan().size() == 1);
        
        fullRes.clearPlantingPlan();
        test("clearPlantingPlan()", fullRes.getPlantingPlan().isEmpty());
        
        // Status queries (on REQUESTED)
        test("isActive() - REQUESTED", fullRes.isActive());
        test("isConfirmed() - not yet", !fullRes.isConfirmed());
        test("isCancelled() - not cancelled", !fullRes.isCancelled());
        test("isCompleted() - not completed", !fullRes.isCompleted());
        
        // Status transitions
        Reservation transitionRes = new Reservation("R003", plot, gardener, range);
        test("confirm()", transitionRes.confirm());
        test("isConfirmed() - after confirm", transitionRes.isConfirmed());
        
        Reservation cancelRes = new Reservation("R004", plot, gardener, range);
        test("cancel() - from REQUESTED", cancelRes.cancel());
        test("isCancelled() - after cancel", cancelRes.isCancelled());
        
        Reservation completeRes = new Reservation("R005", plot, gardener, range);
        completeRes.confirm();
        test("complete() - from CONFIRMED", completeRes.complete());
        test("isCompleted() - after complete", completeRes.isCompleted());
        
        // Invalid transitions
        Reservation invalidRes = new Reservation("R006", plot, gardener, range);
        try {
            invalidRes.complete(); // Can't go REQUESTED -> COMPLETED
            test("Invalid transition throws exception", false);
        } catch (IllegalStateException e) {
            test("Invalid transition throws exception", true);
        }
        
        // conflictsWith()
        Reservation confirmedRes = new Reservation("R007", plot, gardener, range);
        confirmedRes.confirm();
        DateRange overlapping = new DateRange(today.plusDays(10), today.plusDays(40));
        DateRange nonOverlapping = new DateRange(today.plusDays(60), today.plusDays(90));
        test("conflictsWith() - overlapping", confirmedRes.conflictsWith(overlapping));
        test("conflictsWith() - non-overlapping", !confirmedRes.conflictsWith(nonOverlapping));
        
        // Requested doesn't cause conflicts
        Reservation requestedRes = new Reservation("R008", plot, gardener, range);
        test("conflictsWith() - REQUESTED doesn't conflict", !requestedRes.conflictsWith(overlapping));
        
        // Validation methods
        GardenPlot restrictedPlot = new GardenPlot("P002", "Restricted Plot");
        restrictedPlot.addAllowedCrop("Tomato");
        Reservation validCropRes = new Reservation("R009", restrictedPlot, gardener, range, 
                                                    Arrays.asList(new Crop("Tomato")));
        test("validateCrops() - allowed crop", validCropRes.validateCrops());
        
        Reservation invalidCropRes = new Reservation("R010", restrictedPlot, gardener, range, 
                                                      Arrays.asList(new Crop("Pepper")));
        test("validateCrops() - disallowed crop", !invalidCropRes.validateCrops());
        
        DateRange longRange = new DateRange(today, today.plusDays(100));
        Reservation longRes = new Reservation("R011", plot, gardener, longRange, 
                                               Arrays.asList(new Crop("Tomato", 60)));
        test("validateGrowingPeriod() - long enough", longRes.validateGrowingPeriod());
        
        DateRange shortRange = new DateRange(today, today.plusDays(10));
        Reservation shortRes = new Reservation("R012", plot, gardener, shortRange, 
                                                Arrays.asList(new Crop("Tomato", 60)));
        test("validateGrowingPeriod() - too short", !shortRes.validateGrowingPeriod());
        
        // isCurrentlyActive() and isPeriodEnded()
        DateRange activeRange = new DateRange(today.minusDays(5), today.plusDays(5));
        Reservation activeRes = new Reservation("R013", plot, gardener, activeRange);
        activeRes.confirm();
        test("isCurrentlyActive()", activeRes.isCurrentlyActive());
        
        DateRange pastRange = new DateRange(today.minusDays(30), today.minusDays(10));
        Reservation pastRes = new Reservation("R014", plot, gardener, pastRange);
        test("isPeriodEnded()", pastRes.isPeriodEnded());
        
        // equals() and hashCode()
        Reservation sameId = new Reservation("R001", plot, gardener, range);
        Reservation differentId = new Reservation("R999", plot, gardener, range);
        test("equals() - same ID", fullRes.equals(sameId));
        test("equals() - different ID", !fullRes.equals(differentId));
        test("hashCode() - same for equal objects", fullRes.hashCode() == sameId.hashCode());
        
        // toString()
        test("toString() - returns string", fullRes.toString() != null);
        
        System.out.println();
    }
    
    // ==================== GARDENSYSTEM TESTS ====================
    
    private static void testGardenSystem() {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.println("Testing GardenSystem class");
        System.out.println("─────────────────────────────────────────────────────────────");
        
        GardenSystem system = new GardenSystem();
        test("Constructor", system != null);
        
        // Initial state
        test("getPlots() - initially empty", system.getPlots().isEmpty());
        test("getGardeners() - initially empty", system.getGardeners().isEmpty());
        test("getReservations() - initially empty", system.getReservations().isEmpty());
        
        // Plot management
        GardenPlot plot1 = new GardenPlot("P001", "Sunny Garden", 25, "North");
        GardenPlot plot2 = new GardenPlot("P002", "Shady Spot", 20, "South");
        
        test("addPlot() - first plot", system.addPlot(plot1));
        test("addPlot() - second plot", system.addPlot(plot2));
        test("addPlot() - duplicate returns false", !system.addPlot(plot1));
        test("addPlot() - null returns false", !system.addPlot(null));
        test("getPlots() - has 2 plots", system.getPlots().size() == 2);
        
        GardenPlot foundPlot = system.findPlotById("P001");
        test("findPlotById() - found", foundPlot != null);
        test("findPlotById() - correct plot", foundPlot.equals(plot1));
        test("findPlotById() - not found", system.findPlotById("P999") == null);
        test("findPlotById() - null", system.findPlotById(null) == null);
        
        // Gardener management
        Gardener gardener1 = new Gardener("G001", "Alice", "alice@email.com", "555-1234");
        Gardener gardener2 = new Gardener("G002", "Bob", "bob@email.com", "555-5678");
        
        test("registerGardener() - first gardener", system.registerGardener(gardener1));
        test("registerGardener() - second gardener", system.registerGardener(gardener2));
        test("registerGardener() - duplicate returns false", !system.registerGardener(gardener1));
        test("registerGardener() - null returns false", !system.registerGardener(null));
        test("getGardeners() - has 2 gardeners", system.getGardeners().size() == 2);
        
        Gardener foundGardener = system.findGardenerById("G001");
        test("findGardenerById() - found", foundGardener != null);
        test("findGardenerById() - correct gardener", foundGardener.equals(gardener1));
        test("findGardenerById() - not found", system.findGardenerById("G999") == null);
        
        // Availability queries
        LocalDate today = LocalDate.now();
        DateRange range = new DateRange(today, today.plusDays(30));
        
        List<GardenPlot> available = system.findAvailablePlots(range);
        test("findAvailablePlots() - all available initially", available.size() == 2);
        
        Crop tomato = new Crop("Tomato", 60);
        plot1.addAllowedCrop("Tomato");
        List<GardenPlot> availableForCrop = system.findAvailablePlots(range, tomato);
        test("findAvailablePlots(range, crop) - with restrictions", availableForCrop.size() == 2);
        
        test("isPlotAvailable() - available", system.isPlotAvailable("P001", range));
        
        // Reservation creation
        List<Crop> plantingPlan = Arrays.asList(tomato);
        Reservation res1 = system.createReservation("P001", "G001", range, plantingPlan);
        test("createReservation() - success", res1 != null);
        test("createReservation() - reservation added", system.getReservations().size() == 1);
        test("createReservation() - initial status REQUESTED", 
             res1.getStatus() == ReservationStatus.REQUESTED);
        
        // Simplified reservation creation
        DateRange range2 = new DateRange(today.plusDays(60), today.plusDays(90));
        Reservation res2 = system.createReservation("P002", "G002", range2);
        test("createReservation() - without planting plan", res2 != null);
        
        // Invalid reservation creation
        Reservation invalid1 = system.createReservation("P999", "G001", range, null);
        test("createReservation() - invalid plot returns null", invalid1 == null);
        
        Reservation invalid2 = system.createReservation("P001", "G999", range, null);
        test("createReservation() - invalid gardener returns null", invalid2 == null);
        
        // Find reservation
        Reservation foundRes = system.findReservationById(res1.getReservationID());
        test("findReservationById() - found", foundRes != null);
        test("findReservationById() - not found", system.findReservationById("R9999") == null);
        
        // Confirm reservation
        test("confirmReservation() - success", system.confirmReservation(res1.getReservationID()));
        test("confirmReservation() - status changed", res1.getStatus() == ReservationStatus.CONFIRMED);
        
        // After confirming, plot is no longer available for overlapping dates
        test("isPlotAvailable() - not available after confirmation", 
             !system.isPlotAvailable("P001", range));
        
        // bookPlot() - create and confirm in one step
        DateRange range3 = new DateRange(today.plusDays(100), today.plusDays(130));
        Reservation bookedRes = system.bookPlot("P001", "G001", range3, null);
        test("bookPlot() - creates and confirms", bookedRes != null);
        test("bookPlot() - status is CONFIRMED", bookedRes.getStatus() == ReservationStatus.CONFIRMED);
        
        // Cancel reservation
        test("cancelReservation() - success", system.cancelReservation(res2.getReservationID()));
        test("cancelReservation() - status changed", res2.getStatus() == ReservationStatus.CANCELLED);
        
        // Complete reservation
        test("completeReservation() - success", system.completeReservation(res1.getReservationID()));
        test("completeReservation() - status changed", res1.getStatus() == ReservationStatus.COMPLETED);
        
        // Active reservations
        List<Reservation> activeRes = system.getActiveReservations();
        test("getActiveReservations() - only active returned", activeRes.size() == 1); // bookedRes
        
        // Reservations for gardener
        List<Reservation> gardener1Res = system.getReservationsForGardener("G001");
        test("getReservationsForGardener()", gardener1Res.size() == 2); // res1 and bookedRes
        
        // Reservations for plot
        List<Reservation> plot1Res = system.getReservationsForPlot("P001");
        test("getReservationsForPlot()", plot1Res.size() == 2); // res1 and bookedRes
        
        // Remove gardener with active reservations should fail
        test("removeGardener() - with active reservations fails", !system.removeGardener("G001"));
        
        // Remove gardener without active reservations
        test("removeGardener() - without active reservations", system.removeGardener("G002"));
        
        // Remove plot with active reservations should fail
        test("removePlot() - with active reservations fails", !system.removePlot("P001"));
        
        // Complete the remaining reservation to test plot removal
        system.completeReservation(bookedRes.getReservationID());
        
        // Now can remove gardener
        test("removeGardener() - after all reservations completed", system.removeGardener("G001"));
        
        // Reports
        String plantingReport = system.generatePlantingReport();
        test("generatePlantingReport() - returns string", plantingReport != null && !plantingReport.isEmpty());
        
        String availabilityReport = system.generateAvailabilityReport();
        test("generateAvailabilityReport() - returns string", 
             availabilityReport != null && !availabilityReport.isEmpty());
        
        // toString()
        test("toString() - returns string", system.toString() != null);
        
        System.out.println();
    }
    
    
    private static void test(String testName, boolean condition) {
        if (condition) {
            System.out.println(" PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            failed++;
        }
    }
}
