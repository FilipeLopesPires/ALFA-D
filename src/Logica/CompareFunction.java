package Logica;

/**
 * This interface is used to implement the operation join and remove that are executed according
 * to a condition.
 * This interface is implemented for several Classes that implement the different kinds of comparions
 */
public interface CompareFunction {
    public boolean compare(Object o1, Object o2);
}
