# Pixel Pennant V0.1.0 — Design

## Product promise

Pixel Pennant is a fast, cheerful, fictional 16-bit baseball game built for short
landscape sessions. V0.1.0 delivers one complete matchup: the hometown Toronto
Bluebirds against the Detroit Motors. It uses no real-league marks, people, logos,
stadiums, or art.

## Brainstorm conclusions

Several control schemes were considered: gesture-only controls, a simulation-first
at-bat, and a small set of large arcade buttons. Large buttons won because they are
readable on phones, responsive, and make every phase understandable without a
tutorial. The game alternates between compact decision moments:

1. **Pitching:** choose FAST, CURVE, or CHANGE; tap a location in the strike-zone
   grid; stop a sweeping power meter.
2. **Batting:** move a visible target with a directional pad; choose SWING or BUNT.
3. **Fielding:** fielders pursue automatically; choose 1ST, 2ND, 3RD, or HOME.
4. **Baserunning:** runners move automatically; choose ADVANCE, HOLD, or RETURN.

Every action resolves quickly. Outcomes favor good aim/timing but preserve enough
variation for replayability. Between actions, a short status message explains what
happened. A persistent scoreboard shows inning half, count, outs, hits, runs, and
bases. After the selected 3, 6, or 9 innings, a line score offers REMATCH and MENU.

## Visual direction

The fictional **Harbour Light Park** uses a sunset sky, striped grass, warm infield,
blocky crowds, and a city silhouette. Dark navy panels, cyan/royal-blue Bluebirds,
orange Motors, hard-edged geometry, integer-ish spacing, and nearest-neighbor
rendering evoke a colorful 16-bit broadcast. The original Bluebirds identity is a
simple wing/pennant motif—not an imitation of any existing Toronto sports logo.

## Architecture

`core` owns all portable Java:

- `engine`: deterministic rules, inning/count/base transitions, line score, and
  serializable-style snapshots. It has no LibGDX imports.
- `model`: player identity and ratings plus teams/rosters. It anticipates future
  persistence without implementing seasons, trades, contracts, injuries, or other
  franchise systems.
- `screen`: LibGDX rendering and touch input. It translates button presses into
  engine commands and never edits baseball state directly.

`android` contains only the launcher and Android resources. The engine can later be
reused by franchise mode or another renderer.

## V0.1.0 boundary

Included: title, inning choice, one matchup, batting/pitching/fielding/baserunning
decisions, complete inning/count/base tracking, line score, rematch, and menu.

Explicitly excluded: alternate clubs, roster management, seasons, saved statistics,
trades, draft, contracts, development, injuries, franchise mode, online play,
licensed content, audio packs, and monetization.

