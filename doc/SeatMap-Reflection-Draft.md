# SeatMap reflection draft

Review and personalize this draft to match your own work, learning, and the
assignment's reflection prompt before submitting it.

An important design issue in this project was defining the seat map's initial
value. My first plan treated clearing the map as removing reservations while
preserving its dimensions. After considering the inherited Standard contract,
I chose a default 1-by-1 available map. Clear restores that value, newInstance
creates a fresh map in that state, and transferFrom moves the complete source
value before resetting the source. This makes the standard operations
consistent even when the two maps originally have different dimensions.

The mathematical model helped separate the component's behavior from its
storage. It describes dimensions and a set of reserved coordinates. The
implementation stores dimensions and a sparse set of integer seat indexes,
while the correspondence explains how those indexes represent coordinates.
The kernel exposes only the essential changes and queries. Counting, checking
fullness, and the common methods are layered above it through kernel methods.

The testing approach checks complete state, rather than only the seat being
changed. It covers first, middle, and last coordinates, every reservation
pattern on small layouts, reset behavior, and independence after transfer.
An array-based fixture also checks that equality and hashing depend on the
abstract value rather than the storage choice. The two clients show how the
same component supports manual booking and automatic check-in assignment.

One tradeoff is that the sparse representation saves storage for mostly
available maps, but the generic secondary methods still scan every seat.
A future version could examine whether faster aggregate queries justify
changing the kernel. I would also keep passenger records outside this
component so its responsibilities remain clear.
