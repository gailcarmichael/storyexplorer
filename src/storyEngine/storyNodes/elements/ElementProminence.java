package storyEngine.storyNodes.elements;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Text;

public class ElementProminence
{
	@Attribute(name="element")
	protected StoryElements m_element;
	
	@Attribute(name="name")
	protected String m_name;
	
	@Text
	protected int m_prominence;
	
	
	public ElementProminence(
			@Attribute(name="element") StoryElements element, 
			@Attribute(name="name") String name,
			@Text int prominence)
	{
		m_element = element;
		m_name = name;
		m_prominence = prominence;
	}
}
