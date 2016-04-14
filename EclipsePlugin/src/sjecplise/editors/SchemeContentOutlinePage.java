package sjecplise.editors;

import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import se.pp.forsberg.scheme.DebugInformation;
import se.pp.forsberg.scheme.Parser;
import se.pp.forsberg.scheme.SchemeException;
import se.pp.forsberg.scheme.values.Value;

public class SchemeContentOutlinePage extends ContentOutlinePage {
  SchemeDocumentModel model;
    
  static class SchemeTreeContentProvider implements ITreeContentProvider, SchemeDocumentListener {

    SchemeDocumentModel model;
    DebugInformation debugInformation;
    
//    static class Node {
//      Object value;
//      Object parent;
//      public Node(Object value, Object parent) {
//        this.value = value;
//        this.parent = parent;
//      }
//    }
    
    //private Object input;
    public SchemeTreeContentProvider() {}
    public SchemeTreeContentProvider(SchemeDocumentModel model) {
      setModel(model);
    }
    public void setModel(SchemeDocumentModel model) {
      if (model == this.model) return;
      if (this.model != null) {
        this.model.removeDocumentListener(this);
      }
      this.model = model;
      if (model != null) {
        this.model.addDocumentListener(this);
      }
      refresh();
    }

    private void refresh() {
      this.debugInformation = model.getDebugInformation();
    }
    // Root elements
    @Override
    public Object[] getElements(Object inputElement) {
      if (debugInformation == null) {
        return new Object[]{};
      }
      return debugInformation.getRoot().getChildren().toArray();
    }
    
    @Override
    public void dispose() {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      debugInformation = null;
      refresh();
//      if (!(newInput instanceof IFileEditorInput)) {
//        return;
//      }
//      IFileEditorInput input = (IFileEditorInput) newInput;
//      InputStream in;
//      try {
//        in = input.getFile().getContents();
//      } catch (CoreException e) {
//        e.printStackTrace();
//        return;
//      }
//      Parser parser = new Parser(in);
//      Value v;
//      try {
//        v = parser.read();
//        while (v != null && !v.isEof()) {
//          v = parser.read();
//        }
//      } catch (SchemeException e) {
//        return;
//      }
//      debugInformation = parser.getDebugInformation();
    }


    @Override
    public Object[] getChildren(Object parentElement) {
      if (!(parentElement instanceof DebugInformation.Node)) return new Object[]{};
      DebugInformation.Node node = (DebugInformation.Node) parentElement;
      return node.getChildren().toArray();
    }
    @Override
    public Object getParent(Object element) {
      if (!(element instanceof DebugInformation.Node)) return null;
      DebugInformation.Node node = (DebugInformation.Node) element;
      return node.getParent();
    }

    @Override
    public boolean hasChildren(Object element) {
      if (!(element instanceof DebugInformation.Node)) return false;
      DebugInformation.Node node = (DebugInformation.Node) element;
      return node.getChildren().size() > 0;
    } // new SchemeContentProvider());
    @Override
    public void documentChanged(SchemeDocumentEvent e) {
      refresh();
    }

  }
  
  //private IDocumentProvider documentProvider;
 // private SchemeEditor editor;
  private IEditorInput input;
  private SchemeTreeContentProvider provider;
  private TreeViewer viewer;
  
  public SchemeContentOutlinePage() {}
  public SchemeContentOutlinePage(SchemeDocumentModel model) {
   // this.documentProvider = documentProvider;
   // this.editor = schemeEditor;
    setModel(model);
  }
  public void setModel(SchemeDocumentModel model) {
    if (this.model == model) return;
    this.model = model;
  }

  public void setInput(IEditorInput editorInput) {
    this.input = editorInput;
  }
  
  @Override
  public void createControl(Composite parent) {
    super.createControl(parent);
    viewer = getTreeViewer();
    provider = new SchemeTreeContentProvider(model);
    viewer.setContentProvider(provider);
    viewer.setLabelProvider(new LabelProvider() { // new MyLabelProvider());
      @Override
      public String getText(Object element) {
        return element.toString();
      }

    });
    viewer.addSelectionChangedListener(this);
    viewer.setInput(input);
  }

  public void update() {
    viewer.setInput(input);
  }

}
