import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Immutable value object representing a date range.
 * Encapsulates start and end dates with validation and utility methods.
 */
public final class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Constructs a DateRange with validation.
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @throws IllegalArgumentException if startDate is after endDate or either is null
     */
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

    // ==================== GETTERS ====================

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if this date range is valid (start <= end).
     * Always true for properly constructed DateRange objects.
     */
    public boolean isValid() {
        return !startDate.isAfter(endDate);
    }

    /**
     * Checks if this date range overlaps with another.
     * Two ranges overlap if they share at least one common day.
     * @param other the other date range
     * @return true if the ranges overlap
     */
    public boolean overlaps(DateRange other) {
        if (other == null) return false;
        return !this.endDate.isBefore(other.startDate) && !this.startDate.isAfter(other.endDate);
    }

    /**
     * Checks if a specific date falls within this range (inclusive).
     * @param date the date to check
     * @return true if the date is within the range
     */
    public boolean contains(LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Checks if this range completely contains another range.
     * @param other the other date range
     * @return true if this range contains the other range
     */
    public boolean contains(DateRange other) {
        if (other == null) return false;
        return !other.startDate.isBefore(this.startDate) && !other.endDate.isAfter(this.endDate);
    }

    /**
     * Returns the length of this date range in days (inclusive).
     * @return number of days in the range
     */
    public long lengthInDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * Checks if this date range is in the past.
     * @return true if the end date is before today
     */
    public boolean isInPast() {
        return endDate.isBefore(LocalDate.now());
    }

    /**
     * Checks if this date range is in the future.
     * @return true if the start date is after today
     */
    public boolean isInFuture() {
        return startDate.isAfter(LocalDate.now());
    }

    /**
     * Checks if today falls within this date range.
     * @return true if today is within the range
     */
    public boolean isCurrentlyActive() {
        return contains(LocalDate.now());
    }

    // ==================== OBJECT METHODS ====================

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
