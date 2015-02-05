grammar Scheme;
@header {
  import se.pp.forsberg.scheme.values.*;
  import se.pp.forsberg.scheme.values.numbers.*;
  import se.pp.forsberg.scheme.SyntaxErrorException;
  import java.lang.String;
  import java.lang.Character;
  import java.lang.Integer;
  import java.lang.Number;
}

@parser::members {
  boolean foldCase = false;
}

tokenWs : intertokenSpace token ;
token : identifier | bool | number | character | string | label
      | '(' | ')' | '#(' | '#' u '8(' |  '\'' | '`' | ',' | ',@' ;
// Delimiter is what ends a (non-pipe) identifier, a boolean, character, dot or number 
delimiterChar : ' ' | '\t'| '\r' | '\n' | '|' | '(' | ')' | '"' | EOF ;
delimiter : whitespace | '|' | '(' | ')' | '"' | EOF ;
intralineWhitespace : ' ' | '\t' ;
whitespace : intralineWhitespace | lineEnding ;
lineEnding : '\n' | '\r\n' | '\r' ;
comment : ';' ~('\r' | '\n')* lineEnding | nestedComment | '#;' intertokenSpace datum ;
nestedComment : '#|' commentText commentCont* '|#' ;
commentText : notNestedComment* ;
notNestedComment : ~('#' | '|') | '#' ~'|' | '|' ~'#' ;
commentCont : nestedComment commentText ;
directive : '#!' f o l d '-' c a s e {foldCase=true;}| '#!' n o '-' f o l d '-' c a s e {foldCase=false;};
atmosphere : whitespace | comment | directive ;
intertokenSpace : atmosphere* ;

number returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   num2  { $value = $num2.value; } 
 | num8  { $value = $num8.value; } 
 | num10 // { " \t\r\n\042)|()".indexOf(getCurrentToken().getText().charAt(0)) > 0 }?
          { $value = $num10.value; } 
 | num16 { $value = $num16.value; };
//   num[2]  { $value = $num.value; } 
// | num[8]  { $value = $num.value; } 
// | num[10] { $value = $num.value; } 
// | num[16] { $value = $num.value; } ;
num2 returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   prefix2 complex2[$prefix2.value.isExact()] { $value = $complex2.value; };
num8 returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   prefix8 complex8[$prefix8.value.isExact()] { $value = $complex8.value; };
num10 returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   prefix10 complex10[$prefix10.value.isExact()] { $value = $complex10.value; };
num16 returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   prefix16 complex16[$prefix16.value.isExact()] { $value = $complex16.value; };
//num[int rx] returns [se.pp.forsberg.scheme.values.numbers.Number value] :
//   prefix[rx] complex[rx, $prefix.value.isExact()] { $value = $complex.value; };
complex2[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   real2[ex]                               { $value = $real2.value; }
 | r1 = real2[ex] '@' r2 = real2[ex]    { $value = Complex.makePolar($r1.value, $r2.value).simplify(); }
 | r2 = real2[ex] '+' r3 = ureal2[ex] i { $value = new Complex($r2.value, $r3.value, $ex).simplify(); }
 | r4 = real2[ex] '-' r5 = ureal2[ex] i { $value = new Complex($r4.value, $r5.value.negate(), $ex).simplify(); }
 | real2[ex] '+' i                         { $value = new Complex($real2.value, LongInteger.ONE, $ex).simplify(); }
 | real2[ex] '-' i                         { $value = new Complex($real2.value, LongInteger.MINUS_ONE, $ex).simplify(); }
 | real2[ex] infnan i                      { $value = new Complex($real2.value, $infnan.value, $ex).simplify(); }
 | '+' ureal2[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal2.value, $ex).simplify(); }
 | '-' ureal2[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal2.value.negate(), $ex).simplify(); }
 | infnan i                                   { $value = new Complex(LongInteger.ZERO, $infnan.value, $ex).simplify(); } 
 | '+' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.ONE, $ex).simplify(); }
 | '-' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.MINUS_ONE, $ex).simplify(); } ;
