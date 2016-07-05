package sjecplise.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

import se.pp.forsberg.scheme.SchemeException;

public class SchemeDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner;
      try {
        partitioner = new FastPartitioner(
					new SchemeScanner(element, false),
					SchemeScanner.getContentTypes());
      } catch (SchemeException e) {
        throw new CoreException(new Status(Status.ERROR, "sjeme", "Scheme exception " + e.getMessage()));
      }
			//partitioner = new SchemeDocumentPartitioner();
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}