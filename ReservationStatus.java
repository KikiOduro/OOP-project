// status states for a reservation
public enum ReservationStatus {
    REQUESTED("Pending approval"),
    CONFIRMED("Active and confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Successfully completed");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // check if we can go from current status to new status
    // REQUESTED -> CONFIRMED or CANCELLED
    // CONFIRMED -> COMPLETED or CANCELLED
    // CANCELLED and COMPLETED are final (can't change)
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

    // still in progress (not cancelled or done)
    public boolean isActive() {
        return this == REQUESTED || this == CONFIRMED;
    }

    // can't change anymore
    public boolean isTerminal() {
        return this == CANCELLED || this == COMPLETED;
    }

    // only confirmed reservations actually take up the plot
    public boolean occupiesPlot() {
        return this == CONFIRMED;
    }
}
