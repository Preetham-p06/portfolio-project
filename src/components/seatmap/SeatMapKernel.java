package components.seatmap;

import components.standard.Standard;

/**
 * Seat-map kernel with one-based coordinates and available or reserved seats.
 * A seat is available exactly when its coordinate is absent from
 * {@code this.reserved}. Passenger information is outside this component's
 * model. References supplied to component methods are non-null.
 *
 * <p>
 * Dimensions are part of the abstract value. The seat operations
 * {@link #reserve(int, int)} and {@link #release(int, int)} preserve them; they
 * are not constant throughout an object's lifetime. Inherited standard
 * operations can replace the complete value, including its dimensions.
 * </p>
 *
 * <p>
 * Every concrete implementation in this family provides the no-argument and
 * two-argument constructors specified below. Consequently, the initial value
 * used by {@link Standard} is always {@code INITIAL_SEAT_MAP}, even for an
 * object originally constructed with other dimensions. Constructor contracts
 * below specify implementing classes; they are not Java interface methods.
 * </p>
 *
 * <p>
 * The three {@link Standard} methods are inherited, not new secondary
 * operations. Their contracts specialize to this model as follows. A transfer
 * does not require matching dimensions.
 * </p>
 *
 * <pre>
 * clear():
 *   requires true
 *   clears this
 *   ensures this = INITIAL_SEAT_MAP
 *
 * newInstance():
 *   requires true
 *   ensures
 *     newInstance = INITIAL_SEAT_MAP and
 *     this = #this and
 *     [newInstance is a fresh object with the same dynamic type as this]
 *
 * transferFrom(source: SeatMap):
 *   requires
 *     [source is non-null and is not the same object as this] and
 *     [source has the same dynamic type as this]
 *   replaces this
 *   clears source
 *   ensures this = #source and source = INITIAL_SEAT_MAP
 * </pre>
 *
 * <p>
 * In contracts, {@code #this} and {@code #source} denote incoming abstract
 * values; {@code =} compares abstract values, not object identities. Arithmetic
 * in the mathematical model uses unbounded mathematical integers. The phrase
 * {@code A minus B} denotes set difference. An
 * implementation checking the capacity restriction in Java must avoid
 * overflowing before it checks, for example by multiplying after a
 * {@code long} conversion.
 * </p>
 *
 * @mathsubtypes <pre>
 * SEAT is (row: integer, seat: integer)
 * </pre>
 * @mathdefinitions <pre>
 * MAX_CAPACITY: integer = 2147483647
 *
 * SEATS(
 *   rowCount: integer,
 *   seatsInRow: integer
 * ): finite set of SEAT satisfies
 *   for all p: SEAT
 *     (p is in SEATS(rowCount, seatsInRow) iff
 *       1 &lt;= p.row &lt;= rowCount and
 *       1 &lt;= p.seat &lt;= seatsInRow)
 *
 * INITIAL_SEAT_MAP = (1, 1, {})
 * </pre>
 * @mathmodel <pre>
 * type SeatMapKernel is modeled by (
 *   rows: integer,
 *   seatsPerRow: integer,
 *   reserved: finite set of SEAT
 * )
 * exemplar m
 * constraint
 *   1 &lt;= m.rows and
 *   1 &lt;= m.seatsPerRow and
 *   m.rows * m.seatsPerRow &lt;= MAX_CAPACITY and
 *   m.reserved is subset of SEATS(m.rows, m.seatsPerRow)
 * </pre>
 * @initially <pre>
 * ():
 *   ensures this = INITIAL_SEAT_MAP
 *
 * (int rows, int seatsPerRow):
 *   requires
 *     1 &lt;= rows and
 *     1 &lt;= seatsPerRow and
 *     rows * seatsPerRow &lt;= MAX_CAPACITY
 *   ensures this = (rows, seatsPerRow, {})
 * </pre>
 */
public interface SeatMapKernel extends Standard<SeatMap> {

    /**
     * Reports the number of rows.
     *
     * @return the number of rows in this map
     * @requires true
     * @ensures rows = this.rows and this = #this
     */
    int rows();

    /**
     * Reports the number of seats in each row.
     *
     * @return the number of seats per row in this map
     * @requires true
     * @ensures seatsPerRow = this.seatsPerRow and this = #this
     */
    int seatsPerRow();

    /**
     * Reports whether the specified seat is available.
     *
     * @param row
     *            the one-based row number
     * @param seat
     *            the one-based position within the row
     * @return true exactly when the specified seat is available
     * @requires (row, seat) is in SEATS(this.rows, this.seatsPerRow)
     * @ensures <pre>
     * isAvailable = ((row, seat) is not in this.reserved) and
     * this = #this
     * </pre>
     */
    boolean isAvailable(int row, int seat);

    /**
     * Reserves one available seat. All other seats and both dimensions remain
     * unchanged.
     *
     * @param row
     *            the one-based row number of the seat to reserve
     * @param seat
     *            the one-based position within the row of the seat to reserve
     * @updates this
     * @requires <pre>
     * (row, seat) is in SEATS(this.rows, this.seatsPerRow) and
     * (row, seat) is not in this.reserved
     * </pre>
     * @ensures <pre>
     * this = (#this.rows, #this.seatsPerRow,
     *         #this.reserved union {(row, seat)})
     * </pre>
     */
    void reserve(int row, int seat);

    /**
     * Makes one reserved seat available. All other seats and both dimensions
     * remain unchanged.
     *
     * @param row
     *            the one-based row number of the seat to release
     * @param seat
     *            the one-based position within the row of the seat to release
     * @updates this
     * @requires <pre>
     * (row, seat) is in SEATS(this.rows, this.seatsPerRow) and
     * (row, seat) is in this.reserved
     * </pre>
     * @ensures <pre>
     * this = (#this.rows, #this.seatsPerRow,
     *         #this.reserved minus {(row, seat)})
     * </pre>
     */
    void release(int row, int seat);
}
