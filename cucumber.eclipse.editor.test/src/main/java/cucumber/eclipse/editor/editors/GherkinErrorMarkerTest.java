package cucumber.eclipse.editor.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.junit.Test;

import cucumber.eclipse.editor.steps.IStepProvider;
import cucumber.eclipse.steps.integration.Step;
import gherkin.parser.Parser;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
    	
    	IDocumentProvider documentProvider = createMock(IDocumentProvider.class);
    	expect(documentProvider.getDocument(anyObject())).andStubReturn(document);

    	ITextEditor editor = createNiceMock(ITextEditor.class);
    	expect(editor.getDocumentProvider()).andStubReturn(documentProvider);
    	
    	IWorkspace workspace = createNiceMock(IWorkspace.class);
    	    	
    	final IFile inputFile = createMock(IFile.class);
    	
    	IStepProvider stepProvider = createMock(IStepProvider.class);
    	expect(stepProvider.getStepsInEncompassingProject(inputFile)).andStubReturn(
    		Collections.<Step>emptySet());
    	
    	
//    	EasyMock.
//
//    	doAnswer(new Answer<IMarker>() {
//
//			@Override
//			public IMarker answer(InvocationOnMock invocation) throws Throwable {
//				IMarker marker = new TestMarker((String) invocation.getArguments()[0], inputFile);
//                createdMarkers.add(marker);
//                return marker;
//			}
//		}).when(inputFile.createMarker(anyString()));
    	
    	replay(editor, documentProvider, stepProvider, inputFile);
    	
    	return new GherkinErrorMarker(editor, stepProvider, inputFile, document);
    }
}
