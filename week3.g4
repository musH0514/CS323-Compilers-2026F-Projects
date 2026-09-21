grammar week3;

fragment ESCAPED_QUOTE: '\\''"';

IF: 'if';
INT: 'int';
STRING: 'String';
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
STRINGS: '"'(ESCAPED_QUOTE | ~["])*'"';
NUMBERS: [0-9]*;
PARENTHESIS: '('|')';
BRACES: '{'|'}';
SEMICOLON: ';';
ADD: '+';
ASSIGNMENT: '=';
GREATER: '>';
WS: [ \t\r\n]+ -> skip;