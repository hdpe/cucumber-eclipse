package cucumber.eclipse.editor.steps;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.text.Document;
import org.junit.Test;

public class StepCompletionUtilTest {

    @Test
    public void getStepPrefixReturnsToStartOfStepName() throws Exception {
        String source = "    Given an x\n"; // x at position 13
        Document document = new Document(source);
        
        String prefix = StepCompletionUtil.getStepPrefix(document, 13);
        
        assertThat(prefix, is("an "));
    }
}
