ns2-scc-profiler
================

Generates distance based metrics for natural selection 2 maps.

![](https://i.imgur.com/uH1lnfs.png)

[Try it online](http://www.mostlyoriginal.net/games/ns2-scc-profiler/)

### Graphs

- All routes: shortest routes between all points of interest,
- RT run times: runtime in seconds to all RTs from each node for both teams.
- RT head start: which team will be able to reach RTs fastest and by how many seconds based on what techpoints they own,
- Presence: what area they can cover effectively depending on the techpoints they own, and where battlefronts might end up.

### About the code

A quick prototype, so don't expect highly reusable code.

### Library Versions

LibGDX 1.5.3, LibGDX-AI 1.4.1-SNAPSHOT (pathfinding), and Artemis-ODB 0.8.1