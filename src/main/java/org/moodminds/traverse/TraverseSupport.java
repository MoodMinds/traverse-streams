package org.moodminds.traverse;

import org.moodminds.elemental.Association;
import org.moodminds.elemental.KeyValue;
import org.moodminds.function.*;

import java.util.NoSuchElementException;

/**
 * This interface acts as a potential (if supported) Traverse Source, supplying a {@link Traverser}
 * to the specified traverse function. If traversal is not supported due to the nature of an implementation,
 * it throws a {@link TraverseSupportException}.
 * <p>
 * Traverse Sources can be partitioned or divided into segments, and each segment can be processed
 * by a dedicated {@link Traverser} provided to the specified traverse function. This allows the traverse
 * function to be invoked multiple times with a specific {@link Traverser} for each segment of the Source.
 * <p>
 * The traversal can be performed sequentially, handling each segment one by one, or in parallel using different
 * {@link Thread threads}. Consequently, the specified traverse function must be prepared to handle this parallelism,
 * unless sequential traversal is explicitly performed using the {@link #sequence(Executable1Throwing2, Association) sequence()}
 * method.
 * <p>
 * Sequential traversals run in the same {@link Thread thread} as the caller. Parallel traversal may use the caller
 * {@link Thread thread} for certain segments, while other segments may be handled by dedicated {@link Thread thread} pools
 * or use other techniques to obtain a running {@link Thread thread}.
 * <p>
 * A Traverse Source inherently defines its traversal mode, which can be either sequential or parallel.
 * This preference is engaged by calling the {@link #traverse(Executable1Throwing2, Association) traverse()} method.
 * When a Traverse Source is inherently sequential, it processes segments one after the other, ensuring a sequential
 * approach. In contrast, if it is inherently parallel, traversal is executed concurrently to enhance efficiency.
 * You also have the flexibility to explicitly opt for either sequential or parallel traversal using the
 * {@link #sequence(Executable1Throwing2, Association) sequence()} and {@link #parallel(Executable1Throwing2, Association) parallel()} methods,
 * respectively.
 * <p>
 * Traverse Sources must guarantee that each parallel segment traversal strictly adheres to the principles of
 * <a href="/java/util/concurrent/package-summary.html#MemoryVisibility"><i>happens-before</i></a> memory visibility.
 * This ensures that memory writes from parallel threads are properly flushed and made visible upon the completion
 * of the overall Source traversal.
 * <p>
 * Please be aware that not all Traverse Sources can support parallel traversal. In such cases, invoking the
 * {@link #parallel(Executable1Throwing2, Association) parallel()} method will still result in sequential segment
 * traversal which is performed in the caller {@link Thread thread}.
 * <p>
 * In addition to the traverse function, traverse methods accept an {@link Association} or {@link KeyValue} varargs
 * Context arguments, which can be provided to propagate contextual information during the traversal process.
 * <p>
 * A Traverse Source should implement a short-circuiting mechanism to immediately stop the traversal of other segments
 * (and their elements) if any segment is incompletely traversed, whether in a sequential or parallel context. This can
 * be achieved by ceasing all {@link Traverser Traversers} from providing items. To initiate a short-circuit in a Traverse
 * Source, stop consuming items from the {@link Traverser} within the traverse function while traversing a segment.
 * <p>
 * As a result of a Source traversal, a boolean completion flag is returned,
 * signifying whether the Source was entirely traversed or not.
 *
 * @param <V> the type of item values
 * @param <E> the type of potential exceptions
 */
public interface TraverseSupport<V, E extends Exception> {

