package Logica;
public enum DataTypes {
    Int, Str, Re, Bool, Table, Column, Line;

    @Override
    public String toString() {
        switch (this) {
            case Re:
                return "real";
            case Int:
                return "int";
            case Str:
                return "String";
            case Bool:
                return "bool";
            case Table:
                return "table";
            case Column:
                return "column";
            case Line:
                return "line";
        }
        return ""; //Program never reaches this return
    }

    public boolean accept(DataTypes dt) {
        if (this == DataTypes.Re)
            return dt == Re || dt == Int;
        return this == dt;
    }
}
