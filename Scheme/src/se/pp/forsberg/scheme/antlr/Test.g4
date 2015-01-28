grammar Test;

// Problem:
// 1/2 is a rational
// /2 is an identifier
// (x y) is a list
// parsing "1/2" returns a rational 1/2
// parsing "(1/2)" returns list(1 /2), should be list(1/2)
// We must require numbers to be terminated only by ws () "

program: (ws* thing)+ ws*;
ws: ' ' | '\t' | '\r' | '\n';
thing: symbol | number | string | list;
number: integer | integer slash integer 
//{
//  _input.LT(1).getType() == EOF || "\r\n\t()\042".indexOf(_input.LT(1).getText().charAt(0)) >= 0 
//}?
//{
//  Token t = getCurrentToken();
//  System.out.println("Slash " + t.getText());
//  System.out.println(t.getType() == EOF);
//}
//|
//integer &delimiter
//{
//  Token t = getCurrentToken();
//  System.out.println("No slash " + t.getText());
//  System.out.println(t.getType() == EOF);
//}
;
symbol: initial subsequent*;
delimiter: ws | '(' | ')' | '"' | EOF;
slash: '/';
initial: 'a' | slash;
subsequent: initial | digit;
digit: '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
//{ getCurrentToken().getType() == EOF || "\r\n\t()\042".indexOf(getCurrentToken().getText().charAt(0)) >= 0 }?

integer: digit+;
string: '"' (symbol | number)* '"';
list: '(' thing* ')';