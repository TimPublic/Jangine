package internal.util;


import internal.main.Engine;

/**
 * The delta timer is a singleton, providing the delta time,
 * meaning the time between the last {@link DeltaTimer#update()} call and now.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class DeltaTimer {
    private long _runtime;


    private static DeltaTimer _instance;


    private DeltaTimer() {

    }

    /**
     * Returns the singleton instance of the delta timer.
     *
     * @return delta timer
     *
     * @author Tim Kloepper
     */
    public static DeltaTimer get() {
        if (_instance == null) {
            _instance = new DeltaTimer();
        }

        return _instance;
    }


    // -+- UPDATE LOOP -+- //

    /**
     * Should be called every frame, in order to keep track of the time,
     * the {@link Engine} is already running.
     *
     * @author Tim Kloepper
     */
    public void update() {
        _runtime = System.nanoTime();
    }


    // -+- DELTA TIME LOGIC -+- //

    /**
     * Returns the time passed, after the last frame or {@link DeltaTimer#update()} call,
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