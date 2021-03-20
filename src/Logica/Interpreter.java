package Logica;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class Interpreter extends ALFAInterpreterBaseListener {
    /**
     * Data types of each column
     */
    private DataTypes[] header;
    /**
     * Name of each column
     */
    private String[] names;
    /**
     * Matriz where is stores the content of the table during the parsing
     */
    private ArrayList[] table;

    /**
     * current column parsing
     */
    private int currentCol;
    /**
     * Variable used to know if there's a semantic error in the line
     * (the number of columns in the current line is different from the number of columns of the header).
     * If so, the interpreter must skip all the cells in the current line.
     */
    private boolean parseCell = true;
    
    private int width,height;
    /**
     * Indicate if there was some semantic errors
     */
    private boolean errors = false;
    
    public boolean errors() {
        return errors;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    
    /**
     * Constructs the map used to store the table content in the class Table
     * @return map to put in the class Table
     */
    public Map<Header, List> getTable() {
        Header[] h = new Header[header.length];
        for (int i = 0; i < header.length; i++)
            h[i] = new Header(header[i], names[i]);
        Map<Header, List> retVal = new LinkedHashMap<>();
        for (int i = 0; i < header.length; i++)
            retVal.put(h[i], table[i]);
        return retVal;
    }
    
    @Override public void enterHeadeR(ALFAInterpreterParser.HeadeRContext ctx) {
        int size = ctx.headerCell().size();
        header = new DataTypes[size];
        names = new String[size];
        table = new ArrayList[size];
        width = size;
    }
    
    @Override public void exitHeadeR(ALFAInterpreterParser.HeadeRContext ctx) {
        currentCol = 0;
    }
    
    @Override public void exitHeaderCell(ALFAInterpreterParser.HeaderCellContext ctx) {
        switch (ctx.type().getText().toLowerCase()) {
            case "real":
                header[currentCol] = DataTypes.Re;
                table[currentCol] = new ArrayList<Double>();
                break;
            case "int":
                header[currentCol] = DataTypes.Int;
                table[currentCol] = new ArrayList<Integer>();
                break;
            case "string":
                header[currentCol] = DataTypes.Str;
                table[currentCol] = new ArrayList<String>();
                break;
            case "bool":
                header[currentCol] = DataTypes.Bool;
                table[currentCol] = new ArrayList<Boolean>();
                break;
        }
        names[currentCol] = ctx.ID().getText();
        currentCol++;
    }
    
    @Override public void enterLines(ALFAInterpreterParser.LinesContext ctx) {
        height = ctx.start.getLine() - 1;
        if (header.length != ctx.cell().size()) {
            parseCell = false;
            System.err.println(String.format("line %d Number of columns doesn't match number of header columns", ctx.start.getLine()));
            errors = true;
        }
    }
    
    @Override public void exitLines(ALFAInterpreterParser.LinesContext ctx) {
        currentCol = 0;
        parseCell = true;
    }
    
    @Override public void exitCell(ALFAInterpreterParser.CellContext ctx) {
        if (parseCell) {
            if (ctx.STRING() != null)
                if (!header[currentCol].accept(DataTypes.Str)) {
                    System.err.println(String.format(
                        "line %d:%d Expected String, Got %s",
                        ctx.start.getLine(), currentCol+1, header[currentCol]));
                    errors = true;
                }
                else {
                    String text = ctx.STRING().getText(); 
                    table[currentCol].add(text.substring(1, text.length()-1));
                }
            else if (ctx.REAL() != null)
                if (!header[currentCol].accept(DataTypes.Re)) {
                    System.err.println(String.format(
                        "line %d:%d Expected real, Got %s",
                        ctx.start.getLine(), currentCol+1, header[currentCol]));
                    errors = true;
                }
                else
                    table[currentCol].add(Double.parseDouble(ctx.REAL().getText()));
            else if (ctx.integer() != null)
                if (!header[currentCol].accept(DataTypes.Int)) {
                    System.err.println(String.format(
                        "line %d:%d Expected int, Got %s",
                        ctx.start.getLine(), currentCol+1, header[currentCol]));
                    errors = true;
                }
                else
                    table[currentCol].add(Integer.parseInt(ctx.integer().getText()));
            else if (ctx.bool() != null)
                if (!header[currentCol].accept(DataTypes.Bool)) {
                    System.err.println(String.format(
                        "line %d:%d Expected bool, Got %s",
                        ctx.start.getLine(), currentCol+1, header[currentCol]));
                    errors = true;
                }
                else
                    table[currentCol].add(Boolean.parseBoolean(ctx.bool().getText()));
            else
                table[currentCol].add(null);
        }
        currentCol++;
    }
}
