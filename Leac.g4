grammar Leac;

program     : 'program' IDF varDeclList funDeclList instr EOF
            ;

varDeclList : varDecl*
            ;

varDecl     : 'var' identList ':' typeName ';'
            ;

identList   : IDF (',' IDF)*
            ;

typeName    : atomType
            | arrayType
            ;

atomType    : 'void'
            | 'bool'
            | 'int'
            | 'string'
            ;

arrayType   : 'array' '[' rangeList ']' 'of' atomType
            ;

rangeList   : boundRange (',' boundRange)*
            ;

boundRange  : bound '..' bound
            ;

bound       : MINUS? INTCST ;

funDeclList : funDecl*
            ;

funDecl     : 'function' IDF '(' argList ')' ':' atomType varDeclList instr
            ;

argList     : ( arg (',' arg)* )?
            ;

arg         : IDF ':' typeName
            | REF IDF ':' typeName
            ;

compInstr   : '{' sequence? '}' # blockInstr
            | 'while' expr 'do' instr # whileInstr
            | 'if' expr 'then' instrIf=instr ('else' instrElse=instr)? # ifInstr
            ;

instr       : singleInstr (';'?) | compInstr
            ;

singleInstr : 'return' expr? # returnInstr
            | 'read' lvalue  # readInstr
            | 'write' expr # writeInstr
            | 'writeln' expr? # writeInstr
            | lvalue '=' expr # affectInstr
            | IDF '(' exprList? ')' # funCallInstr
            ;

sequence    : singleInstr (';' sequence)? (';'?)
            | compInstr sequence?
            ;

lvalue      : IDF ('[' exprList ']')?
            ;

exprList    : expr (',' expr)*
            ;

expr        : cste # cstExpr
            | '(' expr ')' # subExpr
            | IDF '(' exprList? ')' # funCallExpr
            | lvalue # lvalueExpr
            | IDF # idfExpr
            /* opérateurs ordonnés par précédance décroissante (spécifique ANTLR4) */
            |<assoc=right> expr op='^' expr # binaryExpr
            | op=('-' | 'not') expr     # unaryExpr
            | expr op=('*' | '/') expr  # binaryExpr
            | expr op=('+' | '-' ) expr # binaryExpr
            | expr op=('<' | '<=' | '>' | '>=') expr # binaryExpr 
            | expr op=('==' | '!=') expr # binaryExpr
            | expr op='and' expr # binaryExpr
            | expr op='or' expr  # binaryExpr
            ;

cste        : INTCST
            | BOOLCST
            | STRCST
            ;


/* Mots clés */

PROGRAM  : 'program';
FUNCTION : 'function';
VAR      : 'var';
REF      : 'ref';
WHILE    : 'while';
DO       : 'do';
IF       : 'if';
THEN     : 'then';
ELSE     : 'else';
RETURN   : 'return';
READ     : 'read';
WRITE    : 'write';
WRITELN  : 'writeln';

ARRAY    : 'array';
OF       : 'of';

VOID     : 'void';
INT      : 'int';
BOOL     : 'bool';
STRING   : 'string';

NOT      : 'not';
AND      : 'and';
OR       : 'or';

/* Symboles */

COLON    : ':';
SEMICOL  : ';';
COMA     : ',';
RANGE    : '..';

LPAREN   : '(';
RPAREN   : ')';

LBRACE   : '{';
RBRACE   : '}';

LBRACKET : '[';
RBRACKET : ']';

AFFECT   : '=';

EXPONENT : '^';
MINUS    : '-';
PLUS     : '+';
TIMES    : '*';
DIVIDE   : '/';

EQ       : '==';
NEQ      : '!=';
GT       : '>';
GTEQ     : '>=';
LT       : '<';
LTEQ     : '<=';

/* Règles lexicales */

STRCST   : '"' .*? '"';

INTCST   : [0-9]+;

BOOLCST  : 'true'
         | 'false'
         ;

IDF      : [a-zA-Z][a-zA-Z0-9]*;

COMMENT  : '/*' .*? '*/' -> skip;

WS       : [ \r\t\n]+ -> skip;
