package cucumber.eclipse.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;

import cucumber.eclipse.editor.editors.Editor;

public class CucumberFeatureLaunchUtils {

	private CucumberFeatureLaunchUtils() {
		// NO ENTRY NO INSTANCES
	}

	protected static IProject getProject() {
		IWorkbenchPage page = JDIDebugUIPlugin.getActivePage();
		if (page != null) {
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IFileEditorInput input = (IFileEditorInput) part.getEditorInput();
				IFile file = input.getFile();
				IProject activeProject = file.getProject();
				return activeProject;
			}
		}
		return null;
	}

	protected static String getFeaturePath() {
		IWorkbenchPage page = JDIDebugUIPlugin.getActivePage();
		if (page != null) {
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IFileEditorInput input = (IFileEditorInput) part.getEditorInput();
				return input.getFile().getLocation().toString();
			}
		}
		return null;
	}

	protected static int getScenarioLineNumber() {
		IWorkbenchPage page = JDIDebugUIPlugin.getActivePage();
		if (page != null) {
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				try {
					return ((Editor) part).getModel().getScenarioOrScenarioOutline(
							((Editor) part).getSelection().getOffset()).getLine();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return -1;
	}

	protected static String getScenarioName() {
		IWorkbenchPage page = JDIDebugUIPlugin.getActivePage();
		if (page != null) {
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				try {
					return ((Editor) part).getModel().getScenarioOrScenarioOutline(
							((Editor) part).getSelection().getOffset()).getName();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void updateFromConfig(ILaunchConfiguration config, String attrib, Text text) {
		String s = "";
		try {
			s = config.getAttribute(attrib, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		text.setText(s);
	}
	
	public static boolean updateFromConfig(ILaunchConfiguration config, String attrib) {
		boolean b = false;
		try {
			b = config.getAttribute(attrib, b);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return b;
	}
}
