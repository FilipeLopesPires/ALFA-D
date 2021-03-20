package Logica;

public class EqualFunction implements CompareFunction {

    @Override
    public boolean compare(Object o1, Object o2) {
        return o1.equals(o2);
    }

}
