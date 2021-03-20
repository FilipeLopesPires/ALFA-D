package Logica;
import java.util.*;

/**
 * The Column class represents a single column. 
 * Like the Table class, it contains a Header and a List for objects to be saved in.
 * This class is very limited compared to the Table class. It is restricted to only one Header (hence being a column) and it's purpose is simply to contain information.
 * To manipulate a column type variable, it is recommended to use a Table instance and keep it's width equal to one.
 */
public class Column {
    
    // ATTRIBUTES
    
    /**
     * Column's header, holding the name of the column and the data type of the variables that the column can hold.
     */
    private Header header;
    /**
     * Column's storage, able to hold variable amounts of data (of the same data type).
     */
    private List column;
    /**
     * Column's width. This variable must at all times be equal to one.
     */
    private final int width = 1;
    /**
     * Column's height. This variable holds the number of tuples present in the column.
     */
    private int height;
    
    // CONSTRUCTORS
    
    /**
     * Constructor of the column with the definition of its header.
     * @param header Column's header.
     */
    public Column(Header header, int line) {
        this.header = header;
        
        switch(header.getType()) {                                              // check if DataTypes are valid
            case Int:
                column = new ArrayList<Integer>();
                break;
            case Re:
                column = new ArrayList<Double>();
                break;
            case Str:
                column = new ArrayList<String>();
                break;
            case Bool:
                column = new ArrayList<Boolean>();
                break;
            case Table:
            case Column:
            case Line:
            default:
                System.err.println("line " + line + " [Increase] Invalid Header type. Unable to complete action.");
                break;
        }
    }
    
    public Column(Header h, List l) {
        header = h;
        column = l;
        height = l.size();
    }
    
    // RENAME
    
    /**
     * Renames the column's header.
     * @param newName Name to replace the header's current name.
     */
    public Column rename(String newName) {
        header.setName(newName);
        return this;
    }
    
    // GETTERS
    
    /**
     * Getter function for the column's header.
     * @return Column's header, holding the name of the column and the data type of the variables that the column can hold.
     */
    public Header getHeader() { return header; }
    /**
     * Getter function for the column's content.
     * @return Column's storage, able to hold variable amounts of data (of the same data type).
     */
    public List getColumn() { return column; }
    
    // TO STRING
    
    @Override
    public String toString() {
        String retval = "";
        retval += header.getName() + "\n\n";
        for(Object o: column) {
            retval += o + "\n";
        }
        return retval;
    }
}
