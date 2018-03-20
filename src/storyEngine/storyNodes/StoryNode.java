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
	
	@Attribute(name="lastNode", required=false)
	protected boolean m_lastNode;

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
	protected int m_selectedChoiceIndex;

	public StoryNode(
			@Attribute(name="id") String id, 
			@Attribute(name="type") NodeType type,
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="functionalDescription") FunctionalDescription funcDesc,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		this(id, type, false, teaserText, eventText, funcDesc, null, choices);
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
		this(id, type, false, teaserText, eventText, funcDesc, prerequisite, choices);
	}


	public StoryNode(
			@Attribute(name="id") String id,  
			@Attribute(name="type") NodeType type,
			@Attribute(name="lastNode", required=false) boolean lastNode,
			@Element(name="teaserText") String teaserText, 
			@Element(name="eventText") String eventText,
			@Element(name="functionalDescription") FunctionalDescription funcDesc,
			@Element(name="prerequisite", required=false) Prerequisite prerequisite,
			@ElementList(name="choices", inline=true) ArrayList<Choice> choices)
	{
		m_id = id;
		m_type = type;
		m_lastNode = false;
		m_teaserText = teaserText;
		m_eventText = eventText;
		m_functionalDesc = funcDesc;
		m_prerequisite = prerequisite;
		m_choices = choices;
		
		resetNode();
	}


	public String getID() { return m_id; }
	public String getTeaserText() { return m_teaserText; }
	public String getEventText() { return m_eventText; }
	
	public boolean isLastNode() { return m_lastNode; }
	
	public boolean isKernel() { return m_type == NodeType.kernel; }
	public boolean isSatellite() { return m_type == NodeType.satellite; }
	
	public boolean isConsumed() { return m_consumed; }
	
	
	public boolean featuresElement(String id)
	{
		boolean features = false;
		if (m_functionalDesc != null)
		{
			features = m_functionalDesc.featuresElement(id);
		}
		return features;
	}
	
	public ArrayList<String> getElementIDs()
	{
		ArrayList<String> elementIDs = new ArrayList<String>();
		
		if (m_functionalDesc != null)
		{
			elementIDs.addAll(m_functionalDesc.getElementIDs());
		}
		
		return elementIDs;
	}
	
	
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
	
	
	public float getProminenceValueForElement(String elementID)
	{
		float prominence = 0;
		
		if (m_functionalDesc != null)
		{
			prominence = m_functionalDesc.getProminenceValueForElement(elementID);
		}
		
		return prominence;
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
	
	
	////////////////////////////////////////////////////////////////
	
	
	public int getNumChoices()
	{
		int numChoices = 0;
		
		if (m_choices != null)
		{
			numChoices = m_choices.size();
		}
		
		return numChoices;
	}
	
	
	public String getTextForChoice(int index)
	{
		String text = null;
		
		if (m_choices != null && index < m_choices.size())
		{
			text = m_choices.get(index).getText();
		}
		
		return text;
	}
	
	
	public int getSelectedChoice() { return m_selectedChoiceIndex; }
	
	
	public void setSelectedChoice(int choiceIndex)
	{ 
		m_selectedChoiceIndex = Math.max(0, choiceIndex); 
	}
	
	public boolean selectedChoiceIsValid()
	{
		return
			(m_choices == null && m_selectedChoiceIndex < 0) ||
			(m_choices != null && m_choices.size() == 1 && m_selectedChoiceIndex < 0) ||
			(m_choices != null && m_selectedChoiceIndex >= 0
							   && m_selectedChoiceIndex < m_choices.size());
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	public String getOutcomeTextForSelectedChoice()
	{
		String text = null;
		
		if (m_choices != null && selectedChoiceIsValid())
		{
			text = m_choices.get(getSelectedChoice()).getOutcome().getOutcomeText();
		}
		
		return text;
	}
	
	
	public void applyOutcomeForSelectedChoice(StoryState state, StoryElementCollection c)
	{
		// Node-specific changes
		m_consumed = true;
		state.addNodeToScenesSeen(this);
		
		// Apply the outcome for the selected choice
		if (m_choices != null && selectedChoiceIsValid())
		{
			Outcome outcome = m_choices.get(getSelectedChoice()).getOutcome();
			if (outcome != null)
			{
				outcome.applyOutcome(state, c);
			}
		}
	}
	
	public void resetRelevantDesireValuesInStoryState(StoryState state)
	{
		// Reset desire values in state that appear in the functional description
		if (m_functionalDesc != null)
		{
			m_functionalDesc.resetDesireValues(state);
		}
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	public void resetNode()
	{
		m_consumed = false;
		m_selectedChoiceIndex = -1;
	}
}
