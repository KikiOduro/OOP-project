import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// Gardener - a user who can reserve garden plots
public class Gardener {
    private final String gardenerID;
    private String name;
    private String email;
    private String phoneNumber;
    private final List<Reservation> reservations;

    // constructors

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

    public Gardener(String gardenerID, String name) {
        this(gardenerID, name, null, null);
    }

    public Gardener(String gardenerID) {
        this(gardenerID, "Unknown", null, null);
    }

    // getters

    public String getGardenerID() {
        return gardenerID;
    }

    public String getName() {
        return name;
    }

    public String getGardenerName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Reservation> getReservations() {
        return Collections.unmodifiableList(reservations);
    }

    // setters

    public void setName(String name) {
        this.name = name != null ? name.trim() : this.name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public List<Reservation> getActiveReservations() {
        List<Reservation> active = new ArrayList<>();
        for (Reservation res : reservations) {
            if (res.getStatus().isActive()) {
                active.add(res);
            }
        }
        return active;
    }

    public int getActiveReservationCount() {
        return getActiveReservations().size();
    }

    public boolean hasActiveReservations() {
        return !getActiveReservations().isEmpty();
    }

    public boolean hasReservationForPlot(String plotID) {
        for (Reservation res : reservations) {
            if (res.getStatus().isActive() && 
                res.getPlot().getPlotID().equals(plotID)) {
                return true;
            }
        }
        return false;
    }

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

    // detailed output with contact info
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
