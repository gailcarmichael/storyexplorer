package storyEngine.storyNodes;

import org.simpleframework.xml.Element;

import storyEngine.storyElements.StoryElementCollection;

public class Choice
{
	@Element(name="text", required=false)
	protected String m_text;
	
	@Element(name="outcome", required=false)
	protected Outcome m_outcome;
	
	
	public Choice(String text)
	{
		this(text, null);
	}
	
	public Choice(Outcome outcome)
	{
		this(null, outcome);
	}
	
	public Choice()
	{
		this(null, null);
	}
	
	public Choice(
			@Element(name="text", required=false) String text,
			@Element(name="outcome", required=false) Outcome outcome)
	{
		m_text = text;
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
	