//package se.pp.forsberg.scheme;
//
//import se.pp.forsberg.scheme.Evaluator.Continuation;
//
///**
// * This class is used as a simple means to implement continuations in Java.
// * When thrown, it's caught at the parser top level and the specified continuation
// * is then used to rebuild the call stack.
// * 
// */
//public class ContinuationException extends RuntimeException {
//
//  private static final long serialVersionUID = 1L;
//  final private Continuation continuation;
//  
//  public ContinuationException(Continuation continuation) {
//    this.continuation = continuation;
//  }
//
//  public Continuation getContinuation() {
////    return continuation;
//  }
//}
