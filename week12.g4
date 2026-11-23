grammar week12;

// ---------- parser ----------
program : function EOF ;

function : INT Identifier LPAREN RPAREN block ;

block   : LBRACE stmt* RBRACE ;

stmt    : varDecl SEMI
        | assign  SEMI
        | retStmt SEMI
        ;

varDecl : INT Identifier (ASSIGN expr)? ;
assign  : Identifier ASSIGN expr ;
retStmt : RETURN expr ;

expr    : expr ( STAR | DIV) expr               # MulDiv
        | expr ( PLUS|MINUS) expr               # AddSub
        | expr (LE|GE|EQ|NEQ|LT|GT) expr        # Compare
        | LPAREN expr RPAREN                    # Paren
        | Identifier                            # Var
        | NUMBER                                # Num
        ;


RETURN  : 'return';
ASSIGN  : '=';
PLUS    : '+';
MINUS   : '-';
STAR    : '*';
DIV     : '/';
SEMI    : ';';
COMMA   : ',';
LE      : '<=';
GE      : '>=';
EQ      : '==';
NEQ     : '!=';
LT      : '<';
GT      : '>';
LPAREN  : '(';
RPAREN  : ')';
LBRACE  : '{';
RBRACE  : '}';
INT     : 'int';
Identifier      : [a-zA-Z_] [a-zA-Z_0-9]* ;
NUMBER          : [0-9]+ ;
fragment EscapeChar : '\\' ( 'n' | 't' | '\'' | '\\' | '0' );
WS              : [ \t\r\n]+ -> skip ;