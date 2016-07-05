package sjecplise.editors;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;

import se.pp.forsberg.scheme.DebugInformation;
import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Eof;

public class SchemeDocumentModel {
  
  DebugInformation debugInformation;
  IDocument document;
  
  public SchemeDocumentModel(IDocument document) {
    setDocument(document);
  }
  public void setDocument(IDocument document) {
    if (this.document != null) {
      this.document.removeDocumentListener(listener);
      debugInformation = null;
      fireDocumentChanged();
    }
    this.document = document;
    if (document != null) {
      document.addDocumentListener(listener);
      reparse();
      fireDocumentChanged();
    }
  }
  
  private void reparse()  {
    Parser parser = new Parser(new StringReader(document.get()));
    parser.getTokenizer().setEclipseMode(true);
    try {
      while (parser.read() != Eof.EOF) {
      }
    } catch (SchemeException x) {
      x.printStackTrace();
      throw new RuntimeException("Unhandled SchemeException", x);
    }
    debugInformation = parser.getDebugInformation();
  }

  private class Listener implements ITextInputListener, IDocumentListener {

    // ITextInputListener
    @Override
    public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {

      oldInput.removeDocumentListener(this);
      //oldInput.removeDocumentPartitioningListener(this);

      //oldInput.removePositionUpdater(fPositionUpdater);
      //oldInput.removePositionCategory(fPositionCategory);
    }

    @Override
    public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
      setDocument(newInput);
    }

    // IDcouemntListener
    @Override
    public void documentAboutToBeChanged(DocumentEvent event) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void documentChanged(DocumentEvent event) {
      reparse();
      fireDocumentChanged();
    }
  }
  private Listener listener = new Listener();

  private List<SchemeDocumentListener> documentListeners = new ArrayList<SchemeDocumentListener>();

  public void addDocumentListener(SchemeDocumentListener listener) {
    documentListeners.add(listener);
  }

  public void removeDocumentListener(SchemeDocumentListener listener) {
    documentListeners.remove(listener);
  }
  private void fireDocumentChanged() {
    SchemeDocumentEvent e = new SchemeDocumentEvent();
    for (SchemeDocumentListener listener: documentListeners) {
      listener.documentChanged(e);
    }
  }

  public DebugInformation getDebugInformation() {
    return debugInformation;
  }
  
}
