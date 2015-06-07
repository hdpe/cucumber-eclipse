package cucumber.eclipse.editor.steps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import cucumber.eclipse.steps.integration.IStepDefinitions;
import cucumber.eclipse.steps.integration.Step;
import cucumber.eclipse.steps.integration.StepListener;

public class ExtensionRegistryStepProvider implements IStepProvider {

	final static String EXTENSION_POINT_STEPDEFINITIONS_ID = "cucumber.eclipse.steps.integration";

	private Set<Step> steps = new HashSet<Step>();

	private List<IStepDefinitions> stepDefinitions = getStepDefinitions();

	private IFile file;
	
	public ExtensionRegistryStepProvider(IFile file) {
		this.file = file;
		reloadSteps();
	}

	public void addStepListener(StepListener listener) {
		for (IStepDefinitions stepDef : stepDefinitions) {
			stepDef.addStepListener(listener);
		}
	}

	@Override
	public TextEdit createStepSnippet(gherkin.formatter.model.Step step, IFile targetFile,
			IDocument targetDocument) throws IOException, CoreException {
		for (IStepDefinitions stepDef : stepDefinitions) {
			if (stepDef.getStepGenerator().supports(targetFile)) {
				return stepDef.getStepGenerator().createStepSnippet(step, targetDocument);
			}
		}
		
		return null;
	}

	public Set<Step> getStepsInEncompassingProject() {
		return steps;
	}

	public void reloadSteps() {
		steps.clear();
		for (IStepDefinitions stepDef : stepDefinitions) {
			steps.addAll(stepDef.getSteps(file));
		}
	}

	public void removeStepListener(StepListener listener) {
		for (IStepDefinitions stepDef : stepDefinitions) {
			stepDef.removeStepListener(listener);
		}
	}

	private static List<IStepDefinitions> getStepDefinitions() {
		List<IStepDefinitions> stepDefs = new ArrayList<IStepDefinitions>();
		IConfigurationElement[] config = Platform
				.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT_STEPDEFINITIONS_ID);
		try {
			for (IConfigurationElement ce : config) {
				Object obj = ce.createExecutableExtension("class");
				if (obj instanceof IStepDefinitions) {
					stepDefs.add((IStepDefinitions) obj);
				}
			}
		} catch (CoreException e) {
		}
		return stepDefs;
	}
}
