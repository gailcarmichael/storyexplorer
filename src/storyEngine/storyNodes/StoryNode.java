package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.Story;
import storyEngine.StoryState;
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

	@ElementList(name="choices", inline=true, required=false)
	protected ArrayList<Choice> m_choices;
	
	protected boolean m_consumed;


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
		
		m_consumed = false;
	}


	public String getID() { return m_id; }
	public String getTeaserText() { return m_teaserText; }
	public String getEventText() { return m_eventText; }
	
	public boolean isKernel() { return m_type == NodeType.kernel; }
	public boolean isSatellite() { return m_type == NodeType.satellite; }
	
	public boolean isConsumed() { return m_consumed; }
	
	
	public String toString()
	{
		return m_id + ": " + m_teaserText;
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	public boolean passesPrerequisite(StoryState storyState)
	{
		if (m_prerequisite == null)
		{
			return true;
		}
		else
		{
			return m_prerequisite.passes(storyState);
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
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
		
		if (m_choices != null)
		{
			for (Choice c : m_choices)
			{
				if (!c.isValid(elements))
				{
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	public float calculatePriorityScore(Story story, StoryElementCollection elementCol)
	{
		float score = 0;
		
		if (m_functionalDesc != null)
		{
			score = m_functionalDesc.calculatePriorityScore(story, elementCol);
		}
		
		return score;
	}
}
