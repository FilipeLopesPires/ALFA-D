package Gramatica;
import java.util.*;
public class SemanticaVisit extends ALFA_DBaseVisitor<Type>{

	private List<Map<String, Type>> variaveis = new ArrayList();
	private Map<String, Type> funcoes = new HashMap();
	private Map<String, HashMap<String, Type>> varFunc = new HashMap();

	private boolean inFunction=false;
	private boolean inIf=false;
	private Type currentFunction=null;
	private String funcName=null;

	private List<String> erros= new ArrayList();



	@Override public Type visitMain(ALFA_DParser.MainContext ctx) { 
		variaveis.add(new HashMap<String, Type>());

		for(int i=0; i<ctx.functionDef().size(); i++){
			funcoes.put(ctx.functionDef().get(i).ID().getText(), Type.valueOf(ctx.functionDef().get(i).type().getText().toUpperCase()));
			varFunc.put(ctx.functionDef().get(i).ID().getText(), new HashMap());
			for(int j=0; j<ctx.functionDef().get(i).listArgs().arg().size(); j++){
				varFunc.get(ctx.functionDef().get(i).ID().getText()).put(ctx.functionDef().get(i).listArgs().arg().get(j).ID().getText(), Type.valueOf(ctx.functionDef().get(i).listArgs().arg().get(j).type().getText().toUpperCase()));
			}
		}

		visitChildren(ctx); 
		if(erros.size()>0){
			Collections.reverse(erros);
			for(String s : erros){
				System.err.print(s);
			}
			System.exit(1);
		}
		return null;
	}
	@Override public Type visitCodeLine(ALFA_DParser.CodeLineContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCodeBlock(ALFA_DParser.CodeBlockContext ctx) { variaveis.add(new HashMap<String, Type>());visitChildren(ctx);variaveis.remove(variaveis.get(variaveis.size()-1));return null; }
	@Override public Type visitBlockStat(ALFA_DParser.BlockStatContext ctx) { return visitChildren(ctx); }
	@Override public Type visitStat(ALFA_DParser.StatContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitRet(ALFA_DParser.RetContext ctx) {
		Type aux=Type.VOID;
		if(!inFunction){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Uso da palavra reservada 'return' no local errado!"));
		}
		if(ctx.expr()!=null){
			if(visit(ctx.expr())!=currentFunction){
				erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Incongruência entre tipo retornado e tipo que se deve retornar!"));
			}
			aux=visit(ctx.expr());
		}

		return aux; 
	}
	
	@Override public Type visitColumnFunction(ALFA_DParser.ColumnFunctionContext ctx) { 
		if(visit(ctx.functionCall())!=Type.COLUMN){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Função tem que retornar uma coluna!"));
		}
		return visitChildren(ctx); 
	}
	
	@Override public Type visitRename(ALFA_DParser.RenameContext ctx) { return visitChildren(ctx); }
	@Override public Type visitTABLECOL(ALFA_DParser.TABLECOLContext ctx) { return visitChildren(ctx); }
	@Override public Type visitTABLESUB(ALFA_DParser.TABLESUBContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitTABLEID(ALFA_DParser.TABLEIDContext ctx) { 
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				return visitChildren(ctx);
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return Type.TABLE;
	}
	
	@Override public Type visitTABLELOAD(ALFA_DParser.TABLELOADContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitTABLEFUNC(ALFA_DParser.TABLEFUNCContext ctx) {
		if(visit(ctx.functionCall())!=Type.TABLE){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Função tem que retornar uma tabela!"));
		}
		return visitChildren(ctx); 
	}
	
	@Override public Type visitTABLECREATE(ALFA_DParser.TABLECREATEContext ctx) { return visitChildren(ctx); }
	@Override public Type visitTABLELINE(ALFA_DParser.TABLELINEContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCOLSEL(ALFA_DParser.COLSELContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCOLHEAD(ALFA_DParser.COLHEADContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCOLCREATE(ALFA_DParser.COLCREATEContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitCOLFUNC(ALFA_DParser.COLFUNCContext ctx) { 
		if(visit(ctx.functionCall())!=Type.COLUMN){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Função tem que retornar uma coluna!"));
		}
		return visitChildren(ctx); 
	}
	
	@Override public Type visitCOLID(ALFA_DParser.COLIDContext ctx) { 
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				return visitChildren(ctx);
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return Type.COLUMN;
	}
	
	@Override public Type visitLINESEL(ALFA_DParser.LINESELContext ctx) { return visitChildren(ctx); }
	@Override public Type visitLINEARRAY(ALFA_DParser.LINEARRAYContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitLINEFUNC(ALFA_DParser.LINEFUNCContext ctx) { 
		if(visit(ctx.functionCall())!=Type.LINE){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Função tem que retornar uma linha!"));
		}
		return visitChildren(ctx); 
	}
	
	@Override public Type visitLINEID(ALFA_DParser.LINEIDContext ctx) { 
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				return visitChildren(ctx);
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return Type.LINE;
	}
	
	@Override public Type visitREMOVECOND(ALFA_DParser.REMOVECONDContext ctx) { return visitChildren(ctx); }
	@Override public Type visitREMOVEAT(ALFA_DParser.REMOVEATContext ctx) { return visitChildren(ctx); }
	@Override public Type visitREMOVEATARRAY(ALFA_DParser.REMOVEATARRAYContext ctx) { return visitChildren(ctx); }
	@Override public Type visitINCREASECOL(ALFA_DParser.INCREASECOLContext ctx) { return visitChildren(ctx); }
	@Override public Type visitINCREASEIMPCOL(ALFA_DParser.INCREASEIMPCOLContext ctx) { return visitChildren(ctx); }
	@Override public Type visitDECREASECOL(ALFA_DParser.DECREASECOLContext ctx) { return visitChildren(ctx); }
	@Override public Type visitADDTUPLESBOTTOM(ALFA_DParser.ADDTUPLESBOTTOMContext ctx) { return visitChildren(ctx); }
	@Override public Type visitADDTUPLESPOSIT(ALFA_DParser.ADDTUPLESPOSITContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCLEARALL(ALFA_DParser.CLEARALLContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCLEARCOL(ALFA_DParser.CLEARCOLContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCLEARLINE(ALFA_DParser.CLEARLINEContext ctx) { return visitChildren(ctx); }
	@Override public Type visitSAVE(ALFA_DParser.SAVEContext ctx) { return visitChildren(ctx); }
	@Override public Type visitUNION(ALFA_DParser.UNIONContext ctx) { return visitChildren(ctx); }
	@Override public Type visitDIFFERENCE(ALFA_DParser.DIFFERENCEContext ctx) { return visitChildren(ctx); }
	@Override public Type visitJOINNATURAL(ALFA_DParser.JOINNATURALContext ctx) { return visitChildren(ctx); }
	@Override public Type visitJOINON(ALFA_DParser.JOINONContext ctx) { return visitChildren(ctx); }
	@Override public Type visitINTERSECT(ALFA_DParser.INTERSECTContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCOND(ALFA_DParser.CONDContext ctx) { return visitChildren(ctx); }//ver!!!!!!!!!!!!!!!!!!!!!
	@Override public Type visitJOINCOND(ALFA_DParser.JOINCONDContext ctx) { return visitChildren(ctx); }//ver!!!!!!!!!!!!!!
	
	@Override public Type visitType(ALFA_DParser.TypeContext ctx) { return Type.valueOf(ctx.getText().toUpperCase()); }
	
	@Override public Type visitDECLAREASSIGN(ALFA_DParser.DECLAREASSIGNContext ctx) {
		if(inFunction){
			for(String aux : varFunc.keySet()){
				if(varFunc.get(aux).containsKey(ctx.ID().getText())){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável já definida!"));
					return visitChildren(ctx);
				}
			}
		}
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável já definida!"));
				return visitChildren(ctx);
			}
		}

		if(visit(ctx.expr())!=visit(ctx.type()) && visit(ctx.expr())!=Type.VOID){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo Inválido!"));
		}

		variaveis.get(variaveis.size()-1).put(ctx.ID().getText(), visit(ctx.type()));
		visitChildren(ctx);
		return visit(ctx.type()); 
	}
	
	@Override public Type visitASSIGN(ALFA_DParser.ASSIGNContext ctx) { 
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				if(visit(ctx.expr())!=variaveis.get(i).get(ctx.ID().getText()) && visit(ctx.expr())!=Type.VOID){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo Inválido!"));
				}
				return visitChildren(ctx);
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return null;
	}
	
	@Override public Type visitDeclaration(ALFA_DParser.DeclarationContext ctx) {
		if(inFunction){
			for(String aux : varFunc.keySet()){
				if(varFunc.get(aux).containsKey(ctx.ID().getText())){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável já definida!"));
					return visitChildren(ctx);
				}
			}
		}
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável já definida!"));
				return visitChildren(ctx);
			}
		}
		variaveis.get(variaveis.size()-1).put(ctx.ID().getText(), visit(ctx.type()));
		return visit(ctx.type()); 
	}
	
	@Override public Type visitElem(ALFA_DParser.ElemContext ctx) { return visitChildren(ctx); }
	@Override public Type visitUpdateElem(ALFA_DParser.UpdateElemContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitEXPRBOOL(ALFA_DParser.EXPRBOOLContext ctx) {visitChildren(ctx); return Type.BOOL; }
	
	@Override public Type visitEXPRNUMBER(ALFA_DParser.EXPRNUMBERContext ctx) { visitChildren(ctx);return Type.INT; }

	@Override public Type visitEXPRFUNCTION(ALFA_DParser.EXPRFUNCTIONContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitEXPRMULTDIV(ALFA_DParser.EXPRMULTDIVContext ctx) { 
		if(visit(ctx.left)!=Type.INT && visit(ctx.left)!=Type.REAL || visit(ctx.right)!=Type.INT && visit(ctx.right)!=Type.REAL){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
		}
		return (visit(ctx.left)==Type.REAL || visit(ctx.right)==Type.REAL)? Type.REAL : Type.INT;
	}
	
	@Override public Type visitEXPRPARENTESIS(ALFA_DParser.EXPRPARENTESISContext ctx) { return visit(ctx.expr()); }
	
	@Override public Type visitEXPRLINE(ALFA_DParser.EXPRLINEContext ctx) { visitChildren(ctx);return Type.LINE; }
	
	@Override public Type visitEXPROP(ALFA_DParser.EXPROPContext ctx) { visitChildren(ctx); return Type.TABLE; }
	
	@Override public Type visitEXPRID(ALFA_DParser.EXPRIDContext ctx) {
		if(inFunction){	
			Map aux=varFunc.get(funcName);
			if(aux.containsKey(ctx.ID().getText()))
				return (Type) aux.get(ctx.ID().getText());
		}

		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				return variaveis.get(i).get(ctx.ID().getText());
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return null;
	}
	
	@Override public Type visitEXPRADDSUB(ALFA_DParser.EXPRADDSUBContext ctx) { 
		if(visit(ctx.left)!=Type.INT && visit(ctx.left)!=Type.REAL && visit(ctx.left)!=Type.STRING || visit(ctx.right)!=Type.INT && visit(ctx.right)!=Type.REAL && visit(ctx.right)!=Type.STRING){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
		}

		if(visit(ctx.left)==Type.STRING || visit(ctx.right)==Type.STRING){return Type.STRING;}
		if(visit(ctx.left)==Type.REAL || visit(ctx.right)==Type.REAL){return Type.REAL;}
		return Type.INT;
	}
	
	@Override public Type visitEXPRTABLE(ALFA_DParser.EXPRTABLEContext ctx) {visitChildren(ctx); return Type.TABLE; }
	
	@Override public Type visitEXPREXP(ALFA_DParser.EXPREXPContext ctx) {
		if(visit(ctx.base)!=Type.INT && visit(ctx.base)!=Type.REAL || visit(ctx.exp)!=Type.INT && visit(ctx.exp)!=Type.REAL){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
		}
		return Type.REAL;
	}
	
	@Override public Type visitEXPRREAL(ALFA_DParser.EXPRREALContext ctx) { visitChildren(ctx);return Type.REAL; }
	
	@Override public Type visitEXPRSTRING(ALFA_DParser.EXPRSTRINGContext ctx) { visitChildren(ctx);return Type.STRING; }
	
	@Override public Type visitEXPRREPLACE(ALFA_DParser.EXPRREPLACEContext ctx) { return visit(ctx.replace()); }
	
	@Override public Type visitEXPRMANIP(ALFA_DParser.EXPRMANIPContext ctx) { visitChildren(ctx);return Type.TABLE; }
	
	@Override public Type visitEXPRELEM(ALFA_DParser.EXPRELEMContext ctx) { return Type.VOID; }
	
	@Override public Type visitEXPRCOLLUMN(ALFA_DParser.EXPRCOLLUMNContext ctx) { visitChildren(ctx);return Type.COLUMN; }
	
	@Override public Type visitEXPRRENAME(ALFA_DParser.EXPRRENAMEContext ctx) { visitChildren(ctx);return Type.COLUMN; }
	
	@Override public Type visitPrint(ALFA_DParser.PrintContext ctx) { return visitChildren(ctx); }
	@Override public Type visitConditional(ALFA_DParser.ConditionalContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCicles(ALFA_DParser.CiclesContext ctx) { return visitChildren(ctx); }
	@Override public Type visitCicleFor(ALFA_DParser.CicleForContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitREPLACEBY1AFTER(ALFA_DParser.REPLACEBY1AFTERContext ctx) {
		
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				if(variaveis.get(i).get(ctx.ID().getText())!=Type.INT && variaveis.get(i).get(ctx.ID().getText())!=Type.REAL){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
				}
				return variaveis.get(i).get(ctx.ID().getText());
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return null;

	}
	
	@Override public Type visitREPLACEBY1BEFORE(ALFA_DParser.REPLACEBY1BEFOREContext ctx) {
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				if(variaveis.get(i).get(ctx.ID().getText())!=Type.INT && variaveis.get(i).get(ctx.ID().getText())!=Type.REAL){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
				}
				return variaveis.get(i).get(ctx.ID().getText());
			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return null;

	}
	
	@Override public Type visitREPLACEBYID(ALFA_DParser.REPLACEBYIDContext ctx) { 
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				
				if(variaveis.get(i).get(ctx.ID().getText())!=Type.INT && variaveis.get(i).get(ctx.ID().getText())!=Type.REAL){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
				}
				if(visit(ctx.expr())!=variaveis.get(i).get(ctx.ID().getText())){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Atribuição inválida!"));
				}
				return variaveis.get(i).get(ctx.ID().getText());

			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return null;


	}
	
	@Override public Type visitREPLACEASSIGN(ALFA_DParser.REPLACEASSIGNContext ctx) {
		for(int i=0; i<variaveis.size(); i++){
			if(variaveis.get(i).containsKey(ctx.ID().getText())){
				
				if(variaveis.get(i).get(ctx.ID().getText())!=Type.INT && variaveis.get(i).get(ctx.ID().getText())!=Type.REAL){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Tipo inválido!"));
				}
				if(visit(ctx.expr())!=variaveis.get(i).get(ctx.ID().getText())){
					erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Atribuição inválida!"));
				}
				return variaveis.get(i).get(ctx.ID().getText());

			}
		}

		erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Variável '"+ctx.ID().getText()+"' não definida!"));
		 
		return null;

	}
	
	@Override public Type visitForInit(ALFA_DParser.ForInitContext ctx) { return visitChildren(ctx); }
	@Override public Type visitForUpdate(ALFA_DParser.ForUpdateContext ctx) { return visitChildren(ctx); }
	@Override public Type visitWHILE(ALFA_DParser.WHILEContext ctx) { return visitChildren(ctx); }
	@Override public Type visitDOWHILE(ALFA_DParser.DOWHILEContext ctx) { return visitChildren(ctx); }
	
	@Override public Type visitFunctionCall(ALFA_DParser.FunctionCallContext ctx) { 
		if(!funcoes.containsKey(ctx.ID().getText())){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Função '"+ctx.ID().getText()+"' não definida!"));
		}
		
		if(ctx.expr().size()!=varFunc.get(ctx.ID().getText()).keySet().size()){
			erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Número de Parametros tem que ser igual!"));
		}

		for(int i=0; i<ctx.expr().size(); i++){
			if(visit(ctx.expr().get(i)) != varFunc.get(ctx.ID().getText()).get((varFunc.get(ctx.ID().getText()).keySet()).toArray()[i])){
				erros.add(String.format("[ERROR at line %d] %s\n", ctx.getStart().getLine(),"Incongruência nos parametros!"));
			}
		}

		return funcoes.get(ctx.ID().getText());
	}
	
	@Override public Type visitFunctionDef(ALFA_DParser.FunctionDefContext ctx) { 
		currentFunction=Type.valueOf(ctx.type().getText().toUpperCase());
		inFunction=true;
		funcName=ctx.ID().getText();
		//funcoes.put(ctx.ID().getText(), Type.valueOf(ctx.type().getText().toUpperCase()));
		visitChildren(ctx);
		inFunction=false;
		return Type.valueOf(ctx.type().getText().toUpperCase());
	}
	
	@Override public Type visitListArgs(ALFA_DParser.ListArgsContext ctx) {
		//varFunc.put(funcName, new HashMap());
		return visitChildren(ctx);
	}
	@Override public Type visitFunctionBlock(ALFA_DParser.FunctionBlockContext ctx) { variaveis.add(new HashMap<String, Type>());visitChildren(ctx);variaveis.remove(variaveis.get(variaveis.size()-1));return null;  }

	@Override public Type visitArg(ALFA_DParser.ArgContext ctx) {
		//varFunc.get(funcName).put(ctx.ID().getText(), Type.valueOf(ctx.type().getText().toUpperCase()));
		return visitChildren(ctx); 
	}
}
