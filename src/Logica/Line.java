package Logica;
import java.util.*;

/**
 * Line class - Line is a Table with only one row.
 * 
 *
 */
public class Line {

	// ATRIBUTES

	private Map<Header, Object> line;
	//private Map<Header, Function> functions;
	private LinkedHashMap<Header, Class> columnTypes;
	private int width;
	private final int height = 1;

	// CONSTRUCTORS

	/**
	 * 
	 * @param line,
	 *            Map with Headers that are keys and Objects are the values which
	 *            corresponds to each element
	 */
	public Line(Map<Header, Object> lineMap, int line) {
		this.line = lineMap;
		//functions = new LinkedHashMap<>();
		columnTypes = new LinkedHashMap<>();
		for (Header h : lineMap.keySet()) {
            switch (h.getType()) {
                case Bool:
                    columnTypes.put(h, Boolean.class);
                    break;
                case Int:
                    columnTypes.put(h, Integer.class);
                    break;
                case Re:
                    columnTypes.put(h, Double.class);
                    break;
                case Str:
                    columnTypes.put(h, String.class);
                    break;
                case Table:
                case Line:
                case Column:
                    System.err.println("line " + line + " [newLine] Type " + h.getType() + " can't be used in Lines");
                    System.exit(1);
            }
        }
		width = lineMap.keySet().size();
	}
	
	public Line(Object... objs) {
        line = new LinkedHashMap<>();
        columnTypes = new LinkedHashMap<>();
        int i = 1;
        for (Object o : objs) {
            switch (o.getClass().getSimpleName()) {
                case "Boolean":
                    line.put(new Header(DataTypes.Bool, "header" + i), o);
                    columnTypes.put(new Header(DataTypes.Bool, "header" + i), o.getClass());
                    break;
                case "Integer":
                    line.put(new Header(DataTypes.Int, "header" + i), o);
                    columnTypes.put(new Header(DataTypes.Int, "header" + i), o.getClass());
                    break;
                case "Double":
                    line.put(new Header(DataTypes.Re, "header" + i), o);
                    columnTypes.put(new Header(DataTypes.Re, "header" + i), o.getClass());
                    break;
                case "String":
                    line.put(new Header(DataTypes.Str, "header" + i), o);
                    columnTypes.put(new Header(DataTypes.Str, "header" + i), o.getClass());
                    break;
            }
            i+=1;
        }
        width=objs.length;
    }

	// GETTERS
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * 
	 * @return map with Column Types
	 */
	public LinkedHashMap<Header, Class> getColumnTypes() {
		return columnTypes;
	}

	// get line
	/**
	 * 
	 * @return the map with the headers and elements
	 */
	public Map<Header, Object> getLine() {
		return this.line;
	}

	// TO STRING

	// formated string with the table visualy well structured
	@Override
	public String toString() {

		int[] spaces = new int[width]; // Max spaces occupied by each column
		// Check header lenght
		{
			Iterator<Header> it = line.keySet().iterator();
			for (int i = 0; i < width; i++) {
				spaces[i] = it.next().getName().length();
			}
		}
		// Check columns length
		{
			Iterator<Object> it = line.values().iterator();
			for (int i = 0; i < width; i++) {
				Object o = it.next();
				if (o == null) {
					if (spaces[i] < 4)
						spaces[i] = 4;
					else
						continue;

				}
				if (o.toString().length() > spaces[i])
					spaces[i] = o.toString().length();
			}
		}

		StringBuilder retval = new StringBuilder("|");
		// Write the content of the headers
		int iSpaces = 0;
		for (Header h : line.keySet()) // add header names
			retval.append(String.format("%" + spaces[iSpaces++] + "s|", h.getName()));
		retval.append("\n");

		// Add line separator between headers and columns
		for (int space : spaces)
			for (int i = 0; i < space; i++)
				retval.append("-");
		for (int i = 0; i < width; i++)
			retval.append("-");
		retval.append("-\n");
		// Writes the content of the columns
		iSpaces = 0;

		for (int i = 0; i < 1; i++, iSpaces = 0) {
			retval.append("|");
			for (Object e : line.values())
				retval.append(String.format("%" + spaces[iSpaces++] + "s|", e));
			retval.append("\n");
		}

		return retval.toString();
	}
}
