import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a gardener who can make reservations for garden plots.
 * Encapsulates gardener information and manages their reservations.
 */
public class Gardener {
    private final String gardenerID;
    private String name;
    private String email;
    private String phoneNumber;
    private final List<Reservation> reservations;

    // ==================== CONSTRUCTORS ====================

    /**
     * Full constructor with all details.
     * @param gardenerID unique identifier
     * @param name gardener's name
     * @param email contact email
     * @param phoneNumber contact phone
     */
    public Gardener(String gardenerID, String name, String email, String phoneNumber) {
        if (gardenerID == null || gardenerID.trim().isEmpty()) {
            throw new IllegalArgumentException("Gardener ID cannot be null or empty");
        }
        this.gardenerID = gardenerID.trim();
        this.name = name != null ? name.trim() : "Unknown";
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.reservations = new ArrayList<>();
    }

    /**
     * Simplified constructor with ID and name.
     * @param gardenerID unique identifier
     * @param name gardener's name
     */
    public Gardener(String gardenerID, String name) {
        this(gardenerID, name, null, null);
    }

    /**
     * Minimal constructor with just ID.
     * @param gardenerID unique identifier
     */
    public Gardener(String gardenerID) {
        this(gardenerID, "Unknown", null, null);
    }

    //GETTERS

    public String getGardenerID() {
        return gardenerID;
    }

    public String getName() {
        return name;
    }

    public String getGardenerName() {
        return name; // Alias for compatibility
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns an unmodifiable view of this gardener's reservations.
     */
    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    // ==================== SETTERS ====================

    public void setName(String name) {
        this.name = name != null ? name.trim() : this.name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // ==================== RESERVATION MANAGEMENT ====================

    /**
     * Adds a reservation to this gardener's list.
     * Package-private: should be called by GardenSystem when creating reservations.
     * @param reservation the reservation to add
     */
    void addReservation(Reservation reservation) {
        if (reservation != null && !reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }

    /**
     * Removes a reservation from this gardener's list.
     * @param reservation the reservation to remove
     */
    void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    /**
     * Gets all active (non-terminal) reservations.
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
     * Gets the count of active reservations.
     * @return number of active reservations
     */
    public int getActiveReservationCount() {
        return getActiveReservations().size();
    }

    /**
     * Checks if this gardener has any active reservations.
     * @return true if there are active reservations
     */
    public boolean hasActiveReservations() {
        return !getActiveReservations().isEmpty();
    }

    /**
     * Checks if this gardener has a reservation for a specific plot.
     * @param plotID the plot ID to check
     * @return true if an active reservation exists for the plot
     */
    public boolean hasReservationForPlot(String plotID) {
        for (Reservation res : reservations) {
            if (res.getStatus().isActive() && 
                res.getPlot().getPlotID().equals(plotID)) {
                return true;
            }
        }
        return false;
    }

    // OBJECT METHODS 

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gardener gardener = (Gardener) o;
        return Objects.equals(gardenerID, gardener.gardenerID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gardenerID);
    }

    @Override
    public String toString() {
        return "Gardener{" +
               "id='" + gardenerID + '\'' +
               ", name='" + name + '\'' +
               ", activeReservations=" + getActiveReservationCount() +
               '}';
    }

    /**
     * Returns a detailed string with contact info.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gardener: ").append(name).append(" (").append(gardenerID).append(")\n");
        if (email != null) {
            sb.append("  Email: ").append(email).append("\n");
        }
        if (phoneNumber != null) {
            sb.append("  Phone: ").append(phoneNumber).append("\n");
        }
        sb.append("  Active Reservations: ").append(getActiveReservationCount());
        return sb.toString();
    }
}
