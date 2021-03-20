grammar ALFA_D;

@header {
    package Gramatica;
}

main: functionDef* codeLine* EOF;

codeLine: (codeBlock | blockStat | stat? ';');

codeBlock: '{' codeLine* '}';

blockStat: conditional
    	| cicles;

stat: ret 
    | assign		
    | print			
    | operation		
    | rename		
    | columnFunction
    | updateElem	
    | declaration	
    | functionCall
    | replace	
;

ret: 'return' expr? ;

columnFunction: column '=' functionCall;

rename: column '->' ID;

table: 'load' '(' STRING ')'						#TABLELOAD
     | 'create' '(' (type ID(','type ID)*)? ')'			#TABLECREATE
     | INDEX table INDEX						#TABLESUB
     | table INDEX							#TABLECOL
     | INDEX table 							#TABLELINE
     | functionCall							#TABLEFUNC
     | ID 								#TABLEID;

column: table SELECTION							#COLSEL
      | table '.' ID 							#COLHEAD
      | 'createCol' '(' type ID ')'					#COLCREATE
      | functionCall							#COLFUNC
      | ID 								#COLID;
 
line: SELECTION table 							#LINESEL
    | ARRAY								#LINEARRAY
    | functionCall							#LINEFUNC
    | ID 								#LINEID;


operation: table 'remove' 'where' '(' ID op=('<'|'>'|'>='|'<='|'=='|'!=') val=(STRING|NUMBER|REAL|BOOLEAN) ')'      #REMOVECOND
         | table 'remove' 'where' '(' '@' NUMBER ')'                            #REMOVEAT
         | table 'remove' 'where' '(' '@' '(' NUMBER '--' NUMBER ')' ')'                #REMOVEATARRAY
         | table 'increase' '(' column ')'                                  #INCREASECOL
         | table 'increase' '(' type ID ')'                             #INCREASEIMPCOL
         | table 'decrease' '(' ID ')'                                  #DECREASECOL
         | table 'add' '(' line ')'                                     #ADDTUPLESBOTTOM
         | table 'add' '(' line ',' NUMBER ')'                              #ADDTUPLESPOSIT
         | table (('clear' '(' ')') | ('remove' 'where' '(' '@' '*' ')'))   #CLEARALL
         | table 'clear' '(' ID ')'                                 #CLEARCOL
         | table 'clear' '(' line ')'                                   #CLEARLINE
         | table 'save' '(' STRING ')'                                  #SAVE;
		 

manipulation: table 'union' table                                           #UNION
        | table 'intersect' table                                           #INTERSECT
        | table 'difference' table                                          #DIFFERENCE
        | table 'join' table                                                #JOINNATURAL
        | table 'join' table 'on' ID op=('<'|'>'|'>='|'<='|'=='|'!=') ID        #JOINON;


condition: left=condition op=('&&' | '||') right=condition          #JOINCOND
	     | left=expr (op=('<'|'>'|'>='|'<='|'=='|'!=') right=expr)?   #COND;


type: 'Table'
    | 'Column'
    | 'Line'
    | 'bool'
    | 'int'
    | 'real'
    | 'void'
    | 'String';


assign: type ID '=' expr				#DECLAREASSIGN
      | ID '=' expr					#ASSIGN;

declaration: type ID;

elem: SELECTION table SELECTION;

updateElem: elem '=' val=(STRING|NUMBER|REAL|BOOLEAN);


expr returns[Type tipo]: '('  expr ')' 					#EXPRPARENTESIS
    | <assoc=right> base=expr op='^' exp=expr 		#EXPREXP
    | left=expr op=('*'|'/') right=expr 		#EXPRMULTDIV
    | left=expr op=('+'|'-') right=expr 		#EXPRADDSUB
    | rename						#EXPRRENAME
    | replace						#EXPRREPLACE
    | manipulation					#EXPRMANIP
    | operation						#EXPROP
    | elem 						#EXPRELEM
    | ID 						#EXPRID
    | functionCall					#EXPRFUNCTION
    | table 						#EXPRTABLE
    | column 						#EXPRCOLLUMN
    | line 						#EXPRLINE
    | BOOLEAN						#EXPRBOOL
    | ('+'|'-')? NUMBER					#EXPRNUMBER
    | ('+'|'-')? REAL 					#EXPRREAL
    | STRING 						#EXPRSTRING
    ;

print: 'print' '(' expr ')';

conditional: 'if' '(' condition ')' codeBlock ('else if' '(' condition ')' codeBlock)* ('else' codeBlock)?;

cicles: cicleFor
      | cicleWhile;

cicleFor: 'for' '(' forInit? ';' condition? ';' forUpdate? ')' codeBlock;

replace: ID op=('++'|'--')  			#REPLACEBY1AFTER
       | op=('--'|'++')ID 				#REPLACEBY1BEFORE
       | ID op=('+='|'-=') expr 			#REPLACEBYID
       | ID '=' expr 				#REPLACEASSIGN;

forInit: assign (',' assign)*;
forUpdate: replace (',' replace)*;

cicleWhile: 'while' '(' condition ')' codeBlock			#WHILE
          | 'do' codeBlock 'while' '(' condition ')'		#DOWHILE
          ;



functionCall: ID '(' (expr (',' expr)*)? ')';

functionDef: 'def' type ID listArgs functionBlock;
listArgs: '(' (arg (',' arg)*)? ')';
arg: type ID;

functionBlock: '{' codeLine* ret ';' '}';



INDEX: '[' [0-9]+ '--' [0-9]+ ']';

SELECTION: '[' [0-9]+ ']';

ARRAY: '<' (STRING|NUMBER|REAL|BOOLEAN)(',' (STRING|NUMBER|REAL|BOOLEAN))*  '>';

BOOLEAN: 'true'|'false';

NULL: 'null';

STRING: '"' ('\\"' | ~'"')*? '"';

ID: [a-zA-Z] [0-9a-zA-Z_]*;

NUMBER: [0-9]+;

REAL: [0-9]+'.'[0-9]+;

WS: [ \r\t\n]+ -> skip;

COMMENT: '--' .*? '\n' -> skip;

MULTILINECOMMENT: '-*' .*? '*-' -> skip;
