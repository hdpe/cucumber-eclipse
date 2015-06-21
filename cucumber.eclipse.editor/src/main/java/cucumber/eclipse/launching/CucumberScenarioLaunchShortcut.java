package cucumber.eclipse.launching;

import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.ui.IEditorPart;

public class CucumberScenarioLaunchShortcut extends CucumberFeatureLaunchShortcut {
  	
	@Override
	protected String getName(ILaunchConfigurationType type) {
		return CucumberFeaureLaunchUtils.getScenarioName();
	}

	@Override
	protected void initializeConfiguration(ILaunchConfigurationWorkingCopy config) {
		super.initializeConfiguration(config);
		config.setAttribute(CucumberFeatureLaunchConstants.ATTR_LINE_NUMBER, CucumberFeaureLaunchUtils.getScenarioLineNumber());
	}
}
