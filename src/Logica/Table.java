package Logica;
import java.util.*;
import java.io.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * The Table class represents a table. This class contains a Header and a List
 * for each of it's columns and is completely free to be manipulated. The list
 * of implemented operations include: creating an instance manually or from a
 * file; increasing/decreasing the number of columns; renaming columns;
 * adding/removing tuples and lines; clearing tuples, lines, columns and the
 * entire table; saving table into file. There are also available operations
 * between Table objects: union, intersection, difference and join.
 */
public class Table {

	// ATTRIBUTES

	/**
	 * Content of the table - the table itself.
	 */
	private Map<Header, List> table;
	// private Map<Header,Function> functions;
	/**
	 * Data type of each column of the table.
	 */
	private Map<Header, Class> columnTypes;
	/**
	 * Number of columns of the table.
	 */
	private int width;
	/**
	 * Number of tuples of the table.
	 */
	private int height;

	// CONSTRUCTORS

	/**
	 * Constructor of type 0: creates an empty table with no columns.
	 */
	public Table() {
		table = new LinkedHashMap<>();
		// functions = new LinkedHashMap<>();
		columnTypes = new LinkedHashMap<>();
		width = height = 0;
	}

	/**
	 * Constructor of type 1: creates an empty table with given columns
	 * 
	 * @param columns
	 *            Array of Headers containing the information about each column.
	 * @param line
	 *            line where the method was called.
	 */
	public Table(Header[] columns, int line) {
		table = new LinkedHashMap<>();
		// functions = new LinkedHashMap<>();
		columnTypes = new LinkedHashMap<>();
		width = height = 0;

		increase(columns, line);
	}

	/**
	 * Constructor of type 2: creates a table with the content of a well structured
	 * file. This constructor uses a grammar to parse the file. To read the file was
	 * implemented a interpreter that check the table semantics while parsing the
	 * file. If the file is not found, an error message will be shown and the
	 * program will stop.
	 * 
	 * @param fileName
	 *            name of the file to read.
	 * @param line
	 *            line where the method was called.
	 */
	public Table(String fileName, int line) {
		CharStream input = null;
		try {
			input = CharStreams.fromFileName(fileName);
		} catch (IOException e) {
			System.err.println("Line " + line + " [Constructor] File '" + fileName + "' not found.");
			System.exit(1);
		}
		ALFAInterpreterLexer lexer = new ALFAInterpreterLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ALFAInterpreterParser parser = new ALFAInterpreterParser(tokens);
		ParseTree tree = parser.main();
		ParseTreeWalker walker = new ParseTreeWalker();
		boolean errors = false;
		if (parser.getNumberOfSyntaxErrors() == 0) {
			Interpreter interpreter = new Interpreter();
			walker.walk(interpreter, tree);
			if (!interpreter.errors()) {
				table = interpreter.getTable();
				width = interpreter.getWidth();
				height = interpreter.getHeight();
			} else
				errors = true;
		} else
			errors = true;
		if (errors)
			System.exit(1);
		// Update map columnTypes according to headers types
		columnTypes = new LinkedHashMap<>();
		for (Header h : table.keySet()) {
			switch (h.getType()) {
			case Int:
				columnTypes.put(h, Integer.class);
				break;
			case Re:
				columnTypes.put(h, Double.class);
				break;
			case Str:
				columnTypes.put(h, String.class);
				break;
			case Bool:
				columnTypes.put(h, Boolean.class);
				break;
			}
		}
	}

	// RENAME

	/**
	 * Renames a column's Header. If table does not contain a column with the given
	 * name, an error message will be shown and the program will stop.
	 * 
	 * @param oldName
	 *            Current name of the Header who's information will be updated.
	 * @param newName
	 *            Name to replace the Header's current name.
	 * @param line
	 *            line where the method was called.
	 */
	public void rename(String oldName, String newName, int line) {
		for (Header h : table.keySet()) {
			if (h.getName().equals(oldName)) { // check if table contains header with given name
				h.setName(newName);
				return;
			}
		}
		System.err.println("Line " + line + " [Rename] Table does not contain column with name " + oldName
				+ ". Unable to complete action.");
		System.exit(1);
	}

	// INCREASE

	/**
	 * Increases the table by several (empty) columns. If the table already contains
	 * one of the given Headers, an error message will be shown and the program will
	 * stop.
	 * 
	 * @param newColumns
	 *            headers of the columns to be added to the table.
	 * @param line
	 *            line where the method was called.
	 */
	public void increase(Header[] newColumns, int line) {
		for (Header h : newColumns) {
			this.increase(h, line);
		}
	}

	/**
	 * Increases the table by one (empty) column. If the table already contains the
	 * given Header of if its data type is invalid, an error message will be shown
	 * and the program will stop.
	 * 
	 * @param newColumn
	 *            header of the column to be added to the table.
	 * @param line
	 *            line where the method was called.
	 */
	public void increase(Header newColumn, int line) {
		if (table.containsKey(newColumn)) { // check if there are no repeated header names
			System.err.println("Line " + line + " [Increase] Table already contains element" + newColumn.getName()
					+ ". Unable to complete action.");
			System.exit(1);
		}

		switch (newColumn.getType()) { // check if DataTypes are valid
		case Int:
			table.put(newColumn, new ArrayList<Integer>());
			columnTypes.put(newColumn, Integer.class);
			break;
		case Re:
			table.put(newColumn, new ArrayList<Double>());
			columnTypes.put(newColumn, Double.class);
			break;
		case Str:
			table.put(newColumn, new ArrayList<String>());
			columnTypes.put(newColumn, String.class);
			break;
		case Bool:
			table.put(newColumn, new ArrayList<Boolean>());
			columnTypes.put(newColumn, Boolean.class);
			break;
		case Table:
		case Column:
		case Line:
		default:
			System.err.println("Line " + line + " [Increase] Invalid Header type. Unable to complete action.");
			System.exit(1);
		}

		width++;
		for (int i = 0; i < height; i++) {
			table.get(newColumn).add(null);
		}
	}
	
