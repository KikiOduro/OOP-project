import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Main facade/controller for the Garden Reservation System.
 * Coordinates all operations between Gardeners, GardenPlots, and Reservations.
 * Implements the Facade pattern to provide a simplified interface.
 */
public class GardenSystem {
    private final List<GardenPlot> plots;
    private final List<Gardener> gardeners;
    private final List<Reservation> reservations;
    private int reservationCounter;

    // Configuration constants
    private static final int MAX_ACTIVE_RESERVATIONS_PER_GARDENER = 3;

    //  CONSTRUCTOR 

    public GardenSystem() {
        this.plots = new ArrayList<>();
        this.gardeners = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.reservationCounter = 0;
    }

    //  GETTERS

    /**
     * Returns an unmodifiable view of all plots.
     */
    public List<GardenPlot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    /**
     * Returns an unmodifiable view of all gardeners.
     */
    public List<Gardener> getGardeners() {
        return Collections.unmodifiableList(gardeners);
    }

    /**
     * Returns an unmodifiable view of all reservations.
     */
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    //  PLOT MANAGEMENT 

    /**
     * Adds a new plot to the system.
     * @param plot the plot to add
     * @return true if added successfully, false if already exists
     */
    public boolean addPlot(GardenPlot plot) {
        if (plot == null) return false;
        
        if (findPlotById(plot.getPlotID()).isPresent()) {
            return false; // Already exists
        }
        plots.add(plot);
        return true;
    }

    /**
     * Removes a plot from the system.
     * @param plotId the ID of the plot to remove
     * @return true if removed, false if not found or has active reservations
     */
    public boolean removePlot(String plotId) {
        Optional<GardenPlot> plotOpt = findPlotById(plotId);
        if (!plotOpt.isPresent()) return false;
        
        GardenPlot plot = plotOpt.get();
        if (!plot.getActiveReservations().isEmpty()) {
            return false; // Can't remove plot with active reservations
        }
        return plots.remove(plot);
    }

    /**
     * Finds a plot by its ID.
     * @param plotId the plot ID
     * @return Optional containing the plot if found
     */
    public Optional<GardenPlot> findPlotById(String plotId) {
        if (plotId == null) return Optional.empty();
        for (GardenPlot plot : plots) {
            if (plot.getPlotID().equals(plotId)) {
                return Optional.of(plot);
            }
        }
        return Optional.empty();
    }

    // GARDENER MANAGEMENT 

    /**
     * Registers a new gardener in the system.
     * @param gardener the gardener to register
     * @return true if registered successfully, false if already exists
     */
    public boolean registerGardener(Gardener gardener) {
        if (gardener == null) return false;
        
        if (findGardenerById(gardener.getGardenerID()).isPresent()) {
            return false; // Already registered
        }
        gardeners.add(gardener);
        return true;
    }

    /**
     * Removes a gardener from the system.
     * @param gardenerId the ID of the gardener to remove
     * @return true if removed, false if not found or has active reservations
     */
    public boolean removeGardener(String gardenerId) {
        Optional<Gardener> gardenerOpt = findGardenerById(gardenerId);
        if (!gardenerOpt.isPresent()) return false;
        
        Gardener gardener = gardenerOpt.get();
        if (gardener.hasActiveReservations()) {
            return false; // Can't remove gardener with active reservations
        }
        return gardeners.remove(gardener);
    }

    /**
     * Finds a gardener by their ID.
     * @param gardenerId the gardener ID
     * @return Optional containing the gardener if found
     */
    public Optional<Gardener> findGardenerById(String gardenerId) {
        if (gardenerId == null) return Optional.empty();
        for (Gardener g : gardeners) {
            if (g.getGardenerID().equals(gardenerId)) {
                return Optional.of(g);
            }
        }
        return Optional.empty();
    }

    // AVAILABILITY QUERIES 

    /**
     * Finds all plots available during a given date range.
     * @param range the date range to check
     * @return list of available plots
     */
    public List<GardenPlot> findAvailablePlots(DateRange range) {
        List<GardenPlot> available = new ArrayList<>();
        if (range == null || !range.isValid()) return available;
        
        for (GardenPlot plot : plots) {
            if (plot.isAvailable(range)) {
                available.add(plot);
            }
        }
        return available;
    }

    /**
     * Finds all plots available for a specific crop during a date range.
     * @param range the date range to check
     * @param crop the crop to plant
     * @return list of available and suitable plots
     */
    public List<GardenPlot> findAvailablePlots(DateRange range, Crop crop) {
        List<GardenPlot> available = new ArrayList<>();
        if (range == null || !range.isValid()) return available;
        
        for (GardenPlot plot : plots) {
            if (plot.isAvailable(range) && 
                (crop == null || plot.isCropAllowed(crop))) {
                available.add(plot);
            }
        }
        return available;
    }

    /**
     * Checks if a specific plot is available during a date range.
     * @param plotId the plot ID
     * @param range the date range
     * @return true if available
     */
    public boolean isPlotAvailable(String plotId, DateRange range) {
        Optional<GardenPlot> plotOpt = findPlotById(plotId);
        return plotOpt.isPresent() && plotOpt.get().isAvailable(range);
    }

