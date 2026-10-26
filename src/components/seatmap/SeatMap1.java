package components.seatmap;

import java.util.HashSet;
import java.util.Set;

/**
 * Seat map represented by dimensions and a sparse set of reserved seat indexes.
 * Coordinates are converted to zero-based row-major indexes. An absent index
 * represents an available seat. The set is private and exclusively owned by
 * this object, except while its ownership is moved by {@code transferFrom}.
 *
 * <p>
 * This class is final, so constructing a new {@code SeatMap1} in
 * {@code newInstance} always preserves the receiver's dynamic type.
 * </p>
 *
 * @convention <pre>
 * 1 &lt;= this.rowCount and
 * 1 &lt;= this.seatCount and
 * this.rowCount * this.seatCount &lt;= Integer.MAX_VALUE and
 * [this.reservedSeats is non-null and contains only non-null integers] and
 * [this.reservedSeats is exclusively owned by this object] and
 * for all index: integer
 *   where ([index is an element of this.reservedSeats])
 *     (0 &lt;= index &lt; this.rowCount * this.seatCount)
 * </pre>
 * @correspondence <pre>
 * this = (this.rowCount, this.seatCount,
 *   {(row, seat): SEAT |
 *     1 &lt;= row &lt;= this.rowCount and
 *     1 &lt;= seat &lt;= this.seatCount and
 *     [(row - 1) * this.seatCount + (seat - 1)
 *       is an element of this.reservedSeats]})
 * </pre>
 */
public final class SeatMap1 extends SeatMapSecondary {

    /** Number of rows in this map. */
    private int rowCount;

    /** Number of seats in each row. */
    private int seatCount;

    /** Zero-based row-major indexes of reserved seats. */
    private Set<Integer> reservedSeats;

    /**
     * Creates an all-available representation of the specified dimensions.
     *
     * @param rows
     *            the number of rows
     * @param seatsPerRow
     *            the number of seats in each row
     * @replaces this
     * @requires <pre>
     * rows &gt; 0 and seatsPerRow &gt; 0 and
     * rows * seatsPerRow &lt;= Integer.MAX_VALUE
     * </pre>
     * @ensures this = (rows, seatsPerRow, {})
     */
    private void createNewRep(int rows, int seatsPerRow) {
        this.rowCount = rows;
        this.seatCount = seatsPerRow;
        this.reservedSeats = new HashSet<>();
    }

    /**
     * Converts valid one-based coordinates to a zero-based seat index.
     * The capacity invariant guarantees that the calculation fits in an int.
     *
     * @param row
     *            the one-based row number
     * @param seat
     *            the one-based seat number within the row
     * @return the zero-based row-major index of the seat
     * @requires <pre>
     * 1 &lt;= row &lt;= this.rowCount and
     * 1 &lt;= seat &lt;= this.seatCount
     * </pre>
     * @ensures <pre>
     * indexOf = (row - 1) * this.seatCount + (seat - 1) and
     * this = #this
     * </pre>
     */
    private int indexOf(int row, int seat) {
        assert row >= 1 && row <= this.rowCount : "Invalid row";
        assert seat >= 1 && seat <= this.seatCount : "Invalid seat";
        return (row - 1) * this.seatCount + (seat - 1);
    }

    /**
     * Constructs a 1-by-1 map with its seat available.
     *
     * @ensures this = (1, 1, {})
     */
    public SeatMap1() {
        this(1, 1);
    }

    /**
     * Constructs an all-available map of the specified dimensions.
     *
     * @param rows
     *            the positive number of rows
     * @param seatsPerRow
     *            the positive number of seats in each row
     * @requires <pre>
     * rows &gt; 0 and seatsPerRow &gt; 0 and
     * rows * seatsPerRow &lt;= Integer.MAX_VALUE
     * </pre>
     * @ensures this = (rows, seatsPerRow, {})
     */
    public SeatMap1(int rows, int seatsPerRow) {
        assert rows > 0 : "Rows must be positive";
        assert seatsPerRow > 0 : "Seats per row must be positive";
        assert (long) rows * seatsPerRow <= Integer.MAX_VALUE
                : "Total capacity must fit in an int";
        this.createNewRep(rows, seatsPerRow);
    }

    /** {@inheritDoc} */
    @Override
    public int rows() {
        return this.rowCount;
    }

    /** {@inheritDoc} */
    @Override
    public int seatsPerRow() {
        return this.seatCount;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAvailable(int row, int seat) {
        return !this.reservedSeats.contains(this.indexOf(row, seat));
    }

    /** {@inheritDoc} */
    @Override
    public void reserve(int row, int seat) {
        final int index = this.indexOf(row, seat);
        assert !this.reservedSeats.contains(index)
                : "The seat must be available";
        this.reservedSeats.add(index);
    }

    /** {@inheritDoc} */
    @Override
    public void release(int row, int seat) {
        final int index = this.indexOf(row, seat);
        assert this.reservedSeats.contains(index)
                : "The seat must be reserved";
        this.reservedSeats.remove(index);
    }

    /**
     * {@inheritDoc}
     *
     * @ensures this = (1, 1, {})
     */
    @Override
    public void clear() {
        this.createNewRep(1, 1);
    }

    /**
     * {@inheritDoc}
     *
     * @ensures <pre>
     * newInstance = (1, 1, {}) and this = #this and
     * [newInstance is a fresh SeatMap1]
     * </pre>
     */
    @Override
    public SeatMap newInstance() {
        return new SeatMap1();
    }

    /**
     * {@inheritDoc}
     *
     * @requires <pre>
     * source /= null and [source is not the same object as this] and
     * [source has the same dynamic type as this]
     * </pre>
     * @ensures this = #source and source = (1, 1, {})
     */
    @Override
    public void transferFrom(SeatMap source) {
        assert source != null : "Source must be non-null";
        assert source != this : "Source must be a different object";
        assert source.getClass() == this.getClass()
                : "Source must have the same dynamic type";

        final SeatMap1 localSource = (SeatMap1) source;
        this.rowCount = localSource.rowCount;
        this.seatCount = localSource.seatCount;
        this.reservedSeats = localSource.reservedSeats;
        localSource.createNewRep(1, 1);
    }
}