	public void increase(Column c, int line) {
        if (table.containsKey(c.getHeader())) {
            System.err.println("line " + line + " [Increase Collumn] There's a column in the table with the name " + c.getHeader().getName() + ".");
            System.exit(1);
        }
        
        if (c.getColumn().size() > height) {
            for (Header h : table.keySet())
                for (int i = 0; i < c.getColumn().size() - height; i++ )
                    table.get(h).add(null);
            height = c.getColumn().size(); 
        }
        else if (c.getColumn().size() < height) {
            for (Header h : table.keySet())
                for (int i = 0; i < c.getColumn().size() - height; i++ )
                    c.getColumn().add(null);
        }
        
        table.put(c.getHeader(), c.getColumn());
        width += 1;
    }

	// DECREASE

	/**
     * Decreases the table by one column. If the table does not contain given Header, a warning message will be shown and no update will be done.
     * @param oldColumn header of the column to be deleted.
     * @param line decrease was called
     */
    public void decrease(String oldColumn, int line) {
        for (Header h : table.keySet())
            if (h.getName().equals(oldColumn)) {
                table.remove(h);
                width -= 1;
            }
        
        System.err.println("line " + line + " [Decrease] Table does not contain column" + oldColumn + ". Unable to complete action.");
        System.exit(1);
    }

	// CLEAR

	/**
	 * Clears entire table, leaving the headers intact.
	 */
	public Table clear() {
		for (Header column : table.keySet()) {
			table.get(column).clear();
		}
		height = 0;
		return this;
	}

	/**
     * Clears a column from the table. If the table does not contain the given header, a warning message will be shown and no update will be done.
     * @param column header of the column to be cleared.
     * @param line where clear was called
     */
    public Table clear(String column, int line) {
        for (Header h : table.keySet()) {
            if (h.getName().equals(column)){
                for(int i = 0; i < height; i++)
                    table.get(column).set(i,null);
                return this;
            }
        }
        
        System.err.println("line " + line + " [Clear Column] No column with name " + column + " was found. Unable to complete action.");
        System.exit(1);
        return null;
        
    }
    
    public Table clear(Line l, int line) {
        if (width != l.getLine().keySet().size()) {
            System.err.println("line " + line + " [Remove Line] Number of columns are different.");
            System.exit(1);
        }
        int i = 0;
        for (Header h : l.getLine().keySet()) {
            if (h.getType() != getHeader(i).getType()) {
                System.err.println("line" + line + " [Remove Line] Data types don't match");
                System.exit(1);
            }
            i++;
        }
        
loopRem:for (i = height - 1; i >= 0; i--) {
            int j = 0;
            for (Object o : l.getLine().values()) {
                if (!table.get(getHeader(j)).get(i).equals(o))
                    continue loopRem;
                j++;
            }
            for (Header h : table.keySet())
                table.get(h).remove(i);
        }
        return this;
    }

	/**
	 * Clears a line from the table. If the table's size is less or equal than the
	 * given index, an error message will be shown and the program will stop.
	 * 
	 * @param index
	 *            index of the line to be cleared.
	 * @param line
	 *            line where the method was called.
	 */
	public Table clear(int index, int line) {
		if (index >= height) { // check if index inside of bounds
			System.err.println("Line " + line + " [Clear Line] Table's size is less that index " + index
					+ ". Unable to complete action.");
			System.exit(1);
		}

		for (Header column : table.keySet()) {
			table.get(column).set(index, null);
		}

		return this;
	}

	// ADD

	/**
	 * Adds an element to a specific column and inserts it at the bottom of the
	 * table. The remaining elements of the line are set to null. If the table does
	 * not contain a column with the given header or if the element to be inserted
	 * isn't of the same type of the column, an error message will be shown and the
	 * program will stop.
	 * 
	 * @param column
	 *            header of the column where the element will be inserted to.
	 * @param element
	 *            element to be inserted.
	 * @param line
	 *            line where the method was called.
	 */
	public void add(Header column, Object element, int line) {
		if (!table.containsKey(column)) { // check if column exists
			System.err.println("Line " + line + " [Add] No column with name " + column.getName()
					+ " was found. Unable to complete action.");
			System.exit(1);
		}

		if (!columnTypes.get(column).getName().equals(element.getClass().getName())) {// check if element is of same
																						// type of column
			System.err.println("Line " + line + " [Add] Element isn´t of same type as column " + column.getName()
					+ ". Unable to complete action.");
			System.exit(1);
		}

		table.get(column).add(element);
		for (Header c : table.keySet()) {
			if (c.equals(column)) {
				continue;
			}

			table.get(c).add(null);
		}
		height++;
	}

	/**
	 * Adds an element to a specific column and inserts it in a specific index of
	 * the table's column. All the lines in that index and bellow move one index
	 * ahead, and the remaining elements of the new line are set to null. If the
	 * table does not contain a column with the given header, if the element to be
	 * inserted isn't of the same type of the column, or if the given index is
	 * greater or equal to the table's width, an error message will be shown and the
	 * program will stop.
	 * 
	 * @param column
	 *            header of the column where the element will be inserted to.
	 * @param element
	 *            element to be inserted.
	 * @param index
	 *            index of the table where the element will be inserted to.
	 * @param line
	 *            line where the method was called.
	 */
	public void add(Header column, Object element, int index, int line) {
		if (index > height) { // check if index inside of bounds
			System.err.println("Line " + line + " [Add] Table's size is less that index " + index
					+ ". Unable to complete action.");
			System.exit(1);
		}

		if (!table.containsKey(column)) { // check if column exists
			System.err.println("Line " + line + " [Add] No column with name " + column.getName()
					+ " was found. Unable to complete action.");
			System.exit(1);
		}

		if (!columnTypes.get(column).getName().equals(element.getClass().getName())) {// check if element is of same
																						// type of column
			System.err.println("Line " + line + " [Add] Element isn´t of same type as column " + column.getName()
					+ ". Unable to complete action.");
			System.exit(1);
		}

		table.get(column).add(index, element); // add element in specific index
		for (Header c : table.keySet()) {
			if (c.equals(column)) {
				continue;
			}

			table.get(c).add(index, null); // add null to remaining elements
		}
		height++;
	}

