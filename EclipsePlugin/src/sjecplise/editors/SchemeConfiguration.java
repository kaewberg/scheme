package sjecplise.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import se.pp.forsberg.scheme.SchemeException;

public class SchemeConfiguration extends SourceViewerConfiguration {
	private SchemeDoubleClickStrategy doubleClickStrategy;
  // private Object element;

	public SchemeConfiguration() {//, Object element) {
		//this.element = element;
	}
	static String[] tokenTypeNames;
	static {
	  SchemeScanner.TokenType[] tokenTypes = SchemeScanner.TokenType.values();
    tokenTypeNames = new String[tokenTypes.length];
    for (int i = 0; i < tokenTypes.length; i++) {
      tokenTypeNames[i] = tokenTypes[i].toString();
    }
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return tokenTypeNames;
		//return SchemeDocumentPartitioner.getContentTypes();
	}
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new SchemeDoubleClickStrategy();
		return doubleClickStrategy;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
	  PresentationReconciler reconciler = new PresentationReconciler();
    
	  //sourceViewer.
	  DefaultDamagerRepairer dr;
    try {
      dr = new DefaultDamagerRepairer(new SchemeScanner(null, true)) {
        @Override
        // DefaultDamagerRepairer only damages the line edited, which makes it impossible
        // to have nested comments.
        // Rewrite to damage current partition
        public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e, boolean documentPartitioningChanged) {
          if (!documentPartitioningChanged) {
//            try {
//              IRegion info= fDocument.getLineInformationOfOffset(e.getOffset());
//              int start= Math.max(partition.getOffset(), info.getOffset());
//
//              int end= e.getOffset() + (e.getText() == null ? e.getLength() : e.getText().length());
//
//              if (info.getOffset() <= end && end <= info.getOffset() + info.getLength()) {
//                // optimize the case of the same line
//                end= info.getOffset() + info.getLength();
//              } else
//                end= endOfLineOf(end);
//
//              end= Math.min(partition.getOffset() + partition.getLength(), end);
//              return new Region(start, end - start);
              return new Region(partition.getOffset(), partition.getOffset() + partition.getLength());

//            } catch (BadLocationException x) {
//            }
          }
          return partition;
        }
      };
    } catch (SchemeException e) {
      return null;
    }
	  reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	  for (String type: SchemeScanner.getContentTypes()) {
	    reconciler.setDamager(dr, type);
	    reconciler.setRepairer(dr, type);
	  }
	  //SchemePresentationReconciler reconciler = new SchemePresentationReconciler();
	  return reconciler;
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
	  // TODO Auto-generated method stub
	  return super.getTextHover(sourceViewer, contentType);
	}
}