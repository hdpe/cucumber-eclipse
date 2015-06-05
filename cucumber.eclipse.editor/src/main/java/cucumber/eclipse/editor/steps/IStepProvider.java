package cucumber.eclipse.editor.steps;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.text.edits.TextEdit;

import cucumber.eclipse.steps.integration.Step;
import cucumber.eclipse.steps.integration.StepListener;

public interface IStepProvider {

	void addStepListener(StepListener listener);
	
	TextEdit createStepSnippet(IFile stepFile, gherkin.formatter.model.Step step)
			throws IOException, CoreException;

	Set<Step> getStepsInEncompassingProject();

	void reloadSteps();

	void removeStepListener(StepListener listener);
}
