package storyEngine.storyNodes;

import org.simpleframework.xml.Element;

import storyEngine.storyElements.StoryElementCollection;

public class Choice
{
	@Element(name="text")
	protected String m_text;
	
	@Element(name="outcome", required=false)
	protected Outcome m_outcome;
	
	
	public Choice(String text)
	{
		m_text = text;
	}
	
	public Choice(
			@Element(name="text") String text,
			@Element(name="outcome", required=false) Outcome outcome)
	{
		this(text);
		m_outcome = outcome;
	}
	
	
	public void setOutcome(Outcome o)
	{
		m_outcome = o;
	}
	
	
	public boolean isValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		return isValid;
	}
}
	