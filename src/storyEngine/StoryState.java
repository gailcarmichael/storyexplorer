package storyEngine;

import java.util.ArrayList;
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
	// Elements of type QuantifiableStoryStateOnly
	@ElementMap(required=false, inline=true, entry="value", key="id", attribute=true)
	protected HashMap<String, Integer> m_elementValues;
	
	// Elements of type Quantifiable
	@ElementMap(required=false, inline=true, entry="desire", key="id", attribute=true)
	protected HashMap<String, Float> m_elementDesires;
	
	// Element tags
	@ElementList(required=false, inline=true)
	protected ArrayList<String> m_tagList;
	
	protected ArrayList<StoryNode> m_scenesSeen;
	
	
	public StoryState(
			@ElementMap(required=false, inline=true, entry="value", key="id", attribute=true) HashMap<String, Integer> elementValues,
			@ElementMap(required=false, inline=true, entry="desire", key="id", attribute=true) HashMap<String, Float> elementDesires,
			@ElementList(required=false, inline=true) ArrayList<String> tagList)
	{
		m_elementValues = elementValues;
		m_elementDesires = elementDesires;
		m_tagList = tagList;
		m_scenesSeen = new ArrayList<StoryNode>();
	}
	
	
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
			System.err.println("StoryState has no element with id " + id);
			return -1;
		}
	}
	
	public boolean taggedWithElement(String id)
	{
		return m_tagList.contains(id);
	}
	
	///////////////////////////////////////////////////////////////
	
	public void addNodeToScenesSeen(StoryNode n)
	{
		m_scenesSeen.add(n);
	}
	
	public boolean seenScene(String sceneID)
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