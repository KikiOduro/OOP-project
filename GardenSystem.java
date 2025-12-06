import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// GardenSystem - main controller that ties everything together
public class GardenSystem {
    private final List<GardenPlot> plots;
    private final List<Gardener> gardeners;
    private final List<Reservation> reservations;
    private int reservationCounter;

    private static final int MAX_ACTIVE_RESERVATIONS_PER_GARDENER = 3;

    public GardenSystem() {
        this.plots = new ArrayList<>();
        this.gardeners = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.reservationCounter = 0;
    }

    // getters

    public List<GardenPlot> getPlots() {
        return Collections.unmodifiableList(plots);
    }

    public List<Gardener> getGardeners() {
        return Collections.unmodifiableList(gardeners);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    // plot management

    public boolean addPlot(GardenPlot plot) {
        if (plot == null) return false;
        
        if (findPlotById(plot.getPlotID()) != null) {
            return false; // Already exists
        }
        plots.add(plot);
        return true;
    }

    public boolean removePlot(String plotId) {
        GardenPlot plot = findPlotById(plotId);
        if (plot == null) return false;
        
        if (!plot.getActiveReservations().isEmpty()) {
            return false;
        }
        return plots.remove(plot);
    }

    public GardenPlot findPlotById(String plotId) {
        if (plotId == null) return null;
        for (GardenPlot plot : plots) {
            if (plot.getPlotID().equals(plotId)) {
                return plot;
            }
        }
        return null;
    }

    // gardener management

    public boolean registerGardener(Gardener gardener) {
        if (gardener == null) return false;
        
        if (findGardenerById(gardener.getGardenerID()) != null) {
            return false; // Already registered
        }
        gardeners.add(gardener);
        return true;
    }

    public boolean removeGardener(String gardenerId) {
        Gardener gardener = findGardenerById(gardenerId);
        if (gardener == null) return false;
        
        if (gardener.hasActiveReservations()) {
            return false;
        }
        return gardeners.remove(gardener);
    }

    public Gardener findGardenerById(String gardenerId) {
        if (gardenerId == null) return null;
        for (Gardener g : gardeners) {
            if (g.getGardenerID().equals(gardenerId)) {
                return g;
            }
        }
        return null;
    }

    // availability queries

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

    public boolean isPlotAvailable(String plotId, DateRange range) {
        GardenPlot plot = findPlotById(plotId);
        return plot != null && plot.isAvailable(range);
    }

    // reservation management

    public Reservation createReservation(String plotId,
                                         String gardenerId,
                                         DateRange range,
                                         List<Crop> plantingPlan) {
        if (plotId == null || gardenerId == null || range == null || !range.isValid()) {
            System.out.println("Error: Invalid reservation details.");
            return null;
        }

        GardenPlot plot = findPlotById(plotId);
        Gardener gardener = findGardenerById(gardenerId);

        if (plot == null) {
            System.out.println("Error: Plot not found - " + plotId);
            return null;
        }
        if (gardener == null) {
            System.out.println("Error: Gardener not found - " + gardenerId);
            return null;
        }

        if (gardener.getActiveReservationCount() >= MAX_ACTIVE_RESERVATIONS_PER_GARDENER) {
            System.out.println("Error: Gardener has reached maximum active reservations.");
            return null;
        }

        if (!plot.isAvailable(range)) {
            System.out.println("Error: Plot is not available for the requested dates.");
            return null;
        }

        if (plantingPlan != null) {
            for (Crop crop : plantingPlan) {
                if (!plot.isCropAllowed(crop)) {
                    System.out.println("Error: Crop '" + crop.getName() + "' is not allowed on this plot.");
                    return null;
                }
            }
        }

        String reservationId = generateReservationId();
        Reservation reservation = new Reservation(reservationId, plot, gardener, range, plantingPlan);

        reservations.add(reservation);
        plot.addReservation(reservation);
        gardener.addReservation(reservation);

        return reservation;
    }

    public Reservation createReservation(String plotId, String gardenerId, DateRange range) {
        return createReservation(plotId, gardenerId, range, null);
    }

    // create and confirm in one step
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

    public boolean confirmReservation(String reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Error: Reservation not found - " + reservationId);
            return false;
        }
        
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

    public boolean cancelReservation(String reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Error: Reservation not found - " + reservationId);
            return false;
        }
        try {
            reservation.cancel();
            reservation.getPlot().release();
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean completeReservation(String reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation == null) {
            System.out.println("Error: Reservation not found - " + reservationId);
            return false;
        }
        try {
            reservation.complete();
            reservation.getPlot().release();
            return true;
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public Reservation findReservationById(String reservationId) {
        if (reservationId == null) return null;
        for (Reservation res : reservations) {
            if (res.getReservationID().equals(reservationId)) {
                return res;
            }
        }
        return null;
    }

    public List<Reservation> getActiveReservations() {
        List<Reservation> active = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.isActive()) {
                active.add(res);
            }
        }
        return active;
    }

    public List<Reservation> getReservationsForGardener(String gardenerId) {
        List<Reservation> result = new ArrayList<>();
        Gardener gardener = findGardenerById(gardenerId);
        if (gardener != null) {
            result.addAll(gardener.getReservations());
        }
        return result;
    }

    public List<Reservation> getReservationsForPlot(String plotId) {
        List<Reservation> result = new ArrayList<>();
        GardenPlot plot = findPlotById(plotId);
        if (plot != null) {
            result.addAll(plot.getReservations());
        }
        return result;
    }

    // reports

    public String generatePlantingReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("       GARDENMATE PLANTING REPORT      \n");
        sb.append("═══════════════════════════════════════\n\n");

        if (reservations.isEmpty()) {
            sb.append("No reservations in the system.\n");
            return sb.toString();
        }

        sb.append("SUMMARY\n");
        sb.append("───────────────────────────────────────\n");
        sb.append("Total Plots: ").append(plots.size()).append("\n");
        sb.append("Total Gardeners: ").append(gardeners.size()).append("\n");
        sb.append("Total Reservations: ").append(reservations.size()).append("\n");
        sb.append("Active Reservations: ").append(getActiveReservations().size()).append("\n\n");

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

    // helpers

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
