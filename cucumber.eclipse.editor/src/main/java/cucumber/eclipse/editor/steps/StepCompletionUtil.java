package cucumber.eclipse.editor.steps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import cucumber.eclipse.steps.integration.Step;

public class StepCompletionUtil {

	public static String getStepPrefix(IDocument document, int offset) {
		// TODO: support internationalised keywords
		try {
			int line = document.getLineOfOffset(offset);
			int lineOffset = document.getLineOffset(line);
			String all = document.get(lineOffset, offset - lineOffset);
			Matcher m = Pattern.compile("(?:Given|And|Or|When|Then)\\s+(.*?)$").matcher(all);
			if (m.find()) {
				return m.group(1);
			}
		}
		catch (BadLocationException exception) {
			return "";
		}
		return "";
	}

	public static boolean isStepValidForPosition(Step step, IDocument document, int offset) {
		return isStepValidForPrefix(step, getStepPrefix(document, offset));
	}

	public static boolean isStepValidForPrefix(Step step, String prefix) {
		// TODO: remedy this laughably performant regex evaluation loop
		for (int i = step.getText().length(); i >= prefix.length(); i --) {
			String patternText = step.getText().substring(0, i);
			try {
				if (Pattern.compile(patternText).matcher(prefix).matches()) {
					return true;
				}
			}
			catch (PatternSyntaxException exception) {
				// ignore
			}
		}
		
		return false;
	}
}
