package sjecplise.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class SchemeEditor extends TextEditor {

  private SchemeDocumentModel model;
	private SchemeContentOutlinePage outline;

	public SchemeEditor() {
		super();
		setSourceViewerConfiguration(new SchemeConfiguration());
		setDocumentProvider(new SchemeDocumentProvider());
	}
	public void dispose() {
		super.dispose();
	}
	@Override
	public void doSave(IProgressMonitor progressMonitor) {
	  super.doSave(progressMonitor);
	  if (outline != null) {
	    outline.update();
	  }
	}
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
	  // IShowInSource
	  if (IContentOutlinePage.class.equals(adapter)) {
	    if (model == null) {
        model = new SchemeDocumentModel(getDocumentProvider().getDocument(getEditorInput()));
      }
	    if (outline == null) {
//	      outline = new SchemeContentOutlinePage(
//                       getDocumentProvider(), this);
//	      outline.setInput(getEditorInput());
	      
        outline = new SchemeContentOutlinePage();
     }
	    outline.setModel(model);
     return outline;
	 }
//	  else if (IShowInSource.class.equals(adapter)) {
//   } else if (AbstractTextEditor.class.equals(adapter)) {
//   } else if (IVerticalRulerInfo.class.equals(adapter)) {
//   } else if (IFindReplaceTarget.class.equals(adapter)) {
   //} else if (IColumnSupport.class.equals(adapter)) {
   //} else if (JTextEditorDropTargetListener.class.equals(adapter)) {
//	 } else {
//	   Class unknown = adapter;
//	 }
   return super.getAdapter(adapter);
	}

}
