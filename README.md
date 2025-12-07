# Multithreading Manager
**Author:** Huoth Sitha  
**Program:** ITC International Program - Year 3  
**Course:** Software Engineering Concept

A JavaFX application designed to demonstrate concurrent task management. It allows users to run, pause, resume, and stop two independent background threads: one for generating Prime numbers and another for the Fibonacci sequence.

## Features

### Dual Thread Control
Manage two separate tasks simultaneously without freezing the UI.

### Prime Number Generator
Calculates prime numbers within a user-defined range (Min/Max).

### Fibonacci Sequence Generator
Generates the sequence up to a specific limit.

### Task Controls
- **Start:** Begins the computation on a background thread.  
- **Pause/Resume:** Uses `wait()` and `notify()` to temporarily halt execution.  
- **Stop:** Uses cooperative cancellation (`cancel()`) to safely terminate the thread.

### Visual Feedback
Real-time progress bars, status labels, and scrollable text output.

### Console Debugging
Logs thread state changes (Waiting/Resumed) to the console for verification.

## Prerequisites
- **Java:** JDK 21  
- **Maven:** 3.x  
- **JavaFX:** 21.0.1 (Managed via Maven)

## How to Run

### Compile the project:
```bash
mvn clean compile

```
### Run:
```bash
mvn javafx:run

```