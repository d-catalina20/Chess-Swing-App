# Java Chess Engine & GUI

A robust, object-oriented Chess application built with Java Swing. This project features a custom move validation engine, a graphical user interface, and a local JSON-based persistence layer for user authentication and match history.

## Key Features

* **Move Validation Engine:** Implements strict chess rules including pawn promotion.
* **User Authentication:** Custom login/register system using credentials.
* **Data Persistence:** Uses `simple-json` to simulate a NoSQL database, storing user profiles and game states locally.
* **Computer Opponent:** Includes a Player vs. Computer mode capable of executing valid moves.
* **Interactive UI:** Responsive Swing interface with legal move highlighting and IRL updates of the captured pieces and the made moves.

## Tech Stack

* **Language:** Java 17+
* **GUI:** Java Swing (Custom JPanels, Timers)
* **Data:** JSON (SimpleJSON library)
* **Architecture:** MVC (Model-View-Controller)

## Screenshots

1. Login Panel
<img width="1232" height="867" alt="image" src="https://github.com/user-attachments/assets/4a5246f0-c9b3-40cf-8e0d-25f0b4550859" />

2. Game Menu
<img width="1232" height="867" alt="image" src="https://github.com/user-attachments/assets/31f90ac4-87dc-46f6-b229-11a7cbb23578" />

3. Chess Board
<img width="1232" height="867" alt="image" src="https://github.com/user-attachments/assets/c1103158-0ce0-4419-afa6-b4484ab7254a" />

## How to Run

1. Clone the repository.
2. Open the project in IntelliJ IDEA or Eclipse.
3. Add the `.jar` file found in the `lib/` folder to your project's Build Path / Libraries.
4. Run the Main.java file.

## Project Structure

The codebase is organized into modular packages to ensure separation of concerns:

* `src/MainManagement`: The application core. Contains the entry point (`Main.java`), user session management (`User.java`), and the **JSON Persistence Layer** (`JsonReaderUtil`, `JsonWriterUtil`).
* `src/GUI`: All graphical components built with **Java Swing** (Login screens, Board rendering, Menus).
* `src/pieces`: Custom PNGs for each chess piece used in the graphical interface of the game.
* `src/GameObjects`: Core game entities such as the chess pieces and design pattern classes.
* `src/GameState`: Logic for managing the flow of the game (turn switching, check/checkmate detection, game history).
* `src/Exceptions`: Custom exception handling for invalid moves or user errors.
* `src/lib`: External dependencies (JSON-simple library).
