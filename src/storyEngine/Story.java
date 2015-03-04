package storyEngine;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.storyNodes.StoryNode;

@Root
public class Story
{
	@ElementList(name="storyNodes")
	ArrayList<StoryNode> m_nodes;
	
	
	public Story(
			@ElementList(name="storyNodes") ArrayList<StoryNode> nodes)
	{
		m_nodes = nodes;
	}
	
	
	public ArrayList<StoryNode> getNodes() { return m_nodes; }
}
