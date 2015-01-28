lexer grammar Scheme2Lexer_old;

//TokenWs : IntertokenSpace Token ;
//Token : Identifier | Bool | Number | Character | String | Label | LabelReference | BeginSExpressionComment | Directive
//     | LeftParen | RightParen | BeginVector | BeginByteVector |  Quote | QuasiQuote | Unquote | UnquoteSplicing | Dot;
// Delimiter is what ends a (non-pipe) identifier, a boolean, character, dot or number 

BeginSExpressionComment: '#;';
BeginByteVector: '#' U '8(';
LeftParen: '(';
RightParen: ')';
BeginVector: '#(';
Quote: '\'';
QuasiQuote: '`';
Unquote: ',';
UnquoteSplicing: ',@';
Dot: '.';

// fragment Delimiter : Whitespace | '|' | '(' | ')' | '"' | EOF ;
fragment Delimiter: [ \t\r\n|()"];
fragment DelimitedText: ~[ \t\r\n|()"]*; 
fragment IntralineWhitespace : ' ' | '\t' ;
fragment Whitespace : IntralineWhitespace | LineEnding ;
fragment LineEnding : '\n' | '\r\n' | '\r' ;
fragment Comment : ';' ~('\r' | '\n')* LineEnding | NestedComment ; //| '#;' intertokenSpace datum ;
fragment NestedComment : '#|' CommentText CommentCont* '|#' ;
fragment CommentText : NotNestedComment* ;
fragment NotNestedComment : ~('#' | '|') | '#' ~'|' | '|' ~'#' ;
fragment CommentCont : NestedComment CommentText ;
Directive : '#!' DelimitedText; // f o l d '-' c a s e {foldCase=true;}| '#!' n o '-' f o l d '-' c a s e {foldCase=false;};
fragment Atmosphere : Whitespace | Comment ; // | directive ;
IntertokenSpace : Atmosphere+ -> skip;

Identifier:
   Initial DelimitedText 
 | '|' SymbolElement*  '|'
 | PeculiarIdentifier;

fragment Initial : Letter | SpecialInitial ;

fragment A : 'a' | 'A'; fragment B : 'b' | 'B'; fragment C : 'c' | 'C'; fragment D : 'd' | 'D'; fragment E : 'e' | 'E';
fragment F : 'f' | 'F'; fragment G : 'g' | 'G'; fragment H : 'h' | 'H'; fragment I : 'i' | 'I'; fragment J : 'j' | 'J';
fragment K : 'k' | 'K'; fragment L : 'l' | 'L'; fragment M : 'm' | 'M'; fragment N : 'n' | 'N'; fragment O : 'o' | 'O';
fragment P : 'p' | 'P'; fragment Q : 'q' | 'Q'; fragment R : 'r' | 'R'; fragment S : 's' | 'S'; fragment T : 't' | 'T';
fragment U : 'u' | 'U'; fragment V : 'v' | 'V'; fragment W : 'w' | 'W'; fragment X : 'x' | 'X'; fragment Y : 'y' | 'Y';
fragment Z : 'z' | 'Z';
fragment Letter : A | B | C | D | E | F | G | H | I | J | K | L | M | N | O | P | Q | R | S | T | U | V | W | X | Y | Z ;

fragment SpecialInitial :
   '!' | '$' | '%' | '&' | '*' | '/' | ':' | '<' | '=' | '>' | '?' | '^' | '_' | '~' ;
fragment Subsequent : Initial | Digit10 | SpecialSubsequent ;
fragment Digit10 : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' ;
fragment Digit16 : Digit10 | A | B | C | D | E | F ;
fragment ExplicitSign : '+' | '-' ;
fragment SpecialSubsequent : ExplicitSign | '.' | '@' ;
fragment InlineHexEscape: '\\' X HexScalarValue ';';
fragment HexScalarValue : Digit16+ ;
fragment MnemonicEscape:
   '\\' A 
 | '\\' B 
 | '\\' T 
 | '\\' N 
 | '\\' R;
fragment PeculiarIdentifier : ExplicitSign
                   | ExplicitSign SignSubsequent Subsequent* 
                   | ExplicitSign '.' DotSubsequent Subsequent*
                   | '.' DotSubsequent Subsequent* ;
fragment DotSubsequent : SignSubsequent | '.' ;
fragment SignSubsequent : Initial | ExplicitSign | '@' ;
fragment SymbolElement:
   ~[|\\]
 | InlineHexEscape           
 | MnemonicEscape            
 | '\\|'                     ;

Bool: '#' (T | F) DelimitedText;
Character: '#\\' DelimitedText;

String: '"' StringElement* '"';
fragment StringElement:
   
   ~["\\] 
 | MnemonicEscape     
 | '\\"'              
 | '\\\\'             
 | '\\' IntralineWhitespace* LineEnding IntralineWhitespace*
 | InlineHexEscape;
 
 Label: '#' Digit10+ '=';
 LabelReference: '#' Digit10+ '#';
 
 
 Number:
   '#' (E | I | B | O | D | X) DelimitedText
 | Sign Digit10 DelimitedText
 | Sign '.' Digit10 DelimitedText;
fragment Sign: ExplicitSign | ;