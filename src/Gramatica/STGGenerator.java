package Gramatica;
import org.stringtemplate.v4.*;
import java.util.*;

public class STGGenerator extends ALFA_DBaseVisitor<ST> {
	private STGroupFile stg;
	private static int varCount=0;
	private boolean nextFuncArg=false;
	private List<Map<String, Type>> variaveis = new ArrayList();

	@Override public ST visitMain(ALFA_DParser.MainContext ctx) { 
		variaveis.add(new HashMap<String, Type>());
	    stg = new STGroupFile("Gramatica/java.stg");
	    ST res = stg.getInstanceOf("class");
	    Iterator<ALFA_DParser.CodeLineContext> iter =  ctx.codeLine().iterator();
	    Iterator<ALFA_DParser.FunctionDefContext> iter1 =  ctx.functionDef().iterator();
	    while(iter1.hasNext()){
	    	res.add("function", visit(iter1.next()));
	    }
	    while(iter.hasNext()){
	    	res.add("stat", visit(iter.next()));
	    }
	    return res;

	}


	@Override public ST visitCodeLine(ALFA_DParser.CodeLineContext ctx) { 
		if(ctx.stat()!=null){
			return visit(ctx.stat());
		}
		if(ctx.codeBlock()!=null){
			return visit(ctx.codeBlock());
		}
		if(ctx.blockStat()!=null){
			return visit(ctx.blockStat());
		}
		return null;
	}
	@Override public ST visitCodeBlock(ALFA_DParser.CodeBlockContext ctx) {
		variaveis.add(new HashMap<String, Type>());
		ST res = stg.getInstanceOf("codeBlock");
	    Iterator<ALFA_DParser.CodeLineContext> iter =  ctx.codeLine().iterator();
	    while(iter.hasNext()){
	    	res.add("stat", visit(iter.next()));
	    }
	    variaveis.remove(variaveis.get(variaveis.size()-1));
	    return res;
	}
	@Override public ST visitBlockStat(ALFA_DParser.BlockStatContext ctx) { 
		if(ctx.conditional()!=null){
			return visit(ctx.conditional());
		}
		if(ctx.cicles()!=null){
			return visit(ctx.cicles());
		}
		return null; 
	}
	@Override public ST visitStat(ALFA_DParser.StatContext ctx) { 
		if(ctx.ret()!=null){
			return visit(ctx.ret());
		}
		if(ctx.assign()!=null){
			return visit(ctx.assign());
		}
		if(ctx.print()!=null){
			return visit(ctx.print());
		}
		if(ctx.operation()!=null){
			return visit(ctx.operation());
		}
		if(ctx.rename()!=null){
			return visit(ctx.rename());
		}
		if(ctx.columnFunction()!=null){
			return visit(ctx.columnFunction());
		}
		if(ctx.updateElem()!=null){
			return visit(ctx.updateElem());
		}
		if(ctx.declaration()!=null){
			return visit(ctx.declaration());
		}
		if(ctx.functionCall()!=null){
			return visit(ctx.functionCall());
		}
		if(ctx.replace()!=null){
			return visit(ctx.replace());
		}
		return null;
	}
	@Override public ST visitRet(ALFA_DParser.RetContext ctx) { 
		ST res = stg.getInstanceOf("return");
		if(ctx.expr()!=null){
			res.add("stat", visit(ctx.expr()));
		}
	    return res;
	}

