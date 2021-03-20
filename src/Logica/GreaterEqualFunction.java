package Logica;

public class GreaterEqualFunction implements CompareFunction {

    @Override
    public boolean compare(Object o1, Object o2) {
        if (o1.getClass().getSimpleName().equals("Integer")
         && o2.getClass().getSimpleName().equals("Integer")) {
            return ((int) o1) >= ((int) o2);
        }
        else if (o1.getClass().getSimpleName().equals("Double")
              && o2.getClass().getSimpleName().equals("Double")) {
            return ((double) o1) >= ((double) o2);
        }
        else if (o1.getClass().getSimpleName().equals("Integer")
              && o2.getClass().getSimpleName().equals("Double")) {
            return ((int) o1) >= ((double) o2);
        }
        else if (o1.getClass().getSimpleName().equals("Double")
              && o2.getClass().getSimpleName().equals("Integer")) {
            return ((double) o1) >= ((int) o2);
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