    // RESERVATION MANAGEMENT 

    /**
     * Creates a new reservation request.
     * @param plotId the ID of the plot to reserve
     * @param gardenerId the ID of the gardener
     * @param range the reservation period
     * @param plantingPlan optional list of crops
     * @return the created reservation, or null if creation failed
     */
    public Reservation createReservation(String plotId,
                                         String gardenerId,
                                         DateRange range,
                                         List<Crop> plantingPlan) {
        // Validate inputs
        if (plotId == null || gardenerId == null || range == null || !range.isValid()) {
            System.out.println("Error: Invalid reservation details.");
            return null;
        }

        // Find plot and gardener
        Optional<GardenPlot> plotOpt = findPlotById(plotId);
        Optional<Gardener> gardenerOpt = findGardenerById(gardenerId);

        if (!plotOpt.isPresent()) {
            System.out.println("Error: Plot not found - " + plotId);
            return null;
        }
        if (!gardenerOpt.isPresent()) {
            System.out.println("Error: Gardener not found - " + gardenerId);
            return null;
        }

        GardenPlot plot = plotOpt.get();
        Gardener gardener = gardenerOpt.get();

        // Check gardener's reservation limit
        if (gardener.getActiveReservationCount() >= MAX_ACTIVE_RESERVATIONS_PER_GARDENER) {
            System.out.println("Error: Gardener has reached maximum active reservations.");
            return null;
        }

        // Check plot availability
        if (!plot.isAvailable(range)) {
            System.out.println("Error: Plot is not available for the requested dates.");
            return null;
        }

        // Validate crops if provided
        if (plantingPlan != null) {
            for (Crop crop : plantingPlan) {
                if (!plot.isCropAllowed(crop)) {
                    System.out.println("Error: Crop '" + crop.getName() + "' is not allowed on this plot.");
                    return null;
                }
            }
        }

        // Create the reservation
        String reservationId = generateReservationId();
        Reservation reservation = new Reservation(reservationId, plot, gardener, range, plantingPlan);

        // Register the reservation
        reservations.add(reservation);
        plot.addReservation(reservation);
        gardener.addReservation(reservation);

        return reservation;
    }

    /**
     * Simplified reservation creation without planting plan.
     */
    public Reservation createReservation(String plotId, String gardenerId, DateRange range) {
        return createReservation(plotId, gardenerId, range, null);
    }

    /**
     * Shortcut: creates and immediately confirms a reservation.
     * @return the confirmed reservation, or null if failed
     */
    public Reservation bookPlot(String plotId,
                                String gardenerId,
                                DateRange range,
                                List<Crop> plantingPlan) {
        Reservation reservation = createReservation(plotId, gardenerId, range, plantingPlan);
        if (reservation != null) {
            confirmReservation(reservation.getReservationID());
        }
        return reservation;
    }

