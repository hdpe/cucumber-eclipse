package cucumber.eclipse.editor.editors;

import static cucumber.eclipse.editor.editors.DocumentUtil.getDocumentLanguage;
import gherkin.formatter.model.BasicStatement;
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
import cucumber.eclipse.steps.integration.StepListener;
import cucumber.eclipse.steps.integration.StepsChangedEvent;

public class GherkinModel implements StepListener {

	private IStepProvider stepProvider;
	
	private IMarkerManager markerManager;
	
	private IFile file;
	
	private IDocument document;
	
	private String documentLanguage;
	
	private List<PositionedElement> elements = new ArrayList<PositionedElement>();
	
	static final String ERROR_ID = "cucumber.eclipse.editor.editors.Editor.syntaxerror";
	
	static final String UNMATCHED_STEP_ERROR_ID = "cucumber.eclipse.editor.editors.Editor.unmatchedsteperror";

	
	public GherkinModel(IStepProvider stepProvider, IMarkerManager markerManager, IFile file) {
		this.stepProvider = stepProvider;
		this.markerManager = markerManager;
		this.file = file;
		this.stepProvider.addStepListener(this);
	}

	public void onStepsChanged(StepsChangedEvent event) {
		stepProvider.reloadSteps();
		if (document != null) {
			update();
		}
	}

	public cucumber.eclipse.steps.integration.Step getStep(String selectedLine) {
		return new StepMatcher().matchSteps(documentLanguage,
				stepProvider.getStepsInEncompassingProject(), selectedLine);
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
	
	public PositionedElement getStepElement(int offset) throws BadLocationException {
		PositionedElement element = getMostSpecificElement(offset);
		return element != null && element.isStep() ? element : null;
	}
	
	public BasicStatement getScenarioOrScenarioOutline(int offset) throws BadLocationException {
		PositionedElement element = getMostSpecificElement(offset);
		if (element == null) {
			return null;
		}
		if (element.isScenario() || element.isScenarioOutline()) {
			return element.getStatement();
		}
		BasicStatement scenario = element.getContainingScenario();
		if (scenario != null) {
			return scenario;
		}
		BasicStatement scenarioOutline = element.getContainingScenarioOutline();
		if (scenarioOutline != null) {
			return scenarioOutline;
		}
		return null;
	}

	public void updateFromDocument(final IDocument document) {
		this.document = document;
		update();
	}

	public void update() {
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
	
	public void dispose() {
		stepProvider.removeStepListener(this);
	}
	
	private PositionedElement getMostSpecificElement(int offset) throws BadLocationException {
		PositionedElement featureElement = getFeatureElement();
		return featureElement == null ? null : getMostSpecificElement(offset, featureElement);
	}
	
	private PositionedElement getMostSpecificElement(int offset, PositionedElement searchFrom) throws BadLocationException {
		for (PositionedElement element : searchFrom.getChildren()) {
			if (element.toPosition().includes(offset)) {
				return getMostSpecificElement(offset, element);
			}
		}
		return searchFrom;
	}

	private void removeExistingMarkers() {
		markerManager.removeAll(ERROR_ID, file);
		markerManager.removeAll(UNMATCHED_STEP_ERROR_ID, file);
	}
}
