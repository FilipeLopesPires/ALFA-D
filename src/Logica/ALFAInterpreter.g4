grammar ALFAInterpreter;

@header {
    package Logica;
}

main: headeR lines*;

headeR: headerCell (',' headerCell)* NL;

headerCell: type ':' ID;

type: 'real'
    | 'int'
    | 'String'
    | 'bool'
    ;

lines: cell (',' cell)* NL;

cell: STRING | REAL | integer | bool | ;

integer: NUMBER+;

bool: 'false'
    | 'true'
    ;

STRING: '"' (.|'\\"')*? '"';
ID: LETTER (NUMBER | LETTER)*;
LETTER: [a-zA-Z_];
NUMBER: [0-9];
REAL: NUMBER* '.' NUMBER+;
NL: '\r'? '\n';
WS: [\t ] -> skip;
ERROR: .;
