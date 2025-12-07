package com.example.threadmanager.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

    // --- Prime Components ---
    @FXML private TextField primeMinField, primeMaxField;
    @FXML private Button primeStartBtn, primePauseBtn, primeResumeBtn, primeStopBtn, primeRestartBtn;
    @FXML private ProgressBar primeProgressBar;
    @FXML private Label primeStatusLabel;
    @FXML private TextArea primeOutputArea;

    // --- Fibonacci Components ---
    @FXML private TextField fibCountField;
    @FXML private Button fibStartBtn, fibPauseBtn, fibResumeBtn, fibStopBtn, fibRestartBtn;
    @FXML private ProgressBar fibProgressBar;
    @FXML private Label fibStatusLabel;
    @FXML private TextArea fibOutputArea;

    private GeneratorTask primeTask;
    private GeneratorTask fibTask;

    // --- Prime Actions ---
    @FXML
    protected void onPrimeStart() {
        startPrimeTask();
    }

    @FXML
    protected void onPrimePause() {
        if (primeTask != null) primeTask.pause();
        primeStatusLabel.setText("Paused");
        setButtonState(true, false, true, true, true, "prime");
    }

    @FXML
    protected void onPrimeResume() {
        if (primeTask != null) primeTask.resume();
        primeStatusLabel.setText("Running...");
        setButtonState(true, true, false, true, true, "prime");
    }

    @FXML
    protected void onPrimeStop() {
        if (primeTask != null) primeTask.cancel();
        // UI feedback for stopping
        primeStatusLabel.setText("Stopping...");
        setButtonState(false, false, false, false, true, "prime");
    }

    @FXML
    protected void onPrimeRestart() {
        startPrimeTask();
    }

    private void startPrimeTask() {
        if (primeTask != null && primeTask.isRunning()) primeTask.cancel();

        primeOutputArea.clear();
        primeStatusLabel.setText("Running...");

        int min = 2;
        Integer max = null;
        try {
            if (!primeMinField.getText().isEmpty()) min = Integer.parseInt(primeMinField.getText());
            if (!primeMaxField.getText().isEmpty()) max = Integer.parseInt(primeMaxField.getText());
        } catch (NumberFormatException e) {
            primeOutputArea.setText("Error: Invalid numbers");
            return;
        }

        primeTask = new PrimeGeneratorTask(min, max);
        primeProgressBar.progressProperty().bind(primeTask.progressProperty());
        primeTask.messageProperty().addListener((obs, old, msg) -> primeOutputArea.appendText(msg + "\n"));
        
        primeTask.setOnSucceeded(e -> {
            primeStatusLabel.setText("Completed");
            setButtonState(false, false, false, false, true, "prime");
        });
        
        primeTask.setOnCancelled(e -> primeStatusLabel.setText("Stopped"));
        primeTask.setOnFailed(e -> primeStatusLabel.setText("Error"));
        
        setButtonState(true, true, false, true, true, "prime");
        new Thread(primeTask).start();
    }

    // --- Fibonacci Actions ---
    @FXML
    protected void onFibStart() {
        startFibTask();
    }

    @FXML
    protected void onFibPause() {
        if (fibTask != null) fibTask.pause();
        fibStatusLabel.setText("Paused");
        setButtonState(true, false, true, true, true, "fib");
    }

    @FXML
    protected void onFibResume() {
        if (fibTask != null) fibTask.resume();
        fibStatusLabel.setText("Running...");
        setButtonState(true, true, false, true, true, "fib");
    }

    @FXML
    protected void onFibStop() {
        if (fibTask != null) fibTask.cancel();
        fibStatusLabel.setText("Stopping...");
        setButtonState(false, false, false, false, true, "fib");
    }

    @FXML
    protected void onFibRestart() {
        startFibTask();
    }

    private void startFibTask() {
        if (fibTask != null && fibTask.isRunning()) fibTask.cancel();

        fibOutputArea.clear();
        fibStatusLabel.setText("Running...");

        Long maxVal = null;
        try {
            if (!fibCountField.getText().isEmpty()) maxVal = Long.parseLong(fibCountField.getText());
        } catch (NumberFormatException e) {
            fibOutputArea.setText("Error: Invalid number");
            return;
        }

        fibTask = new FibonacciGeneratorTask(maxVal);
        fibProgressBar.progressProperty().bind(fibTask.progressProperty());
        fibTask.messageProperty().addListener((obs, old, msg) -> fibOutputArea.appendText(msg + "\n"));

        fibTask.setOnSucceeded(e -> {
            fibStatusLabel.setText("Completed");
            setButtonState(false, false, false, false, true, "fib");
        });
        
        fibTask.setOnCancelled(e -> fibStatusLabel.setText("Stopped"));
        fibTask.setOnFailed(e -> fibStatusLabel.setText("Error"));

        setButtonState(true, true, false, true, true, "fib");
        new Thread(fibTask).start();
    }

    private void setButtonState(boolean running, boolean canPause, boolean canResume, boolean canStop, boolean canRestart, String type) {
        if (type.equals("prime")) {
            primeStartBtn.setDisable(running);
            primePauseBtn.setDisable(!canPause);
            primeResumeBtn.setDisable(!canResume);
            primeStopBtn.setDisable(!canStop);
            primeRestartBtn.setDisable(!canRestart);
        } else {
            fibStartBtn.setDisable(running);
            fibPauseBtn.setDisable(!canPause);
            fibResumeBtn.setDisable(!canResume);
            fibStopBtn.setDisable(!canStop);
            fibRestartBtn.setDisable(!canRestart);
        }
    }

    // --- Inner Classes for Threads ---
    
    abstract static class GeneratorTask extends Task<Void> {
        protected boolean paused = false;
        protected final Object lock = new Object();

        public void pause() { paused = true; }
        public void resume() {
            synchronized (lock) {
                paused = false;
                lock.notify();
            }
        }
        
        // DEBUGGING: Log to console when pausing
        protected void checkPause() throws InterruptedException {
            synchronized (lock) {
                if (paused) {
                    System.out.println(">>> [Thread] is now PAUSED. Entering WAIT state.");
                }
                while (paused) {
                    lock.wait();
                    System.out.println(">>> [Thread] RESUMED from WAIT state.");
                }
            }
        }
    }

    static class PrimeGeneratorTask extends GeneratorTask {
        private final int min;
        private final Integer max;

        public PrimeGeneratorTask(int min, Integer max) { this.min = min; this.max = max; }

        @Override
        protected Void call() throws Exception {
            int current = min;
            while (!isCancelled()) {
                checkPause();
                
                // DEBUGGING: Print active work
                System.out.println("[Prime Thread] processing: " + current);

                if (isPrime(current)) updateMessage(String.valueOf(current));
                
                if (max != null) {
                    updateProgress(current - min, max - min);
                    if (current >= max) break;
                } else {
                    updateProgress(-1, 0);
                }
                
                current++;
                Thread.sleep(100);
            }
            return null;
        }

        private boolean isPrime(int num) {
            if (num <= 1) return false;
            for (int i = 2; i <= Math.sqrt(num); i++) if (num % i == 0) return false;
            return true;
        }
    }

    static class FibonacciGeneratorTask extends GeneratorTask {
        private final Long maxVal;

        public FibonacciGeneratorTask(Long maxVal) { this.maxVal = maxVal; }

        @Override
        protected Void call() throws Exception {
            long a = 0, b = 1;
            updateMessage(String.valueOf(a));
            
            while (!isCancelled()) {
                checkPause();
                long next = a + b;
                
                // DEBUGGING: Print active work
                System.out.println("[Fib Thread] processing: " + next);

                if (maxVal != null && next > maxVal) break;
                if (next < 0) break; 

                updateMessage(String.valueOf(next));
                a = b;
                b = next;

                if (maxVal != null) updateProgress(next, maxVal);
                else updateProgress(-1, 0);

                Thread.sleep(200);
            }
            return null;
        }
    }
}