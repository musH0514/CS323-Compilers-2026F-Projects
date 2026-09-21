grammar Practice1;

expr  	: factor DOT factor DOT factor DOT factor
        ;

factor  : NUMBER
        | ERROR
        ;

DOT: '.';
NUMBER: [0-9] | [0-9][0-9] | '0'[0-9][0-9] | '1'[0-9][0-9] | '2'[0-4][0-9] | '25'[0-5];
ERROR: [0-9][0-9][0-9][0-9]+ | [3-9][0-9][0-9] | '2'[6-9][0-9] | '25'[6-9];
