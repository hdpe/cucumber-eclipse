package cucumber.eclipse.editor.snippet;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import cucumber.eclipse.editor.Activator;
import cucumber.eclipse.editor.steps.IStepProvider;
import gherkin.formatter.model.Step;

public class SnippetGenerator {

	private final IStepProvider stepProvider;
	
	public SnippetGenerator(IStepProvider stepProvider) {
		this.stepProvider = stepProvider;
	}
	
	public void generateSnippet(Step step, IFile stepFile) {
		try {
			TextEdit edit = stepProvider.createStepSnippet(stepFile, step);

			ITextEditor editor = openEditor(stepFile);

			edit.apply(editor.getDocumentProvider().getDocument(editor.getEditorInput()));
		}
		catch (PartInitException exception) {
			logException(step, stepFile, exception);
		}
		catch (BadLocationException exception) {
			logException(step, stepFile, exception);
		}
		catch (IOException exception) {
			logException(step, stepFile, exception);
		}
		catch (CoreException exception) {
			logException(step, stepFile, exception);
		}
	}

	private static void logException(Step step, IFile stepFile, Exception exception) {
		Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
			String.format("Couldn't generate snippet %s for %s", step.getName(), stepFile), exception));
	}

	private static ITextEditor openEditor(IFile stepFile) throws PartInitException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
		IEditorDescriptor desc = workbench.getEditorRegistry().getDefaultEditor(stepFile.getName());
		
		ITextEditor editor = (ITextEditor) page.openEditor(new FileEditorInput(stepFile), desc.getId());
		return editor;
	}
}

