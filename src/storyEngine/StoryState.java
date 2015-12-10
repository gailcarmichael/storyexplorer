package storyEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.StoryNode;

// There is meant to be one central story state for a
// story's run-through. It tracks where in a story we
// are, including statistics about how long it has been
// since we've seen certain themes, characters, and so on.
// Other values like tension can also be tracked.

public class StoryState
{
	
	protected final static int RESET_DESIRE_VALUE = 0;
	protected final static float DESIRE_RATE_INCREASE = 1.0f; // this may end up being data-driven eventually
	
	
	// Elements of type QuantifiableStoryStateOnly
	@ElementMap(required=false, inline=true, entry="value", key="id", attribute=true)
	protected HashMap<String, Float> m_elementValues;
	
	// Elements of type Quantifiable
	@ElementMap(required=false, inline=true, entry="desire", key="id", attribute=true)
	protected HashMap<String, Float> m_elementDesires;
	
	// Element tags
	@ElementList(required=false, inline=true)
	protected ArrayList<String> m_tagList;
	
	protected ArrayList<StoryNode> m_scenesSeen;
	
	
	public StoryState(
			@ElementMap(required=false, inline=true, entry="value", key="id", attribute=true) HashMap<String, Float> elementValues,
			@ElementMap(required=false, inline=true, entry="desire", key="id", attribute=true) HashMap<String, Float> elementDesires,
			@ElementList(required=false, inline=true) ArrayList<String> tagList)
	{
		m_elementValues = elementValues;
		m_elementDesires = elementDesires;
		m_tagList = tagList;
		m_scenesSeen = new ArrayList<StoryNode>();
		
		if (m_elementValues == null) m_elementValues = new HashMap<String, Float>();
		if (m_elementDesires == null) m_elementDesires = new HashMap<String, Float>();
		if (m_tagList == null) m_tagList = new ArrayList<String>();
	}
	
	
	public boolean isDesireValue(String id) { return m_elementDesires.containsKey(id); }
	
	
	///////////////////////////////////////////////////////////////
	
	
	public StoryState clone()
	{
		StoryState newState = new StoryState(
				new HashMap<String, Float>(m_elementValues),
				new HashMap<String, Float>(m_elementDesires),
				new ArrayList<String>(m_tagList));
		
		newState.m_scenesSeen = new ArrayList<StoryNode>(m_scenesSeen);
		
		return newState;
	}
	
	
	///////////////////////////////////////////////////////////////
	
	
	public String toString()
	{
		String toReturn = "";
		
		System.out.println("Story state: ");
		
		if (m_elementValues != null)
		{
			for (String id : m_elementValues.keySet())
			{
				toReturn += "\t" + id + ": " + m_elementValues.get(id) + "\n";
			}
		}
		
		if (m_elementDesires != null)
		{
			for (String id : m_elementDesires.keySet())
			{
				toReturn += "\t" + id + ": " + m_elementDesires.get(id) + "\n";
			}
		}
		
		if (m_tagList != null)
		{
			for (String id : m_tagList)
			{
				toReturn += id + "\n";
			}
		}
		
		return toReturn.substring(0, toReturn.length()-2);
	}
	
	
	///////////////////////////////////////////////////////////////
	
	
	public float getValueForElement(String id)
	{
		if (m_elementValues.containsKey(id))
		{
			return m_elementValues.get(id);
		}
		else if (m_elementDesires.containsKey(id))
		{
			return m_elementDesires.get(id);
		}
		else
		{
			System.err.println("StoryState has no quantifiable element with id " + id);
			return -1;
		}
	}
	
	
	public void setValueForElement(String id, float value)
	{
		if (m_elementValues.containsKey(id))
		{
			m_elementValues.put(id, value);
		}
		else if (m_elementDesires.containsKey(id))
		{
			m_elementDesires.put(id, value);
		}
		else
		{
			System.err.println("StoryState has no quantifiable element with id " + id);
		}
	}
	
	
	public boolean taggedWithElement(String id)
	{
		return m_tagList.contains(id);
	}
	
	
	public void removeTag(String id)
	{
		if (m_tagList.remove(id))
		{
			m_tagList.remove(id);
		}
		else
		{
			System.err.println("StoryState has no taggable element with id " + id);
		}
	}
	
	
	public void addTag(String id)
	{
		if (!m_tagList.contains(id))
		{
			m_tagList.add(id);
		}
	}
	
	
	///////////////////////////////////////////////////////////////
	
	
	public int getNumScenesSeen() { return m_scenesSeen.size(); }
	public ArrayList<StoryNode> getScenesSeen() { return m_scenesSeen; }
	
	
	public void addNodeToScenesSeen(StoryNode n)
	{
		if (!m_scenesSeen.contains(n))
		{
			m_scenesSeen.add(n);
		}
	}
	
	
	public boolean haveSeenScene(String sceneID)
	{
		boolean seenScene = false;
		
		for (StoryNode n : m_scenesSeen)
		{
			if (n.getID().equals(sceneID))
			{
				seenScene = true;
				break;
			}
		}
		
		return seenScene;
	}
	
