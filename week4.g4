grammar week4;

root: expr <EOF>;
expr: LPAREN expr RPAREN
    | expr ADD expr
    | expr MINUS expr
    | expr MUL expr
    | expr DIV expr
    | factor;
factor: NUMBER;

NUMBER: [0-9]+;
LPAREN: '(';
RPAREN: ')';
ADD: '+';
MINUS: '-';
MUL: '*';
DIV: '/';
WS: [ \t\r\n]+ -> skip;