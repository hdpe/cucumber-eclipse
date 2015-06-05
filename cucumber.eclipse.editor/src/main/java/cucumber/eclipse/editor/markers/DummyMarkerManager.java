package cucumber.eclipse.editor.markers;

import org.eclipse.core.resources.IFile;

public class DummyMarkerManager implements IMarkerManager {

	@Override
	public void add(String type, IFile file, int severity, String message, int lineNumber,
			int charStart, int charEnd) {
		// do nothing
	}

	@Override
	public void removeAll(String type, IFile file) {
		// do nothing
	}
}
