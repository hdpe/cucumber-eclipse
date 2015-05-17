package cucumber.eclipse.editor.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.texteditor.ITextEditor;

public class TestTextEditor extends TestEditorPart implements ITextEditor {

    private final IDocument document;
    
    public TestTextEditor(IDocument document) {
        this.document = document;
    }
    
    public IDocumentProvider getDocumentProvider() {
        return new IDocumentProvider() {
            
            public IDocument getDocument(Object element) {
                return document;
            }

            // default implementations
            
            public void saveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite)
                    throws CoreException {
            }
            
            public void resetDocument(Object element) throws CoreException {
            }
            
            public void removeElementStateListener(IElementStateListener listener) {
            }
            
            public boolean mustSaveDocument(Object element) {
                return false;
            }
            
            public boolean isDeleted(Object element) {
                return false;
            }
            
            public long getSynchronizationStamp(Object element) {
                return 0;
            }
            
            public long getModificationStamp(Object element) {
                return 0;
            }
            
            public IAnnotationModel getAnnotationModel(Object element) {
                return null;
            }
            
            public void disconnect(Object element) {
            }
            
            public void connect(Object element) throws CoreException {
            }
            
            public void changed(Object element) {
            }
            
            public boolean canSaveDocument(Object element) {
                return false;
            }
            
            public void addElementStateListener(IElementStateListener listener) {
            }
            
            public void aboutToChange(Object element) {
            }
        };
    }

    // default implementations
    
    public void close(boolean save) {
    }

    public boolean isEditable() {
        return false;
    }

    public void doRevertToSaved() {
    }

    public void setAction(String actionID, IAction action) {
    }

    public IAction getAction(String actionId) {
        return null;
    }

    public void setActionActivationCode(String actionId, char activationCharacter, int activationKeyCode,
            int activationStateMask) {
    }

    public void removeActionActivationCode(String actionId) {
    }

    public boolean showsHighlightRangeOnly() {
        return false;
    }

    public void showHighlightRangeOnly(boolean showHighlightRangeOnly) {
    }

    public void setHighlightRange(int offset, int length, boolean moveCursor) {
    }

    public IRegion getHighlightRange() {
        return null;
    }

    public void resetHighlightRange() {
    }

    public ISelectionProvider getSelectionProvider() {
        return null;
    }

    public void selectAndReveal(int offset, int length) {
    }
}
