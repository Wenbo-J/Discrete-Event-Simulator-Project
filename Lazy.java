import java.util.function.Supplier;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implements a supplier of a result with caching.
 *
 * @author cs2030
 * @param <T> the type of the result supplied by this cached supplier
 **/
public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private Optional<T> cache;

    private Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
        this.cache = Optional.<T>empty();
    }

    /**
     * Returns a {@code Lazy} as a supplier of a result.
     *
     * @param t the result to be supplied
     * @param <T> the type of the value
     * @return a {@code Lazy} with the result to be supplied
     */
    public static <T> Lazy<T> of(T t) {
        return new Lazy<T>(() -> t);
    }

    /**
     * Returns a {@code Lazy} as a supplier of a result.
     *
     * @param supplier the supplier of the result
     * @param <T> the type of the value
     * @return a {@code Lazy} with the result to be supplied
     * @throws NoSuchElementException if supplier is {@code null}
     */
    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<T>(
            Optional.<Supplier<T>>ofNullable(supplier).orElseThrow());
    }

    /**
     * Returns a {@code Lazy} describing by applying the given mapping
     * function to the value in a lazily evaluated manner.
     *
     * @param mapper the mapping function to apply to a value
     * @param <R> The type of value of the {@code Lazy} returned by the
     *            mapping function
     * @return a {@code Lazy} describing the result of applying a mapping
     *         function to the value of this {@code Lazy}
     */
    public <R> Lazy<R> map(Function<? super T, ? extends R> mapper) {
        return Lazy.<R>of(() -> mapper.apply(this.get()));
    }

    /**
     * Returns a {@code Lazy} describing by applying the given 
     * {@code Lazy}-bearing mapping function to the value in a lazily 
     * evaluated manner.
     *
     * <p>This method is similar to {@link #map(Function)}, but the mapping
     * function is one whose result is already a {@code Lazy}, and if
     * invoked, {@code flatMap} does not wrap it within an additional
     * {@code Lazy}.
     *
     * @param <R> The type of value of the {@code Lazy} returned by the
     *            mapping function
     * @param mapper the mapping function to apply to a value
     * @return the result of applying a {@code Lazy}-bearing mapping
     *         function to the value of this {@code Lazy}
     */
    public <R> Lazy<R> flatMap(Function<? super T, ? extends Lazy<? extends R>> mapper) {
        return Lazy.<R>of(() -> mapper.apply(this.get()).get());
    }

    /**
     * Gets a result.
     *
     * <p>Caches the result the first time the supplier is invoked.
     *
     * @return a result
     */
    @Override
    public T get() {
        return this.cache.orElseGet(() -> {
            T v = this.supplier.get();
            this.cache = Optional.<T>of(v);
            return v;
        });
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Lazy}.
     * The other object is considered equal if:
     * <ul>
     * <li>it is also an {@code Lazy} and;
     * <li>the values are "equal to" each other via {@code equals()}.
     * </ul>
     *
     * @param obj an object to be tested for equality
     * @return {@code true} if the other object is "equal to" this object
     *         otherwise {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Lazy<?> lazy) {
            return this.get().equals(lazy.get());
        } else {
            return false;
        }
    }
}
