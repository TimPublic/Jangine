package internal.util;


import java.util.ArrayList;


// Is a singleton-class, that logs to the console, as long as it is active.
// There are options for safe logging, which safes the message to a buffer,
// if the logger is not active, to be logged as soon as the logger is active again.

/**
 * Is a singleton class, that logs to the console.
 * <p>
 * The logger cam be deactivated and activated.
 * If it is deactivated, the messages can be saved to a buffer,
 * assuming you use the {@link JangineLogger#logSafe(String)} method.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
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


    // -+- LOGGING-STATE MANAGEMENT -+- //

    /**
     * Sets the state of the logger.
     * Upon setting the state to true,
     * all messages inside the buffer get logged
     * and the buffer gets cleared.
     *
     * @param isLogging new state
     *
     * @author Tim Kloepper
     */
    public void setIsLogging(boolean isLogging) {
        _isLogging = isLogging;

        if (isLogging == true) {
            logBuffer();
        }
    }
    /**
     * Returns the current status of this logger.
     *
     * @return status
     *
     * @author Tim Kloepper
     */
    public boolean isLogging() {
        return _isLogging;
    }


    // -+- BUFFER-LOGIC -+- //

    /**
     * Adds a message to the buffer.
     *
     * @param message
     *
     * @author Tim Kloepper
     */
    public void addToBuffer(String message) {
        _messageBuffer.add(message);
    }
    /**
     * Clears the whole buffer, meaning all messages inside the buffer get lost.
     */
    public void clearBuffer() {
        _messageBuffer.clear();
    }


    // -+- LOGGING-LOGIC -+- //
    /**
     * Tries to print a message.
     * If the logger is not logging,
     * the message is lost.
     *
     * @param message
     *
     * @author Tim Kloepper
     */
    public void log(String message) {
        if (!_isLogging) {return;}

        System.out.println(message);
    }
    /**
     * Tries to print a message.
     * If the logger is not logging,
     * the message gets stored in the buffer.
     *
     * @param message
     *
     * @return success of printing
     *
     * @author Tim Kloepper
     */
    public boolean logSafe(String message) {
        if (!_isLogging) {
            _messageBuffer.add(message);
            return false;
        }

        log(message);

        return true;
    }
    /**
     * Tries to log the messages inside the buffer and clears it.
     * If this logger is currently not logging,
     * the messages are lost.
     *
     * @author Tim Kloepper
     */
    public void logBuffer() {
        for (String message : _messageBuffer) {
            log(message);
        }

        _messageBuffer.clear();
    }
    /**
     * Only logs the messages inside the buffer and clears it,
     * if the logger is logging.
     *
     * @return success of the logging
     *
     * @author Tim Kloepper
     */
    public boolean logBufferSafe() {
        if (!_isLogging) {return false;}

        logBuffer();

        return true;
    }


}