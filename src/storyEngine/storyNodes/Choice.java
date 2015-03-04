package storyEngine.storyNodes;

import org.simpleframework.xml.Element;

public class Choice
{
	@Element(name="text")
	protected String m_text;
	
	
	public Choice(@Element(name="text") String text)
	{
		m_text = text;
	}
}
