package subway.domain;

import java.util.Objects;

public class Distance {

    private static final int MIN_VALUE = 0;

    private final int value;

    public Distance(final int value) {
        validatePositive(value);
        this.value = value;
    }

    private void validatePositive(final int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException("거리는 음수가 될 수 없습니다.");
        }
    }

    public Distance subtract(final Distance otherDistance) {
        return new Distance(this.value - otherDistance.value);
    }

    public Distance add(final Distance otherDistance) {
        return new Distance(this.value + otherDistance.value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