	/**
	 * Adds an entire line to the table and inserts it at the bottom of the table.
	 * If the given line's width is different from the table's width or if the data
	 * types are not compatible, an error message will be shown and the program will
	 * stop.
	 * 
	 * @param elements
	 *            line of elements to be inserted.
	 * @param line
	 *            line where the method was called.
	 */
	public void add(Line elements, int line) {
		if (elements.getWidth() != width) { // check if number of arguments match table's format
			System.err.println("Line " + line + " [Add] Number of elements to add (" + elements.getWidth()
					+ ") is different from table's width (" + width + "). Unable to complete action.");
			System.exit(1);
		}

		// check if line's data types are compatible with table's data types
		int i = 0;
		for (Map.Entry<Header, Class> lineColumn : elements.getColumnTypes().entrySet()) {
			if (!columnTypes
				.get(getHeader(i))
				.getSimpleName()
				.equals(lineColumn
					.getValue()
					.getSimpleName())) {
				System.err.println("Line " + line + "[Add]" + lineColumn.getKey() + " isn´t of same type as "
						+ columnTypes.get(lineColumn.getKey()).getSimpleName() + ". Unable to complete action.");
				System.exit(1);
				return;
			}
			i += 1;
		}
		// insert line at the bottom of the table
		i = 0;
		for (Map.Entry<Header, Class> lineColumn : elements.getColumnTypes().entrySet()) {
			table.get(getHeader(i)).add(elements.getLine().get(lineColumn.getKey()));
			i += 1;
		}

		height++;
	}

	/**
	 * Adds an entire line to the table at a specific index. If the given line's
	 * width is different from the table's width, if the data types are not
	 * compatible, or if the given index is greater or equal to the table's width,
	 * an error message will be shown and the program will stop.
	 * 
	 * @param elements
	 *            line of elements to be inserted.
	 * @param index
	 *            index of the table where the line will be inserted to.
	 * @param line
	 *            line where the method was called.
	 */
	public void add(Line elements, int index, int line) {
		if (index > height) { // check if index inside of bounds
			System.err.println("Line " + line + " [Add] Table's size is less that index " + index
					+ ". Unable to complete action.");
			System.exit(1);
		}

		if (elements.getWidth() != width) { // check if number of arguments match table's format
			System.err.println("Line " + line + " [Add] Number of elements to add (" + elements.getWidth()
					+ ") is different from table's width (" + width + "). Unable to complete action.");
			System.exit(1);
		}

		// check if line's data types are compatible with table's data types

		for (Map.Entry<Header, Class> lineColumn : elements.getColumnTypes().entrySet()) {
			if (!columnTypes.get(lineColumn.getKey()).getSimpleName().equals(lineColumn.getValue().getSimpleName())) {
				System.err.println("Line " + line + "[Add]" + lineColumn.getKey() + " isn´t of same type as "
						+ columnTypes.get(lineColumn.getKey()).getSimpleName() + ". Unable to complete action.");
				System.exit(1);
				return;
			}
		}
		// insert line at the spcefic line of the table
		for (Map.Entry<Header, Class> lineColumn : elements.getColumnTypes().entrySet()) {
			table.get(lineColumn.getKey()).add(index, elements.getLine().get(lineColumn.getKey()));
		}

		height++;
	}

	/**
	 * Replaces an element of a specific column in a specific index. This method
	 * alters nothing but the element in the given position. If the table does not
	 * contain a column with the given header, if the element to be inserted isn't
	 * of the same type of the column, or if the given index is greater or equal to
	 * the table's width, an error message will be shown and the program will stop.
	 * 
	 * @param column
	 *            header of the column where the element will be replaced.
	 * @param element
	 *            element to replace the current one.
	 * @param index
	 *            index of the table where the element will be replaced.
	 * @param line
	 *            line where the method was called.
	 */
	public void replace(Header column, Object element, int index, int line) {
		if (index >= height) { // check if index inside of bounds
			System.err.println("Line " + line + " [Replace] Table's size is less that index " + index
					+ ". Unable to complete action.");
			System.exit(1);
		}

		if (!table.containsKey(column)) { // check if column exists
			System.err.println("Line " + line + " [Replace] No column with name " + column.getName()
					+ " was found. Unable to complete action.");
			System.exit(1);
		}

		if (!columnTypes.get(column).getName().equals(element.getClass().getName())) {// check if element is of same
																						// type of column
			System.err.println("Line " + line + " [Replace] Element isn´t of same type as column " + column.getName()
					+ ". Unable to complete action.");
			System.exit(1);
		}

		table.get(column).set(index, element);
	}
	
	public void setElem(int lineInd, int colInd, Object object, int line) {
	    if (lineInd < 0 || lineInd >= height) {
	        System.err.println("line " + line + " [Replace] Index out of bounds, height: " + height);
	        System.exit(1);
	    }
	    else if (colInd < 0 || colInd >= width) {
	        System.err.println("line " + line + " [Replace] Index out of bounds, width: " + width);
            System.exit(1);
        }
	    
	    replace(getHeader(colInd), object, lineInd, line);
	}

	// REMOVE

