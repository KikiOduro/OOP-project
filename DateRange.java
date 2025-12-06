import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

// DateRange - represents a start and end date
public final class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // getters

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // utility methods

    public boolean isValid() {
        return !startDate.isAfter(endDate);
    }

    // check if two date ranges share any days
    public boolean overlaps(DateRange other) {
        if (other == null) return false;
        return !this.endDate.isBefore(other.startDate) && !this.startDate.isAfter(other.endDate);
    }

    // check if a date is within this range
    public boolean contains(LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // check if this range fully contains another range
    public boolean contains(DateRange other) {
        if (other == null) return false;
        return !other.startDate.isBefore(this.startDate) && !other.endDate.isAfter(this.endDate);
    }

    // how many days in this range (inclusive)
    public long lengthInDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public boolean isInPast() {
        return endDate.isBefore(LocalDate.now());
    }

    public boolean isInFuture() {
        return startDate.isAfter(LocalDate.now());
    }

    public boolean isCurrentlyActive() {
        return contains(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(startDate, dateRange.startDate) &&
               Objects.equals(endDate, dateRange.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return startDate + " to " + endDate;
    }
}
