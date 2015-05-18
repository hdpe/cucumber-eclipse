package cucumber.eclipse.editor.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cucumber.eclipse.editor.steps.IStepProvider;
import cucumber.eclipse.editor.tests.TestMarker;
import cucumber.eclipse.steps.integration.Step;
import gherkin.parser.Parser;

import static java.util.Collections.emptySet;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GherkinErrorMarkerTest {

    @Test
    public void stepMarksOnlyUnmatchedStep() throws Exception {
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
            final List<IMarker> createdMarkers) throws CoreException {
    	ITextEditor editor = mock(ITextEditor.class);
    	IDocumentProvider documentProvider = mock(IDocumentProvider.class);
    	when(documentProvider.getDocument(any())).thenReturn(document);
    	when(editor.getDocumentProvider()).thenReturn(documentProvider);
    	
    	IStepProvider stepProvider = mock(IStepProvider.class);
    	
    	final IFile inputFile = mock(IFile.class);
    	
    	doAnswer(new Answer<IMarker>() {
    		
			@Override
			public IMarker answer(InvocationOnMock invocation) throws Throwable {
				IMarker marker = new TestMarker((String) invocation.getArguments()[0], inputFile);
                createdMarkers.add(marker);
                return marker;
			}
		}).when(inputFile.createMarker(anyString()));
    	
    	return new GherkinErrorMarker(editor, stepProvider, inputFile, document);
    }

    private IStepProvider newStepProvider() {
        return new IStepProvider() {
            @Override
			public Set<Step> getStepsInEncompassingProject(IFile featurefile) {
                return emptySet();
            }
        };
    }
}
