package cucumber.eclipse.steps.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;

public class Step {

	private String text;
	private IResource source;
	private int lineNumber;
	private String lang;
	private Pattern compiledText;
	private List<String> parameterNames = new ArrayList<String>();
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
		this.compiledText = Pattern.compile(text);
	}
	public IResource getSource() {
		return source;
	}
	public void setSource(IResource source) {
		this.source = source;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public boolean matches(String s) {
		return compiledText.matcher(s).matches();
	}
	
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public List<String> getParameterNames() {
		return parameterNames;
	}
	public void setParameterNames(List<String> parameterNames) {
		this.parameterNames = parameterNames;
	}
	@Override
	public String toString() {
		return "Step [text=" + text + ", source=" + source + ", lineNumber="
				+ lineNumber + "]";
	}
}
