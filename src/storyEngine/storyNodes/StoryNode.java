package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class StoryNode
{
	@Element(name="id")
	protected String m_id;
	
	@Element(name="teaserText")
	protected String m_teaserText;
	
	@Element(name="eventText")
	protected String m_eventText;
	
	@ElementList(name="choices", inline=true)
	protected ArrayList<Choice> m_choices;
	
	@Element(name="functionalDescription", required=false)
	protected FunctionalDescription m_functionalDesc;
	
	
	public StoryNode(
			@Element(name="id") String id, 
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices,
			@Element(name="functionalDescription") FunctionalDescription funcDesc)
	{
		m_id = id;
		m_teaserText = teaserText;
		m_eventText = eventText;
		m_choices = choices;
		m_functionalDesc = funcDesc;
	}
	
	
	public String getID() { return m_id; }
	public String getTeaserText() { return m_teaserText; }
	public String getEventText() { return m_eventText; }
}
