package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


// A list of StoryNodes will be stored in an XML document representing
// the story. When designing the pieces of a StoryNode, one should avoid
// inheritance when possible so writers of the XML do not have to include
// the class name of an object in the XML to distinguish it. 

@Root
public class StoryNode
{
	@Element(name="id")
	protected String m_id;

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
			@Element(name="id") String id, 
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="functionalDescription") FunctionalDescription funcDesc,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, teaserText, eventText, funcDesc, null, choices);
	}


	public StoryNode(
			@Element(name="id") String id, 
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="prerequisite", required=false) Prerequisite prerequisite,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, teaserText, eventText, null, prerequisite, choices);
	}


	public StoryNode(
			@Element(name="id") String id, 
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, teaserText, eventText, null, null, choices);
	}


	public StoryNode(
			@Element(name="id") String id, 
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="functionalDescription") FunctionalDescription funcDesc,
			@Element(name="prerequisite", required=false) Prerequisite prerequisite,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		m_id = id;
		m_teaserText = teaserText;
		m_eventText = eventText;
		m_functionalDesc = funcDesc;
		m_prerequisite = prerequisite;
		m_choices = choices;
	}


	public String getID() { return m_id; }
	public String getTeaserText() { return m_teaserText; }
	public String getEventText() { return m_eventText; }
}
