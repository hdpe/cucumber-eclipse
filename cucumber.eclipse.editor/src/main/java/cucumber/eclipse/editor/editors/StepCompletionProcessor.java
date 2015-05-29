package cucumber.eclipse.editor.editors;

import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import cucumber.eclipse.steps.integration.Step;

public class StepCompletionProcessor implements IContentAssistProcessor {

	private Editor editor;
	
	public StepCompletionProcessor(Editor editor) {
		this.editor = editor;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		List<Step> availableSteps = editor.getAvailableSteps();
		ICompletionProposal[] result = new ICompletionProposal[availableSteps.size()];
		for (int i = 0; i < availableSteps.size(); i ++) {
			Step step = availableSteps.get(i);
			result[i] = new CompletionProposal(step.getText(), editor.getSelection().getOffset(),
					editor.getSelection().getLength(), step.getText().length());
		}
		return result;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return new IContextInformation[0];
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}
}
