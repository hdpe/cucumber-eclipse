package cucumber.eclipse.editor.steps;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateVariable;

import cucumber.eclipse.steps.integration.Step;

class StepCompletionTemplate extends Template {

	private static final String GROUP_CONTENT
		= "(?:"
			+ "[^\\)\\\\]" // not ) or \
			+ "|" // OR
			+ "\\\\." // \ anything
		+ ")*";

	private static final String CUSTOM_CHARACTER_CLASS
			= "\\[" // [
				+ "(?:"
					+ "[^\\]\\\\]" // not ] or \
					+ "|" // OR
					+ "\\\\." // \ anything
				+ ")*"
			+ "\\]"; // ]
	
	private static final String PREDEFINED_CHARACTER_CLASS
			= "\\." // dot
			+ "|" // OR
			+ "\\\\[dDsSwW]" // \d etc.
			+ "|" // OR
			+ "\\\\p\\{[^\\}]+\\}"; // \p{Alpha} etc.

	private static final String QUANTIFIER
			= "(?:"
				+ "\\?" // ?
				+ "|" // OR
				+ "\\*" // *
				+ "|" // OR
				+ "\\+" // +
				+ "|" // OR
				+ "\\|" // |
				+ "|" // OR
				+ "\\{[^\\}]+\\}" // {n} etc.
			+ ")";

	private static final Pattern INDEFINITE_REGION_PATTERN = Pattern.compile(
			"(?:"
				+ "\\(" // (
					+ "(" // group content... group 1
						+ GROUP_CONTENT
					+ ")"
				+ "\\)" // )
				+ "|" // OR
				+ "("  // character class... group 2
					+ CUSTOM_CHARACTER_CLASS
					+ "|" // OR
					+ PREDEFINED_CHARACTER_CLASS
				+ ")"
			+ ")"
			+ QUANTIFIER + "*" // with an optional quantifier
			+ "|" // OR
			+ "(.)" // any character... group 3
			+ QUANTIFIER + "+" // with a quantifier
		);
	
	private final Step step;

	StepCompletionTemplate(Step step, String contextTypeId) {
		super(step.getText(), "", contextTypeId, step.getText().replace("$", "$$"), false);
		this.step = step;
	}

	Step getStep() {
		return step;
	}

	@Override
	public boolean matches(String prefix, String contextTypeId) {
		return StepCompletionUtil.isStepValidForPrefix(step, prefix);
	}

	TemplateBuffer toBuffer() {
		int argumentIdx = 0;
		StringBuffer sb = new StringBuffer();
		List<TemplateVariable> variables = new ArrayList<TemplateVariable>();
		
		Matcher matcher = INDEFINITE_REGION_PATTERN.matcher(stripLineBoundaries(step.getText()));
		
		while (matcher.find()) {
			String replacement;

			String groupContent = matcher.group(1);
			String characterClass = matcher.group(2);
			
			// character class
			if (characterClass != null) {
				replacement = characterClass;
			}
			// single character + quantifier
			else if (groupContent == null) {
				replacement = matcher.group(3);
			}
			// non-capturing group
			else if (groupContent.startsWith("?:")) {
				replacement = groupContent.substring(2);
			}
			// capturing group
			else {
				if (argumentIdx < step.getParameterNames().size()) {
					replacement = step.getParameterNames().get(argumentIdx);
				}
				else {
					replacement = groupContent;
				}
				argumentIdx ++;
			}
			
			matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
			
			variables.add(new TemplateVariable(String.format("var%d", variables.size()),
					replacement, new int[] {sb.length() - replacement.length()}));
		}
		
		matcher.appendTail(sb);
		
		return new TemplateBuffer(sb.toString(), variables.toArray(
				new TemplateVariable[variables.size()]));
	}

	private static String stripLineBoundaries(String regex) {
		return regex.replaceAll("\\^", "") // ^ at start
				.replaceAll("([^\\\\])\\$$", "$1"); // $ at end
	}
}