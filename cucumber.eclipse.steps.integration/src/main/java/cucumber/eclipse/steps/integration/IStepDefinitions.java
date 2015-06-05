package cucumber.eclipse.steps.integration;

import java.util.Set;

import org.eclipse.core.resources.IFile;

public interface IStepDefinitions {

    void addStepListener(StepListener listener);

    IStepGenerator getStepGenerator();
    
    Set<Step> getSteps(IFile featurefile);

    void removeStepListener(StepListener listener);
}
