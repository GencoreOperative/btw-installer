/*
 * Copyright 2018 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package uk.co.gencoreoperative.btw.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provides a simple signalling view on a per class basis which allows registered
 * listeners to be notified when items are added or removed from this {@link Context}.
 *
 * The {@link Context} allows a caller to register their interest in the presence or
 * removal of a specific class from the context. When this instance is added or removed,
 * all registered listeners will be notified of the change.
 */
public class Context extends Observable {
    private final Map<Class, AtomicReference> values = new HashMap<>();
    private final Map<Class, List<Observer>> actionsMap = new HashMap<>();

    /**
     * Allows a caller to register their interest in being notified about changes to the
     * context when items are added.
     *
     * @param classOfInterest
     * @param action
     */
    public synchronized void register(Class classOfInterest, Observer action) {
        if (!actionsMap.containsKey(classOfInterest)) {
            actionsMap.put(classOfInterest, new ArrayList<>());
        }
        actionsMap.get(classOfInterest).add(action);

        // Ensure values map is initialised for this class
        if (!values.containsKey(classOfInterest)) {
            values.put(classOfInterest, new AtomicReference());
        }
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg);
    }

    /**
     * Add a value to the context, which will signal all registered {@link Observer}
     * of the change for that particular class of value.
     *
     * @param value Non null value to add to the context.
     * @param <T> The type of the value being added.
     */
    @SuppressWarnings("unchecked")
    public <T> void add(T value) {
        Class<?> aClass = value.getClass();
        if (!actionsMap.containsKey(aClass)) return;

        values.get(aClass).set(value);
        actionsMap.get(aClass).forEach(o -> o.update(Context.this, value));
    }

    /**
     * Removes a value from the {@link Context}. All registered listeners
     * will be signalled with a {@code null} signal to indicate that the element
     * has been removed.
     *
     * @param value
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> void remove(T value) {
        Class<?> aClass = value.getClass();
        if (!actionsMap.containsKey(aClass)) return;

        values.get(aClass).set(null);
        actionsMap.get(aClass).forEach(o -> o.update(Context.this, null));
    }

    /**
     * Retrieves the value stored in this Context.
     *
     * @param classOfInterest The class of the object to return.
     * @param <T> The type of the class that will correspond to the return type.
     * @return Null if the value is not stored, otherwise non null.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> classOfInterest) {
        if (!actionsMap.containsKey(classOfInterest)) return null;
        return (T) values.get(classOfInterest).get();
    }

    /**
     * @param classOfInterest Non null class.
     * @return True if the context contains a value that corresponds to the requested class.
     */
    public boolean contains(Class classOfInterest) {
        return values.containsKey(classOfInterest) && values.get(classOfInterest).get() != null;
    }

    /**
     * @param classOfInterest An array of classes to check.
     * @return True only if all requested classes have a value present in the context.
     */
    public boolean contains(Class ... classOfInterest) {
        return Arrays.stream(classOfInterest).allMatch(this::contains);
    }
}
