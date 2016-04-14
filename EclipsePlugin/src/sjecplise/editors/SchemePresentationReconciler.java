//package sjecplise.editors;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.eclipse.jface.text.DocumentEvent;
//import org.eclipse.jface.text.IDocument;
//import org.eclipse.jface.text.IRegion;
//import org.eclipse.jface.text.ITextViewer;
//import org.eclipse.jface.text.ITypedRegion;
//import org.eclipse.jface.text.TextPresentation;
//import org.eclipse.jface.text.presentation.IPresentationDamager;
//import org.eclipse.jface.text.presentation.IPresentationReconciler;
//import org.eclipse.jface.text.presentation.IPresentationRepairer;
//
//public class SchemePresentationReconciler implements IPresentationReconciler, IPresentationDamager, IPresentationRepairer, SchemeDocumentListener {
//
//  private ITextViewer textViewer;
//  private SchemeDocumentPartitioner partitioner;
//  private IDocument document;
//  private Map<DocumentEvent, SchemeDocumentEvent> cachedEvents = new HashMap<DocumentEvent, SchemeDocumentEvent>();
//  
//  // IPresentationReconciler
//  @Override
//  public void install(ITextViewer viewer) {
//    textViewer = viewer;
//    partitioner = (SchemeDocumentPartitioner) textViewer.getDocument().getDocumentPartitioner();
//    //partitioner.addDocumentListener(this);
//    cachedEvents.clear();
//  }
//
//  @Override
//  public void uninstall() {
//    //partitioner.removeDocumentListener(this);
//    partitioner = null;
//    textViewer = null;
//    cachedEvents.clear();
//  }
//
//  @Override
//  public IPresentationDamager getDamager(String contentType) {
//    return this;
//  }
//
//  @Override
//  public IPresentationRepairer getRepairer(String contentType) {
//    return this;
//  }
//
//  // IPresentationDamager
//  @Override
//  public void setDocument(IDocument document) {
//    this.document = document;
//    cachedEvents.clear();
//  }
//
//  @Override
//  public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent event, boolean documentPartitioningChanged) {
//    if (partitioner == null) {
//      return null;
//    }
//    //TODO
//    return null;
//  }
//  
//  // IPresentationRepairer
//
//  @Override
//  public void createPresentation(TextPresentation presentation, ITypedRegion damage) {
//    // TODO Auto-generated method stub
//    
//  }
//  
//  // SchemeDocumentListener
//  @Override
//  public void documentChanged(SchemeDocumentEvent e) {
//    // TODO Auto-generated method stub
//    
//  }
//}
