package cucumber.eclipse.editor.editors;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.text.edits.TextEdit;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import gherkin.formatter.model.BasicStatement;
import gherkin.formatter.model.Scenario;
import cucumber.eclipse.editor.markers.IMarkerManager;
import cucumber.eclipse.editor.steps.IStepProvider;
import cucumber.eclipse.editor.tests.TestFile;
import cucumber.eclipse.steps.integration.Step;
import cucumber.eclipse.steps.integration.StepListener;

public class GherkinModelTest {

    @Test
    public void stepContainerFoldRangeExtendsToLineFollowingLastStep() throws BadLocationException {        
        String source = "Feature: x\n"
                + "\n"
                + "  Scenario: 1\n" // line 2
                + "    Given y with\n"
                + "      \"\"\"\n"
                + "      a\n"
                + "      \"\"\"\n"
                + "\n" // line 7
                + "  Scenario: 2\n"
                + "    Given z\n";
        Document document = new Document(source);
        GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
        
        model.updateFromDocument(document);
        Position range = model.getFoldRanges().get(1);
        
        assertThat("offset", range.getOffset(), is(document.getLineOffset(2)));
        assertThat("range", range.getLength(), is(document.getLineOffset(7) - document.getLineOffset(2)));
    }
    
    @Test
    public void scenarioOutlineFoldRangeExtendsToLineFollowingLastExampleRow() throws BadLocationException {        
        String source = "Feature: x\n"
                + "\n"
                + "  Scenario Outline: 1\n" // line 2
                + "    Given y\n"
                + "\n"
                + "    Examples:\n"
                + "      | a | b |\n"
                + "      | 1 | 2 |\n"
                + "\n" // line 8
                + "  Scenario: 2\n"
                + "    Given z\n";
        Document document = new Document(source);
        GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
        
        model.updateFromDocument(document);
        Position range = model.getFoldRanges().get(1);
        
        assertThat("offset", range.getOffset(), is(document.getLineOffset(2)));
        assertThat("range", range.getLength(), is(document.getLineOffset(8) - document.getLineOffset(2)));
    }
    
    @Test
    public void examplesFoldRangeExtendsToLineFollowingLastRow() throws BadLocationException {        
        String source = "Feature: x\n"
                + "\n"
                + "  Scenario Outline: 1\n"
                + "    Given y\n"
                + "\n"
                + "    Examples:\n" // line 5
                + "      | a | b |\n"
                + "      | 1 | 2 |\n"
                + "\n" // line 8
                + "  Scenario: 2\n"
                + "    Given z\n";
        Document document = new Document(source);
        GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
        
        model.updateFromDocument(document);
        Position range = model.getFoldRanges().get(2);
        
        assertThat("offset", range.getOffset(), is(document.getLineOffset(5)));
        assertThat("range", range.getLength(), is(document.getLineOffset(8) - document.getLineOffset(5)));
    }
    
    @Test
    public void featureElementHasAttachedChildren() throws BadLocationException {        
        String source = "Feature: x\n"
                + "\n"
                + "  Background:\n"
                + "    When x\n"
                + "\n"
                + "  Scenario Outline: 1\n"
                + "    Given y\n"
                + "\n"
                + "    Examples:\n"
                + "      | a | b |\n"
                + "      | 1 | 2 |\n"
                + "\n"
                + "  Scenario: 2\n"
                + "    Given z\n";
        Document document = new Document(source);
        GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
        
        model.updateFromDocument(document);
        PositionedElement feature = model.getFeatureElement();
        
        assertThat("feature.children.size",
                feature.getChildren().size(), is(3));
        assertThat("feature.children[0].background",
                feature.getChildren().get(0).isBackground(), is(true));
        assertThat("feature.children[1].scenarioOutline",
                feature.getChildren().get(1).isScenarioOutline(), is(true));
        assertThat("feature.children[2].scenario",
                feature.getChildren().get(2).isScenario(), is(true));
        assertThat("feature.children[0].children.size",
                feature.getChildren().get(0).getChildren().size(), is(1));
        assertThat("feature.children[0].children[0].step",
                feature.getChildren().get(0).getChildren().get(0).isStep(), is(true));
        assertThat("feature.children[1].children.size",
                feature.getChildren().get(1).getChildren().size(), is(1));
        assertThat("feature.children[1].children[0].step",
                feature.getChildren().get(1).getChildren().get(0).isStep(), is(true));
        assertThat("feature.children[2].children.size",
                feature.getChildren().get(2).getChildren().size(), is(1));
        assertThat("feature.children[2].children[0].step",
                feature.getChildren().get(2).getChildren().get(0).isStep(), is(true));
    }
    
    @Test
    public void getStepElementReturnsElement() throws BadLocationException {
    	String source = "Feature: x\n"
	            + "\n"
	            + "  Scenario: 1\n"
	            + "    Given y\n" // line 3
	            + "    And z\n";
	    Document document = new Document(source);
	    GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
	    model.updateFromDocument(document);
	    
	    int stepOffset = source.indexOf("Given");
		BasicStatement statement = model.getStepElement(stepOffset).getStatement();
	    
		assertThat(statement.getName(), is("y"));
    }
    
    @Test
    public void getScenarioOrScenarioOutlineInScenarioReturnsScenario() throws BadLocationException {
    	String source = "Feature: x\n"
    			+ "\n"
    			+ "  Scenario: 1\n"
    			+ "    Given y\n"
    			+ "\n"
    			+ "  Scenario: 2\n"
    			+ "    Given z\n";
    	Document document = new Document(source);        
    	GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
    	model.updateFromDocument(document);
    	
    	BasicStatement actual = model.getScenarioOrScenarioOutline(source.indexOf("Scenario: 1"));
    	
    	assertThat(((Scenario) actual).getName(), is("1"));
    }
    
    @Test
    public void getScenarioOrScenarioOutlineInScenarioStepReturnsScenario() throws BadLocationException {
    	String source = "Feature: x\n"
    			+ "\n"
    			+ "  Scenario: 1\n"
    			+ "    Given y\n"
    			+ "\n"
    			+ "  Scenario: 2\n"
    			+ "    Given z\n";
	    Document document = new Document(source);        
	    GherkinModel model = new GherkinModel(newStepProvider(), newMarkerManager(), new TestFile());
	    model.updateFromDocument(document);
	    
	    BasicStatement actual = model.getScenarioOrScenarioOutline(source.indexOf("Given y"));
	    
	    assertThat(((Scenario) actual).getName(), is("1"));
    }
    
    private IStepProvider newStepProvider() {
        return new IStepProvider() {
            public void addStepListener(StepListener listener) {
            }

            @Override
			public TextEdit createStepSnippet(gherkin.formatter.model.Step step, IFile targetFile,
			        IDocument targetDocument) throws IOException, CoreException {
				return null;
			}

			public Set<Step> getStepsInEncompassingProject() {
                return emptySet();
            }

            public void reloadSteps() {
            }

            public void removeStepListener(StepListener listener) {
            }
        };
    }

    private IMarkerManager newMarkerManager() {
        return new IMarkerManager() {
            public void removeAll(String type, IFile file) {
            }
            
            public void add(String type, IFile file, int severity, String message, int lineNumber, int charStart, int charEnd) {
            }
        };
    }
}
