package internal.util;

import java.util.ArrayList;

public class JangineLogger {


    private boolean _isLogging;
    private ArrayList<String> _messageBuffer;


    private static JangineLogger _instance;


    private JangineLogger() {
        _isLogging = true;
        _messageBuffer = new ArrayList<>();
    }

    public static JangineLogger get() {
        if (_instance == null) {
            _instance = new JangineLogger();
        }

        return _instance;
    }


    // Sets the state of the logger -> Should log, or not.
    public void setIsLogging(boolean isLogging) {
        _isLogging = isLogging;
    }
    // Tells, if the logger currently logs or not.
    public boolean isLogging() {
        return _isLogging;
    }

    // Adds a message to the message-buffer.
    public void addToBuffer(String message) {
        _messageBuffer.add(message);
    }
    // Clears the whole message-buffer.
    public void clearBuffer() {
        _messageBuffer.clear();
    }

    // Tries to print a message.
    // If the logger is not logging,
    // the message is lost.
    public void log(String message) {
        if (!_isLogging) {return;}

        System.out.println(message);
    }
    // Tries to print a message.
    // If the logger is not logging,
    // the message gets stored in the message-buffer.
    // Also returns printing success.
    public boolean logSafe(String message) {
        if (!_isLogging) {
            _messageBuffer.add(message);
            return false;
        }

        log(message);

        return true;
    }

    // Tries to log the messages inside the buffer and
    // clears it.
    // Does not concern with the state of the logger.
    public void logBuffer() {
        for (String message : _messageBuffer) {
            log(message);
        }

        _messageBuffer.clear();
    }
    // Only logs the messages inside the buffer and clears it,
    // when the logger is logging.
    // Also returns logging success.
    public boolean logBufferSafe() {
        if (!_isLogging) {return false;}

        logBuffer();

        return true;
    }


}