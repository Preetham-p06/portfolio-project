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
}
