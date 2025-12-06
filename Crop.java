import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

// Crop - immutable value object for plants
public final class Crop {
    private final String name;
    private final int minGrowingDays;
    private final Set<Season> bestSeasons;
    private final String description;

    // seasons for growing
    public enum Season {
        SPRING, SUMMER, FALL, WINTER
    }

    // constructors
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

    public Crop(String name) {
        this(name, 0, null, null);
    }

    public Crop(String name, int minGrowingDays) {
        this(name, minGrowingDays, null, null);
    }

    // getters

    public String getName() {
        return name;
    }

    public int getMinGrowingDays() {
        return minGrowingDays;
    }

    public Set<Season> getBestSeasons() {
        return bestSeasons;
    }

    public String getDescription() {
        return description;
    }

    // check if date range is long enough for this crop
    public boolean canGrowIn(DateRange dateRange) {
        if (dateRange == null) return false;
        return dateRange.lengthInDays() >= minGrowingDays;
    }

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
        String result = name;
        if (minGrowingDays > 0) {
            result += " (" + minGrowingDays + " days min)";
        }
        if (!bestSeasons.isEmpty()) {
            result += " - Best in: " + bestSeasons;
        }
        return result;
    }
}
