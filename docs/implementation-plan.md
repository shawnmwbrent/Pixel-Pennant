# V0.1.0 Implementation Plan

1. Specify engine behavior with JUnit tests for counts, walks, strikeouts, outs,
   scoring, half-inning transitions, hit advancement, and game completion.
2. Implement immutable player identity/ratings and fictional team factories.
3. Implement `BaseballGame` as the rendering-independent source of truth.
4. Add the LibGDX application state machine and reusable pixel-styled controls.
5. Connect pitching, batting, assisted fielding, and assisted baserunning commands
   to engine events; add the persistent scoreboard and final line score.
6. Add a landscape Android launcher and V0.1.0 application metadata.
7. Run unit tests, Gradle verification, and Android assembly; retain the generated
   debug APK as the installable deliverable.

Implementation follows red/green/refactor: engine tests are written before their
production classes, then presentation is added only after the rules suite passes.
