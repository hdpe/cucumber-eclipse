package cucumber.eclipse.editor.steps;

import java.util.Set;

import cucumber.eclipse.steps.integration.Step;
import cucumber.eclipse.steps.integration.StepListener;

public interface IStepProvider {

	void addStepListener(StepListener listener);
	
	Set<Step> getStepsInEncompassingProject();

	void reloadSteps();

	void removeStepListener(StepListener listener);
}
