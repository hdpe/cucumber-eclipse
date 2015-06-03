package cucumber.eclipse.editor.steps;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

import cucumber.eclipse.editor.editors.Editor;

import cucumber.eclipse.steps.integration.Step;

public class StepCompletionProcessor extends TemplateCompletionProcessor {

	private static class StepCompletionTemplateContext extends DocumentTemplateContext {
		
		StepCompletionTemplateContext(TemplateContextType type, IDocument document,
				int offset, int length) {
			super(type, document, offset, length);
		}

		@Override
		public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {
			if (!canEvaluate(template))
				return null;
			
			TemplateBuffer buffer = ((StepCompletionTemplate) template).toBuffer();
			getContextType().resolve(buffer, this);
			return buffer;
		}
	}

	private static class StepCompletionProposal extends TemplateProposal {

		StepCompletionProposal(Template template, TemplateContext context, IRegion region,
				Image image, int relevance) {
			super(template, context, region, image, relevance);
		}

		@Override
		public String getDisplayString() {
			return getTemplate().getName();
		}
	}
	
	private static final TemplateContextType CONTEXT_TYPE = new TemplateContextType(
			StepCompletionProcessor.class.getName());
	
	private final Editor editor;
	
	public StepCompletionProcessor(Editor editor) {
		this.editor = editor;
	}
	
	@Override
	protected TemplateContext createContext(ITextViewer viewer, IRegion region) {
		TemplateContextType contextType = getContextType(viewer, region);
		if (contextType != null) {
			IDocument document = viewer.getDocument();
			return new StepCompletionTemplateContext(contextType, document, region.getOffset(),
					region.getLength());
		}
		return null;
	}
	
	@Override
	protected ICompletionProposal createProposal(Template template, TemplateContext context,
			IRegion region, int relevance) {
		return new StepCompletionProposal(template, context, region, getImage(template), relevance);
	}
	
	@Override
	protected Template[] getTemplates(String contextTypeId) {
		List<Step> availableSteps = editor.getAvailableSteps();
		Template[] result = new Template[availableSteps.size()];
		for (int i = 0; i < availableSteps.size(); i ++) {
			Step step = availableSteps.get(i);
			result[i] = new StepCompletionTemplate(step, contextTypeId);
		}
		return result;
	}

	@Override
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		return CONTEXT_TYPE;
	}

	@Override
	protected Image getImage(Template template) {
		return null;
	}
}