complex8[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   real8[ex]                               { $value = $real8.value; }
 | r1 = real8[ex] '@' r2 = real8[ex]    { $value = Complex.makePolar($r1.value, $r2.value).simplify(); }
 | r3 = real8[ex] '+' r4 = ureal8[ex] i { $value = new Complex($r3.value, $r4.value, $ex).simplify(); }
 | r5 = real8[ex] '-' r6 = ureal8[ex] i { $value = new Complex($r5.value, $r6.value.negate(), $ex).simplify(); }
 | real8[ex] '+' i                         { $value = new Complex($real8.value, LongInteger.ONE, $ex).simplify(); }
 | real8[ex] '-' i                         { $value = new Complex($real8.value, LongInteger.MINUS_ONE, $ex).simplify(); }
 | real8[ex] infnan i                      { $value = new Complex($real8.value, $infnan.value, $ex).simplify(); }
 | '+' ureal8[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal8.value, $ex).simplify(); }
 | '-' ureal8[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal8.value.negate(), $ex).simplify(); }
 | infnan i                                   { $value = new Complex(LongInteger.ZERO, $infnan.value, $ex).simplify(); } 
 | '+' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.ONE, $ex).simplify(); }
 | '-' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.MINUS_ONE, $ex).simplify(); } ;
complex10[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   real10[ex]                               { $value = $real10.value; }
 | r1 = real10[ex] '@' r2 = real10[ex]    { $value = Complex.makePolar($r1.value, $r2.value).simplify(); }
 | r2 = real10[ex] '+' r3 = ureal10[ex] i { $value = new Complex($r2.value, $r3.value, $ex).simplify(); }
 | r4 = real10[ex] '-' r5 = ureal10[ex] i { $value = new Complex($r4.value, $r5.value.negate(), $ex).simplify(); }
 | real10[ex] '+' i                         { $value = new Complex($real10.value, LongInteger.ONE, $ex).simplify(); }
 | real10[ex] '-' i                         { $value = new Complex($real10.value, LongInteger.MINUS_ONE, $ex).simplify(); }
 | real10[ex] infnan i                      { $value = new Complex($real10.value, $infnan.value, $ex).simplify(); }
 | '+' ureal10[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal10.value, $ex).simplify(); }
 | '-' ureal10[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal10.value.negate(), $ex).simplify(); }
 | infnan i                                   { $value = new Complex(LongInteger.ZERO, $infnan.value, $ex).simplify(); } 
 | '+' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.ONE, $ex).simplify(); }
 | '-' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.MINUS_ONE, $ex).simplify(); } ;
complex16[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Number value] :
   real16[ex]                               { $value = $real16.value; }
 | r1 = real16[ex] '@' r2 = real16[ex]    { $value = Complex.makePolar($r1.value, $r2.value).simplify(); }
 | r2 = real16[ex] '+' r3 = ureal16[ex] i { $value = new Complex($r2.value, $r3.value, $ex).simplify(); }
 | r4 = real16[ex] '-' r5 = ureal16[ex] i { $value = new Complex($r4.value, $r5.value.negate(), $ex).simplify(); }
 | real16[ex] '+' i                         { $value = new Complex($real16.value, LongInteger.ONE, $ex).simplify(); }
 | real16[ex] '-' i                         { $value = new Complex($real16.value, LongInteger.MINUS_ONE, $ex).simplify(); }
 | real16[ex] infnan i                      { $value = new Complex($real16.value, $infnan.value, $ex).simplify(); }
 | '+' ureal16[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal16.value, $ex).simplify(); }
 | '-' ureal16[ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal16.value.negate(), $ex).simplify(); }
 | infnan i                                   { $value = new Complex(LongInteger.ZERO, $infnan.value, $ex).simplify(); } 
 | '+' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.ONE, $ex).simplify(); }
 | '-' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.MINUS_ONE, $ex).simplify(); } ;
