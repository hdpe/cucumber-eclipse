package cucumber.eclipse.editor.editors;

import static java.util.Collections.emptySet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import gherkin.parser.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.junit.Test;

import cucumber.eclipse.editor.steps.IStepProvider;
import cucumber.eclipse.editor.tests.TestFile;
import cucumber.eclipse.editor.tests.TestMarker;
import cucumber.eclipse.editor.tests.TestTextEditor;
import cucumber.eclipse.steps.integration.Step;

public class GherkinErrorMarkerTest {

    @Test
    public void stepMarksOnlyUnmatchedStep() throws BadLocationException {
        String source = "Feature: x\n"
                + "\n"
                + "  Scenario: x\n"
                + "    Given x\n"; // step name at line 3, position 10
        List<IMarker> createdMarkers = new ArrayList<IMarker>();
        Document document = new Document(source);
        
        new Parser(newErrorMarker(document, createdMarkers)).parse(source, "", 0);
        
        assertThat(IMarker.CHAR_START,
                createdMarkers.get(0).getAttribute(IMarker.CHAR_START, 0),
                is(document.getLineOffset(3) + 10));
        assertThat(IMarker.CHAR_END,
                createdMarkers.get(0).getAttribute(IMarker.CHAR_END, 0),
                is(document.getLineOffset(3) + 11));
    }

    private GherkinErrorMarker newErrorMarker(Document document,
            final List<IMarker> createdMarkers) {
        return new GherkinErrorMarker(new TestTextEditor(document), newStepProvider(),
                new TestFile() {
                    public IMarker createMarker(String type) throws CoreException {
                        IMarker marker = new TestMarker(type, this);
                        createdMarkers.add(marker);
                        return marker;
                    }
                }, document);
    }

    private IStepProvider newStepProvider() {
        return new IStepProvider() {
            public Set<Step> getStepsInEncompassingProject(IFile featurefile) {
                return emptySet();
            }
        };
    }
}
