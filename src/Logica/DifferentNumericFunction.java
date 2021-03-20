package Logica;

public class DifferentNumericFunction implements CompareFunction {

    @Override
    public boolean compare(Object o1, Object o2) {
        if (o1.getClass().getSimpleName().equals(o2.getClass().getSimpleName())) {
            return !o1.equals(o2);
        }
        else if (o1.getClass().getSimpleName().equals("Integer")
              && o2.getClass().getSimpleName().equals("Double")) {
            Integer aInt = (Integer) o1;
            Double a = new Double((double) aInt);
            return !a.equals(o2);
        }
        else if (o1.getClass().getSimpleName().equals("Double")
              && o2.getClass().getSimpleName().equals("Integer")) {
            Integer bInt = (Integer) o2;
            Double b = new Double((double) bInt);
            return !o1.equals(b);
        }
        else {
            /**
             * The code never reaches this point because
             * - Tables can't have other type other than Integer, Double, String or Boolean);
             * - This classes is instanciated according to the types used
             */
            System.err.println("Invalid Types");
            System.exit(1);
            return false;
        }
    }

}