	/**
	 * Removes all the elements from a specific column that are equal to the given
	 * element (sets them to null). If the column does not contain any element equal
	 * to the given one, nothing happens. If the table does not contain a column
	 * with the given header, an error message will be shown and the program will
	 * stop.
	 * 
	 * @param column
	 *            header of the column where the elements will be deleted.
	 * @param element
	 *            element to be searched for in the column.
	 * @param line
	 *            line where the method was called.
	 */
	public void remove(Header column, Object element, int line) {
		if (!table.containsKey(column)) { // check if column exists
			System.err.println("Line " + line + " [Remove] No column with name " + column.getName()
					+ " was found. Unable to complete action.");
			System.exit(1);
		}

		int tmpindex;
		while (table.get(column).contains(element)) {
			tmpindex = table.get(column).indexOf(element);
			table.get(column).set(tmpindex, null);
		}
	}
	
	public void remove(int ind1, int ind2, int line) {
        if (ind1 >= ind2) {
            System.err.println("line " + line + " [Remove Interval] Invalid index interval.");
            System.exit(1);
        }
        else if (ind1 < 0 || ind1 >= height || ind2 < 0 || ind2 >= height) {
            System.err.println("line " + line + " [Remove Interval] Index out of bounds, size: " + height);
            System.exit(1);
        }
        
        for (Header h : table.keySet())
            for (int i = 0; i < ind2 - ind1; i++)
                table.get(h).remove(ind1);
    }

	/**
	 * Removes an element from a specific column from a specific index of the
	 * table's column (sets it to null). If the table does not contain a column with
	 * the given header or if the given index is greater or equal to the table's
	 * width, an error message will be shown and the program will stop.
	 * 
	 * @param column
	 *            header of the column where the element will be removed.
	 * @param index
	 *            index of the table where the element will be removed.
	 * @param line
	 *            line where the method was called.
	 */
	public void remove(Header column, int index, int line) {
		if (index >= height) { // check if index inside of bounds
			System.err.println("Line " + line + " [Remove] Table's size is less that index " + index
					+ ". Unable to complete action.");
			System.exit(1);
		}

		if (!table.containsKey(column)) { // check if column exists
			System.err.println("Line " + line + " [Remove] No column with name " + column.getName()
					+ " was found. Unable to complete action.");
			System.exit(1);
		}

		table.get(column).set(index, null);
	}

	/**
	 * Removes entire line of the table at a specific index. All lines bellow move
	 * up one index. If the given index is greater or equal to the table's width, an
	 * error message will be shown and the program will stop.
	 * 
	 * @param index
	 *            index of the table where the elements will be removed.
	 * @param line
	 *            line where the method was called.
	 */
	public void remove(int index, int line) {
		if (index >= height) { // check if index inside of bounds
			System.err.println("Line " + line + " [Remove] Invalid index " + index + ". Unable to complete action.");
			System.exit(1);
		}

		for (Header column : table.keySet()) {
			table.get(column).remove(index);
		}
		height--;
	}

	/**
	 * Creates a table containing all elements that are either in the first table or
	 * in the second table (does not remove duplicates). If the tables are not
	 * compatible, an error message will be shown and the program will stop.
	 * 
	 * @param thisTable
	 *            first table, who's content will appear first in the result.
	 * @param otherTable
	 *            second table, who's content will appear last in the result.
	 * @param line
	 *            line where the method was called.
	 * @return table with the result of the union operation.
	 */
	public static Table union(Table thisTable, Table otherTable, int line) {
		if (thisTable.width != otherTable.width) { // check if sizes match
			System.err.println(
					"Line " + line + " [Union] Tables have different number of columns. Unable to complete action.");
			System.exit(1);
		}

		boolean error = false;
		String errorString = "";
		for (int i = 0; i < thisTable.width; i++) { // check if columns are compatible
			if (thisTable.getHeader(i).getType() != thisTable.getHeader(i).getType()) {
				errorString = "Tables are not compatible in column " + thisTable.getHeader(i).getName() + ".";
				error = true;
			}
		}
		if (error) {
			System.err.println("Line " + line + " [Union] " + errorString + " Unable to complete action.");
			System.exit(1);
		}

		Table retval = new Table();
		for (Header h : thisTable.table.keySet()) {
			retval.increase(h, line);
		}
		for (int j = 0; j < otherTable.height; j++) {
			retval.add(thisTable.getHeader(0), thisTable.getColumn(0).get(j), line);
		}
		for (int i = 1; i < thisTable.width; i++) {
			for (int j = 0; j < otherTable.height; j++) {
				retval.replace(thisTable.getHeader(i), thisTable.getColumn(i).get(j), j, line);
			}
		}
		int tmpHeight = thisTable.height;
		for (int j = 0; j < otherTable.height; j++) {
			retval.add(thisTable.getHeader(0), otherTable.getColumn(0).get(j), line);
		}
		for (int i = 1; i < thisTable.width; i++) {
			for (int j = 0; j < otherTable.height; j++) {
				retval.replace(thisTable.getHeader(i), otherTable.getColumn(i).get(j), tmpHeight + j, line);
			}
		}
		return retval;
	}