    /**
     * Try to traverse the Source explicitly sequentially with the given {@link Executable1Throwing2} segment traverse
     * function and {@link KeyValue key-value} varargs context.
     * <p>
     * The specified {@link KeyValue key-values} must not contain {@code null} keys or values to avoid {@link NullPointerException}.
     *
     * @param traverse the given {@link Executable1Throwing2} segment traverse function
     * @param ctx the given {@link KeyValue} varargs context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link KeyValue key-values}
     * have {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception> boolean sequence(Executable1Throwing2<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2> traverse, KeyValue<?, ?>... ctx) throws E, H1, H2;

    /**
     * Try to traverse the Source explicitly sequentially with the given {@link Executable1Throwing2} segment traverse function
     * and {@link Association} context.
     * <p>
     * The specified {@link Association} must contain non-null keys and values, and its {@link Association#get(Object)}
     * method should raise a {@link NoSuchElementException} instead of returning {@code null} when no value is associated
     * with a key.
     *
     * @param traverse the given {@link Executable1Throwing2} segment traverse function
     * @param ctx the given {@link Association} context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link Association}
     * contains {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception> boolean sequence(Executable1Throwing2<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2> traverse, Association<Object, Object, ?> ctx) throws E, H1, H2;

    /**
     * Try to traverse the Source either sequentially or in parallel depending on the Source mode with the given
     * {@link Executable1Throwing2} segment traverse function and {@link KeyValue key-value} varargs context.
     * <p>
     * The specified {@link KeyValue key-values} must not contain {@code null} keys or values to avoid {@link NullPointerException}.
     *
     * @param traverse the given {@link Executable1Throwing2} segment traverse function
     * @param ctx the given {@link KeyValue} varargs context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link KeyValue key-values}
     * have {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception> boolean traverse(Executable1Throwing2<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2> traverse, KeyValue<?, ?>... ctx) throws E, H1, H2;

    /**
     * Try to traverse the Source either sequentially or in parallel depending on the Source mode with the given
     * {@link Executable1Throwing2} segment traverse function and {@link Association} context.
     * <p>
     * The specified {@link Association} must contain non-null keys and values, and its {@link Association#get(Object)}
     * method should raise a {@link NoSuchElementException} instead of returning {@code null} when no value is associated
     * with a key.
     *
     * @param traverse the given {@link Executable1Throwing2} segment traverse function
     * @param ctx the given {@link Association} context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link Association}
     * contains {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception> boolean traverse(Executable1Throwing2<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2> traverse, Association<Object, Object, ?> ctx) throws E, H1, H2;

    /**
     * Try to traverse the Source explicitly in parallel (if the Source does support parallelism or sequentially,
     * otherwise) with the given {@link Executable1Throwing2} segment traverse function and {@link KeyValue key-value} varargs
     * context.
     * <p>
     * The specified {@link KeyValue key-values} must not contain {@code null} keys or values to avoid {@link NullPointerException}.
     *
     * @param traverse the given {@link Executable1Throwing2} segment traverse function
     * @param ctx the given {@link KeyValue} varargs context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link KeyValue key-values}
     * have {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception> boolean parallel(Executable1Throwing2<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2> traverse, KeyValue<?, ?>... ctx) throws E, H1, H2;

    /**
     * Try to traverse the Source explicitly in parallel (if the Source does support parallelism or sequentially,
     * otherwise) with the given {@link Executable1Throwing2} segment traverse function and {@link Association} context.
     * <p>
     * The specified {@link Association} must contain non-null keys and values, and its {@link Association#get(Object)}
     * method should raise a {@link NoSuchElementException} instead of returning {@code null} when no value is associated
     * with a key.
     *
     * @param traverse the given {@link Executable1Throwing2} segment traverse function
     * @param ctx the given {@link Association} context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link Association}
     * contains {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception> boolean parallel(Executable1Throwing2<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2> traverse, Association<Object, Object, ?> ctx) throws E, H1, H2;


    /**
     * Try to traverse the Source explicitly sequentially with the given {@link Executable1Throwing3} segment traverse
     * function and {@link KeyValue key-value} varargs context.
     * <p>
     * The specified {@link KeyValue key-values} must not contain {@code null} keys or values to avoid {@link NullPointerException}.
     *
     * @param traverse the given {@link Executable1Throwing3} segment traverse function
     * @param ctx the given {@link KeyValue} varargs context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @param <H3> the possible exception 3 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws H2 in the case of traverse function error 3
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link KeyValue key-values}
     * have {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean sequence(Executable1Throwing3<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2, ? extends H3> traverse, KeyValue<?, ?>... ctx) throws E, H1, H2, H3;

    /**
     * Try to traverse the Source explicitly sequentially with the given {@link Executable1Throwing3} segment traverse function
     * and {@link Association} context.
     * <p>
     * The specified {@link Association} must contain non-null keys and values, and its {@link Association#get(Object)}
     * method should raise a {@link NoSuchElementException} instead of returning {@code null} when no value is associated
     * with a key.
     *
     * @param traverse the given {@link Executable1Throwing3} segment traverse function
     * @param ctx the given {@link Association} context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @param <H3> the possible exception 3 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws H2 in the case of traverse function error 3
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link Association}
     * contains {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean sequence(Executable1Throwing3<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2, ? extends H3> traverse, Association<Object, Object, ?> ctx) throws E, H1, H2, H3;

    /**
     * Try to traverse the Source either sequentially or in parallel depending on the Source mode with the given
     * {@link Executable1Throwing3} segment traverse function and {@link KeyValue key-value} varargs context.
     * <p>
     * The specified {@link KeyValue key-values} must not contain {@code null} keys or values to avoid {@link NullPointerException}.
     *
     * @param traverse the given {@link Executable1Throwing3} segment traverse function
     * @param ctx the given {@link KeyValue} varargs context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @param <H3> the possible exception 3 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws H2 in the case of traverse function error 3
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link KeyValue key-values}
     * have {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean traverse(Executable1Throwing3<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2, ? extends H3> traverse, KeyValue<?, ?>... ctx) throws E, H1, H2, H3;

    /**
     * Try to traverse the Source either sequentially or in parallel depending on the Source mode with the given
     * {@link Executable1Throwing3} segment traverse function and {@link Association} context.
     * <p>
     * The specified {@link Association} must contain non-null keys and values, and its {@link Association#get(Object)}
     * method should raise a {@link NoSuchElementException} instead of returning {@code null} when no value is associated
     * with a key.
     *
     * @param traverse the given {@link Executable1Throwing3} segment traverse function
     * @param ctx the given {@link Association} context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @param <H3> the possible exception 3 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws H2 in the case of traverse function error 3
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link Association}
     * contains {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean traverse(Executable1Throwing3<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2, ? extends H3> traverse, Association<Object, Object, ?> ctx) throws E, H1, H2, H3;

    /**
     * Try to traverse the Source explicitly in parallel (if the Source does support parallelism or sequentially,
     * otherwise) with the given {@link Executable1Throwing3} segment traverse function and {@link KeyValue key-value} varargs
     * context.
     * <p>
     * The specified {@link KeyValue key-values} must not contain {@code null} keys or values to avoid {@link NullPointerException}.
     *
     * @param traverse the given {@link Executable1Throwing3} segment traverse function
     * @param ctx the given {@link KeyValue} varargs context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @param <H3> the possible exception 3 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws H2 in the case of traverse function error 3
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link KeyValue key-values}
     * have {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean parallel(Executable1Throwing3<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2, ? extends H3> traverse, KeyValue<?, ?>... ctx) throws E, H1, H2, H3;

    /**
     * Try to traverse the Source explicitly in parallel (if the Source does support parallelism or sequentially,
     * otherwise) with the given {@link Executable1Throwing3} segment traverse function and {@link Association} context.
     * <p>
     * The specified {@link Association} must contain non-null keys and values, and its {@link Association#get(Object)}
     * method should raise a {@link NoSuchElementException} instead of returning {@code null} when no value is associated
     * with a key.
     *
     * @param traverse the given {@link Executable1Throwing3} segment traverse function
     * @param ctx the given {@link Association} context
     * @return the completion flag indicating either the Source was completely traversed, or not
     * @param <H1> the possible exception 1 type throwing by the traverse function
     * @param <H2> the possible exception 2 type throwing by the traverse function
     * @param <H3> the possible exception 3 type throwing by the traverse function
     * @throws E in case of the traversal error
     * @throws H1 in the case of traverse function error 1
     * @throws H2 in the case of traverse function error 2
     * @throws H2 in the case of traverse function error 3
     * @throws NullPointerException if the specified traverse function is {@code null} or {@link Association}
     * contains {@code null} keys or values
     * @throws TraverseSupportException if traversal is not supported
     */
    <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean parallel(Executable1Throwing3<? super Traverser<? extends V, ? extends E>, ? extends H1, ? extends H2, ? extends H3> traverse, Association<Object, Object, ?> ctx) throws E, H1, H2, H3;


