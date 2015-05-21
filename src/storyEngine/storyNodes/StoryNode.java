package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.storyElements.StoryElementCollection;


// A list of StoryNodes will be stored in an XML document representing
// the story. When designing the pieces of a StoryNode, one should avoid
// inheritance when possible so authors of the XML won't have to include
// the class name of an object in the XML to distinguish it. 

@Root
public class StoryNode
{
	@Attribute(name="id")
	protected String m_id;
	
	@Attribute(name="type")
	protected NodeType m_type;

	@Element(name="teaserText")
	protected String m_teaserText;

	@Element(name="eventText")
	protected String m_eventText;

	@Element(name="functionalDescription", required=false)
	protected FunctionalDescription m_functionalDesc;

	@Element(name="prerequisite", required=false)
	protected Prerequisite m_prerequisite;

	@ElementList(name="choices", inline=true)
	protected ArrayList<Choice> m_choices;


	public StoryNode(
			@Attribute(name="id") String id, 
			@Attribute(name="type") NodeType type,
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="functionalDescription") FunctionalDescription funcDesc,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, type, teaserText, eventText, funcDesc, null, choices);
	}


	public StoryNode(
			@Attribute(name="id") String id,  
			@Attribute(name="type") NodeType type,
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="prerequisite", required=false) Prerequisite prerequisite,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, type, teaserText, eventText, null, prerequisite, choices);
	}


	public StoryNode(
			@Attribute(name="id") String id,  
			@Attribute(name="type") NodeType type,
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, type, teaserText, eventText, null, null, choices);
	}


	public StoryNode(
			@Attribute(name="id") String id,  
			@Attribute(name="type") NodeType type,
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="functionalDescription") FunctionalDescription funcDesc,
			@Element(name="prerequisite", required=false) Prerequisite prerequisite,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		m_id = id;
		m_type = type;
		m_teaserText = teaserText;
		m_eventText = eventText;
		m_functionalDesc = funcDesc;
		m_prerequisite = prerequisite;
		m_choices = choices;
	}


	public String getID() { return m_id; }
	public String getTeaserText() { return m_teaserText; }
	public String getEventText() { return m_eventText; }
	
	
	public boolean isValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		// A node is valid if its functional description, prerequisite, and
		// choices are all individually valid.
		
		if (m_functionalDesc != null && 
			!m_functionalDesc.isValid(elements))
		{
			isValid = false;
		}
		
		if (m_prerequisite != null &&
			!m_prerequisite.isValid(elements))
		{
			isValid = false;
		}
		
		for (Choice c : m_choices)
		{
			if (!c.isValid(elements))
			{
				isValid = false;
			}
		}
		
		return isValid;
	}
}