	float getProminenceForMostRecentNodeWithElement(String elementID)
	{
		float desire = -1;
		
		for (int i = m_scenesSeen.size()-1; i >= 0; i--)
		{
			desire = m_scenesSeen.get(i).getProminenceValueForElement(elementID);
			if (desire > 0) // i.e. if an element is featured at all
			{
				break;
			}
		}
		
		return desire;
	}
	
	
	///////////////////////////////////////////////////////////////
	
	
	public float getLargestDesireValue()
	{
		return Collections.max(m_elementDesires.values());
	}
	
	
	public void resetDesireValue(String id)
	{
		if (m_elementDesires.containsKey(id))
		{
			m_elementDesires.put(id, (float) RESET_DESIRE_VALUE);
		}
		else
		{
			System.err.println("StoryState could not reset desire value for " + id);
		}
	}
	
	
	void increaseDesireValues()
	{
		for (String id : m_elementDesires.keySet())
		{
			float value = m_elementDesires.get(id);
			m_elementDesires.put(id, value += DESIRE_RATE_INCREASE);
		}
	}
	
	
	///////////////////////////////////////////////////////////////
	
	
	public boolean isValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		// A story state is valid if all the elements in the
		// collection have values or desires in the correct
		// place in the story state
		
		for (String id : elements.getIDs())
		{
			StoryElement e = elements.getElementWithID(id);
			boolean thisElementValid = true;
			
			if (e.getType() == ElementType.quantifiableStoryStateOnly)
			{
				// This element should be in the values, but not the desires
				if ((m_elementDesires != null && m_elementDesires.containsKey(id)) ||
						m_elementValues == null ||
						!m_elementValues.containsKey(id))
				{
					thisElementValid = false;
				}
			}
			else if (e.getType() == ElementType.quantifiable)
			{
				// This element should be in the desires, but not the values
				if ((m_elementValues != null && m_elementValues.containsKey(id)) ||
						m_elementDesires == null ||
						!m_elementDesires.containsKey(id))
				{
					thisElementValid = false;
				}
			}
			
			if (!thisElementValid)
			{
				System.err.println("StoryState is not valid; element with id " + id
						+ " and type " + e.getType() + " is not in the right place");
				isValid = false;
			}
		}
		
		// Check that all tags are valid elements
		if (m_tagList != null)
		{
			for (String id : m_tagList)
			{
				StoryElement e = elements.getElementWithID(id);
				if (e != null && e.getType() != ElementType.taggable)
				{
					System.err.println("StoryState is not valid; element with id " + id
							+ " should be taggable, but has type " + e.getType());
					isValid = false;
				}
			}
		}
		
		
		return isValid;
	}
	
	
	///////////////////////////////////////////////////////////////
}