	@Override public ST visitRename(ALFA_DParser.RenameContext ctx) {
	    ST res = stg.getInstanceOf("renameColumn");
        
        res.add("column", visit(ctx.column()).render());
        res.add("name", ctx.ID().getText());
        
        return res;
	}
	@Override public ST visitTABLECOL(ALFA_DParser.TABLECOLContext ctx) {
	    ST res = stg.getInstanceOf("getSubTable");
        
        res.add("beginLine", -1);
        res.add("endLine", -1);
        
        String ind = ctx.INDEX().getText();
        ind = ind.substring(1, ind.length() - 1);
        String[] inds = ind.split("--");
        res.add("beginCol", Integer.parseInt(inds[0]));
        res.add("endCol", Integer.parseInt(inds[1]));
        
        res.add("table1", visit(ctx.table()));
        
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitTABLESUB(ALFA_DParser.TABLESUBContext ctx) {
	    ST res = stg.getInstanceOf("getSubTable");

        String ind = ctx.INDEX(0).getText();
        ind = ind.substring(1, ind.length() - 1);
        String[] inds = ind.split("--");
        res.add("beginLine", Integer.parseInt(inds[0]));
        res.add("endLine", Integer.parseInt(inds[1]));
        
        ind = ctx.INDEX(1).getText();
        ind = ind.substring(1, ind.length() - 1);
        inds = ind.split("--");
        res.add("beginCol", Integer.parseInt(inds[0]));
        res.add("endCol", Integer.parseInt(inds[1]));
        
        res.add("table1", visit(ctx.table()));
        
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitTABLEID(ALFA_DParser.TABLEIDContext ctx) {
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.ID().getText());
        return res;
	}
	@Override public ST visitTABLELOAD(ALFA_DParser.TABLELOADContext ctx) {
	    ST res = stg.getInstanceOf("createTableFile");
        
        res.add("file", ctx.STRING().getText());
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitTABLEFUNC(ALFA_DParser.TABLEFUNCContext ctx) {
	    return visit(ctx.functionCall());
	}
	@Override public ST visitTABLECREATE(ALFA_DParser.TABLECREATEContext ctx) {
	    ST res = stg.getInstanceOf("createTableHeaders");
        ST headers = stg.getInstanceOf("listArgs");
        for (int i = 0; i < ctx.type().size(); i++) {
            ST header = stg.getInstanceOf("createHeader");
            header.add("dataType", ctx.type(i).getText());
            header.add("name", ctx.ID(i).getText());
            headers.add("arg", header.render());
        }
        
        res.add("header", headers.render());
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitTABLELINE(ALFA_DParser.TABLELINEContext ctx) {
	    ST res = stg.getInstanceOf("getSubTable");
        
        String ind = ctx.INDEX().getText();
        ind = ind.substring(1, ind.length() - 1);
        String[] inds = ind.split("--");
        res.add("beginLine", Integer.parseInt(inds[0]));
        res.add("endLine", Integer.parseInt(inds[1]));

        res.add("beginCol", -1);
        res.add("endCol", -1);
        
        res.add("table1", visit(ctx.table()));
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitCOLSEL(ALFA_DParser.COLSELContext ctx) {
	    ST res = stg.getInstanceOf("tableGetColumnIndex");
	    
	    res.add("table", visit(ctx.table()).render());
	    String index = ctx.SELECTION().getText();
	    index = index.substring(1, index.length() - 1);
	    res.add("index", index);
	    res.add("line", ctx.start.getLine());
	    return res;
	}
	@Override public ST visitCOLHEAD(ALFA_DParser.COLHEADContext ctx) {
        ST res = stg.getInstanceOf("tableGetColumnName");
        
        res.add("table", visit(ctx.table()).render());
        res.add("name", ctx.ID().getText());
        res.add("line", ctx.start.getLine());
        return res;
	}
	@Override public ST visitCOLCREATE(ALFA_DParser.COLCREATEContext ctx) {
	    ST res = stg.getInstanceOf("newColumn");
	    
	    ST header = stg.getInstanceOf("createHeader");
	    header.add("dataType", ctx.type().getText());
	    header.add("name", ctx.ID().getText());
	    
	    res.add("line", ctx.start.getLine());
	    res.add("header", header);
	    
	    return res;
	}
	@Override public ST visitCOLFUNC(ALFA_DParser.COLFUNCContext ctx) {
	    return visit(ctx.functionCall());
	}
	@Override public ST visitCOLID(ALFA_DParser.COLIDContext ctx) {
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.ID().getText());
        return res;
	}
	@Override public ST visitLINESEL(ALFA_DParser.LINESELContext ctx) {
	    ST res = stg.getInstanceOf("tableGetLineIndex");
	    
	    res.add("table", visit(ctx.table()).render());
	    String index = ctx.SELECTION().getText();
	    index = index.substring(1, index.length() - 1);
	    res.add("index", index);
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitLINEARRAY(ALFA_DParser.LINEARRAYContext ctx) {
	    ST res = stg.getInstanceOf("newLineArray");
	    
	    String arr = ctx.ARRAY().getText();
	    arr = arr.substring(1, arr.length() - 1);
	    String[] objs = arr.split(",");
	    
	    for (int i = 0; i < objs.length; i++)
	        res.add("obj", objs[i]);
	    
	    return res;
	}
	@Override public ST visitLINEFUNC(ALFA_DParser.LINEFUNCContext ctx) {
	    return visit(ctx.functionCall());
	}
	@Override public ST visitLINEID(ALFA_DParser.LINEIDContext ctx) {
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.ID().getText());
        return res;
	}
	@Override public ST visitREMOVECOND(ALFA_DParser.REMOVECONDContext ctx) {
	    ST res = stg.getInstanceOf("removeWithCompareOperation");
	    
	    res.add("table", visit(ctx.table()).render());
	    res.add("colName", ctx.ID().getText());
	    res.add("op", ctx.op.getText());
	    res.add("obj", ctx.val.getText());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitREMOVEAT(ALFA_DParser.REMOVEATContext ctx) {
	    ST res = stg.getInstanceOf("removeTableIndex");
	    
	    res.add("table", visit(ctx.table()).render());
	    res.add("index", ctx.NUMBER().getText());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitREMOVEATARRAY(ALFA_DParser.REMOVEATARRAYContext ctx) {
	    ST res = stg.getInstanceOf("removeTableIndexInterval");
	    
	    res.add("table", visit(ctx.table()).render());
	    res.add("index1", ctx.NUMBER(0).getText());
	    res.add("index2", ctx.NUMBER(1).getText());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}

	@Override public ST visitINCREASECOL(ALFA_DParser.INCREASECOLContext ctx) {
	    ST res = stg.getInstanceOf("increaseTable");

	    res.add("table", visit(ctx.table()).render());
	    res.add("header", visit(ctx.column()).render());
	    res.add("line", ctx.start.getLine());

	    return res;
	}
	@Override public ST visitINCREASEIMPCOL(ALFA_DParser.INCREASEIMPCOLContext ctx) {
	    ST res = stg.getInstanceOf("increaseTable");
	    
	    res.add("table", visit(ctx.table()).render());

	    ST header = stg.getInstanceOf("createHeader");
	    header.add("dataType", ctx.type().getText());
	    header.add("name", ctx.ID().getText());

	    res.add("header", header.render());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitDECREASECOL(ALFA_DParser.DECREASECOLContext ctx) {
	    ST res = stg.getInstanceOf("decreaseTableHeaderName");
	    
	    res.add("table", visit(ctx.table()).render());
	    res.add("name", ctx.ID().getText());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitADDTUPLESBOTTOM(ALFA_DParser.ADDTUPLESBOTTOMContext ctx) {
	    ST res = stg.getInstanceOf("addTableLines");
        
        res.add("table", visit(ctx.table()).render());
        res.add("lineClass", visit(ctx.line()).render());
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitADDTUPLESPOSIT(ALFA_DParser.ADDTUPLESPOSITContext ctx) {
	    ST res = stg.getInstanceOf("addTableLineIndex");
	    
	    res.add("table", visit(ctx.table()).render());
	    res.add("index", ctx.NUMBER().getText());
	    res.add("lineClass", visit(ctx.line()).render());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitCLEARALL(ALFA_DParser.CLEARALLContext ctx) {
	    ST res = stg.getInstanceOf("clearTable");
	    
	    res.add("table", visit(ctx.table()).render());

	    
	    return res;
	}
    @Override public ST visitCLEARCOL(ALFA_DParser.CLEARCOLContext ctx) {
        ST res = stg.getInstanceOf("clearTableHeader");
        
        res.add("table", visit(ctx.table()).render());
        res.add("column", ctx.ID().getText());
        res.add("line", ctx.start.getLine());
        
        return res;
    }
    @Override public ST visitCLEARLINE(ALFA_DParser.CLEARLINEContext ctx) {
        ST res = stg.getInstanceOf("tableClearLine");
        
        res.add("table", visit(ctx.table()).render());
        res.add("lineClass", visit(ctx.line()).render());
        res.add("line", ctx.start.getLine());
        
        return res;
    }
    @Override public ST visitSAVE(ALFA_DParser.SAVEContext ctx) {
        ST res = stg.getInstanceOf("saveTableNameFile");
        
        res.add("table", visit(ctx.table()).render());
        res.add("fileName", ctx.STRING().getText());
        res.add("line", ctx.start.getLine());
        
        return res;
    }
	@Override public ST visitUNION(ALFA_DParser.UNIONContext ctx) {
	    ST res = stg.getInstanceOf("unionTables");
	    
	    res.add("table1", visit(ctx.table(0)).render());
	    res.add("table2", visit(ctx.table(1)).render());
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitINTERSECT(ALFA_DParser.INTERSECTContext ctx) {
	    ST res = stg.getInstanceOf("intersectionTables");
        
        res.add("table1", visit(ctx.table(0)).render());
        res.add("table2", visit(ctx.table(1)).render());
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitDIFFERENCE(ALFA_DParser.DIFFERENCEContext ctx) {
        ST res = stg.getInstanceOf("differenceTables");
        
        res.add("table1", visit(ctx.table(0)).render());
        res.add("table2", visit(ctx.table(1)).render());
        res.add("line", ctx.start.getLine());
        
        return res;
    }
	@Override public ST visitJOINNATURAL(ALFA_DParser.JOINNATURALContext ctx) {
        ST res = stg.getInstanceOf("joinTables");
        
        res.add("table1", visit(ctx.table(0)).render());
        res.add("table2", visit(ctx.table(1)).render());
        res.add("line", ctx.start.getLine());
        
        return res;
    }
	@Override public ST visitJOINON(ALFA_DParser.JOINONContext ctx) {
        ST res = stg.getInstanceOf("joinTablesWithCompare");
        
        res.add("table1", visit(ctx.table(0)).render());
        res.add("table2", visit(ctx.table(1)).render());
        res.add("colName1", ctx.ID(0).getText());
        res.add("op", ctx.op.getText());
        res.add("colName2", ctx.ID(1).getText());
        res.add("line", ctx.start.getLine());
        
        return res;
	}
	@Override public ST visitElem(ALFA_DParser.ElemContext ctx) {
	    ST res = stg.getInstanceOf("tableElem");
	    
	    res.add("table", visit(ctx.table()).render());
	    String index = ctx.SELECTION(0).getText();
	    index = index.substring(1, index.length() - 1);
	    res.add("index1", index);
	    index = ctx.SELECTION(1).getText();
	    index = index.substring(1, index.length() - 1);
	    res.add("index2", index);
	    res.add("line", ctx.start.getLine());
	    
	    return res;
	}
	@Override public ST visitUpdateElem(ALFA_DParser.UpdateElemContext ctx) {
	    ST res = stg.getInstanceOf("tableSetElem");
	    
	    res.add("table", visit(ctx.elem().table()).render());
	    String index = ctx.elem().SELECTION(0).getText();
        index = index.substring(1, index.length() - 1);
        res.add("index1", index);
        index = ctx.elem().SELECTION(1).getText();
        index = index.substring(1, index.length() - 1);
        res.add("index2", index);
        res.add("obj", ctx.val.getText());
        res.add("line", ctx.start.getLine());
	    
	    return res;
	}

	@Override public ST visitType(ALFA_DParser.TypeContext ctx) { return visitChildren(ctx); } //nao é preciso implementar este

	@Override public ST visitCOND(ALFA_DParser.CONDContext ctx) {
        if(ctx.op!=null){
        	ST left=visit(ctx.left);
        	ST right=visit(ctx.right);

        	if(ctx.left.tipo==Type.STRING || ctx.right.tipo==Type.STRING){
        		ST res = stg.getInstanceOf("compareStrings");
	        	res.add("string1", left);
	        	res.add("string2", right);
	        	return res;
        	}
        	ST res = stg.getInstanceOf("compare");
        	res.add("object1", left);
        	res.add("object2", right);
        	res.add("name", ctx.op.getText());
        	return res;
        }
        ST res = stg.getInstanceOf("elem");
    	res.add("stat", visit(ctx.left));
    	return res;
	}
	@Override public ST visitJOINCOND(ALFA_DParser.JOINCONDContext ctx) {
		ST res = stg.getInstanceOf("compare");
        res.add("object1", visit(ctx.left));
        res.add("object2", visit(ctx.right));
        res.add("name", ctx.op.getText());
        return res;
	}
	@Override public ST visitDECLAREASSIGN(ALFA_DParser.DECLAREASSIGNContext ctx) {
		variaveis.get(variaveis.size()-1).put(ctx.ID().getText(), Type.valueOf(ctx.type().getText().toUpperCase()));
		ST res = stg.getInstanceOf("assignType");
        res.add("type", ctx.type().getText());
        res.add("lhs", ctx.ID().getText());
        res.add("rhs", visit(ctx.expr()));
        return res;
	}
	@Override public ST visitASSIGN(ALFA_DParser.ASSIGNContext ctx) {
		ST res = stg.getInstanceOf("assign");
        res.add("lhs", ctx.ID().getText());
        res.add("rhs", visit(ctx.expr()));
        return res;
	}
	@Override public ST visitDeclaration(ALFA_DParser.DeclarationContext ctx) {
		variaveis.get(variaveis.size()-1).put(ctx.ID().getText(), Type.valueOf(ctx.type().getText().toUpperCase()));	
		ST res = stg.getInstanceOf("assignType");
        res.add("type", ctx.type().getText());
        res.add("lhs", ctx.ID().getText());
        return res;
	}

	@Override public ST visitEXPRBOOL(ALFA_DParser.EXPRBOOLContext ctx) {
		ctx.tipo=Type.BOOL;
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.BOOLEAN().getText());
        return res;
	}
	@Override public ST visitEXPRNUMBER(ALFA_DParser.EXPRNUMBERContext ctx) { 
		ctx.tipo=Type.INT;
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.getText());
        return res;
	}
	@Override public ST visitEXPRMULTDIV(ALFA_DParser.EXPRMULTDIVContext ctx) {
		if(ctx.left.tipo==Type.REAL || ctx.right.tipo==Type.REAL){
			ctx.tipo=Type.REAL;
		}
		else {
			ctx.tipo=Type.INT;
		}
		ST res = stg.getInstanceOf("math");
        res.add("object1", visit(ctx.left));
        res.add("object2", visit(ctx.right));
        res.add("name", ctx.op.getText());
        return res;
	}
	@Override public ST visitEXPRPARENTESIS(ALFA_DParser.EXPRPARENTESISContext ctx) {
		ctx.tipo=ctx.expr().tipo;
	    ST res = stg.getInstanceOf("parentheses");
	    
	    res.add("expr", visit(ctx.expr()).render());
	    
	    return res;
	}
	@Override public ST visitEXPRLINE(ALFA_DParser.EXPRLINEContext ctx) { ctx.tipo=Type.LINE;return visit(ctx.line()); }
	@Override public ST visitEXPROP(ALFA_DParser.EXPROPContext ctx) { ctx.tipo=Type.TABLE;return visit(ctx.operation()); }
	@Override public ST visitEXPRID(ALFA_DParser.EXPRIDContext ctx) {
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				ctx.tipo=variaveis.get(i).get(ctx.ID().getText());
			}
		}
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.ID().getText());
        return res;
	}
	@Override public ST visitEXPRADDSUB(ALFA_DParser.EXPRADDSUBContext ctx) {
		if(ctx.left.tipo==Type.STRING || ctx.right.tipo==Type.STRING){
			ctx.tipo=Type.STRING;
		}
		else if(ctx.left.tipo==Type.REAL || ctx.right.tipo==Type.REAL){
			ctx.tipo=Type.REAL;
		}
		else {
			ctx.tipo=Type.INT;
		}
		ST res = stg.getInstanceOf("math");
        res.add("object1", visit(ctx.left));
        res.add("object2", visit(ctx.right));
        res.add("name", ctx.op.getText());
        return res;
	}
	@Override public ST visitEXPRTABLE(ALFA_DParser.EXPRTABLEContext ctx) { ctx.tipo=Type.TABLE;return visit(ctx.table()); }

	@Override public ST visitEXPREXP(ALFA_DParser.EXPREXPContext ctx) {
		ctx.tipo=(ctx.base.tipo==Type.REAL || ctx.exp.tipo==Type.REAL)? Type.REAL : Type.INT;
	    ST res = stg.getInstanceOf("power");
	    
	    res.add("base", visit(ctx.base).render());
	    res.add("exp", visit(ctx.exp).render());
	    
	    return res;
	}

	@Override public ST visitEXPRREAL(ALFA_DParser.EXPRREALContext ctx) {
		ctx.tipo=Type.REAL;
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.getText());
        return res;
	}
	@Override public ST visitEXPRSTRING(ALFA_DParser.EXPRSTRINGContext ctx) {
		ctx.tipo=Type.STRING;
		ST res = stg.getInstanceOf("elem");
        res.add("stat", ctx.STRING().getText());
        return res;
	}
	@Override public ST visitEXPRREPLACE(ALFA_DParser.EXPRREPLACEContext ctx) { ctx.tipo=Type.REAL;return visit(ctx.replace()); }
	@Override public ST visitEXPRMANIP(ALFA_DParser.EXPRMANIPContext ctx) { ctx.tipo=Type.TABLE;return visit(ctx.manipulation()); }
	@Override public ST visitEXPRELEM(ALFA_DParser.EXPRELEMContext ctx) { ctx.tipo=Type.VOID;return visit(ctx.elem()); }
	@Override public ST visitEXPRCOLLUMN(ALFA_DParser.EXPRCOLLUMNContext ctx) { ctx.tipo=Type.COLUMN;return visit(ctx.column()); }
	@Override public ST visitEXPRFUNCTION(ALFA_DParser.EXPRFUNCTIONContext ctx) { return visit(ctx.functionCall()); }
	@Override public ST visitEXPRRENAME(ALFA_DParser.EXPRRENAMEContext ctx) { ctx.tipo=Type.COLUMN;return visit(ctx.rename()); }
	@Override public ST visitPrint(ALFA_DParser.PrintContext ctx) {
		ST res = stg.getInstanceOf("print");
	    res.add("elem", visit(ctx.expr()));
	    return res;
	}
	@Override public ST visitConditional(ALFA_DParser.ConditionalContext ctx) {
		ST aux = stg.getInstanceOf("ifStat");
		ST res = aux;
		int i;
	    for(i = 0; i<ctx.condition().size(); i++){
	    	aux.add("cond", visit(ctx.condition().get(i)));
	    	aux.add("ifStats", visit(ctx.codeBlock().get(i)));
	    	if(i<ctx.condition().size()-1){
	    		ST aux2 = stg.getInstanceOf("ifStat");
		    	aux.add("elseStats", aux2);
		    	aux=aux2;
	    	}
	    }
	    
	    if(ctx.codeBlock().size()>ctx.condition().size()){
	    	aux.add("elseStats", visit(ctx.codeBlock().get(i)));
	    }

	    return res;
	}
	@Override public ST visitCicles(ALFA_DParser.CiclesContext ctx) {
		if(ctx.cicleFor()!=null){
			return visit(ctx.cicleFor());
		}
		if(ctx.cicleWhile()!=null){
			return visit(ctx.cicleWhile());
		}
		return null; 
	}
	@Override public ST visitCicleFor(ALFA_DParser.CicleForContext ctx) {
		ST res = stg.getInstanceOf("forLoop");
		if(ctx.forInit()!=null){
			res.add("initFor", visit(ctx.forInit()));
		}
	    if(ctx.condition()!=null){
			res.add("condition", visit(ctx.condition()));
		}
		if(ctx.forUpdate()!=null){
			res.add("update", visit(ctx.forUpdate()));
		}
	    res.add("stat", visit(ctx.codeBlock()));
	    return res;
	}
	@Override public ST visitREPLACEBY1AFTER(ALFA_DParser.REPLACEBY1AFTERContext ctx) {
		ST res = stg.getInstanceOf("replaceByOneAfter");
        res.add("number", ctx.ID().getText());
        res.add("op", ctx.op.getText());
        return res;
	}
	@Override public ST visitREPLACEBY1BEFORE(ALFA_DParser.REPLACEBY1BEFOREContext ctx) {
		ST res = stg.getInstanceOf("replaceByOneBefore");
        res.add("number", ctx.ID().getText());
        res.add("op", ctx.op.getText());
        return res;
	}
	@Override public ST visitREPLACEBYID(ALFA_DParser.REPLACEBYIDContext ctx) {
		ST res = stg.getInstanceOf("replaceByN");
        res.add("number", ctx.ID().getText());
        res.add("op", ctx.op.getText());
        res.add("stat", visit(ctx.expr()));
        return res;
	}
	@Override public ST visitREPLACEASSIGN(ALFA_DParser.REPLACEASSIGNContext ctx) {
		ST res = stg.getInstanceOf("assign");
        res.add("lhs", ctx.ID().getText());
        res.add("rhs", visit(ctx.expr()));
        return res;
	}
	@Override public ST visitForInit(ALFA_DParser.ForInitContext ctx) {
		ST res = stg.getInstanceOf("listArgs");
        Iterator<ALFA_DParser.AssignContext> iter = ctx.assign().iterator();
	    while(iter.hasNext()){
	    	res.add("arg", visit(iter.next()));
	    }
        return res;
	}
	@Override public ST visitForUpdate(ALFA_DParser.ForUpdateContext ctx) {
		ST res = stg.getInstanceOf("listArgs");
        Iterator<ALFA_DParser.ReplaceContext> iter = ctx.replace().iterator();
	    while(iter.hasNext()){
	    	res.add("arg", visit(iter.next()));
	    }
        return res;
	}
	@Override public ST visitWHILE(ALFA_DParser.WHILEContext ctx) {
		ST res = stg.getInstanceOf("whileLoop");
        res.add("condition", visit(ctx.condition()));
        res.add("stat", visit(ctx.codeBlock()));
        return res;
	}
	@Override public ST visitDOWHILE(ALFA_DParser.DOWHILEContext ctx) {
		ST res = stg.getInstanceOf("doWhileLoop");
        res.add("condition", visit(ctx.condition()));
        res.add("stat", visit(ctx.codeBlock()));
        return res;
	}
	@Override public ST visitFunctionCall(ALFA_DParser.FunctionCallContext ctx) { 
		ST res = stg.getInstanceOf("functionCall");
	    res.add("f", ctx.ID().getText());
	    Iterator<ALFA_DParser.ExprContext> iter = ctx.expr().iterator();
	    ST aux=stg.getInstanceOf("listArgs");
	    while(iter.hasNext()){
	    	aux.add("arg", visit(iter.next()));
	    }
	    res.add("list", aux);
	    return res;
	}
	@Override public ST visitFunctionDef(ALFA_DParser.FunctionDefContext ctx) { 
		ST res = stg.getInstanceOf("createFunction");
	    res.add("name", ctx.ID().getText());
	    res.add("type", ctx.type().getText());
	    res.add("listArgs", visit(ctx.listArgs()));
	    res.add("block", visit(ctx.functionBlock()));
	    return res;
	}
	@Override public ST visitListArgs(ALFA_DParser.ListArgsContext ctx) { 
		ST res = stg.getInstanceOf("listArgs");
		Iterator<ALFA_DParser.ArgContext> iter = ctx.arg().iterator();
	    while(iter.hasNext()){
	    	res.add("arg", visit(iter.next()));
	    }
	    return res;
	}
	@Override public ST visitArg(ALFA_DParser.ArgContext ctx) { 
		ST res = stg.getInstanceOf("arg");
	    res.add("name", ctx.ID().getText());
	    res.add("type", ctx.type().getText());
	    return res;
	}
	@Override public ST visitFunctionBlock(ALFA_DParser.FunctionBlockContext ctx) { 
		variaveis.add(new HashMap<String, Type>());
		ST res = stg.getInstanceOf("codeBlock");
	    Iterator<ALFA_DParser.CodeLineContext> iter =  ctx.codeLine().iterator();
	    while(iter.hasNext()){
	    	res.add("stat", visit(iter.next()));
	    }
	    if(ctx.ret()!=null){
	    	res.add("stat", visit(ctx.ret()));
	    }
	    variaveis.remove(variaveis.get(variaveis.size()-1));
	    return res;
	}
}
