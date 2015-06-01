package storyEngine.storyElements;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

// A quantifiable story element that can be tracked in the story's state,
// have some kind of value associated with it in a node, and be used in
// prerequisite rules.

// For example, we could have the category "theme" and the name "heroism,"
// which would be an element that is stored in both the story state and
// the node descriptions, as well as used in priority calculations.

// Another example would be the category "terrain" and the name "mountains."
// This can only be used in node descriptions.

// In some cases, there is only one element in a category. In these cases, it
// is reasonable to use the same name for both the category and the element.

public class StoryElement
{
	@Attribute(name="id")
	protected String m_id;
	
	@Attribute(name="type")
	protected ElementType m_type;
	
	@Element(name="category")
	protected String m_category;
	
	@Element(name="name")
	protected String m_name;
	
	
	public StoryElement(
			@Attribute(name="id") String id, 
			@Element(name="category") String category, 
			@Element(name="name") String name,
			@Attribute(name="type") ElementType type)
	{
		m_id = id;
		m_category = category;
		m_name = name;
		m_type = type;
	}
	
	
	public String getID() { return m_id; }
	public ElementType getType() { return m_type; }
	public String getCategory() { return m_category; }
	public String getName() { return m_name; }
	
	
	public boolean hasDesireValue() { return getType() == ElementType.quantifiable; }
	
	
	public String toString()
	{
		return "StoryElement with ID " + m_id;
	}
}

