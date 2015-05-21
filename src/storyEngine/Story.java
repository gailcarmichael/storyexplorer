package storyEngine;

import java.util.ArrayList;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.StoryNode;
import storyEngine.storyState.StoryState;

@Root
public class Story
{
	@ElementList(name="storyNodes")
	ArrayList<StoryNode> m_nodes;
	
	@Element(name="initialStoryState")
	StoryState m_storyState;
	
	
	public Story(
			@ElementList(name="storyNodes") ArrayList<StoryNode> nodes,
			@Element(name="initialStoryState") StoryState initStoryState)
	{
		m_nodes = nodes;
		m_storyState = initStoryState;
	}
	
	
	public ArrayList<StoryNode> getNodes() { return m_nodes; }
	
	
	public boolean storyIsValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		// Check for validity in everything without stopping to ensure
		// all information is printed out
		
		for (StoryNode node : m_nodes)
		{
			if (!node.isValid(elements))
			{
				isValid = false;
			}
		}
		
		if (!m_storyState.isValid(elements))
		{
			isValid = false;
		}
		
		return isValid;
	}
}
