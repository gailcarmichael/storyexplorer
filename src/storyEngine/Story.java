package storyEngine;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.StoryNode;

@Root
public class Story
{
	protected StoryElementCollection m_elementCol;
	
	@Attribute(name="numTopScenesForUser")
	protected int m_numTopScenesForUser;
	
	@ElementList(name="storyNodes")
	protected ArrayList<StoryNode> m_nodes;
	
	@Element(name="startingNode", required=false)
	protected StoryNode m_startingNode;	
	
	@Element(name="initialStoryState")
	protected StoryState m_storyState;
	
	protected NodePrioritizer m_nodePrioritizer;
	
	
	public Story(
			@Attribute(name="numTopScenesForUser") int numTopScenesForUser,
			@ElementList(name="storyNodes") ArrayList<StoryNode> nodes,
			@Element(name="startingNode", required=false) StoryNode startingNode,
			@Element(name="initialStoryState") StoryState initStoryState)
	{
		m_numTopScenesForUser = numTopScenesForUser;
		m_nodes = nodes;
		m_startingNode = startingNode;
		m_storyState = initStoryState;
		
		m_nodePrioritizer = new NodePrioritizer(this);
	}
	
	
	public ArrayList<StoryNode> getNodes() { return m_nodes; }
	
	public int getNumTopScenesForUser() { return m_numTopScenesForUser; }
	
	public StoryElementCollection getElementCollection() { return m_elementCol; }
	public void setElementCollection(StoryElementCollection c) { m_elementCol = c; }
	
	public float getDesireForElement(String id)
	{
		return m_storyState.getValueForElement(id);
	}
	
	
	/////////////////////////////////////////////////////////////
	
	
	public boolean isValid(StoryElementCollection elements)
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
	
	
	/////////////////////////////////////////////////////////////
	
	
	/* The story is driven forward with this class.  A node will be presented
	 * to a user, who will make choices if necessary; then the node's outcome
	 * will be applied to the story state. After a node is consumed, the top
	 * priority nodes will be recalculated, ready to be presented to the user.
	 */
	
	
	ArrayList<StoryNode> getAvailableNodes()
	{
		ArrayList<StoryNode> availableNodes = new ArrayList<StoryNode>();
		
		for (StoryNode node : m_nodes)
		{
			if (!node.isConsumed() && node.passesPrerequisite(m_storyState))
			{
				availableNodes.add(node);
			}
		}
		
		return availableNodes;
	}
	
	
	public ArrayList<StoryNode> getCurrentSceneOptions()
	{
		ArrayList<StoryNode> currentSceneOptions = new ArrayList<StoryNode>();
		
		if (m_elementCol == null)
		{
			System.err.println("Story could not return current scene options because the story"
					+ " element collection is not available.");
		}
		else
		{
			m_nodePrioritizer.recalculateTopNodes(m_elementCol);
			currentSceneOptions.addAll(m_nodePrioritizer.getTopNodes());
		}
		
		return currentSceneOptions;
	}
}
