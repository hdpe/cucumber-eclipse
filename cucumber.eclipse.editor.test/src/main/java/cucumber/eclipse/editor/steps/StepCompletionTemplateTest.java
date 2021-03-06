package cucumber.eclipse.editor.steps;

import java.util.Arrays;

import org.eclipse.jface.text.templates.TemplateBuffer;
import org.junit.Test;

import cucumber.eclipse.steps.integration.Step;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StepCompletionTemplateTest {
    
    @Test
    public void constructorEscapesDollarInPattern() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("$"), "");
        
        assertThat(template.getPattern(), is("$$"));
    }

    @Test
    public void matchesWithNoPrefixIsTrue() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("^x$"), "");
        
        assertThat(template.matches("", null), is(true));
    }

    @Test
    public void matchesWithFullMatchIsTrue() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("^x$"), "");
        
        assertThat(template.matches("x", null), is(true));
    }

    @Test
    public void matchesWithPrefixMatchIsTrue() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("^xy$"), "");
        
        assertThat(template.matches("x", null), is(true));
    }

    @Test
    public void matchesWithNonPrefixMatchIsFalse() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("^xy$"), "");
        
        assertThat(template.matches("y", null), is(false));
    }

    @Test
    public void matchesWithNoMatchIsFalse() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("^x$"), "");
        
        assertThat(template.matches("y", null), is(false));
    }
    
    @Test
    public void toBufferStripsOpeningHat() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("^x"), "");
        
        assertThat(template.toBuffer().getString(), is("x"));
    }
    
    @Test
    public void toBufferStripsClosingDollar() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("x$"), "");
        
        assertThat(template.toBuffer().getString(), is("x"));
    }
    
    @Test
    public void toBufferRetainsEscapedClosingDollar() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("\\$"), "");
        
        assertThat(template.toBuffer().getString(), is("\\$"));
    }

    @Test
    public void toBufferWithTextCreatesNoVariables() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("x"), "");
        
        TemplateBuffer buf = template.toBuffer();
        
        assertThat("string", buf.getString(), is("x"));
        assertThat("variables.length", buf.getVariables().length, is(0));
    }
    
    @Test
    public void toBufferWithQuantifiedTextCreatesVariable() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("x?"), "");
        
        TemplateBuffer buf = template.toBuffer();
        
        assertThat("string", buf.getString(), is("x"));
        assertThat("variables.length", buf.getVariables().length, is(1));
        assertThat("variables[0].defaultValue", buf.getVariables()[0].getDefaultValue(), is("x"));
    }
    
    @Test
    public void toBufferWithCapturingGroupCreatesVariable() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("(\\d\\))", "x"), "");
        
        TemplateBuffer buf = template.toBuffer();
        
        assertThat("string", buf.getString(), is("x"));
        assertThat("variables.length", buf.getVariables().length, is(1));
        assertThat("variables[0].defaultValue", buf.getVariables()[0].getDefaultValue(), is("x"));
    }
    
    @Test
    public void toBufferWithCapturingGroupWithoutCorrespondingParameterCreatesVariable() {
    	StepCompletionTemplate template = new StepCompletionTemplate(createStep("(\\d)"), "");
    	
    	TemplateBuffer buf = template.toBuffer();
    	
    	assertThat("string", buf.getString(), is("\\d"));
    	assertThat("variables.length", buf.getVariables().length, is(1));
    	assertThat("variables[0].defaultValue", buf.getVariables()[0].getDefaultValue(), is("\\d"));
    }
    
    @Test
    public void toBufferWithNonCapturingGroupCreatesVariable() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("(?:x)"), "");
        
        TemplateBuffer buf = template.toBuffer();
        
        assertThat("string", buf.getString(), is("x"));
        assertThat("variables.length", buf.getVariables().length, is(1));
        assertThat("variables[0].defaultValue", buf.getVariables()[0].getDefaultValue(), is("x"));
    }
    
    @Test
    public void toBufferWithCustomCharacterClassCreatesVariable() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("[x\\]]"), "");
        
        TemplateBuffer buf = template.toBuffer();
        
        assertThat("string", buf.getString(), is("[x\\]]"));
        assertThat("variables.length", buf.getVariables().length, is(1));
        assertThat("variables[0].defaultValue", buf.getVariables()[0].getDefaultValue(), is("[x\\]]"));
    }
    
    @Test
    public void toBufferWithPredefinedCharacterClassCreatesVariable() {
        StepCompletionTemplate template = new StepCompletionTemplate(createStep("\\p{Alpha}"), "");
        
        TemplateBuffer buf = template.toBuffer();
        
        assertThat("string", buf.getString(), is("\\p{Alpha}"));
        assertThat("variables.length", buf.getVariables().length, is(1));
        assertThat("variables[0].defaultValue", buf.getVariables()[0].getDefaultValue(), is("\\p{Alpha}"));
    }

    private static Step createStep(String text, String... parameters) {
        Step step = new Step();
        step.setText(text);
        step.setParameterNames(Arrays.asList(parameters));
        return step;
    }
}
