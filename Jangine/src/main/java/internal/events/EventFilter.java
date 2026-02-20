package internal.events;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.function.Function;


/**
 * Contains two layers, used to verify any {@link I_Event}. <br>
 * The first layer consists of implementation classes of the event class, being the 'interests'.
 * Only events of the specified classes then pass the verification.
 * It is important to note, that if this list is empty, ANY event will pass that first layer.
 * <br><br>
 * The second layer consists of {@link Function} methods, being the 'callbacks', which take in any event and return a boolean.
 * The event also needs to pass all of those methods, in order to be verified.
 *
 * @author Tim Kloepper
 * @version 1.0
 */
public class EventFilter {


    // -+- CREATION -+- //

    public EventFilter() {
        _INTERESTS = new HashSet<>();
        _CALLBACKS = new HashSet<>();

        _CACHE = new IdentityHashMap<>();
    }


    // -+- PARAMETERS -+- //

    // FINALS //

    /**
     * A simple {@link HashSet}, containing the interests,
     * meaning the subclasses of {@link I_Event}, which are valid to pass the interests
     * layer.
     */
    private final HashSet<Class<? extends I_Event>> _INTERESTS;
    /**
     * A simple {@link HashSet}, containing the {@link Function} methods,
     * which any event muss pass, in order to be validated, by this filter.
     */
    private final HashSet<Function<I_Event, Boolean>> _CALLBACKS;

    /**
     * A cache containing events which have already been checked and
     * the result of that check, in order to make verification faster.
     * <br><br>
     * This cache gets cleared as soon as the interests decrease or the callback increase.
     */
    private final IdentityHashMap<I_Event, Boolean> _CACHE;


    // -+- INTERESTS MANAGEMENT -+- //

    /**
     * Adds an interest to the filter.
     *
     * @param interest Subclass of the {@link I_Event} class.
     *
     * @author Tim Kloepper
     */
    public void addInterest(Class<? extends I_Event> interest) {
        if (interest == null) return;

        _INTERESTS.add(interest);
    }

    /**
     * Wraps the {@link EventFilter#addInterest(Class)} method and provides the ability,
     * to add multiple interests at once.
     *
     * @param interests A {@link Collection} of interests, that should be added to this filter.
     */
    public void addInterests(Collection<Class<? extends I_Event>> interests) {
        if (interests == null) return;

        interests.forEach(this::addInterest);
    }

    public void rmvInterest(Class<? extends I_Event> interest) {
        if (interest == null) return;

        _CACHE.clear();

        _INTERESTS.remove(interest);
    }
    public void rmvInterests(Collection<Class<? extends I_Event>> interests) {
        if (interests == null) return;

        _CACHE.clear();

        interests.forEach(this::rmvInterest);
    }

    public void clearInterests() {
        _INTERESTS.clear();

        _CACHE.clear();
    }

    public HashSet<Class<? extends I_Event>> getInterests() {
        return new HashSet<>(_INTERESTS);
    }

    public boolean contains(Class<? extends I_Event> interest) {
        return _INTERESTS.contains(interest);
    }


    // -+- CALLBACK MANAGEMENT -+- //

    public void addCallback(Function<I_Event, Boolean> callback) {
        if (callback == null) return;

        _CALLBACKS.add(callback);

        _CACHE.clear();
    }
    public void addCallbacks(Collection<Function<I_Event, Boolean>> callbacks) {
        if (callbacks == null) return;

        callbacks.forEach(this::addCallback);
    }

    public void rmvCallback(Function<I_Event, Boolean> callback) {
        _CALLBACKS.remove(callback);
    }
    public void rmvCallbacks(Collection<Function<I_Event, Boolean>> callbacks) {
        callbacks.forEach(this::rmvCallback);
    }

    public void clearCallbacks() {
        _CALLBACKS.clear();
    }

    public boolean contains(Function<I_Event, Boolean> callback) {
        return _CALLBACKS.contains(callback);
    }


    // -+- FILTER LOGIC -+- //

    public boolean check(I_Event event) {
        Boolean cacheResult;

        cacheResult = _CACHE.get(event);
        if (cacheResult != null) return cacheResult;

        if (!_INTERESTS.isEmpty()) {
            if (!_INTERESTS.contains(event.getClass())) {
                _CACHE.put(event, false);

                return false;
            }
        }

        for (Function<I_Event, Boolean> callback : _CALLBACKS) {
            if (!callback.apply(event)) {
                _CACHE.put(event, false);

                return false;
            }
        }

        _CACHE.put(event, true);

        return true;
    }
    public ArrayList<I_Event> check(Collection<I_Event> events) {
        ArrayList<I_Event> validEvents;

        validEvents = new ArrayList<>();

        for (I_Event event : events) {
            if (!check(event)) continue;

            validEvents.add(event);
        }

        return validEvents;
    }

    public void clear() {
        clearInterests();
        clearCallbacks();

        _CACHE.clear();
    }


}