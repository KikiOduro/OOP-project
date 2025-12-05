import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a garden plot that can be reserved by gardeners.
 * Manages plot properties, allowed crops, and tracks reservations.
 */
public class GardenPlot {
    private final String plotID;
    private String name;
    private double sizeSqMeters;
    private String location;
    private final Set<String> allowedCrops; // Empty set means all crops allowed
    private final List<Reservation> reservations;
    private Gardener currentGardener; // Currently assigned gardener (for active reservation)

    //  CONSTRUCTORS
    /**
     * Full constructor with all properties.
     * @param plotID unique identifier
     * @param name display name
     * @param sizeSqMeters size in square meters
     * @param location location description
     */
    public GardenPlot(String plotID, String name, double sizeSqMeters, String location) {
        if (plotID == null || plotID.trim().isEmpty()) {
            throw new IllegalArgumentException("Plot ID cannot be null or empty");
        }
        this.plotID = plotID.trim();
        this.name = name != null ? name.trim() : plotID;
        this.sizeSqMeters = Math.max(0, sizeSqMeters);
        this.location = location;
        this.allowedCrops = new HashSet<>();
        this.reservations = new ArrayList<>();
        this.currentGardener = null;
    }

    /**
     * Constructor with ID and name.
     * @param plotID unique identifier
     * @param name display name
     */
    public GardenPlot(String plotID, String name) {
        this(plotID, name, 0, null);
    }

    /**
     * Minimal constructor with just ID.
     * @param plotID unique identifier
     */
    public GardenPlot(String plotID) {
        this(plotID, plotID, 0, null);
    }

    // ==================== GETTERS ====================

    public String getPlotID() {
        return plotID;
    }

    public String getName() {
        return name;
    }

    public double getSizeSqMeters() {
        return sizeSqMeters;
    }

    public String getLocation() {
        return location;
    }

    public Gardener getCurrentGardener() {
        return currentGardener;
    }

    /**
     * Returns an unmodifiable view of allowed crops.
     * Empty set means all crops are allowed.
     */
    public Set<String> getAllowedCrops() {
        return Collections.unmodifiableSet(allowedCrops);
    }

    /**
     * Returns an unmodifiable view of reservations for this plot.
     */
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    // ==================== SETTERS ====================

    public void setName(String name) {
        this.name = name != null ? name.trim() : this.name;
    }

    public void setSizeSqMeters(double sizeSqMeters) {
        this.sizeSqMeters = Math.max(0, sizeSqMeters);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // ==================== CROP RESTRICTIONS ====================

    /**
     * Adds a crop to the allowed crops list.
     * @param cropName name of the crop to allow
     */
    public void addAllowedCrop(String cropName) {
        if (cropName != null && !cropName.trim().isEmpty()) {
            allowedCrops.add(cropName.trim().toLowerCase());
        }
    }

    /**
     * Removes a crop from the allowed crops list.
     * @param cropName name of the crop to disallow
     */
    public void removeAllowedCrop(String cropName) {
        if (cropName != null) {
            allowedCrops.remove(cropName.trim().toLowerCase());
        }
    }

    /**
     * Clears all crop restrictions (allows all crops).
     */
    public void clearCropRestrictions() {
        allowedCrops.clear();
    }

    /**
     * Checks if a specific crop is allowed on this plot.
     * @param crop the crop to check
     * @return true if the crop is allowed (or no restrictions exist)
     */
    public boolean isCropAllowed(Crop crop) {
        if (crop == null) return false;
        if (allowedCrops.isEmpty()) return true; // No restrictions
        return allowedCrops.contains(crop.getName().toLowerCase());
    }

    /**
     * Checks if a crop by name is allowed on this plot.
     * @param cropName the crop name to check
     * @return true if the crop is allowed
     */
    public boolean isCropAllowed(String cropName) {
        if (cropName == null) return false;
        if (allowedCrops.isEmpty()) return true;
        return allowedCrops.contains(cropName.toLowerCase());
    }

    // ==================== AVAILABILITY METHODS ====================

    /**
     * Checks if this plot is available during a given date range.
     * Only considers CONFIRMED reservations as blocking.
     * @param dateRange the date range to check
     * @return true if the plot is available
     */
    public boolean isAvailable(DateRange dateRange) {
        if (dateRange == null) return false;
        
        for (Reservation res : reservations) {
            // Only confirmed reservations block the plot
            if (res.getStatus().occupiesPlot() && 
                res.getDateRange().overlaps(dateRange)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this plot is currently occupied.
     * @return true if there's an active reservation that includes today
     */
    public boolean isCurrentlyOccupied() {
        for (Reservation res : reservations) {
            if (res.getStatus().occupiesPlot() && 
                res.getDateRange().isCurrentlyActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all active reservations for this plot.
     * @return list of active reservations
     */
    public List<Reservation> getActiveReservations() {
        List<Reservation> active = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getStatus().isActive()) {
                active.add(res);
            }
        }
        return active;
    }

    /**
     * Gets confirmed reservations that overlap with a date range.
     * @param dateRange the date range to check
     * @return list of conflicting reservations
     */
    public List<Reservation> getConflictingReservations(DateRange dateRange) {
        List<Reservation> conflicts = new ArrayList<>();
        if (dateRange == null) return conflicts;
        
        for (Reservation res : reservations) {
            if (res.getStatus().occupiesPlot() && 
                res.getDateRange().overlaps(dateRange)) {
                conflicts.add(res);
            }
        }
        return conflicts;
    }

    // ==================== RESERVATION MANAGEMENT ====================

    /**
     * Adds a reservation to this plot.
     * Package-private: should be called by GardenSystem.
     * @param reservation the reservation to add
     */
    void addReservation(Reservation reservation) {
        if (reservation != null && !reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }

    /**
     * Removes a reservation from this plot.
     * @param reservation the reservation to remove
     */
    void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    /**
     * Assigns a gardener to this plot (for tracking current occupancy).
     * @param gardener the gardener to assign
     */
    public void assign(Gardener gardener) {
        this.currentGardener = gardener;
    }

    /**
     * Releases the current gardener assignment.
     */
    public void release() {
        this.currentGardener = null;
    }

    // ==================== OBJECT METHODS ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GardenPlot that = (GardenPlot) o;
        return Objects.equals(plotID, that.plotID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plotID);
    }

    @Override
    public String toString() {
        return "GardenPlot{" +
               "id='" + plotID + '\'' +
               ", name='" + name + '\'' +
               ", size=" + sizeSqMeters + "m²" +
               ", occupied=" + isCurrentlyOccupied() +
               '}';
    }

    /**
     * Returns a detailed string representation.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Plot: ").append(name).append(" (").append(plotID).append(")\n");
        sb.append("  Size: ").append(sizeSqMeters).append(" m²\n");
        if (location != null) {
            sb.append("  Location: ").append(location).append("\n");
        }
        sb.append("  Currently Occupied: ").append(isCurrentlyOccupied() ? "Yes" : "No").append("\n");
        if (!allowedCrops.isEmpty()) {
            sb.append("  Allowed Crops: ").append(allowedCrops).append("\n");
        }
        sb.append("  Active Reservations: ").append(getActiveReservations().size());
        return sb.toString();
    }
}