    /**
     * An object for traversing a segment of a {@link TraverseSupport} Source.
     * <p>
     * A Traverser provides the capability to traverse elements individually using the
     * {@link #next(Executable1Throwing1) next()} method, in bulk sequentially with the
     * {@link #each(Executable1Throwing1) each()} method, or in portions using both the
     * {@link #next(long, Executable1Throwing1) next(number)} and {@link #some(Testable1Throwing1) some()}
     * methods.
     *
     * @param <V> the type of item values
     * @param <E> the type of potential exceptions
     */
    interface Traverser<V, E extends Exception> {

        /**
         * Process a remaining element using the provided {@link Executable1Throwing1} consumer.
         * Return {@code true} if the operation is successful; otherwise, return {@code false}.
         * Any exceptions raised by the consumer are passed on to the caller.
         *
         * @param consumer the given {@link Executable1Throwing1} consumer
         * @return {@code false} if no remaining elements existed, else {@code true}
         * @param <H> the possible exception type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H in case of the consumption error
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H extends Exception> boolean next(Executable1Throwing1<? super V, ? extends H> consumer) throws E, H;

        /**
         * Process the specified number of remaining elements using the provided {@link Executable1Throwing1}
         * consumer. Return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise.
         * Any exceptions raised by the consumer are propagated to the caller.
         *
         * @param number the requested number of elements to consume
         * @param consumer the given {@link Executable1Throwing1} consumer
         * @return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise
         * @param <H> the possible exception type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H in case of the consumption error
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H extends Exception> boolean next(long number, Executable1Throwing1<? super V, ? extends H> consumer) throws E, H;

        /**
         * Process some of the remaining elements with the specified {@link Testable1Throwing1} consumer.
         * The consumer provides a signal indicating whether it anticipates the next remaining element or not.
         * Return {@code true} if the requested demand has been fully met, or {@code false} otherwise. Any exceptions
         * raised by the consumer are conveyed to the caller.
         *
         * @param consumer the given {@link Testable1Throwing1} consumer
         * @return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise
         * @param <H> the possible exception type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H in case of the consumption error
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H extends Exception> boolean some(Testable1Throwing1<? super V, ? extends H> consumer) throws E, H;

        /**
         * Process all remaining elements using the provided {@link Executable1Throwing1} consumer.
         * Any exceptions raised by the consumer are passed on to the caller.
         *
         * @param consumer the given {@link Executable1Throwing1} consumer
         * @param <H> the possible exception type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H in case of the consumption error
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H extends Exception> void each(Executable1Throwing1<? super V, ? extends H> consumer) throws E, H;


        /**
         * Process a remaining element using the provided {@link Executable1Throwing2} consumer.
         * Return {@code true} if the operation is successful; otherwise, return {@code false}.
         * Any exceptions raised by the consumer are passed on to the caller.
         *
         * @param consumer the given {@link Executable1Throwing2} consumer
         * @return {@code false} if no remaining elements existed, else {@code true}
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception> boolean next(Executable1Throwing2<? super V, ? extends H1, ? extends H2> consumer) throws E, H1, H2;

        /**
         * Process the specified number of remaining elements using the provided {@link Executable1Throwing2}
         * consumer. Return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise.
         * Any exceptions raised by the consumer are propagated to the caller.
         *
         * @param number the requested number of elements to consume
         * @param consumer the given {@link Executable1Throwing2} consumer
         * @return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception> boolean next(long number, Executable1Throwing2<? super V, ? extends H1, ? extends H2> consumer) throws E, H1, H2;

        /**
         * Process some of the remaining elements with the specified {@link Testable1Throwing2} consumer.
         * The consumer provides a signal indicating whether it anticipates the next remaining element or not.
         * Return {@code true} if the requested demand has been fully met, or {@code false} otherwise. Any exceptions
         * raised by the consumer are conveyed to the caller.
         *
         * @param consumer the given {@link Testable1Throwing2} consumer
         * @return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception> boolean some(Testable1Throwing2<? super V, ? extends H1, ? extends H2> consumer) throws E, H1, H2;

        /**
         * Process all remaining elements using the provided {@link Executable1Throwing2} consumer.
         * Any exceptions raised by the consumer are passed on to the caller.
         *
         * @param consumer the given {@link Executable1Throwing2} consumer
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception> void each(Executable1Throwing2<? super V, ? extends H1, ? extends H2> consumer) throws E, H1, H2;


        /**
         * Process a remaining element using the provided {@link Executable1Throwing3} consumer.
         * Return {@code true} if the operation is successful; otherwise, return {@code false}.
         * Any exceptions raised by the consumer are passed on to the caller.
         *
         * @param consumer the given {@link Executable1Throwing3} consumer
         * @return {@code false} if no remaining elements existed, else {@code true}
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @param <H3> the possible exception 3 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws H3 in case of consumption error 3
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean next(Executable1Throwing3<? super V, ? extends H1, ? extends H2, ? extends H3> consumer) throws E, H1, H2, H3;

        /**
         * Process the specified number of remaining elements using the provided {@link Executable1Throwing3}
         * consumer. Return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise.
         * Any exceptions raised by the consumer are propagated to the caller.
         *
         * @param number the requested number of elements to consume
         * @param consumer the given {@link Executable1Throwing3} consumer
         * @return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @param <H3> the possible exception 3 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws H3 in case of consumption error 3
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean next(long number, Executable1Throwing3<? super V, ? extends H1, ? extends H2, ? extends H3> consumer) throws E, H1, H2, H3;

        /**
         * Process some of the remaining elements with the specified {@link Testable1Throwing3} consumer.
         * The consumer provides a signal indicating whether it anticipates the next remaining element or not.
         * Return {@code true} if the requested demand has been fully met, or {@code false} otherwise. Any exceptions
         * raised by the consumer are conveyed to the caller.
         *
         * @param consumer the given {@link Testable1Throwing3} consumer
         * @return {@code true} if the requested demand was entirely satisfied, or {@code false} otherwise
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @param <H3> the possible exception 3 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws H3 in case of consumption error 3
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception, H3 extends Exception> boolean some(Testable1Throwing3<? super V, ? extends H1, ? extends H2, ? extends H3> consumer) throws E, H1, H2, H3;

        /**
         * Process all remaining elements using the provided {@link Executable1Throwing3} consumer.
         * Any exceptions raised by the consumer are passed on to the caller.
         *
         * @param consumer the given {@link Executable1Throwing3} consumer
         * @param <H1> the possible exception 1 type throwing by the consumer
         * @param <H2> the possible exception 2 type throwing by the consumer
         * @param <H3> the possible exception 3 type throwing by the consumer
         * @throws E in case of the traversal error
         * @throws H1 in case of consumption error 1
         * @throws H2 in case of consumption error 2
         * @throws H3 in case of consumption error 3
         * @throws NullPointerException if the specified consumer is {@code null}
         */
        <H1 extends Exception, H2 extends Exception, H3 extends Exception> void each(Executable1Throwing3<? super V, ? extends H1, ? extends H2, ? extends H3> consumer) throws E, H1, H2, H3;
    }
}
