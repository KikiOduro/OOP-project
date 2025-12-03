import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Represents a crop that can be planted in a garden plot.
 * Designed as a value object with immutable properties.
 */
public final class Crop {
    private final String name;
    private final int minGrowingDays;
    private final Set<Season> bestSeasons;
    private final String description;

    /**
     * Enum representing growing seasons.
     */
    public enum Season {
        SPRING, SUMMER, FALL, WINTER
    }

    // ==================== CONSTRUCTORS ====================

    /**
     * Full constructor with all properties.
     * @param name the crop name (required)
     * @param minGrowingDays minimum days needed to grow
     * @param bestSeasons seasons when this crop grows best
     * @param description optional description
     */
    public Crop(String name, int minGrowingDays, Set<Season> bestSeasons, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Crop name cannot be null or empty");
        }
        if (minGrowingDays < 0) {
            throw new IllegalArgumentException("Minimum growing days cannot be negative");
        }
        this.name = name.trim();
        this.minGrowingDays = minGrowingDays;
        this.bestSeasons = bestSeasons != null ? 
            Collections.unmodifiableSet(new HashSet<>(bestSeasons)) : 
            Collections.emptySet();
        this.description = description != null ? description.trim() : "";
    }

    /**
     * Simplified constructor with just name.
     * @param name the crop name
     */
    public Crop(String name) {
        this(name, 0, null, null);
    }

    /**
     * Constructor with name and growing days.
     * @param name the crop name
     * @param minGrowingDays minimum days to grow
     */
    public Crop(String name, int minGrowingDays) {
        this(name, minGrowingDays, null, null);
    }

    // ==================== GETTERS ====================

    public String getName() {
        return name;
    }

    public int getMinGrowingDays() {
        return minGrowingDays;
    }

    public Set<Season> getBestSeasons() {
        return bestSeasons; // Already unmodifiable
    }

    public String getDescription() {
        return description;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if a date range provides enough time for this crop to grow.
     * @param dateRange the planting period
     * @return true if the date range is long enough
     */
    public boolean canGrowIn(DateRange dateRange) {
        if (dateRange == null) return false;
        return dateRange.lengthInDays() >= minGrowingDays;
    }

    /**
     * Checks if a given season is optimal for this crop.
     * @param season the season to check
     * @return true if the season is in the best seasons set
     */
    public boolean isGoodSeason(Season season) {
        return bestSeasons.contains(season);
    }

    // ==================== OBJECT METHODS ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crop crop = (Crop) o;
        return Objects.equals(name.toLowerCase(), crop.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a detailed string representation.
     */
    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Crop: ").append(name);
        if (minGrowingDays > 0) {
            sb.append(" (").append(minGrowingDays).append(" days min)");
        }
        if (!bestSeasons.isEmpty()) {
            sb.append(" - Best in: ").append(bestSeasons);
        }
        return sb.toString();
    }
}
