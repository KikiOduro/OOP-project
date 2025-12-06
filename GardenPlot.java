import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// GardenPlot - a plot that gardeners can reserve
public class GardenPlot {
    private final String plotID;
    private String name;
    private double sizeSqMeters;
    private String location;
    private final Set<String> allowedCrops;  // empty = all crops allowed
    private final List<Reservation> reservations;
    private Gardener currentGardener;

    // constructors

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

    public GardenPlot(String plotID, String name) {
        this(plotID, name, 0, null);
    }

    public GardenPlot(String plotID) {
        this(plotID, plotID, 0, null);
    }

    // getters

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

    public Set<String> getAllowedCrops() {
        return Collections.unmodifiableSet(allowedCrops);
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    // setters

    public void setName(String name) {
        this.name = name != null ? name.trim() : this.name;
    }

    public void setSizeSqMeters(double sizeSqMeters) {
        this.sizeSqMeters = Math.max(0, sizeSqMeters);
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // crop restrictions

    public void addAllowedCrop(String cropName) {
        if (cropName != null && !cropName.trim().isEmpty()) {
            allowedCrops.add(cropName.trim().toLowerCase());
        }
    }

    public void removeAllowedCrop(String cropName) {
        if (cropName != null) {
            allowedCrops.remove(cropName.trim().toLowerCase());
        }
    }

    public void clearCropRestrictions() {
        allowedCrops.clear();
    }

    // check if crop is allowed (empty list = everything allowed)
    public boolean isCropAllowed(Crop crop) {
        if (crop == null) return false;
        if (allowedCrops.isEmpty()) return true;
        return allowedCrops.contains(crop.getName().toLowerCase());
    }

    public boolean isCropAllowed(String cropName) {
        if (cropName == null) return false;
        if (allowedCrops.isEmpty()) return true;
        return allowedCrops.contains(cropName.toLowerCase());
    }

    // availability

    // only confirmed reservations block the plot
    public boolean isAvailable(DateRange dateRange) {
        if (dateRange == null) return false;
        
        for (Reservation res : reservations) {
            if (res.getStatus().occupiesPlot() && 
                res.getDateRange().overlaps(dateRange)) {
                return false;
            }
        }
        return true;
    }

    public boolean isCurrentlyOccupied() {
        for (Reservation res : reservations) {
            if (res.getStatus().occupiesPlot() && 
                res.getDateRange().isCurrentlyActive()) {
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getActiveReservations() {
        List<Reservation> active = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getStatus().isActive()) {
                active.add(res);
            }
        }
        return active;
    }

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

    // reservation management (package-private - used by GardenSystem)

    void addReservation(Reservation reservation) {
        if (reservation != null && !reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }

    void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    public void assign(Gardener gardener) {
        this.currentGardener = gardener;
    }

    public void release() {
        this.currentGardener = null;
    }

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
