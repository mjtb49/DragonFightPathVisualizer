# DragonFightPathVisualizer

This is a fabric mod for Minecraft Java 1.16.x. Its purpose is to aid the development of dragon fight strategies for 1.16 speedrunning.

The mod shows the following things with the following colors:

GRAY : The nodes the dragon can travel to in normal phases, and the paths between them it is allowed to take

WHITE : The path intended by the dragon in a HOLDING_PATTERN phase

RED: The path intended by the dragon in a STRAFING phase

BLUE: The path indended by the dragon in a LANDING_APPROACH phase

GREEN: The path intended by the dragon in a TAKEOFF phase

PURPLE: A curve tracing the server side position of the dragon's head over the last 10 seconds. The tip of this curve is where bed hits should be aimed.

YELLOW: The node the dragon is currently heading to.

ORANGE: The dragon's current target (where it is trying to fly to).


The mod also includes a few commands:

/dragon allows the user to choose how each of the elements are rendered, or turn them off.

/printBedDamage lets the user choose whether the mod prints theoretical bed damage amounts for player placed beds in the end (default true).

/setDamageThreshold lets the user choose a number below which theoretical bed damage will not be printed (default 5).
