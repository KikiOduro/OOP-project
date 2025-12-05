import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a reservation linking a Gardener to a GardenPlot for a specific DateRange.
 * Manages the reservation lifecycle through status transitions.
 */
public class Reservation {
    private final String reservationID;
    private final GardenPlot plot;
    private final Gardener gardener;
    private final DateRange dateRange;
    private final List<Crop> plantingPlan;
    private ReservationStatus status;

    // ==================== CONSTRUCTOR ====================

    /**
     * Creates a new reservation with REQUESTED status.
     * @param reservationID unique identifier
     * @param plot the garden plot being reserved
     * @param gardener the gardener making the reservation
     * @param dateRange the reservation period
     * @param plantingPlan list of crops to be planted
     */
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

    /**
     * Constructor without planting plan.
     */
    public Reservation(String reservationID,
                       GardenPlot plot,
                       Gardener gardener,
                       DateRange dateRange) {
        this(reservationID, plot, gardener, dateRange, null);
    }

    // ==================== GETTERS ====================

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

    /**
     * Returns an unmodifiable view of the planting plan.
     */
    public List<Crop> getPlantingPlan() {
        return Collections.unmodifiableList(plantingPlan);
    }

    // ==================== PLANTING PLAN MANAGEMENT ====================

    /**
     * Adds a crop to the planting plan.
     * @param crop the crop to add
     */
    public void addCrop(Crop crop) {
        if (crop != null && !plantingPlan.contains(crop)) {
            plantingPlan.add(crop);
        }
    }

    /**
     * Removes a crop from the planting plan.
     * @param crop the crop to remove
     */
    public void removeCrop(Crop crop) {
        plantingPlan.remove(crop);
    }

    /**
     * Clears the planting plan.
     */
    public void clearPlantingPlan() {
        plantingPlan.clear();
    }

    // ==================== STATUS TRANSITIONS ====================

    /**
     * Attempts to transition to a new status.
     * @param newStatus the target status
     * @return true if the transition was successful
     * @throws IllegalStateException if the transition is not allowed
     */
    public boolean transitionTo(ReservationStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "Cannot transition from " + status + " to " + newStatus);
        }
        this.status = newStatus;
        return true;
    }

    /**
     * Confirms the reservation (REQUESTED -> CONFIRMED).
     * @return true if successful
     * @throws IllegalStateException if transition not allowed
     */
    public boolean confirm() {
        return transitionTo(ReservationStatus.CONFIRMED);
    }

    /**
     * Cancels the reservation (REQUESTED/CONFIRMED -> CANCELLED).
     * @return true if successful
     * @throws IllegalStateException if transition not allowed
     */
    public boolean cancel() {
        return transitionTo(ReservationStatus.CANCELLED);
    }

    /**
     * Marks the reservation as completed (CONFIRMED -> COMPLETED).
     * @return true if successful
     * @throws IllegalStateException if transition not allowed
     */
    public boolean complete() {
        return transitionTo(ReservationStatus.COMPLETED);
    }

    // ==================== STATUS QUERIES ====================

    /**
     * Checks if this reservation is active (not terminal).
     * @return true if REQUESTED or CONFIRMED
     */
    public boolean isActive() {
        return status.isActive();
    }

    /**
     * Checks if this reservation is confirmed.
     * @return true if status is CONFIRMED
     */
    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }

    /**
     * Checks if this reservation is cancelled.
     * @return true if status is CANCELLED
     */
    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }

    /**
     * Checks if this reservation is completed.
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return status == ReservationStatus.COMPLETED;
    }

    /**
     * Checks if the reservation period is currently active.
     * @return true if today falls within the date range and status is CONFIRMED
     */
    public boolean isCurrentlyActive() {
        return isConfirmed() && dateRange.isCurrentlyActive();
    }

    /**
     * Checks if the reservation period has ended.
     * @return true if the end date is in the past
     */
    public boolean isPeriodEnded() {
        return dateRange.isInPast();
    }

    /**
     * Checks if this reservation conflicts with another date range.
     * Only confirmed reservations can cause conflicts.
     * @param otherRange the date range to check
     * @return true if there's a conflict
     */
    public boolean conflictsWith(DateRange otherRange) {
        return status.occupiesPlot() && dateRange.overlaps(otherRange);
    }

    // ==================== VALIDATION ====================

    /**
     * Checks if all crops in the planting plan are allowed on the plot.
     * @return true if all crops are allowed
     */
    public boolean validateCrops() {
        for (Crop crop : plantingPlan) {
            if (!plot.isCropAllowed(crop)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the date range is sufficient for all crops in the planting plan.
     * @return true if the date range is long enough for all crops
     */
    public boolean validateGrowingPeriod() {
        for (Crop crop : plantingPlan) {
            if (!crop.canGrowIn(dateRange)) {
                return false;
            }
        }
        return true;
    }

    // ==================== OBJECT METHODS ====================

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
