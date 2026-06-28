# java-chess-core

A fully functional, desktop-based 2D Chess Game built from scratch in **Java** using the **Swing** framework. The project is engineered with a strict modular design, separating the graphical user interface, the central state machine engine, rule evaluation sets, and transactional special-case move handlers.

---

## 🚀 Key Features

* **Complete Chess Rules Engine**: Turn-based (`White` vs. `Black`) game cycle with deep self-validation. It prevents self-checking actions and rolls back illegal operations natively.
* **Interactive Swing Desktop Interface**:
  * Clean `GridLayout`-driven 8x8 matrix rendering board configurations with dynamic display updating.
  * Explicit visual selected-state markers (`Color.YELLOW`) highlighting chosen items before move commitments.
  * Unicode-based piece rendering (`\u2654` to `\u265F`) optimizing cross-platform look-and-feel without external asset dependency.
* **Advanced Chess Maneuvers Fully Handled**:
  * **Pawn Promotion**: Intercepts terminal pawn ranks to deploy an immediate interactive modal dialog menu (`JOptionPane`), providing seamless selection choices across *Queen*, *Rook*, *Bishop*, and *Knight*.
  * **Castling**: Validates path clearance, king-check boundaries, and individual rights flags across both King-side and Queen-side variations.
  * **En Passant**: Keeps track of active capture targets over temporary tracking states.
* **Live Game Status Matrix Indicators**: Real-time notifications updating bottom window panels during normal status, Check constraints (`White's turn - CHECK!`), or terminal checkmate game cycles.

---

## 📦 Project Architecture & Structure

The project code is divided into four highly focused classes to keep the user interface independent of the algorithmic rule processing logic:

1. **`ChessGameUI`**: The visual presentation engine. Extends `JFrame` to process button grid mapping, layout management (`BorderLayout`), selection styling swaps, modal triggers for promotions, and immediate dispatch updates.
2. **`ChessGameEngine`**: The core state machine configuration layer. Preserves internal board maps (`char[][]`), turn indexes, castling permission bit-arrays, checkmate validations, and simulation undo loops.
3. **`PieceValidation`**: The algorithmic mathematical layer. Provides stateless pure-logic validation operations confirming movement constraints for Pawns, Rooks, Knights, Bishops, Queens, and Kings alongside directional raycast path obstruction checking.
4. **`SpecialMoves`**: Transaction processing segment. Executes multi-step layout transitions such as synchronizing companion Rook swaps during King castling or modifying row cells upon En Passant captures.

---

## 🛠️ Setup and Installation

### Prerequisites
* **Java Development Kit (JDK) 8 or higher** installed on your workstation.
* A standard terminal window, console application, or a preferred Java IDE (IntelliJ IDEA, Eclipse, NetBeans).

### Compilation
Navigate directly to your root directory containing the `.java` files and compile them together:
```bash
javac ChessGameUI.java ChessGameEngine.java PieceValidation.java SpecialMoves.java
```
