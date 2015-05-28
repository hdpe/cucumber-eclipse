package cucumber.eclipse.editor.editors;

import static cucumber.eclipse.editor.editors.DocumentUtil.getDocumentLanguage;
import gherkin.formatter.Formatter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.BasicStatement;
import gherkin.formatter.model.DescribedStatement;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;

import cucumber.eclipse.editor.markers.IMarkerManager;
import cucumber.eclipse.editor.steps.IStepProvider;

/**
 * @author andreas
 *
 */
public class GherkinErrorMarker implements Formatter {

	private static final String ERROR_ID = "cucumber.eclipse.editor.editors.Editor.syntaxerror";

	private static final String UNMATCHED_STEP_ERROR_ID = "cucumber.eclipse.editor.editors.Editor.unmatchedsteperror";

	private final IStepProvider stepProvider;
	private final IMarkerManager markerManager;
	private final IFile file;
	private final IDocument document;
	private final List<PositionedElement> elements = new ArrayList<PositionedElement>();
	private final Stack<PositionedElement> stack = new Stack<PositionedElement>();

	public GherkinErrorMarker(IStepProvider stepProvider, IMarkerManager markerManager, IFile inputfile,
			IDocument doc) {
		this.stepProvider = stepProvider;
		this.markerManager = markerManager;
		this.file = inputfile;
		this.document = doc;
	}

	public List<PositionedElement> getCreatedElements() {
		return elements;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * gherkin.formatter.Formatter#background(gherkin.formatter.model.Background
	 * )
	 */
	@Override
	public void background(Background arg0) {
		handleStepContainer(arg0);
	}


	@Override
	public void close() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#done()
	 */
	@Override
	public void done() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#eof()
	 */
	@Override
	public void eof() {
		while (!stack.isEmpty()) {
			stack.pop().setEndLine(document.getNumberOfLines());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * gherkin.formatter.Formatter#examples(gherkin.formatter.model.Examples)
	 */
	@Override
	public void examples(Examples arg0) {
		int lastLine = getLastExamplesLine(arg0);
		newPositionedElement(arg0).setEndLine(lastLine);
		stack.peek().setEndLine(lastLine);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#feature(gherkin.formatter.model.Feature)
	 */
	@Override
	public void feature(Feature arg0) {
		stack.push(newPositionedElement(arg0));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * gherkin.formatter.Formatter#scenario(gherkin.formatter.model.Scenario)
	 */
	@Override
	public void scenario(Scenario arg0) {
		handleStepContainer(arg0);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#scenarioOutline(gherkin.formatter.model.
	 * ScenarioOutline)
	 */
	@Override
	public void scenarioOutline(ScenarioOutline arg0) {
		handleStepContainer(arg0);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#step(gherkin.formatter.model.Step)
	 */
	@Override
	public void step(Step stepLine) {
		PositionedElement element = newPositionedElement(stepLine);
		stack.peek().addChild(element);
		stack.peek().setEndLine(stepLine.getLineRange().getLast());
		
		try {
			if ("".equals(stepLine.getName())) {
				markMissingStepName(file, document, element);
			}
			else {
				String stepString = stepLine.getKeyword() + stepLine.getName();
				cucumber.eclipse.steps.integration.Step step = new StepMatcher().matchSteps(
						getDocumentLanguage(document), stepProvider.getStepsInEncompassingProject(),
						stepString);
				if (step == null) {
					markUnmatchedStep(file, document, stepLine);
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#syntaxError(java.lang.String,
	 * java.lang.String, java.util.List, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void syntaxError(String state, String event,
			List<String> legalEvents, String uri, Integer line) {

		StringBuffer buf = new StringBuffer("Syntax Error: Expected one of ");
		for (String ev : legalEvents) {
			buf.append(ev);
			buf.append(", ");
		}
		buf.replace(buf.length() - 3, buf.length(), " but got ");
		buf.append(event);
		
		try {
			markerManager.add(ERROR_ID,
					file,
					IMarker.SEVERITY_ERROR,
					buf.toString(),
					line,
					document.getLineOffset(line - 1),
					document.getLineOffset(line - 1) + document.getLineLength(line - 1));
		} catch (BadLocationException e) {
			// Ignore for now.
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gherkin.formatter.Formatter#uri(java.lang.String)
	 */
	@Override
	public void uri(String arg0) {
	}

	private void handleStepContainer(DescribedStatement stmt) {
		if (stack.peek().isStepContainer()) {
			stack.pop();
		}
		PositionedElement element = newPositionedElement(stmt);
		stack.peek().addChild(element);
		stack.push(element);
	}

	private PositionedElement newPositionedElement(BasicStatement stmt) {
		PositionedElement element = new PositionedElement(document, stmt);
		elements.add(element);
		return element;
	}

	private int getLastExamplesLine(Examples examples) {
		int lastline = examples.getLineRange().getLast();
		if (!examples.getRows().isEmpty()) {
			lastline = examples.getRows().get(examples.getRows().size() - 1).getLine(); 
		}
		return lastline;
	}
	
	private void markMissingStepName(IFile featureFile, IDocument doc,
			PositionedElement element) throws BadLocationException {
		
		Position stepPosition = element.toPosition();
		
		markerManager.add(ERROR_ID,
				featureFile,
				IMarker.SEVERITY_WARNING,
				"No step name.",
				element.getStatement().getLine(),
				stepPosition.getOffset(),
				stepPosition.getOffset() + stepPosition.getLength());
	}

	private void markUnmatchedStep(IFile featureFile, IDocument doc,
			gherkin.formatter.model.Step stepLine) throws BadLocationException {
		
		FindReplaceDocumentAdapter find = new FindReplaceDocumentAdapter(doc);
		IRegion region = find.find(doc.getLineOffset(stepLine.getLine() - 1),
				stepLine.getName(), true, true, false, false);

		markerManager.add(UNMATCHED_STEP_ERROR_ID,
				featureFile,
				IMarker.SEVERITY_WARNING,
				"Step does not have a matching glue code.",
				stepLine.getLine(),
				region.getOffset(),
				region.getOffset() + region.getLength());
	}
}