	/**
	 * Creates a table containing all elements that are in the first table and in
	 * the second table. If the tables are not compatible, an error message will be
	 * shown and the program will stop.
	 * 
	 * @param thisTable
	 *            first table.
	 * @param otherTable
	 *            second table.
	 * @param line
	 *            line where the method was called.
	 * @return table with the result of the intersection operation.
	 */
	public static Table intersection(Table thisTable, Table otherTable, int line) {
		if (thisTable.width != otherTable.width) { // check if sizes match
			System.err.println("Line " + line
					+ " [Intersect] Tables have different number of columns. Unable to complete action.");
			System.exit(1);
		}

		boolean error = false;
		String errorString = "";
		for (int i = 0; i < thisTable.width; i++) { // check if columns are compatible
			if (thisTable.getHeader(i).getType() != thisTable.getHeader(i).getType()) {
				errorString = "Tables are not compatible in column " + thisTable.getHeader(i).getName() + ".";
				error = true;
			}
		}
		if (error) {
			System.err.println("Line " + line + " [Intersect] " + errorString + " Unable to complete action.");
			System.exit(1);
		}

		Table retval = new Table();
		for (Header h : thisTable.table.keySet()) {
			retval.increase(h, line);
		}

		boolean valid = false;
		for (int i = 0; i < thisTable.height; i++) { // for each tuple in table 1
			for (int j = 0; j < otherTable.height; j++) { // for each tuple in table 2
				if ((thisTable.table.get(thisTable.getHeader(0)).get(i) == null && otherTable

						.table
						.get(otherTable
						.getHeader(0))
						.get(j) == null)

					|| (thisTable.table.get(thisTable.getHeader(0)).get(i) != null && thisTable.table.get(thisTable.getHeader(0)).get(i)
						.equals(otherTable
						.table
						.get(otherTable
						.getHeader(0))
						.get(j)))) { // check if 1st tuple is equal
					valid = true;
					int l = 0;
					for (Header h : thisTable.table.keySet()) {
						if (thisTable.table.get(h).get(i) == null) {
								if(otherTable.table.get(otherTable.getHeader(l)).get(j) == null) {
									valid = false;
									break;
								}
					    }
						else if (!thisTable.table.get(h).get(i).equals(otherTable.table.get(otherTable.getHeader(l)).get(j))) {
							valid = false;
							break;
						}
						l += 1;
					}
					if (valid) {
						retval.add(thisTable.getHeader(0), thisTable.table.get(thisTable.getHeader(0)).get(i), line);
						for (int k = 1; k < thisTable.width; k++) {
							retval.replace(thisTable.getHeader(k), thisTable.table.get(thisTable.getHeader(k)).get(i),
									retval.height - 1, line);
						}
					}
				}
			}
		}
		System.out.println(retval.height);
		return retval;
	}

	/**
	 * Creates a table containing all elements that are in the first table but not
	 * in the second, and in the second table but not in the first. If the tables
	 * are not compatible, an error message will be shown and the program will stop.
	 * 
	 * @param t1
	 *            first table.
	 * @param t2
	 *            second table.
	 * @param line
	 *            line where the method was called.
	 * @return table with the result of the difference operation.
	 */
	public static Table difference(Table t1, Table t2, int line) {
		if (t1.width != t2.width) { // check if sizes match
			System.err.println("Line " + line
					+ " [Difference] Tables have different number of columns. Unable to complete action.");
			System.exit(1);
		}

		boolean error = false;
		String errorString = "";
		for (int i = 0; i < t1.width; i++) { // check if columns are compatible
			if (t1.getHeader(i).getType() != t2.getHeader(i).getType()) {
				errorString = "Tables are not compatible in column " + t1.getHeader(i).getName() + ".";
				error = true;
			}
		}
		if (error) {
			System.err.println("Line " + line + " [Difference] " + errorString + " Unable to complete action.");
			System.exit(1);
		}

		// Create headers for the new table
		Header[] headers = new Header[t1.width];
		{
			Iterator<Header> it = t1.table.keySet().iterator();

			for (int i = 0; i < t1.width; i++)
				headers[i] = it.next();
		}

		Table retval = new Table(headers, line);

		itT1_1: for (int i = 0; i < t1.height; i++) {
			itT2_1: for (int j = 0; j < t2.height; j++) {
				for (int k = 0; k < t1.width; k++)
					if (!t1.table.get(t1.getHeader(k)).get(i).equals(t2.table.get(t2.getHeader(k)).get(j)))
						continue itT2_1;
				continue itT1_1;
			}
			int ind = 0;
			for (List l : t1.table.values()) {
				retval.table.get(retval.getHeader(ind)).add(l.get(i));
				ind++;
			}
			retval.height += 1;
		}

		itT1_2: for (int i = 0; i < t2.height; i++) {
			itT2_2: for (int j = 0; j < t1.height; j++) {
				for (int k = 0; k < t1.width; k++)
					if (!t2.table.get(t2.getHeader(k)).get(i).equals(t1.table.get(t1.getHeader(k)).get(j)))
						continue itT2_2;
				continue itT1_2;
			}
			int ind = 0;
			for (List l : t2.table.values()) {
				retval.table.get(retval.getHeader(ind)).add(l.get(i));
				ind++;
			}
			retval.height += 1;
		}

		return retval;
	}

	/**
	 * Obtains the Compare Function according to the classes between the operation
	 * and the operation's operator. This function terminates the program if a
	 * numeric operator (>,>=,<,<=) is used with non numeric operands or when a
	 * equal(==) or different(!=) operator is used between different types. This
	 * function is used in the operations remove with condition and join with
	 * condition.
	 * 
	 * @param a
	 *            class of the first operand.
	 * @param b
	 *            class of the second operand.
	 * @param op
	 *            compare operator.
	 * @param line
	 *            line where the method was called.
	 * @return Compare function according to the parameters.
	 */
	private static CompareFunction getCompareFunction(Class a, Class b, CompareOperations op, int line) {
		int type1, type2;

		type1 = getType(a.getSimpleName());
		type2 = getType(b.getSimpleName());

		// parse type
		switch (op) {
		case DIFFERENT:
		case EQUAL:
			if (type1 != type2) {
				System.err.println("Line " + line + " [Join] operator " + op + " requires operands of the same type");
				System.exit(1);
			}
			break;
		case GREATER:
		case GRETER_EQUAL:
		case LESS:
		case LESS_EQUAL:
			if (type1 != 1 || type2 != 1) {
				System.err.println("Line " + line + " [Join] operator " + op + " requires numeric operands");
				System.exit(1);
			}
			break;
		}

		switch (op) {
		case DIFFERENT:
			if (type1 == 1)
				return new DifferentNumericFunction();
			else
				return new DifferentFunction();
		case EQUAL:
			if (type1 == 1)
				return new EqualNumericFunction();
			else
				return new EqualFunction();
		case GREATER:
			return new GreaterFunction();
		case GRETER_EQUAL:
			return new GreaterEqualFunction();
		case LESS:
			return new LessFunction();
		case LESS_EQUAL:
			return new LessEqualFunction();
		default:
			throw new IllegalArgumentException();
		}
	}