//complex[int rx, boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Number value] :
//   real[rx, ex]                               { $value = $real.value; }
// | r1 = real[rx, ex] '@' r2 = real[rx, ex]    { $value = Complex.fromPolar($r1.value, $r2.value, $ex); }
// | r2 = real[rx, ex] '+' r3 = ureal[rx, ex] i { $value = new Complex($r2.value, $r3.value, $ex); }
// | r4 = real[rx,ex ] '-' r5 = ureal[rx, ex] i { $value = new Complex($r4.value, $r5.value.negate(), $ex); }
// | real[rx, ex] '+' i                         { $value = new Complex($real.value, LongInteger.ONE, $ex); }
// | real[rx, ex] '-' i                         { $value = new Complex($real.value, LongInteger.MINUS_ONE, $ex); }
// | real[rx, ex] infnan i                      { $value = new Complex($real.value, $infnan.value, $ex); }
// | '+' ureal[rx, ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal.value, $ex); }
// | '-' ureal[rx, ex] i                        { $value = new Complex(LongInteger.ZERO, $ureal.value.negate(), $ex); }
// | infnan i                                   { $value = new Complex(LongInteger.ZERO, $infnan.value, $ex); } 
// | '+' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.ONE, $ex); }
// | '-' i                                      { $value = new Complex(LongInteger.ZERO, LongInteger.MINUS_ONE, $ex); } 
real2[boolean ex] returns [Real value]:
   sign ureal2[ex] { $value = $sign.text.equals("-") ? $ureal2.value.negate() : $ureal2.value; }
 | infnan             { $value = $infnan.value; };
real8[boolean ex] returns [Real value]:
   sign ureal8[ex] { $value = $sign.text.equals("-") ? $ureal8.value.negate() : $ureal8.value; }
 | infnan             { $value = $infnan.value; };
real10[boolean ex] returns [Real value]:
   sign ureal10[ex] { $value = $sign.text.equals("-") ? $ureal10.value.negate() : $ureal10.value; }
 | infnan             { $value = $infnan.value; };
real16[boolean ex] returns [Real value]:
   sign ureal16[ex] { $value = $sign.text.equals("-") ? $ureal16.value.negate() : $ureal16.value; }
 | infnan             { $value = $infnan.value; };
//real[int rx, boolean ex] returns [Real value]:
//   sign ureal[rx, ex] { $value = $sign.text.equals("-") ? $ureal.value.negate() : $ureal.value; }
// | infnan             { $value = $infnan.value; };
ureal2[boolean ex] returns [Real value] :
   uinteger2[ex]                                 { $value = $uinteger2.value; }
 | i1 = uinteger2[ex] '/' i2 = uinteger2[ex]  { $value = new RationalPair($i1.value, $i2.value, $ex).simplify(); };
ureal8[boolean ex] returns [Real value] :
   uinteger8[ex]                                 { $value = $uinteger8.value; }
 | i1 = uinteger8[ex] '/' i2 = uinteger8[ex]  { $value = new RationalPair($i1.value, $i2.value, $ex).simplify(); };
ureal10[boolean ex] returns [Real value] :
   uinteger10[ex]                                 { $value = $uinteger10.value; }
 | i1 = uinteger10[ex] '/' i2 = uinteger10[ex]  { $value = new RationalPair($i1.value, $i2.value, $ex).simplify(); } 
 | decimal10[ex]                                  { $value = $decimal10.value; };
ureal16[boolean ex] returns [Real value] :
   uinteger16[ex]                                 { $value = $uinteger16.value; }
 | i1 = uinteger16[ex] '/' i2 = uinteger16[ex]  { $value = new RationalPair($i1.value, $i2.value, $ex).simplify(); };
