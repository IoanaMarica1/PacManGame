PacManGame
A multi-level, state-driven Pac-Man clone built using Java Swing.
This project implements core arcade mechanics, advanced game state management, and a robust multiple-level system.
Features Implemented
Classic Gameplay: Full movement control, maze collision detection, and score/life tracking.
Multiple Levels: The game utilizes a map array (String[][] levels) and automatically advances the player to the next level upon eating all the food on the current map.
Ghost Movement: Basic random movement and wall collision detection for all ghosts.
Stable Game Loop: The game runs on a safe, dedicated Event Dispatch Thread (EDT) using javax.swing.Timer for smooth and stable performance.
Game State Management
The application is controlled by the GameState enum, enabling clear separation between different phases:
Menu & Navigation: Includes START_MENU, GAME_OVER_MENU, and HIGH_SCORES viewing (Top 5 scores).
Future Development Plans
High Score Persistence: Implement file I/O (Input/Output) to save high scores permanently on the disk.
Ghost AI (Pathfinding): Introduce basic Artificial Intelligence (AI) for ghosts, allowing them to actively track and chase Pac-Man.
Multiplayer Architecture: Refactor the game into a Client-Server application using Java Socket programming to enable two or more players to play simultaneously on the same map.
