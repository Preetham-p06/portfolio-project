package components.seatmap;

/**
 * {@link SeatMapKernel} enhanced with operations derived from kernel methods.
 * This interface inherits its mathematical model, abstract invariant,
 * constructor specifications, and standard-method semantics from
 * {@code SeatMapKernel}. It introduces no additional abstract state.
 *
 * <p>
 * Both secondary operations can inspect valid coordinates through
 * {@link #rows()}, {@link #seatsPerRow()}, and
 * {@link #isAvailable(int, int)}. The capacity invariant guarantees that the
 * number of available seats is representable as an {@code int}.
 * </p>
 */
public interface SeatMap extends SeatMapKernel {

    /**
     * Reports the total number of available seats.
     *
     * @return the number of seats that are not reserved, between zero and the
     *         map's total capacity, inclusive
     * @requires true
     * @ensures <pre>
     * countAvailable = this.rows * this.seatsPerRow - |this.reserved| and
     * this = #this
     * </pre>
     */
    int countAvailable();

    /**
     * Reports whether all seats are reserved. This can be implemented by
     * checking whether {@link #countAvailable()} returns zero.
     *
     * @return true exactly when the map has no available seats
     * @requires true
     * @ensures <pre>
     * isFull = (|this.reserved| = this.rows * this.seatsPerRow) and
     * this = #this
     * </pre>
     */
    boolean isFull();
}
