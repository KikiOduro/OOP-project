/**
 * Enum representing the possible states of a reservation.
 * Supports state transitions with validation.
 */
public enum ReservationStatus {
    /**
     * Initial state when a reservation is first requested.
     */
    REQUESTED("Pending approval"),

    /**
     * Reservation has been confirmed/approved.
     */
    CONFIRMED("Active and confirmed"),

    /**
     * Reservation was cancelled by gardener or system.
     */
    CANCELLED("Cancelled"),

    /**
     * Reservation period has ended successfully.
     */
    COMPLETED("Successfully completed");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if a transition from this status to another is valid.
     * Valid transitions:
     * - REQUESTED -> CONFIRMED, CANCELLED
     * - CONFIRMED -> COMPLETED, CANCELLED
     * - CANCELLED -> (terminal state, no transitions)
     * - COMPLETED -> (terminal state, no transitions)
     * 
     * @param newStatus the target status
     * @return true if the transition is allowed
     */
    public boolean canTransitionTo(ReservationStatus newStatus) {
        if (newStatus == null || newStatus == this) {
            return false;
        }

        switch (this) {
            case REQUESTED:
                return newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED:
                return newStatus == COMPLETED || newStatus == CANCELLED;
            case CANCELLED:
            case COMPLETED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    /**
     * Checks if this status represents an active reservation.
     * @return true if the reservation is REQUESTED or CONFIRMED
     */
    public boolean isActive() {
        return this == REQUESTED || this == CONFIRMED;
    }

    /**
     * Checks if this status is a terminal state.
     * @return true if CANCELLED or COMPLETED
     */
    public boolean isTerminal() {
        return this == CANCELLED || this == COMPLETED;
    }

    /**
     * Checks if a plot is occupied under this status.
     * Only CONFIRMED reservations actually occupy a plot.
     * @return true if the status means the plot is in use
     */
    public boolean occupiesPlot() {
        return this == CONFIRMED;
    }
}
