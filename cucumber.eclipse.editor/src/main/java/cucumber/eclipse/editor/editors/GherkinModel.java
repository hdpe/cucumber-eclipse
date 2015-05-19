package cucumber.eclipse.editor.editors;

import static cucumber.eclipse.editor.editors.DocumentUtil.getDocumentLanguage;
import gherkin.lexer.LexingError;
import gherkin.parser.ParseError;
import gherkin.parser.Parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import cucumber.eclipse.editor.markers.IMarkerManager;
import cucumber.eclipse.editor.steps.IStepProvider;

public class GherkinModel {

	private IStepProvider stepProvider;
	
	private IMarkerManager markerManager;
	
	private IFile file;
	
	private String documentLanguage;
	
	private List<PositionedElement> elements = new ArrayList<PositionedElement>();
	
	static final String ERROR_ID = "cucumber.eclipse.editor.editors.Editor.syntaxerror";
	
	static final String UNMATCHED_STEP_ERROR_ID = "cucumber.eclipse.editor.editors.Editor.unmatchedsteperror";

	
	public GherkinModel(IStepProvider stepProvider, IMarkerManager markerManager, IFile file) {
		this.stepProvider = stepProvider;
		this.markerManager = markerManager;
		this.file = file;
	}

	public cucumber.eclipse.steps.integration.Step getStep(String selectedLine) {
		return new StepMatcher().matchSteps(documentLanguage,
				stepProvider.getStepsInEncompassingProject(file), selectedLine);
	}

	public PositionedElement getFeatureElement() {
		return elements.isEmpty() ? null : elements.get(0);
	}
	
	public List<Position> getFoldRanges() {
		List<Position> foldRanges = new ArrayList<Position>();
		for (PositionedElement element : elements) {
			if (element.isFeature() || element.isStepContainer() || element.isExamples()) {
				try {
					foldRanges.add(element.toPosition());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return foldRanges;
	}

	public void updateFromDocument(final IDocument document) {
		documentLanguage = getDocumentLanguage(document);
		elements.clear();
		removeExistingMarkers();
		
		GherkinErrorMarker gherkinParser = new GherkinErrorMarker(stepProvider,
				markerManager, file, document);
		Parser p = new Parser(gherkinParser, false);
		
		try {
			p.parse(document.get(), "", 0);
			elements = gherkinParser.getCreatedElements();
		} catch (LexingError le) {
			// TODO: log
		} catch (ParseError pe) {
			// TODO: log
		}
	}

	private void removeExistingMarkers() {
		markerManager.removeAll(ERROR_ID, file);
		markerManager.removeAll(UNMATCHED_STEP_ERROR_ID, file);
	}
}