//ureal[int rx, boolean ex] returns [Real value] :
//   uinteger[rx, ex]                                 { $value = $uinteger.value; }
// | i1 = uinteger[rx, ex] '/' i2 = uinteger[rx, ex]  { $value = new RationalPair($i1.value, $i2.value, $ex); } 
// | decimal[rx, ex]                                  { $value = $decimal.value; };
decimal10[boolean ex] returns [Real value] 
@init { StringBuffer sb = new StringBuffer(); } :
   uinteger10[ex] suffix                       { $value = new DoubleReal($uinteger10.value, "", $suffix.value).simplify(); }
 | '.' (digit10 {sb.append($digit10.text);})+ suffix                   { $value = new DoubleReal(LongInteger.ZERO, sb.toString(), $suffix.value).simplify(); }
 | uinteger10[ex] '.' (digit10 {sb.append($digit10.text);})* suffix { $value = new DoubleReal($uinteger10.value, sb.toString(), $suffix.value).simplify(); };
//decimal[int rx, boolean ex] returns [Real value]
//@init { if (rx != 10) throw new RecognitionException("Expected radix 10", null, _input, _ctx); } :
//   uinteger10 suffix                       { $value = new DoubleReal($uinteger10.value, LongInteger.ZERO, $suffix.value); }
// | '.' uinteger10 suffix                   { $value = new DoubleReal(LongInteger.ZERO, $uinteger10.value, $suffix.value); }
// | uinteger10 '.' uinteger10OrBlank suffix { $value = new DoubleReal($uinteger10.value, $uinteger10OrBlank.value, $suffix.value); };
uinteger2[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Integer value] 
@init { StringBuffer sb = new StringBuffer(); } :
   (digit2 {sb.append($digit2.text);})+ {
     try {
      $value = new LongInteger(Long.parseLong(sb.toString(), 2), ex);
     } catch (NumberFormatException x) {
      throw new RecognitionException("Expected integer radix 2", null, _input, _ctx);
     }
   };
uinteger8[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Integer value] 
@init { StringBuffer sb = new StringBuffer(); } :
   (digit8 {sb.append($digit8.text);})+ {
     try {
      $value = new LongInteger(Long.parseLong(sb.toString(), 8), ex);
     } catch (NumberFormatException x) {
      throw new RecognitionException("Expected integer radix 8", null, _input, _ctx);
     }
   };
uinteger10[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Integer value] 
@init { StringBuffer sb = new StringBuffer(); } :
   (digit10 {sb.append($digit10.text);})+ {
     try {
      $value = new LongInteger(Long.parseLong(sb.toString(), 10), ex);
     } catch (NumberFormatException x) {
      throw new RecognitionException("Expected integer radix 10", null, _input, _ctx);
     }
   };
uinteger16[boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Integer value] 
@init { StringBuffer sb = new StringBuffer(); } :
   (digit16 {sb.append($digit16.text);})+ {
     try {
      $value = new LongInteger(Long.parseLong(sb.toString(), 16), ex);
     } catch (NumberFormatException x) {
      throw new RecognitionException("Expected integer radix 16", null, _input, _ctx);
     }
   };
//uinteger[int rx, boolean ex] returns [se.pp.forsberg.scheme.values.numbers.Integer value] 
//@init { StringBuffer sb = new StringBuffer(); } :
//   (digit[rx] {sb.append($digit.text);})+ {
//     try {
//      $value = new LongInteger(Long.parseLong(sb.toString(), rx), ex);
//     } catch (NumberFormatException x) {
//      throw new RecognitionException("Expected integer radix " + rx, null, _input, _ctx);
//     }
//   };
//uinteger10 returns [se.pp.forsberg.scheme.values.numbers.Integer value] :
//   uinteger[10, true] { $value = $uinteger.value; };
uinteger10OrBlank returns [se.pp.forsberg.scheme.values.numbers.Integer value] :
   uinteger10[true] { $value = $uinteger10.value; }
 |                    { $value = LongInteger.ZERO; };
//uinteger10OrBlank returns [se.pp.forsberg.scheme.values.numbers.Integer value] :
//   uinteger[10, true] { $value = $uinteger.value; }
// |                    { $value = LongInteger.ZERO; };
prefix2 returns [se.pp.forsberg.scheme.values.numbers.Number.Prefix value]:
     radix2 exactness { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(2, $exactness.value); }
   | exactness radix2 { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(2, $exactness.value); };
prefix8 returns [se.pp.forsberg.scheme.values.numbers.Number.Prefix value]:
     radix8 exactness { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(8, $exactness.value); }
   | exactness radix8 { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(8, $exactness.value); };
prefix10 returns [se.pp.forsberg.scheme.values.numbers.Number.Prefix value]:
     radix10 exactness { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(10, $exactness.value); }
   | exactness radix10 { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(10, $exactness.value); };
prefix16 returns [se.pp.forsberg.scheme.values.numbers.Number.Prefix value]:
     radix16 exactness { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(16, $exactness.value); }
   | exactness radix16 { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix(16, $exactness.value); };
//prefix[int rx] returns [se.pp.forsberg.scheme.values.numbers.Number.Prefix value]:
//     radix[rx] exactness { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix($rx, $exactness.value); }
//   | exactness radix[rx] { $value = new se.pp.forsberg.scheme.values.numbers.Number.Prefix($rx, $exactness.value); };
infnan returns [ Real value ] :
   '+' i n f '.0'  { $value = new DoubleReal(Double.POSITIVE_INFINITY); }
 | '-' i n f '.0'  { $value = new DoubleReal(Double.NEGATIVE_INFINITY); }
 | '+' n a n '.0'  { $value = new DoubleReal(Double.NaN); }
 | '-' n a n '.0'  { $value = new DoubleReal(Double.NaN); } ;
suffix returns [se.pp.forsberg.scheme.values.numbers.Integer value]:
   exponentMarker sign uinteger10[true] { $value = $uinteger10.value; }
 |                                { $value = LongInteger.ZERO; } ;
exponentMarker : 'e' | 'E' ;
sign : '+' | '-' | ;
exactness returns [boolean value]:
   '#' i { $value = false; }
 | '#' e { $value = true; }
 |       { $value = true; };
//radix[int rx] : 
//    { $rx == 2 }? radix2
//  | { $rx == 8 }? radix8
//  | { $rx == 10 }? radix10
//  | { $rx == 16 }? radix16;
radix2 : '#' b ;
radix8 : '#' o;
radix10 : '#' d | ;
radix16 : '#' x ;
//digit[int rx] :
//    { $rx == 2 }? digit2
//  | { $rx == 8 }? digit8
//  | { $rx == 10 }? digit10
//  | { $rx == 16 }? digit16;
digit2 : '0' | '1' ;
digit8 : digit2 | '2' | '3' | '4' | '5' | '6' | '7' ;


identifier returns [Identifier value]
@init { StringBuffer sb = new StringBuffer(); } :
   initial {sb.append($initial.text);} (subsequent {sb.append($subsequent.text);})*    { $value = new Identifier(sb.toString(), foldCase); } 
 | '|' (symbolElement {sb.append($symbolElement.value);})* '|'                          { $value = new Identifier(sb.toString(), foldCase); }
 | peculiarIdentifier                                                                  { $value = new Identifier($peculiarIdentifier.text, foldCase); } ;

initial : letter | specialInitial ;
a : 'a' | 'A'; b : 'b' | 'B'; c : 'c' | 'C'; d : 'd' | 'D'; e : 'e' | 'E';
f : 'f' | 'F'; g : 'g' | 'G'; h : 'h' | 'H'; i : 'i' | 'I'; j : 'j' | 'J';
k : 'k' | 'K'; l : 'l' | 'L'; m : 'm' | 'M'; n : 'n' | 'N'; o : 'o' | 'O';
p : 'p' | 'P'; q : 'q' | 'Q'; r : 'r' | 'R'; s : 's' | 'S'; t : 't' | 'T';
u : 'u' | 'U'; v : 'v' | 'V'; w : 'w' | 'W'; x : 'x' | 'X'; y : 'y' | 'Y';
z : 'z' | 'Z';
letter : a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z ;

specialInitial :
   '!' | '$' | '%' | '&' | '*' | '/' | ':' | '<' | '=' | '>'
 | '?' | '^' | '_' | '~' ;
subsequent : initial | digit10 | specialSubsequent ;
digit10 : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' ;
digit16 : digit10 | a | b | c | d | e | f ;
explicitSign : '+' | '-' ;
specialSubsequent : explicitSign | '.' | '@' ;
inlineHexEscape returns [char value] :
 '\\' x hexScalarValue ';' {
   try {
     $value = (char) Integer.parseInt($hexScalarValue.text, 16);
   } catch (NumberFormatException x) {
    throw new RecognitionException("Expected hex scalar value", null, _input, _ctx);
   } 
 };
hexScalarValue : digit16+ ;
mnemonicEscape returns [char value] :
   '\\' a {$value='\07';}
 | '\\' b {$value='\b';}
 | '\\' t {$value='\t';}
 | '\\' n {$value='\n';}
 | '\\' r {$value='\r';};
peculiarIdentifier : explicitSign
                   | explicitSign signSubsequent subsequent*
                   | explicitSign '.' dotSubsequent subsequent*
                   | '.' dotSubsequent subsequent* ;
dotSubsequent : signSubsequent | '.' ;
signSubsequent : initial | explicitSign | '@' ;
symbolElement returns [char value] :
   ch = ~('|' | '\\' | '\\|') {$value=$ch.text.charAt(0);}
 | inlineHexEscape            {$value=$inlineHexEscape.value;}
 | mnemonicEscape             {$value=$mnemonicEscape.value;}
 | '\\|'                      {$value='|';} ;

bool returns [se.pp.forsberg.scheme.values.Boolean value] :
       '#' t         { $value = se.pp.forsberg.scheme.values.Boolean.TRUE; }
     | '#' t r u e   { $value = se.pp.forsberg.scheme.values.Boolean.TRUE; }
     | '#' f         { $value = se.pp.forsberg.scheme.values.Boolean.FALSE; }
     | '#' f a l s e { $value = se.pp.forsberg.scheme.values.Boolean.FALSE; } ;

character returns [se.pp.forsberg.scheme.values.Character value] :
       '#\\' dot = .          { $value = new se.pp.forsberg.scheme.values.Character($dot.text.charAt(0)); }
     | '#\\' characterName    { $value = $characterName.value; }
     | '#\\' x hexScalarValue { 
      try {
         $value = new se.pp.forsberg.scheme.values.Character((char) Integer.parseInt($hexScalarValue.text, 16));
      } catch (NumberFormatException x) {
        throw new RecognitionException("Expected hex scalar value", null, _input, _ctx);
      }};
characterName returns [se.pp.forsberg.scheme.values.Character value] :
       a l a r m         { $value = new se.pp.forsberg.scheme.values.Character('\07'); }
     | b a c k s p a c e { $value = new se.pp.forsberg.scheme.values.Character('\b'); }
     | d e l e t e       { $value = new se.pp.forsberg.scheme.values.Character('\177'); }
     | n e w l i n e     { $value = new se.pp.forsberg.scheme.values.Character('\n'); }
     | e s c a p e       { $value = new se.pp.forsberg.scheme.values.Character('\033'); }
     | n u l l           { $value = new se.pp.forsberg.scheme.values.Character('\0'); }
     | r e t u r n       { $value = new se.pp.forsberg.scheme.values.Character('\r'); }
     | s p a c e         { $value = new se.pp.forsberg.scheme.values.Character(' '); }
     | t a b             { $value = new se.pp.forsberg.scheme.values.Character('\t'); } ;

string returns [se.pp.forsberg.scheme.values.String value]
@init { StringBuffer sb = new StringBuffer(); } :
      '"' (stringElement {if ($stringElement.value != null) sb.append($stringElement.value);})* '"'  { $value = new se.pp.forsberg.scheme.values.String(sb.toString()); };
stringElement returns [Character value] :
   ch = ~('"' | '\\' | '\\\\' | '\\"') {$value = $ch.text.charAt(0);}
 | mnemonicEscape     {$value = $mnemonicEscape.value;} 
 | '\\"'              {$value = '"';} 
 | '\\\\'             {$value = '\\';}
 | '\\' intralineWhitespace* lineEnding intralineWhitespace* {$value = null;}
 | inlineHexEscape    {$value = $inlineHexEscape.value;};

bytevector returns [ByteVector value] 
@init { List<Byte> list = new ArrayList<Byte>(); } :
 '#' u '8(' intertokenSpace (byteval { list.add($byteval.value); } intertokenSpace)* ')' { $value = new ByteVector(list); };
byteval returns [Byte value] : number {
  se.pp.forsberg.scheme.values.numbers.Number number = $number.value;
  if (!number.isExact() || !number.isInteger()) throw new RecognitionException("Expected exact byte", null, _input, _ctx);
  se.pp.forsberg.scheme.values.numbers.Integer integer = (se.pp.forsberg.scheme.values.numbers.Integer) number;
  if (integer.lessThan(LongInteger.ZERO) || integer.greaterThan(new LongInteger(255, true))) throw new RecognitionException("Expected exact byte", null, _input, _ctx);
  $value = integer.asByte();
};  // Must be exact and 0-255

datumWs returns [Value value] :
   intertokenSpace datum intertokenSpace { $value = $datum.value; };
datum returns [Value value] :
   simpleDatum           { $value = $simpleDatum.value; }
 | compoundDatum         { $value = $compoundDatum.value; }
 | label '=' datum       { $value = $datum.value; /* TODO set label in env */ }
 | label '#'             { 
      try {
        $value = new Label(Integer.parseInt($label.text), true);
      } catch (NumberFormatException x) {
        throw new RecognitionException("Expected label to be decimal integer", null, _input, _ctx);
      }
  };
simpleDatum returns [Value value] :
   bool       { $value = $bool.value; }
 | number     { $value = $number.value; }
 | character  { $value = $character.value; } 
 | string     { $value = $string.value; }
 | symbol     { $value = $symbol.value; }
 | bytevector { $value = $bytevector.value; } ;
symbol returns [Value value] :
   identifier  { $value = $identifier.value; };
compoundDatum returns [Value value] :
   list         { $value = $list.value; }
 | vector       { $value = $vector.value; }
 | abbreviation { $value = $abbreviation.value; } ;
list returns [se.pp.forsberg.scheme.values.Value value] :
   '(' endOfList  { $value = $endOfList.value; } ;
endOfList returns [se.pp.forsberg.scheme.values.Value value] :
   ')'                                 { $value = Nil.NIL; }
 | car = datumWs '.' cdr = datumWs ')' { $value = new se.pp.forsberg.scheme.values.Pair($car.value, $cdr.value); }
 | car2 = datumWs cdr2 = endOfList     { $value = new se.pp.forsberg.scheme.values.Pair($car2.value, $cdr2.value); } ;
abbreviation returns [se.pp.forsberg.scheme.values.Pair value] :
   //abbrevPrefix datumWs { $value = new se.pp.forsberg.scheme.values.Pair($abbrevPrefix.value, $datumWs.value); } ;
   abbrevPrefix datumWs { $value = new se.pp.forsberg.scheme.values.Pair($abbrevPrefix.value, new se.pp.forsberg.scheme.values.Pair($datumWs.value, Nil.NIL)); } ;
abbrevPrefix returns [Identifier value] : 
   '\'' { $value = new Identifier("quote"); }
 | '`'  { $value = new Identifier("quasi-quote"); }
 | ','  { $value = new Identifier("unquote"); }
 | ',@' { $value = new Identifier("unquote-splicing"); } ;
vector returns [Vector value] 
@init { List<Value> vector = new ArrayList<Value>(); } :
 '#(' (datumWs {vector.add($datumWs.value);})* ')' { $value = new Vector(vector); } ;
label : '#' uinteger10[true] ;


