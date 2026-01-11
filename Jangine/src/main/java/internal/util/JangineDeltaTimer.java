package internal.util;


/**
 * The delta timer is a singleton, providing the delta time,
 * meaning the time between the last {@link JangineDeltaTimer#update()} call and now.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class JangineDeltaTimer {
    private long _runtime;


    private static JangineDeltaTimer _instance;


    private JangineDeltaTimer() {

    }

    /**
     * Returns the singleton instance of the delta timer.
     *
     * @return delta timer
     *
     * @author Tim Kloepper
     */
    public static JangineDeltaTimer get() {
        if (_instance == null) {
            _instance = new JangineDeltaTimer();
        }

        return _instance;
    }


    // -+- UPDATE LOOP -+- //

    /**
     * Should be called every frame, in order to keep track of the time,
     * the {@link internal.main.JangineEngine} is already running.
     *
     * @author Tim Kloepper
     */
    public void update() {
        _runtime += System.nanoTime();
    }


    // -+- DELTA TIME LOGIC -+- //

    /**
     * Returns the time passed, after the last frame or {@link JangineDeltaTimer#update()} call,
     * in seconds.
     *
     * @return seconds passed since last frame
     *
     * @author Tim Kloepper
     */
    public double getDeltaTime() {
        long currentTime;
        long timeDifference;

        currentTime = System.nanoTime();
        timeDifference = currentTime - _runtime;

        _runtime += System.nanoTime(); // Just get it again, as we computed something, after the last call, meaning the nano time increased.

        return timeDifference / 10e9; // Conversion from nanoseconds to seconds.
    }
}