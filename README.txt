Orbital
(c)2013, Jeffrey Reinecke

Mission:
I decided to cross my interest in simulating full 3-dimensional Kerbal Space
Program rocket flights and it was a good excuse to try out the Scala I've been
learning.

Phases:
[x] Phase 1: Get simulation of a single-stage rocket around Kerbin.
[ ] Phase 2: Get simulation of a multi-stage rocekt around Kerbin.
[ ] Phase 3: Add flight programs (burns at a given time/position trigger, for a duration and direction)
[ ] Phase 4: Add change of reference frame when transitioning between SOIs.
[ ] Phase 5: Add CLI for input and output.

See https://www.pivotaltracker.com/projects/844861 for task tracking.

Where are the tests?:
I'm normally so good with testing--even on personal projects--but this time
around I've got limited time to try out my idea and have so far been focusing on
architecting the code.  When my simulation is giving me useful results for KSP,
I may come back and add some cursory tests, but I have no plan to give proper
and thorough unit tests like I usually do.

LICENSE:

Copyright (c) 2013, Jeffrey C. Reinecke
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the copyright holders nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL JEFFREY REINECKE BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE