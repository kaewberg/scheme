parser grammar Scheme2Parser_old;

options { tokenVocab = Scheme2Lexer_old; }
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
  class MyRecognitionException extends RecognitionException {
    public MyRecognitionException(String s) { this(s, null); }
    public MyRecognitionException(String s, Throwable cause) {
      super(s, Scheme2Parser_old.this, Scheme2Parser_old.this.getInputStream(), _ctx );
    }
  }
}
 
datum returns [Value value]:
   BeginSExpressionComment datum d = datum { $value = $d.value; }
 | Directive {
   if ($Directive.text.equalsIgnoreCase("#!fold-case")) {
     foldCase = true;
   } else if ($Directive.text.equalsIgnoreCase("#!no-fold-case")) {
     foldCase = false;
   } else {
     throw new MyRecognitionException("Unknown directive " + $Directive.text);
   }
 } d = datum { $value = $d.value; }
 | simpleDatum    { $value = $simpleDatum.value; }
 | compoundDatum  { $value = $compoundDatum.value; }
 | Label d=datum  { $value = $d.value; /* TODO set label in env */ }
 | LabelReference {
   try {
        $value = new Label(Integer.parseInt($LabelReference.text), true);
      } catch (NumberFormatException x) {
        throw new MyRecognitionException("Expected label to be decimal integer");
      }
 };
simpleDatum returns [Value value]:
   Bool       { $value = se.pp.forsberg.scheme.values.Boolean.parse($Bool.text); } 
 | Number     { $value = se.pp.forsberg.scheme.values.numbers.Number.parse($Number.text); }
 | Character  { $value = se.pp.forsberg.scheme.values.Character.parse($Character.text); }
 | String     { $value = se.pp.forsberg.scheme.values.String.parse($String.text); } 
 | symbol     { $value = $symbol.value; } 
 | byteVector { $value = $byteVector.value; } 
;// catch [Exception x] { reportError(x.getMessage()); };
symbol returns [Value value]:
 Identifier { $value = se.pp.forsberg.scheme.values.Identifier.parse($Identifier.text, foldCase); } 
;// catch [Exception x] { reportError(x.getMessage()); } ;

compoundDatum returns [Value value] :
   list         { $value = $list.value; }
 | vector       { $value = $vector.value; }
 | abbreviation { $value = $abbreviation.value; } ;
list returns [se.pp.forsberg.scheme.values.Value value] :
   LeftParen endOfList  { $value = $endOfList.value; } ;
endOfList returns [se.pp.forsberg.scheme.values.Value value] :
   RightParen                             { $value = Nil.NIL; }
 | car = datum '.' cdr = datum RightParen { $value = new se.pp.forsberg.scheme.values.Pair($car.value, $cdr.value); }
 | car2 = datum cdr2 = endOfList          { $value = new se.pp.forsberg.scheme.values.Pair($car2.value, $cdr2.value); } ;
abbreviation returns [se.pp.forsberg.scheme.values.Pair value] :
   //abbrevPrefix datumWs { $value = new se.pp.forsberg.scheme.values.Pair($abbrevPrefix.value, $datumWs.value); } ;
   abbrevPrefix datum { $value = new se.pp.forsberg.scheme.values.Pair($abbrevPrefix.value, new se.pp.forsberg.scheme.values.Pair($datum.value, Nil.NIL)); } ;
abbrevPrefix returns [Identifier value] : 
   Quote { $value = new Identifier("quote"); }
 | QuasiQuote  { $value = new Identifier("quasi-quote"); }
 | Unquote  { $value = new Identifier("unquote"); }
 | UnquoteSplicing { $value = new Identifier("unquote-splicing"); } ;

vector returns [Vector value] 
@init { List<Value> vector = new ArrayList<Value>(); } :
 BeginVector (datum {vector.add($datum.value);})* RightParen { $value = new Vector(vector); } ;
 
byteVector returns [ByteVector value] 
@init { List<Byte> list = new ArrayList<Byte>(); } :
 BeginByteVector (byteval { list.add($byteval.value); })* RightParen { $value = new ByteVector(list); };

byteval returns [Byte value] : Number {
  se.pp.forsberg.scheme.values.numbers.Number number = se.pp.forsberg.scheme.values.numbers.Number.parse($Number.text);
  if (!number.isExact() || !number.isInteger()) throw new MyRecognitionException("Expected exact byte");
  se.pp.forsberg.scheme.values.numbers.Integer integer = (se.pp.forsberg.scheme.values.numbers.Integer) number;
  if (integer.lessThan(LongInteger.ZERO) || integer.greaterThan(new LongInteger(255, true))) throw new MyRecognitionException("Expected exact byte");
  $value = integer.asByte();
};