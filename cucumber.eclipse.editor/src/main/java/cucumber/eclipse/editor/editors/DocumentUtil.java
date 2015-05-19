package cucumber.eclipse.editor.editors;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class DocumentUtil {

	static final String getLineText(IDocument doc, int line) {
		try {
			String stepLine = doc.get(doc.getLineOffset(line), doc.getLineLength(line)).trim();
			return stepLine;
		} catch (BadLocationException e) {
			return "";
		}
	}
	
	static String getDocumentLanguage(IDocument document) {
		String lang = null;
		try {
			IRegion lineInfo = document.getLineInformation(0);
			int length = lineInfo.getLength();
			int offset = lineInfo.getOffset();
			String line = document.get(offset, length);

			if (line.contains("language")) {
				int indexOf = line.indexOf(":");
				lang = line.substring((indexOf + 1)).trim();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		return lang;
	}
}