	public static Table join(Table t1, Table t2, int line) {
        //verify if there is columns with the same name
        for (Header h1 : t1.table.keySet())
            for (Header h2 : t2.table.keySet())
                if (h1.compareTo(h2) == 0) {
                    System.err.println("line " + line +" [Join] Theres columns with the same name.");
                    System.exit(1);
                }
        
        //Create headers for the new table
        Header[] headers = new Header[t1.width + t2.width];
        {
            Iterator<Header> it = t1.table.keySet().iterator();
            int i = 0;
            
            for ( ; i < t1.width; i++)
                headers[i] = it.next();
            
            it = t2.table.keySet().iterator();
            
            for (int j = 0; j < t2.width; j++, i++)
                headers[i] = it.next();
        }
        
        Table retVal = new Table(headers, line);
        retVal.width = headers.length;
        
        //update the map columnTypes of the new table
        for (Header h : headers) {
            switch (h.getType()) {
                case Bool:
                    retVal.columnTypes.put(h, Boolean.class);
                    break;
                case Int:
                    retVal.columnTypes.put(h, Integer.class);
                    break;
                case Re:
                    retVal.columnTypes.put(h, Double.class);
                    break;
                case Str:
                    retVal.columnTypes.put(h, String.class);
                    break;
                default:
                    System.err.println("line " + line + " [Join] Type " + h.getType() + " can't be used in tables.");
                    System.exit(1);
                    return null;
            }
        }
        
        {int i = 0;
        for ( ; i < t1.width; i++)
            retVal.getColumn(i).addAll(t1.getColumn(i));
        for (int j = 0; j < t2.width; j++, i++)
            retVal.getColumn(i).addAll(t2.getColumn(j));}
        
        Set<Header> keySet = null;
        if (t1.height < t2.height) {
            keySet = t1.table.keySet();
            retVal.height = t2.height;
        }
        else if (t1.height > t2.height) {
            keySet = t2.table.keySet();
            retVal.height = t1.height;
        }
        else
            retVal.height = t1.height;
        
        if (keySet != null) {
            for (Header h : keySet)
                for (int i = 0; i < Math.abs(t1.height - t2.height); i++)
                    retVal.table.get(h).add(null);
        }
        
        return retVal;
    }



	/**
	 * Creates a table containing all columns from both tables. This table will
	 * contain all elements from both tables where the condition is verified. If the
	 * tables are not compatible according to the condition, an error message will
	 * be shown and the program will stop.
	 * 
	 * @param t1
	 *            first table.
	 * @param t2
	 *            second table.
	 * @param colName1
	 *            name of the column of t1 to compare.
	 * @param op
	 *            compare operation.
	 * @param colName2
	 *            name of the column of t2 to compare.
	 * @param line
	 *            line where the method was called.
	 * @return table with the result of the difference operation.
	 */
	public static Table join(Table t1, Table t2, String colName1, CompareOperations op, String colName2, int line) {
		// get columns to compare
		List l1 = getColumn(t1, colName1);
		if (l1 == null) {
			System.err.println(
					"Line " + line + " [Join] Theres no column with the name " + colName1 + "in the first Table.");
			System.exit(1);
		}
		List l2 = getColumn(t2, colName2);
		if (l2 == null) {
			System.err.println(
					"Line " + line + " [Join] Theres no column with the name " + colName1 + "in the second Table.");
			System.exit(1);
		}

		CompareFunction cf = getCompareFunction(t1.columnTypes.get(getHeader(t1, colName1)),
				t2.columnTypes.get(getHeader(t2, colName2)), op, line);

		// verify if there is columns with the same name
		for (Header h1 : t1.table.keySet())
			for (Header h2 : t2.table.keySet())
				if (h1.compareTo(h2) == 0) {
					System.err.println("line " + line + " [Join] Theres columns with the same name.");
					System.exit(1);
				}

		// Create headers for the new table
		Header[] headers = new Header[t1.width + t2.width];
		{
			Iterator<Header> it = t1.table.keySet().iterator();
			int i = 0;

			for (; i < t1.width; i++)
				headers[i] = it.next();

			it = t2.table.keySet().iterator();

			for (int j = 0; j < t2.width; j++, i++)
				headers[i] = it.next();
		}

		Table retVal = new Table(headers, line);

		// update the map columnTypes of the new table
		for (Header h : headers) {
			switch (h.getType()) {
			case Bool:
				retVal.columnTypes.put(h, Boolean.class);
				break;
			case Int:
				retVal.columnTypes.put(h, Integer.class);
				break;
			case Re:
				retVal.columnTypes.put(h, Double.class);
				break;
			case Str:
				retVal.columnTypes.put(h, String.class);
				break;
			default:
				System.err.println("Line " + line + " [Join] Type " + h.getType() + " can't be used in tables.");
				System.exit(1);
			}
		}

		// update with of the new table
		retVal.width = headers.length;

		// iterate through the two columns to compare
		Iterator<Object> it1 = l1.iterator();
		Iterator<Object> it2 = l2.iterator();

		for (int i = 0; it1.hasNext(); i++) {
			Object o1 = it1.next();
			it2 = l2.iterator();
			for (int j = 0; it2.hasNext(); j++) {
				Object o2 = it2.next();

				if (cf.compare(o1, o2)) { // If they respect the the condition insert the values into the new table
					retVal.height += 1;
					Iterator<List> listIte = t1.table.values().iterator();
					boolean getFromOtherTable = false;
					for (List l : retVal.table.values()) {
						List tmpL = listIte.next();
						if (getFromOtherTable)
							l.add(tmpL.get(j));
						else
							l.add(tmpL.get(i));

						if (!listIte.hasNext()) {
							listIte = t2.table.values().iterator();
							getFromOtherTable = true;
						}
					}
				}
			}
		}

		return retVal;
	}

