# Seat Map interface decisions

The component stores a row count, a seats-per-row count, and a finite set of
reserved coordinates. Each coordinate uses one-based row and seat numbers.
Every valid coordinate outside the reserved set represents an available seat.

## Standard operations and initial value

Each concrete implementation must provide a no-argument constructor producing
the abstract value `(1, 1, {})`: one row, one seat, and no reservations. It also
provides a constructor accepting positive dimensions and producing an
all-available map of that size. These are constructor specifications for
implementations; Java interfaces do not declare constructors.

Because the family has a no-argument constructor, that constructor's value
defines the initial value used by `Standard`, even for objects originally
created through the two-argument constructor.

| Inherited operation | Result |
| --- | --- |
| `clear()` | Restore `(1, 1, {})`. |
| `newInstance()` | Return a fresh object of the same dynamic type with value `(1, 1, {})`; leave the receiver unchanged. |
| `transferFrom(source)` | Give the receiver the source's entire old value, including dimensions, and restore the source to `(1, 1, {})`. |

A transfer requires a non-null source that is a distinct object of the same
dynamic type. Matching dimensions are not required. For example, transferring
a 20-by-6 map into a 10-by-4 map makes the receiver 20-by-6, with exactly the
source's former reservations, and makes the source 1-by-1 and available.

The standard operations remain inherited. `clear()` is not an added method in
the enhanced interface. Releasing every reservation while preserving the
current layout is possible by visiting reserved seats and calling `release()`;
that behavior is distinct from this component's standard initial-value reset.

## Meaning of fixed dimensions

Dimensions remain unchanged during `reserve()`, `release()`, and every query.
They are not immutable for the lifetime of the Java object: standard reset and
transfer operations may replace the complete abstract value. This avoids
adding an equal-dimensions restriction to the inherited transfer contract.

## Capacity and overflow

The abstract invariant requires positive dimensions and a total capacity no
larger than `Integer.MAX_VALUE` (2,147,483,647). Mathematical products in the
contracts are unbounded integer products. An implementation can check the
restriction without overflowing Java's `int` multiplication:

```java
rows > 0 && seatsPerRow > 0
    && (long) rows * seatsPerRow <= Integer.MAX_VALUE
```

The reserved set is a subset of the valid coordinates, so both the reserved
count and available count fit in `int`. For secondary traversal, zero-based
loop counters with strict upper bounds avoid overflowing a one-based counter
when a dimension equals `Integer.MAX_VALUE`:

```java
int count = 0;
for (int r = 0; r < this.rows(); r++) {
    for (int s = 0; s < this.seatsPerRow(); s++) {
        if (this.isAvailable(r + 1, s + 1)) {
            count++;
        }
    }
}
```

## Layering and contracts

`SeatMapKernel extends Standard<SeatMap>` and declares five primary methods:
`rows`, `seatsPerRow`, `isAvailable`, `reserve`, and `release`.
`SeatMap extends SeatMapKernel` declares the two secondary queries
`countAvailable` and `isFull`. It adds no new mathematical state.

`SeatMapSecondary` is an abstract class implementing both enhanced queries.
`countAvailable` reads the dimensions once, visits each valid coordinate, and
counts seats reported available by the kernel. Its zero-based loop counters
are converted to one-based coordinates when querying seats. `isFull` checks
whether `countAvailable()` returns zero. Both implementations preserve the
map, and the class declares no representation fields. Concrete subclasses
remain responsible for the kernel and inherited standard operations.
The secondary class also implements `equals`, `hashCode`, and `toString`
through kernel queries.

`SeatMap1` provides the concrete implementation. It stores dimensions and a
`java.util.HashSet<Integer>` of reserved row-major seat indexes. Its standard
methods reset to the 1-by-1 initial value, create a fresh initial instance, or
transfer the representation and reset the source without sharing the set.

Each seat operation has a valid-coordinate precondition. Reserving also
requires availability; releasing requires an existing reservation. Behavior
outside the preconditions is not specified. Full-tuple postconditions for
updates preserve both dimensions and every other seat. Query postconditions
explicitly preserve the whole map.

The interface files use OSU documentation tags: `@mathsubtypes`,
`@mathdefinitions`, `@mathmodel`, `@initially`, `@updates`, `@requires`, and
`@ensures`. The mathematical model is independent of the eventual Java
representation, so representation invariants and correspondence assertions
belong in the implementing class.

The standard semantics and documentation conventions were checked against:

- [OSU Standard documentation](https://cse22x1.github.io/common/doc/components/standard/Standard.html)
- [OSU MapKernel documentation](https://cse22x1.engineering.osu.edu/common/doc/components/map/MapKernel.html)
- [OSU QueueKernel documentation](https://cse22x1.engineering.osu.edu/common/doc/components/queue/QueueKernel.html)

## Files and required compilation

The deliverables are separate files in this project layout:

```text
src/components/seatmap/SeatMapKernel.java
src/components/seatmap/SeatMap.java
src/components/seatmap/SeatMapSecondary.java
src/components/seatmap/SeatMap1.java
doc/SeatMap-Design.md
```

The kernel contains its complete constructor documentation, interface
declaration, five primary methods and their contracts, and closing brace.
The enhanced interface contains the two secondary method contracts and
declarations. The Java files contain no literal `&#x20;` tokens or backslashes
from chat formatting. HTML escapes for comparison operators inside Javadoc
are intentional; `A minus B` means mathematical set difference.

Compilation passed on October 26, 2026 using `javac 21.0.13` and the actual
OSU library at `lib/components.jar`. The following command exited with status
0 and produced no warnings or errors:

```bash
javac --release 21 -Xlint:all -Werror -cp lib/components.jar -d bin src/components/seatmap/SeatMapKernel.java src/components/seatmap/SeatMap.java src/components/seatmap/SeatMapSecondary.java src/components/seatmap/SeatMap1.java
```

This verifies Java syntax, types, and integration with the OSU `Standard`
interface for both interfaces, the abstract secondary class, and `SeatMap1`.
Compilation does not verify mathematical contracts.

### Local runtime checks

A temporary verification program outside the repository ran with assertions
enabled against the compiled classes and the actual OSU library. It passed
all 682 reservation patterns across layouts with one through three rows and
one through three seats per row. Checks covered available counts, fullness,
seat status, releasing reservations, equality, equal hashes, fresh instances,
clear, transfers across different dimensions, and independence after transfer.
Additional checks covered the maximum valid seat index in a 1-by-Integer.MAX_VALUE
map and one exact `toString` result. Full-map traversal was not attempted on
the maximum-capacity map.

These checks are not the assignment's systematic JUnit suite. The verification
program was temporary and is not included in this repository. Javadoc and
Checkstyle were not validated in this update.
