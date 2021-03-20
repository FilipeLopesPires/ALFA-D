package Logica;

/**
 * The Header class represents the header of a table's column.
 * It contains a name and the data type of the content that the associated column can hold.
 */
public class Header implements Comparable<Header>{
    /**
     * Data type of the variables accepted by the column that is going to hold this header.
     */
    private DataTypes type;
    /**
     * Name of the column that is going to hold this header.
     */
    private String name;

    /**
     * Constructor of the Header class. Here, the data type and the name are defined.
     * @param type Data type of the variables accepted by the column that is going to hold this header.
     * @param name Name of the column that is going to hold this header.
     */
    public Header(DataTypes type, String name) {
        this.name = name;
        this.type = type;
    }

    /**
     * Getter function of the header's data type.
     * @return Data type of the variables accepted by the column that holds this header.
     */
    public DataTypes getType() {
        return type;
    }

    /**
     * Getter function of the header's name.
     * @return Name of the column that holds this header.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets a new name for the header.
     * @param newName Replacing name.
     */
    public void setName(String newName) {
        name = newName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Header other = (Header) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return name + ":" + type.toString();
    }

    @Override
    public int compareTo(Header o) {
        if(this.name.equals(o.name)) {
            return 0;
        }
        return 1;
    }
}
