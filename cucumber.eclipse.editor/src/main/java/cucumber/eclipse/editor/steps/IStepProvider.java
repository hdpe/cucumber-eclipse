package cucumber.eclipse.editor.steps;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import cucumber.eclipse.steps.integration.Step;
import cucumber.eclipse.steps.integration.StepListener;

public interface IStepProvider {

	void addStepListener(StepListener listener);
	
	TextEdit createStepSnippet(gherkin.formatter.model.Step step, IFile targetFile,
			IDocument targetDocument) throws IOException, CoreException;

	Set<Step> getStepsInEncompassingProject();

	void reloadSteps();

	void removeStepListener(StepListener listener);
}
