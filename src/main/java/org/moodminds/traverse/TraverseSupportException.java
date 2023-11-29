package org.moodminds.traverse;

/**
 * An exception to indicate that traversal is not supported in a particular {@link TraverseSupport}.
 */
public class TraverseSupportException extends RuntimeException {

    private static final long serialVersionUID = 2894294301798450286L;

    /**
     * Construct the object with the given message string.
     *
     * @param message the given message string
     */
    public TraverseSupportException(String message) {
        super(message);
    }
}
