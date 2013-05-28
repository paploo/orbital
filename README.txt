Orbital
(c)2013, Jeffrey Reinecke

Mission:
I decided to cross my interest in simulating full 3-dimensional Kerbal Space
Program rocket flights and it was a good excuse to try out the Scala I've been
learning.

Phases:
Step 1: Get simulation of a single-stage rocket around Kerbin.
Step 2: Get simulation of a multi-stage rocekt around Kerbin.
Step 3: Add flight programs (burns at a given time/position trigger, for a duration and direction)
Step 4: Add change of reference frame when transitioning between SOIs.

Step 1 should include a top-level object that should record events such as
periapsis, apoapsis, staging, and delta-v during burns.

Where are the tests?:
I'm normally so good with testing, but this time around I've got limited time
and have so far been focusing on architecting the code.  When things are stable
I may come back and work on them.
