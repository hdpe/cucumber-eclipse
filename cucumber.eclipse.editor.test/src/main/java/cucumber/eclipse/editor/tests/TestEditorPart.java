package cucumber.eclipse.editor.tests;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

public class TestEditorPart implements IEditorPart {

    public void addPropertyListener(IPropertyListener listener) {
    }

    public void createPartControl(Composite parent) {
    }

    public void dispose() {
    }

    public IWorkbenchPartSite getSite() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public Image getTitleImage() {
        return null;
    }

    public String getTitleToolTip() {
        return null;
    }

    public void removePropertyListener(IPropertyListener listener) {
    }

    public void setFocus() {
    }

    public Object getAdapter(Class adapter) {
        return null;
    }

    public void doSave(IProgressMonitor monitor) {
    }

    public void doSaveAs() {
    }

    public boolean isDirty() {
        return false;
    }

    public boolean isSaveAsAllowed() {
        return false;
    }

    public boolean isSaveOnCloseNeeded() {
        return false;
    }

    public IEditorInput getEditorInput() {
        return null;
    }

    public IEditorSite getEditorSite() {
        return null;
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    }
}