	/**
	 * Removes tuples from table if the condition is verified.
	 * 
	 * @param colName
	 *            name of the column compared in the condition.
	 * @param op
	 *            type of compare.
	 * @param obj
	 *            object to compare with column.
	 * @param line
	 *            line where the method was called.
	 */
	public void remove(String colName, CompareOperations op, Object obj, int line) {
		List l = getColumn(this, colName);
		if (l == null) {
			System.err.println("Line " + line + " [Remove] Theres no column with the name " + colName);
			System.exit(1);
		}

		CompareFunction cf = getCompareFunction(columnTypes.get(getHeader(this, colName)), obj.getClass(), op, line);

		for (int i = 0; i < l.size(); i++) {
			if (cf.compare(l.get(i), obj)) {
				for (List col : table.values())
					col.remove(i);
				i -= 1;
			}
		}

		height = l.size();
	}

	/**
	 * Saves table into file with given name. If there is a problem in writting the
	 * table to the file, an error message will be shown and the program will stop.
	 * 
	 * @param fileName
	 *            name of the file where the table will be saved.
	 * @param line
	 *            line where the method was called.
	 */
	public void save(String fileName, int line) {
		File file = new File(fileName);
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(file);
		} catch (IOException e) {
			System.err.println("Line " + line + " [Save] Error while reading file \"" + fileName + "\".");
			System.exit(1);
		}

		int i = 0;
		for (Header h : table.keySet()) {
			printer.print(h.getType() + ":" + h.getName());
			i++;
			if (i != width) {
				printer.print(",");
			}
		}
		if (height > 0) {
			printer.println();
		}

		int j;
		for (i = 0; i < height; i++) {
			j = 0;
			for (List l : table.values()) {
				Object o = l.get(i);
				if (o instanceof String)
					printer.print("\"" + o + "\"");
				else if (o != null)
					printer.print(o);
				j++;
				if (j != width)
					printer.print(",");
			}
			if (i + 1 != height)
				printer.println();
		}

