import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

// Interactive GardenMate System - Gardeners can make their own reservations
public class Main {
    private static GardenSystem system;
    private static Gardener currentGardener;
    private static Scanner scanner;
    private static List<Crop> availableCrops;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        initializeSystem();
        
        printWelcome();
        
        boolean running = true;
        while (running) {
            if (currentGardener == null) {
                running = showLoginMenu();
            } else {
                running = showMainMenu();
            }
        }
        
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     Thank you for using GardenMate! Happy Gardening!  ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        scanner.close();
    }

    private static void initializeSystem() {
        system = new GardenSystem();
        
        // Set up garden plots
        GardenPlot plot1 = new GardenPlot("P001", "Sunny Corner", 25.0, "North Section");
        GardenPlot plot2 = new GardenPlot("P002", "Shady Grove", 30.0, "East Section");
        GardenPlot plot3 = new GardenPlot("P003", "Herb Garden", 15.0, "South Section");
        GardenPlot plot4 = new GardenPlot("P004", "Vegetable Patch", 40.0, "West Section");
        GardenPlot plot5 = new GardenPlot("P005", "Flower Bed", 20.0, "Central Area");
        
        // Herb Garden only allows herbs
        plot3.addAllowedCrop("basil");
        plot3.addAllowedCrop("mint");
        plot3.addAllowedCrop("rosemary");
        plot3.addAllowedCrop("thyme");
        plot3.addAllowedCrop("oregano");
        plot3.addAllowedCrop("parsley");
        
        system.addPlot(plot1);
        system.addPlot(plot2);
        system.addPlot(plot3);
        system.addPlot(plot4);
        system.addPlot(plot5);
        
        // Set up available crops
        availableCrops = new ArrayList<>();
        availableCrops.add(new Crop("Tomatoes", 90, 
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.SUMMER)),
            "Requires full sun and regular watering"));
        availableCrops.add(new Crop("Lettuce", 45,
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.FALL)),
            "Cool weather crop"));
        availableCrops.add(new Crop("Basil", 60,
            new HashSet<>(Arrays.asList(Crop.Season.SUMMER)),
            "Aromatic herb"));
        availableCrops.add(new Crop("Carrots", 70,
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.FALL)),
            "Root vegetable"));
        availableCrops.add(new Crop("Peppers", 80,
            new HashSet<>(Arrays.asList(Crop.Season.SUMMER)),
            "Needs warm weather"));
        availableCrops.add(new Crop("Mint", 50,
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.SUMMER)),
            "Fast-growing herb"));
        availableCrops.add(new Crop("Rosemary", 90,
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.SUMMER, Crop.Season.FALL)),
            "Perennial herb"));
        availableCrops.add(new Crop("Cucumbers", 60,
            new HashSet<>(Arrays.asList(Crop.Season.SUMMER)),
            "Needs consistent watering"));
        availableCrops.add(new Crop("Spinach", 40,
            new HashSet<>(Arrays.asList(Crop.Season.SPRING, Crop.Season.FALL)),
            "Cool weather leafy green"));
        availableCrops.add(new Crop("Zucchini", 50,
            new HashSet<>(Arrays.asList(Crop.Season.SUMMER)),
            "Prolific producer"));
    }

    private static void printWelcome() {
        System.out.println();
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     GARDENMATE - Community Garden Reservation System  ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static boolean showLoginMenu() {
        System.out.println("┌───────────────────────────────────────────────────────┐");
        System.out.println("│                    WELCOME MENU                       │");
        System.out.println("├───────────────────────────────────────────────────────┤");
        System.out.println("│  1. Register as New Gardener                          │");
        System.out.println("│  2. Login (Existing Gardener)                         │");
        System.out.println("│  3. View All Plots                                    │");
        System.out.println("│  4. Exit                                              │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                registerNewGardener();
                break;
            case "2":
                loginGardener();
                break;
            case "3":
                viewAllPlots();
                break;
            case "4":
                return false;
            default:
                System.out.println("\n⚠ Invalid choice. Please try again.\n");
        }
        return true;
    }

    private static boolean showMainMenu() {
        System.out.println("\n┌───────────────────────────────────────────────────────┐");
        System.out.println("│                     MAIN MENU                         │");
        System.out.println("│         Welcome, " + padRight(currentGardener.getName(), 20) + "           │");
        System.out.println("├───────────────────────────────────────────────────────┤");
        System.out.println("│  1. View Available Plots                              │");
        System.out.println("│  2. Make a Reservation                                │");
        System.out.println("│  3. View My Reservations                              │");
        System.out.println("│  4. Cancel a Reservation                              │");
        System.out.println("│  5. View Available Crops                              │");
        System.out.println("│  6. View All Plots                                    │");
        System.out.println("│  7. View My Profile                                   │");
        System.out.println("│  8. Update My Profile                                 │");
        System.out.println("│  9. Logout                                            │");
        System.out.println("│  0. Exit                                              │");
        System.out.println("└───────────────────────────────────────────────────────┘");
        System.out.print("Enter your choice: ");
        
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewAvailablePlots();
                break;
            case "2":
                makeReservation();
                break;
            case "3":
                viewMyReservations();
                break;
            case "4":
                cancelReservation();
                break;
            case "5":
                viewAvailableCrops();
                break;
            case "6":
                viewAllPlots();
                break;
            case "7":
                viewMyProfile();
                break;
            case "8":
                updateMyProfile();
                break;
            case "9":
                logout();
                break;
            case "0":
                return false;
            default:
                System.out.println("\n⚠ Invalid choice. Please try again.");
        }
        return true;
    }

    private static void registerNewGardener() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         REGISTER NEW GARDENER          ");
        System.out.println("═══════════════════════════════════════\n");
        
        System.out.print("Enter your full name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("\n⚠ Name cannot be empty. Registration cancelled.");
            return;
        }
        
        System.out.print("Enter your email (optional, press Enter to skip): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = null;
        
        System.out.print("Enter your phone number (optional, press Enter to skip): ");
        String phone = scanner.nextLine().trim();
        if (phone.isEmpty()) phone = null;
        
        // Generate unique ID
        String gardenerId = "G" + String.format("%03d", system.getGardeners().size() + 1);
        
        Gardener newGardener = new Gardener(gardenerId, name, email, phone);
        
        if (system.registerGardener(newGardener)) {
            currentGardener = newGardener;
            System.out.println("\n✓ Registration successful!");
            System.out.println("  Your Gardener ID: " + gardenerId);
            System.out.println("  You are now logged in as: " + name);
        } else {
            System.out.println("\n✗ Registration failed. Please try again.");
        }
    }

    private static void loginGardener() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("              LOGIN                      ");
        System.out.println("═══════════════════════════════════════\n");
        
        if (system.getGardeners().isEmpty()) {
            System.out.println("No gardeners registered yet. Please register first.");
            return;
        }
        
        System.out.println("Registered Gardeners:");
        for (Gardener g : system.getGardeners()) {
            System.out.println("  " + g.getGardenerID() + " - " + g.getName());
        }
        
        System.out.print("\nEnter your Gardener ID: ");
        String gardenerId = scanner.nextLine().trim().toUpperCase();
        
        Gardener gardener = system.findGardenerById(gardenerId);
        if (gardener != null) {
            currentGardener = gardener;
            System.out.println("\n✓ Welcome back, " + gardener.getName() + "!");
        } else {
            System.out.println("\n✗ Gardener not found. Please check your ID or register.");
        }
    }

    private static void logout() {
        System.out.println("\n✓ Logged out successfully. Goodbye, " + currentGardener.getName() + "!");
        currentGardener = null;
    }

    private static void viewAllPlots() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         ALL GARDEN PLOTS               ");
        System.out.println("═══════════════════════════════════════\n");
        
        for (GardenPlot plot : system.getPlots()) {
            System.out.println("┌─────────────────────────────────────┐");
            System.out.println("│ " + padRight(plot.getName() + " (" + plot.getPlotID() + ")", 35) + " │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ Size: " + padRight(plot.getSizeSqMeters() + " m²", 29) + " │");
            System.out.println("│ Location: " + padRight(plot.getLocation() != null ? plot.getLocation() : "N/A", 25) + " │");
            System.out.println("│ Status: " + padRight(plot.isCurrentlyOccupied() ? "OCCUPIED" : "AVAILABLE", 27) + " │");
            if (!plot.getAllowedCrops().isEmpty()) {
                System.out.println("│ Restricted to: " + padRight(plot.getAllowedCrops().toString(), 19) + " │");
            }
            System.out.println("└─────────────────────────────────────┘");
        }
    }

    private static void viewAvailablePlots() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("      CHECK PLOT AVAILABILITY           ");
        System.out.println("═══════════════════════════════════════\n");
        
        DateRange dateRange = getDateRangeFromUser();
        if (dateRange == null) return;
        
        List<GardenPlot> available = system.findAvailablePlots(dateRange);
        
        if (available.isEmpty()) {
            System.out.println("\n⚠ No plots available for the selected period.");
        } else {
            System.out.println("\n✓ Available plots for " + dateRange + ":\n");
            for (GardenPlot plot : available) {
                System.out.println("  • " + plot.getName() + " (" + plot.getPlotID() + ")");
                System.out.println("    Size: " + plot.getSizeSqMeters() + " m² | Location: " + 
                    (plot.getLocation() != null ? plot.getLocation() : "N/A"));
                if (!plot.getAllowedCrops().isEmpty()) {
                    System.out.println("    Restricted to: " + plot.getAllowedCrops());
                }
                System.out.println();
            }
        }
    }

    private static void makeReservation() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         MAKE A RESERVATION             ");
        System.out.println("═══════════════════════════════════════\n");
        
        // Check if gardener has reached max reservations
        if (currentGardener.getActiveReservationCount() >= 3) {
            System.out.println("⚠ You have reached the maximum number of active reservations (3).");
            System.out.println("  Please cancel or complete an existing reservation first.");
            return;
        }
        
        // Get date range
        System.out.println("Step 1: Select your reservation period");
        DateRange dateRange = getDateRangeFromUser();
        if (dateRange == null) return;
        
        // Show available plots
        List<GardenPlot> available = system.findAvailablePlots(dateRange);
        if (available.isEmpty()) {
            System.out.println("\n⚠ No plots available for the selected period.");
            return;
        }
        
        System.out.println("\nStep 2: Select a plot");
        System.out.println("Available plots for " + dateRange + ":");
        for (int i = 0; i < available.size(); i++) {
            GardenPlot plot = available.get(i);
            System.out.println("  " + (i + 1) + ". " + plot.getName() + " (" + plot.getPlotID() + ")");
            System.out.println("     Size: " + plot.getSizeSqMeters() + " m² | Location: " + 
                (plot.getLocation() != null ? plot.getLocation() : "N/A"));
            if (!plot.getAllowedCrops().isEmpty()) {
                System.out.println("     Restricted to: " + plot.getAllowedCrops());
            }
        }
        
        System.out.print("\nEnter plot number (or 0 to cancel): ");
        int plotChoice;
        try {
            plotChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("\n⚠ Invalid input. Reservation cancelled.");
            return;
        }
        
        if (plotChoice == 0) {
            System.out.println("Reservation cancelled.");
            return;
        }
        
        if (plotChoice < 1 || plotChoice > available.size()) {
            System.out.println("\n⚠ Invalid plot number. Reservation cancelled.");
            return;
        }
        
        GardenPlot selectedPlot = available.get(plotChoice - 1);
        
        // Select crops
        System.out.println("\nStep 3: Select crops to plant (optional)");
        List<Crop> selectedCrops = selectCrops(selectedPlot);
        
        // Confirm reservation
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         CONFIRM RESERVATION            ");
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Plot: " + selectedPlot.getName());
        System.out.println("  Period: " + dateRange);
        System.out.println("  Duration: " + dateRange.lengthInDays() + " days");
        System.out.print("  Crops: ");
        if (selectedCrops.isEmpty()) {
            System.out.println("None specified");
        } else {
            for (int i = 0; i < selectedCrops.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(selectedCrops.get(i).getName());
            }
            System.out.println();
        }
        System.out.println("═══════════════════════════════════════");
        
        System.out.print("\nConfirm reservation? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("yes") || confirm.equals("y")) {
            Reservation reservation = system.createReservation(
                selectedPlot.getPlotID(),
                currentGardener.getGardenerID(),
                dateRange,
                selectedCrops.isEmpty() ? null : selectedCrops
            );
            
            if (reservation != null) {
                // Auto-confirm the reservation
                system.confirmReservation(reservation.getReservationID());
                System.out.println("\n✓ Reservation created and confirmed successfully!");
                System.out.println("  Reservation ID: " + reservation.getReservationID());
                System.out.println("  Status: " + reservation.getStatus());
            } else {
                System.out.println("\n✗ Failed to create reservation. Please try again.");
            }
        } else {
            System.out.println("Reservation cancelled.");
        }
    }

    private static List<Crop> selectCrops(GardenPlot plot) {
        List<Crop> selectedCrops = new ArrayList<>();
        
        // Filter crops based on plot restrictions
        List<Crop> allowedCrops = new ArrayList<>();
        for (Crop crop : availableCrops) {
            if (plot.isCropAllowed(crop)) {
                allowedCrops.add(crop);
            }
        }
        
        if (allowedCrops.isEmpty()) {
            System.out.println("No crops available for this plot.");
            return selectedCrops;
        }
        
        System.out.println("Available crops for this plot:");
        for (int i = 0; i < allowedCrops.size(); i++) {
            Crop crop = allowedCrops.get(i);
            System.out.println("  " + (i + 1) + ". " + crop.getName() + 
                " (min " + crop.getMinGrowingDays() + " days)");
        }
        System.out.println("  0. Skip crop selection");
        
        System.out.println("\nEnter crop numbers separated by commas (e.g., 1,3,5) or 0 to skip:");
        System.out.print("Your selection: ");
        String input = scanner.nextLine().trim();
        
        if (input.equals("0") || input.isEmpty()) {
            return selectedCrops;
        }
        
        String[] choices = input.split(",");
        for (String choice : choices) {
            try {
                int cropNum = Integer.parseInt(choice.trim());
                if (cropNum >= 1 && cropNum <= allowedCrops.size()) {
                    Crop crop = allowedCrops.get(cropNum - 1);
                    if (!selectedCrops.contains(crop)) {
                        selectedCrops.add(crop);
                    }
                }
            } catch (NumberFormatException e) {
                // Skip invalid input
            }
        }
        
        return selectedCrops;
    }

    private static void viewMyReservations() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         MY RESERVATIONS                ");
        System.out.println("═══════════════════════════════════════\n");
        
        List<Reservation> reservations = system.getReservationsForGardener(currentGardener.getGardenerID());
        
        if (reservations.isEmpty()) {
            System.out.println("You have no reservations.");
            return;
        }
        
        System.out.println("Active Reservations:");
        System.out.println("─────────────────────────────────────────");
        boolean hasActive = false;
        for (Reservation res : reservations) {
            if (res.isActive()) {
                hasActive = true;
                printReservationDetails(res);
            }
        }
        if (!hasActive) {
            System.out.println("  No active reservations.\n");
        }
        
        System.out.println("Past Reservations:");
        System.out.println("─────────────────────────────────────────");
        boolean hasPast = false;
        for (Reservation res : reservations) {
            if (!res.isActive()) {
                hasPast = true;
                printReservationDetails(res);
            }
        }
        if (!hasPast) {
            System.out.println("  No past reservations.\n");
        }
    }

    private static void printReservationDetails(Reservation res) {
        System.out.println("  ID: " + res.getReservationID());
        System.out.println("  Plot: " + res.getPlot().getName());
        System.out.println("  Period: " + res.getDateRange());
        System.out.println("  Status: " + res.getStatus() + " - " + res.getStatus().getDescription());
        List<Crop> crops = res.getPlantingPlan();
        System.out.print("  Crops: ");
        if (crops.isEmpty()) {
            System.out.println("None specified");
        } else {
            for (int i = 0; i < crops.size(); i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(crops.get(i).getName());
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void cancelReservation() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        CANCEL A RESERVATION            ");
        System.out.println("═══════════════════════════════════════\n");
        
        List<Reservation> activeReservations = currentGardener.getActiveReservations();
        
        if (activeReservations.isEmpty()) {
            System.out.println("You have no active reservations to cancel.");
            return;
        }
        
        System.out.println("Your active reservations:");
        for (int i = 0; i < activeReservations.size(); i++) {
            Reservation res = activeReservations.get(i);
            System.out.println("  " + (i + 1) + ". " + res.getReservationID() + " - " + 
                res.getPlot().getName() + " (" + res.getDateRange() + ")");
        }
        
        System.out.print("\nEnter number to cancel (or 0 to go back): ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("\n⚠ Invalid input.");
            return;
        }
        
        if (choice == 0) return;
        
        if (choice < 1 || choice > activeReservations.size()) {
            System.out.println("\n⚠ Invalid selection.");
            return;
        }
        
        Reservation toCancel = activeReservations.get(choice - 1);
        
        System.out.print("Are you sure you want to cancel reservation " + 
            toCancel.getReservationID() + "? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("yes") || confirm.equals("y")) {
            if (system.cancelReservation(toCancel.getReservationID())) {
                System.out.println("\n✓ Reservation cancelled successfully.");
            } else {
                System.out.println("\n✗ Failed to cancel reservation.");
            }
        } else {
            System.out.println("Cancellation aborted.");
        }
    }

    private static void viewAvailableCrops() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         AVAILABLE CROPS                ");
        System.out.println("═══════════════════════════════════════\n");
        
        for (Crop crop : availableCrops) {
            System.out.println("┌─────────────────────────────────────┐");
            System.out.println("│ " + padRight(crop.getName(), 35) + " │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ Min Growing Days: " + padRight(String.valueOf(crop.getMinGrowingDays()), 17) + " │");
            System.out.println("│ Best Seasons: " + padRight(crop.getBestSeasons().toString(), 21) + " │");
            if (!crop.getDescription().isEmpty()) {
                System.out.println("│ " + padRight(crop.getDescription(), 35) + " │");
            }
            System.out.println("└─────────────────────────────────────┘");
        }
    }

    private static void viewMyProfile() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("           MY PROFILE                   ");
        System.out.println("═══════════════════════════════════════\n");
        
        System.out.println(currentGardener.toDetailedString());
        System.out.println("\nReservation Summary:");
        System.out.println("  Total Reservations: " + currentGardener.getReservations().size());
        System.out.println("  Active Reservations: " + currentGardener.getActiveReservationCount());
        System.out.println("  Remaining Slots: " + (3 - currentGardener.getActiveReservationCount()));
    }

    private static void updateMyProfile() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("         UPDATE PROFILE                 ");
        System.out.println("═══════════════════════════════════════\n");
        
        System.out.println("Current Information:");
        System.out.println("  Name: " + currentGardener.getName());
        System.out.println("  Email: " + (currentGardener.getEmail() != null ? currentGardener.getEmail() : "Not set"));
        System.out.println("  Phone: " + (currentGardener.getPhoneNumber() != null ? currentGardener.getPhoneNumber() : "Not set"));
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("  1. Name");
        System.out.println("  2. Email");
        System.out.println("  3. Phone Number");
        System.out.println("  0. Cancel");
        
        System.out.print("\nEnter your choice: ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine().trim();
                if (!newName.isEmpty()) {
                    currentGardener.setName(newName);
                    System.out.println("✓ Name updated successfully.");
                }
                break;
            case "2":
                System.out.print("Enter new email: ");
                String newEmail = scanner.nextLine().trim();
                currentGardener.setEmail(newEmail.isEmpty() ? null : newEmail);
                System.out.println("✓ Email updated successfully.");
                break;
            case "3":
                System.out.print("Enter new phone number: ");
                String newPhone = scanner.nextLine().trim();
                currentGardener.setPhoneNumber(newPhone.isEmpty() ? null : newPhone);
                System.out.println("✓ Phone number updated successfully.");
                break;
            case "0":
                break;
            default:
                System.out.println("⚠ Invalid choice.");
        }
    }

    private static DateRange getDateRangeFromUser() {
        System.out.println("Enter dates in format YYYY-MM-DD (e.g., 2025-06-01)");
        
        System.out.print("Start date: ");
        String startStr = scanner.nextLine().trim();
        
        System.out.print("End date: ");
        String endStr = scanner.nextLine().trim();
        
        try {
            LocalDate startDate = LocalDate.parse(startStr, DATE_FORMAT);
            LocalDate endDate = LocalDate.parse(endStr, DATE_FORMAT);
            
            if (startDate.isBefore(LocalDate.now())) {
                System.out.println("\n⚠ Start date cannot be in the past.");
                return null;
            }
            
            DateRange range = new DateRange(startDate, endDate);
            return range;
        } catch (DateTimeParseException e) {
            System.out.println("\n⚠ Invalid date format. Please use YYYY-MM-DD.");
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("\n⚠ " + e.getMessage());
            return null;
        }
    }

    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) return s.substring(0, n);
        return String.format("%-" + n + "s", s);
    }
}
