import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Reservation - links a gardener to a plot for a time period
public class Reservation {
    private final String reservationID;
    private final GardenPlot plot;
    private final Gardener gardener;
    private final DateRange dateRange;
    private final List<Crop> plantingPlan;
    private ReservationStatus status;

    // constructors

    public Reservation(String reservationID,
                       GardenPlot plot,
                       Gardener gardener,
                       DateRange dateRange,
                       List<Crop> plantingPlan) {
        if (reservationID == null || reservationID.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be null or empty");
        }
        if (plot == null) {
            throw new IllegalArgumentException("Plot cannot be null");
        }
        if (gardener == null) {
            throw new IllegalArgumentException("Gardener cannot be null");
        }
        if (dateRange == null) {
            throw new IllegalArgumentException("Date range cannot be null");
        }

        this.reservationID = reservationID.trim();
        this.plot = plot;
        this.gardener = gardener;
        this.dateRange = dateRange;
        this.plantingPlan = plantingPlan != null ? 
            new ArrayList<>(plantingPlan) : new ArrayList<>();
        this.status = ReservationStatus.REQUESTED;
    }

    public Reservation(String reservationID,
                       GardenPlot plot,
                       Gardener gardener,
                       DateRange dateRange) {
        this(reservationID, plot, gardener, dateRange, null);
    }

    // getters

    public String getReservationID() {
        return reservationID;
    }

    public GardenPlot getPlot() {
        return plot;
    }

    public Gardener getGardener() {
        return gardener;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public List<Crop> getPlantingPlan() {
        return Collections.unmodifiableList(plantingPlan);
    }

    // planting plan management

    public void addCrop(Crop crop) {
        if (crop != null && !plantingPlan.contains(crop)) {
            plantingPlan.add(crop);
        }
    }

    public void removeCrop(Crop crop) {
        plantingPlan.remove(crop);
    }

    public void clearPlantingPlan() {
        plantingPlan.clear();
    }

    // status transitions

    public boolean transitionTo(ReservationStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "Cannot transition from " + status + " to " + newStatus);
        }
        this.status = newStatus;
        return true;
    }

    public boolean confirm() {
        return transitionTo(ReservationStatus.CONFIRMED);
    }

    public boolean cancel() {
        return transitionTo(ReservationStatus.CANCELLED);
    }

    public boolean complete() {
        return transitionTo(ReservationStatus.COMPLETED);
    }

    // status checks

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return status == ReservationStatus.COMPLETED;
    }

    public boolean isCurrentlyActive() {
        return isConfirmed() && dateRange.isCurrentlyActive();
    }

    public boolean isPeriodEnded() {
        return dateRange.isInPast();
    }

    // only confirmed reservations cause conflicts
    public boolean conflictsWith(DateRange otherRange) {
        return status.occupiesPlot() && dateRange.overlaps(otherRange);
    }

    // validation

    public boolean validateCrops() {
        for (Crop crop : plantingPlan) {
            if (!plot.isCropAllowed(crop)) {
                return false;
            }
        }
        return true;
    }

    public boolean validateGrowingPeriod() {
        for (Crop crop : plantingPlan) {
            if (!crop.canGrowIn(dateRange)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationID, that.reservationID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationID);
    }

    @Override
    public String toString() {
        String crops = plantingPlan.isEmpty() ? "None" : plantingPlan.size() + " crops";
        return reservationID + " | " + gardener.getName() + " | " + plot.getName() + 
               " | " + dateRange + " | " + status + " | " + crops;
    }
}
