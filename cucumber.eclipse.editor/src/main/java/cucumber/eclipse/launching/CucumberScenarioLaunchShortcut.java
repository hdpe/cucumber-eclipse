package cucumber.eclipse.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import cucumber.eclipse.editor.Activator;

public class CucumberScenarioLaunchShortcut extends CucumberFeatureLaunchShortcut {
  	
	@Override
	protected String getName(ILaunchConfigurationType type) {
		return CucumberFeatureLaunchUtils.getScenarioName();
	}

	@Override
	protected void initializeConfiguration(ILaunchConfigurationWorkingCopy config) {
		super.initializeConfiguration(config);
		config.setAttribute(CucumberFeatureLaunchConstants.ATTR_LINE_NUMBER,
				CucumberFeatureLaunchUtils.getScenarioLineNumber());
	}

	@Override
	protected boolean isGoodMatch(ILaunchConfiguration configuration) {
		return super.isGoodMatch(configuration) && isGoodLineNumber(configuration);
	}

	private boolean isGoodLineNumber(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(CucumberFeatureLaunchConstants.ATTR_LINE_NUMBER, 0)
					== CucumberFeatureLaunchUtils.getScenarioLineNumber();
		}
		catch (CoreException exception) {
			Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					null, exception));
			
			return false;
		}
	}
}