    /**
     * Confirms a pending reservation.
     * @param reservationId the reservation ID
     * @return true if confirmed successfully
     */
    public boolean confirmReservation(String reservationId) {
        Optional<Reservation> resOpt = findReservationById(reservationId);
        if (!resOpt.isPresent()) {
            System.out.println("Error: Reservation not found - " + reservationId);
            return false;
        }

        Reservation reservation = resOpt.get();
        
        // Re-check availability before confirming
        if (!reservation.getPlot().isAvailable(reservation.getDateRange())) {
            System.out.println("Error: Plot is no longer available for the requested dates.");
            return false;
        }

        try {
            reservation.confirm();
            reservation.getPlot().assign(reservation.getGardener());
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancels a reservation.
     * @param reservationId the reservation ID
     * @return true if cancelled successfully
     */
    public boolean cancelReservation(String reservationId) {
        Optional<Reservation> resOpt = findReservationById(reservationId);
        if (!resOpt.isPresent()) {
            System.out.println("Error: Reservation not found - " + reservationId);
            return false;
        }

        Reservation reservation = resOpt.get();
        try {
            reservation.cancel();
            reservation.getPlot().release();
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marks a reservation as completed.
     * @param reservationId the reservation ID
     * @return true if marked completed successfully
     */
    public boolean completeReservation(String reservationId) {
        Optional<Reservation> resOpt = findReservationById(reservationId);
        if (!resOpt.isPresent()) {
            System.out.println("Error: Reservation not found - " + reservationId);
            return false;
        }

        Reservation reservation = resOpt.get();
        try {
            reservation.complete();
            reservation.getPlot().release();
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds a reservation by its ID.
     * @param reservationId the reservation ID
     * @return Optional containing the reservation if found
     */
    public Optional<Reservation> findReservationById(String reservationId) {
        if (reservationId == null) return Optional.empty();
        for (Reservation res : reservations) {
            if (res.getReservationID().equals(reservationId)) {
                return Optional.of(res);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets all active reservations.
     * @return list of active reservations
     */
    public List<Reservation> getActiveReservations() {
        List<Reservation> active = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.isActive()) {
                active.add(res);
            }
        }
        return active;
    }

    /**
     * Gets all reservations for a specific gardener.
     * @param gardenerId the gardener ID
     * @return list of reservations
     */
    public List<Reservation> getReservationsForGardener(String gardenerId) {
        List<Reservation> result = new ArrayList<>();
        Optional<Gardener> gardenerOpt = findGardenerById(gardenerId);
        if (gardenerOpt.isPresent()) {
            result.addAll(gardenerOpt.get().getReservations());
        }
        return result;
    }

    /**
     * Gets all reservations for a specific plot.
     * @param plotId the plot ID
     * @return list of reservations
     */
    public List<Reservation> getReservationsForPlot(String plotId) {
        List<Reservation> result = new ArrayList<>();
        Optional<GardenPlot> plotOpt = findPlotById(plotId);
        if (plotOpt.isPresent()) {
            result.addAll(plotOpt.get().getReservations());
        }
        return result;
    }

    // REPORTS 

    /**
     * Generates a comprehensive planting report.
     * @return formatted report string
     */
    public String generatePlantingReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("       GARDENMATE PLANTING REPORT      \n");
        sb.append("═══════════════════════════════════════\n\n");

        if (reservations.isEmpty()) {
            sb.append("No reservations in the system.\n");
            return sb.toString();
        }

        // Summary
        sb.append("SUMMARY\n");
        sb.append("───────────────────────────────────────\n");
        sb.append("Total Plots: ").append(plots.size()).append("\n");
        sb.append("Total Gardeners: ").append(gardeners.size()).append("\n");
        sb.append("Total Reservations: ").append(reservations.size()).append("\n");
        sb.append("Active Reservations: ").append(getActiveReservations().size()).append("\n\n");

        // Active Reservations
        sb.append("ACTIVE RESERVATIONS\n");
        sb.append("───────────────────────────────────────\n");
        
        boolean hasActive = false;
        for (Reservation res : reservations) {
            if (res.isActive()) {
                hasActive = true;
                appendReservationDetails(sb, res);
            }
        }
        if (!hasActive) {
            sb.append("No active reservations.\n");
        }

        // Completed/Cancelled (last 5)
        sb.append("\nRECENT COMPLETED/CANCELLED\n");
        sb.append("───────────────────────────────────────\n");
        
        int count = 0;
        for (int i = reservations.size() - 1; i >= 0 && count < 5; i--) {
            Reservation res = reservations.get(i);
            if (!res.isActive()) {
                appendReservationDetails(sb, res);
                count++;
            }
        }
        if (count == 0) {
            sb.append("No completed or cancelled reservations.\n");
        }

        return sb.toString();
    }

    /**
     * Generates a plot availability report.
     * @return formatted report string
     */
    public String generateAvailabilityReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("      PLOT AVAILABILITY REPORT         \n");
        sb.append("═══════════════════════════════════════\n\n");

        if (plots.isEmpty()) {
            sb.append("No plots in the system.\n");
            return sb.toString();
        }

        for (GardenPlot plot : plots) {
            sb.append("Plot: ").append(plot.getName());
            sb.append(" (").append(plot.getPlotID()).append(")\n");
            sb.append("  Status: ");
            if (plot.isCurrentlyOccupied()) {
                sb.append("OCCUPIED");
                if (plot.getCurrentGardener() != null) {
                    sb.append(" by ").append(plot.getCurrentGardener().getName());
                }
            } else {
                sb.append("AVAILABLE");
            }
            sb.append("\n");
            
            List<Reservation> activeRes = plot.getActiveReservations();
            if (!activeRes.isEmpty()) {
                sb.append("  Upcoming/Current Reservations:\n");
                for (Reservation res : activeRes) {
                    sb.append("    - ").append(res.getDateRange());
                    sb.append(" (").append(res.getStatus()).append(")\n");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // ==================== HELPER METHODS ====================

    private String generateReservationId() {
        reservationCounter++;
        return "R" + String.format("%04d", reservationCounter);
    }

    private void appendReservationDetails(StringBuilder sb, Reservation res) {
        sb.append("\nReservation: ").append(res.getReservationID()).append("\n");
        sb.append("  Plot: ").append(res.getPlot().getName()).append("\n");
        sb.append("  Gardener: ").append(res.getGardener().getName()).append("\n");
        sb.append("  Period: ").append(res.getDateRange()).append("\n");
        sb.append("  Status: ").append(res.getStatus()).append("\n");
        
        List<Crop> crops = res.getPlantingPlan();
        sb.append("  Crops: ");
        if (crops.isEmpty()) {
            sb.append("None specified");
        } else {
            for (int i = 0; i < crops.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(crops.get(i).getName());
            }
        }
        sb.append("\n");
    }

    // SYSTEM INFO 

    @Override
    public String toString() {
        return "GardenSystem{" +
               "plots=" + plots.size() +
               ", gardeners=" + gardeners.size() +
               ", reservations=" + reservations.size() +
               ", activeReservations=" + getActiveReservations().size() +
               '}';
    }
}
