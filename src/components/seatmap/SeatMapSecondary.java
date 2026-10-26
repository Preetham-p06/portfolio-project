package components.seatmap;

/**
 * Implementations of the secondary seat-map operations using kernel queries.
 * This class introduces no representation fields. Concrete subclasses provide
 * the kernel and standard methods required by {@link SeatMap}.
 */
public abstract class SeatMapSecondary implements SeatMap {

    /**
     * {@inheritDoc}
     */
    @Override
    public final int countAvailable() {
        int available = 0;
        final int rowCount = this.rows();
        final int seatsInRow = this.seatsPerRow();

        // Zero-based counters avoid overflow at Integer.MAX_VALUE.
        for (int row = 0; row < rowCount; row++) {
            for (int seat = 0; seat < seatsInRow; seat++) {
                if (this.isAvailable(row + 1, seat + 1)) {
                    available++;
                }
            }
        }

        return available;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isFull() {
        return this.countAvailable() == 0;
    }

    /**
     * Compares dimensions and every seat's status through kernel queries.
     * Null and objects outside the SeatMap family compare unequal.
     *
     * @param obj
     *            the object to compare, which may be null
     * @return true exactly when obj is a SeatMap with the same abstract value
     * @ensures <pre>
     * equals = [obj is a SeatMap with the same abstract value as this] and
     * this = #this and
     * [if obj is a SeatMap, its abstract value is unchanged]
     * </pre>
     */
    @Override
    public final boolean equals(Object obj) {
        boolean equal = obj == this;
        if (!equal && obj instanceof SeatMap) {
            final SeatMap other = (SeatMap) obj;
            final int rowCount = this.rows();
            final int seatsInRow = this.seatsPerRow();
            equal = rowCount == other.rows()
                    && seatsInRow == other.seatsPerRow();
            for (int row = 0; equal && row < rowCount; row++) {
                for (int seat = 0; equal && seat < seatsInRow; seat++) {
                    equal = this.isAvailable(row + 1, seat + 1)
                            == other.isAvailable(row + 1, seat + 1);
                }
            }
        }
        return equal;
    }

    /**
     * Computes a value hash from the dimensions and seat statuses in row-major
     * order. Equal maps have equal hashes, regardless of representation.
     * Integer wraparound is intentional for hash arithmetic.
     *
     * @return a hash of this map's abstract value
     * @ensures this = #this
     */
    @Override
    public final int hashCode() {
        final int multiplier = 31;
        final int rowCount = this.rows();
        final int seatsInRow = this.seatsPerRow();
        int hash = rowCount;
        hash = multiplier * hash + seatsInRow;
        for (int row = 0; row < rowCount; row++) {
            for (int seat = 0; seat < seatsInRow; seat++) {
                hash = multiplier * hash
                        + (this.isAvailable(row + 1, seat + 1) ? 1 : 0);
            }
        }
        return hash;
    }

    /**
     * Reports dimensions and reserved coordinates in row-major order. For
     * example: SeatMap[rows=2, seatsPerRow=3, reserved={(1,2), (2,3)}].
     *
     * @return a readable description of this map's abstract value
     * @ensures <pre>
     * [toString describes the dimensions and reserved coordinates of this] and
     * this = #this
     * </pre>
     */
    @Override
    public final String toString() {
        final int rowCount = this.rows();
        final int seatsInRow = this.seatsPerRow();
        final StringBuilder text = new StringBuilder("SeatMap[rows=");
        text.append(rowCount).append(", seatsPerRow=")
                .append(seatsInRow).append(", reserved={");
        boolean first = true;
        for (int row = 0; row < rowCount; row++) {
            for (int seat = 0; seat < seatsInRow; seat++) {
                if (!this.isAvailable(row + 1, seat + 1)) {
                    if (!first) {
                        text.append(", ");
                    }
                    text.append('(').append(row + 1).append(',')
                            .append(seat + 1).append(')');
                    first = false;
                }
            }
        }
        text.append("}]");
        return text.toString();
    }
}
