package Logica;
public enum CompareOperations {
    EQUAL, DIFFERENT, GREATER, LESS, GRETER_EQUAL, LESS_EQUAL;
    
    @Override
    public String toString() {
        switch (this) {
            case EQUAL:
                return "==";
            case DIFFERENT:
                return "!=";
            case GREATER:
                return ">";
            case GRETER_EQUAL:
                return ">=";
            case LESS:
                return "<";
            case LESS_EQUAL:
                return "<=";
            default:
                throw new IllegalArgumentException();
        }
    }
}