		printer.close();
	}

	// FUNCTIONS

	/*
	 * 
	 * // define a column's function to be followed public void
	 * defineFunction(Function func, Header column) { if(table.containsKey(column))
	 * { // check if column exists if(!functions.containsKey(column)) { // check if
	 * that column has no function assigned to it functions.put(column, func);
	 * applyFunction(column); } else { // if column has a function assigned to it,
	 * replace function & redo all elements functions.replace(column, func);
	 * clear(column); applyFunction(column); } } }
	 * 
	 * // internal function called to update all tuples from a specific column that
	 * is following a function private void applyFunction(Header column) {
	 * if(!functions.containsKey(column)) { // check if column exists
	 * System.err.println("[ApplyFunction] No column with name " + column.getName()
	 * + " was found. Unable to complete action."); return; }
	 * 
	 * // não é a versão final! Object retval; for(int i=0; i<table.size(); i++) {
	 * retval = functions.get(column).execute(i); // so pode aceitar o argumento
	 * index do tuplo? // como se verifica se o tipo do retorno é compatível com o
	 * tipo do arraylist? table.get(column).set(i, retval); } }
	 * 
	 * // internal function called to update a tuple from a specific column in a
	 * specific index that is following a function private void applyFunction(Header
	 * column, int index) { if(!functions.containsKey(column)) { // check if column
	 * exists System.err.println("[ApplyFunction] No column with name " +
	 * column.getName() + " was found. Unable to complete action."); return; }
	 * if(table.get(column).size()<=index) { // check if index is valid
	 * System.err.println("[ApplyFunction] The column " + column.getName() +
	 * " has less tuples than " + (index+1) + ". Unable to complete action.");
	 * return; }
	 * 
	 * // não é a versão final! Object retval =
	 * functions.get(column).execute(index); table.get(column).set(index, retval); }
	 * 
	 */

	// GETTERS

	/**
	 * Getter function for a column's header by its index in the table.
	 * 
	 * @param index
	 *            index of the column in the table.
	 * @return header of the column in the given index of the table.
	 */
	public Header getHeader(int index) {
		return (Header) table.keySet().toArray()[index];
	}

	/**
	 * Getter function for one of the table's header, given a specific name.
	 * 
	 * @param t
	 *            table where to get the header.
	 * @param name
	 *            name of the header.
	 * @return header of the table, null if theres no header with the name passed.
	 */
	private static Header getHeader(Table t, String name) {
		for (Header h : t.table.keySet())
			if (h.getName().equals(name))
				return h;
		return null;
	}

	/**
	 * Getter function for the content of one of the table's columns, given the
	 * index of the column.
	 * 
	 * @param index
	 *            index of the column to retrieve the content.
	 * @return List with the content of the column.
	 */
	public List getColumn(int index) {
		Iterator it = table.keySet().iterator();
		int ind = 0;
		while (it.hasNext()) {
			Header h = (Header) it.next();
			if (ind == index) {
				return table.get(h);
			}
			ind++;
		}
		return null;
	}

	/**
	 * Getter function for the content of one of the table's columns, given the
	 * column's header.
	 * 
	 * @param h
	 *            header of the column to retrieve the content.
	 * @return List with the content of the column.
	 */
	public List getColumn(Header h) {
		return table.get(h);
	}

	/**
	 * Getter function for one specific , given the index of the column.
	 * 
	 * @param index
	 *            index of the column to retrieve the content.
	 * @return List with the content of the line.
	 */
	public Line getLine(int index, int line) {
		Map<Header, Object> lineMap = new LinkedHashMap<>();
		for (Map.Entry<Header, List> t : table.entrySet()) {
		    lineMap.put(t.getKey(), t.getValue().get(index));
		}
		return new Line(lineMap, line);
	}
	
	/**
	 * Get column by index
	 * @param index of column
	 * @param line where getColumn was called
	 * @return column in index
	 */
	public Column getColumn(int index, int line) {
        if (index < 0 || index >= width) {
            System.err.println("line " + line + "[getColumn] Index out of bounds, size:" + width);
            System.exit(1);
        }
        
        return new Column(getHeader(index), getColumn(index));
    }
	
	public Column getColumn(String name, int line) {
        for (Header h : table.keySet())
            if (h.getName().equals(name))
                return new Column(h, getColumn(h));
        
        System.err.println("line " + line + " [getColumn] There's no column with the name \"" + name + "\".");
        System.exit(1);
        return null;
    }

	/**
	 * Creates a sub-table from the table within the given limits.
	 * 
	 * @param beginCol
	 *            index of the column where the subtable begins.
	 * @param endCol
	 *            index of the column where the subtable ends.
	 * @param beginLine
	 *            index of the tuple where the subtable begins.
	 * @param endLine
	 *            index of the tuple where the subtable ends.
	 * @param line
	 *            line where the method was called.
	 * @return Sub-table created.
	 */
	public Table getSubTable(int beginCol, int endCol, int beginLine, int endLine, int line) {
		if (beginCol >= width || endCol >= width || beginLine >= height || beginLine >= height) {
			System.err.println(
					"Line " + line + " [SubTable] Table's size is less that indexes given. Unable to complete action.");
			System.exit(1);
		}

		if (beginCol < 0) {
			beginCol = 0;
		}
		if (endCol < 0) {
			endCol = width - 1;
		}
		if (beginLine < 0) {
			beginLine = 0;
		}
		if (endLine < 0) {
			endLine = height - 1;
		}

		Header[] cols = new Header[endCol - beginCol + 1];
		Iterator it = table.keySet().iterator();
		int ind = 0;
		int ind2 = 0;
		while (it.hasNext()) {
			Header h = (Header) it.next();
			if (ind >= beginCol && ind <= endCol) {
				cols[ind2] = h;
				ind2++;
			}
			ind++;
		}

		Table subtable = new Table(cols, line);
		for (int j = 0; j < height; j++) {
			if (j >= beginLine && j <= endLine) {
				subtable.add(cols[0], table.get(cols[0]).get(j), line);
			}
		}
		for (int i = 1; i < cols.length; i++) {
			int h = 0;
			for (int j = 0; j < height; j++) {
				if (j >= beginLine && j <= endLine) {
					subtable.replace(cols[i], table.get(cols[i]).get(j), h, line);
					h++;
				}
			}
		}
		return subtable;
	}

	/**
	 * Getter function for a column's content, given its header name.
	 * 
	 * @param t
	 *            Table from where to get the column.
	 * @param name
	 *            Name of the column to find.
	 * @return List with the content of the column, null if no column has the name
	 *         passed in the arguments.
	 */
	private static List getColumn(Table t, String name) {
		for (Header h : t.table.keySet())
			if (h.getName().equals(name))
				return t.table.get(h);
		return null;
	}

	/**
	 * Getter function for column data types of each column.
	 * 
	 * @return Map with the header and the data type of each column.
	 */
	public Map<Header, Class> getColumnTypes() {
		return columnTypes;
	}

	/**
	 * Obtains an integer representing a type. This function is used in the function
	 * getCompareFunction().
	 * 
	 * @param name
	 *            name of the class.
	 * @return integer representing the type:
	 *         <ul>
	 *         <li>1: Numeric type (Integer/Double)</li>
	 *         <li>2: Boolean</li>
	 *         <li>3: String</li>
	 *         </ul>
	 */
	private static int getType(String name) {
		switch (name) {
		case "Integer":
		case "Double":
			return 1;
		case "Boolean":
			return 2;
		case "String":
			return 3;
		default:
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Getter function of one of the table's elements, given the index of the column
	 * and the index of the line.
	 * 
	 * @param indexCol
	 *            index of the column.
	 * @param indexLine
	 *            index of the line.
	 * @return Object representing the element to be retrieved.
	 */
	public Object getElement(int indexCol, int indexLine, int line) {
	    if (indexCol < 0 || indexCol >= width || indexLine < 0 || indexLine >= height) {
	        System.err.println("line " + line + " [GetElement] Index out of bounds, with: " + width + ", height" + height);
	        System.exit(1);
	    }
	    
		return this.table.get(this.getHeader(indexCol)).get(indexLine);
	}

	// TO STRING

	/**
	 * Formats the table in a string.
	 * 
	 * @return formated string with the table visually well structured.
	 */
	@Override
	public String toString() {
		int[] spaces = new int[width]; // Max spaces occupied by each column
		// Check header lenght
		{
			Iterator<Header> it = table.keySet().iterator();
			for (int i = 0; i < width; i++) {
				spaces[i] = it.next().getName().length();
			}
		}
		// Check columns length
		{
			Iterator<List> it = table.values().iterator();
			for (int i = 0; i < width; i++)
				for (Object o : it.next())
					if (o == null) {
						if (4 > spaces[i])
							spaces[i] = 4;
					} else if (o.toString().length() > spaces[i])
						spaces[i] = o.toString().length();
		}
		StringBuilder retval = new StringBuilder("|");

		// Write the content of the headers
		int iSpaces = 0;
		for (Header h : table.keySet()) // add header names
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
		for (int i = 0; i < height; i++, iSpaces = 0) {
			retval.append("|");
			for (List l : table.values())
				retval.append(String.format("%" + spaces[iSpaces++] + "s|", l.get(i)));
			retval.append("\n");
		}

		return retval.toString();
	}
}
