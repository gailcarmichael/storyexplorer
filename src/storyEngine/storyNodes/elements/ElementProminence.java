package storyEngine.storyNodes.elements;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class ElementProminence
{
	@Attribute(name="name")
	protected StoryElement m_element;
	
	@Attribute(name="specifier", required=false)
	protected String m_name;
	
	@Text
	protected int m_prominence;
	
	
	public ElementProminence(
			@Attribute(name="name") StoryElement element, 
			@Attribute(name="specifier") String name,
			@Text int prominence)
	{
		m_element = element;
		m_name = name;
		m_prominence = prominence;
	}
	
	public ElementProminence(
			@Attribute(name="name") StoryElement element, 
			@Text int prominence)
	{
		this(element, null, prominence);
	}
}
