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
	
	// Stuff used for managing story progression
	protected NodePrioritizer m_nodePrioritizer;
	protected StoryNode m_nodeBeingConsumed;
	
	
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
		
		m_nodeBeingConsumed = m_startingNode; // could be null
	}
	
	
	public ArrayList<StoryNode> getNodes() { return m_nodes; }
	public StoryNode getStartingNode() { return m_startingNode; }
	
	public int getNumTopScenesForUser() { return m_numTopScenesForUser; }
	
	public StoryElementCollection getElementCollection() { return m_elementCol; }
	public void setElementCollection(StoryElementCollection c) { m_elementCol = c; }
	
	public float getDesireForElement(String id)
	{
		return m_storyState.getValueForElement(id);
	}
	
	public ArrayList<StoryNode> getScenesSeen()
	{
		return m_storyState.getScenesSeen();
	}
	
	
	public void printStoryState() { System.out.println(m_storyState); }
	
	
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
	
	
	////
	// The story is driven forward with this class.  A node will be presented
	// to a user, who will make choices if necessary; then the node's outcome
	// will be applied to the story state. After a node is consumed, the top
	// priority nodes will be recalculated, ready to be presented to the user.
	////
	
	
	// Could be null at the beginning of the story, in which case the caller
	// should get and present current scene options
	public StoryNode getNodeBeingConsumed() { return m_nodeBeingConsumed; }
	
	
	// Select the next node to consume
	public void startConsumingNode(StoryNode node) { m_nodeBeingConsumed = node; } 
	
	
	// Call this after a node has been presented to a user to apply its
	// outcome to the story state
	public void applyOutcomeAndAdjustDesires()
	{
		if (m_nodeBeingConsumed != null)
		{
			m_nodeBeingConsumed.applyOutcomeForSelectedChoice(m_storyState);
			m_nodeBeingConsumed.resetRelevantDesireValuesInStoryState(m_storyState);
			m_storyState.increaseDesireValues();
		}
		else
		{
			System.err.println("Could not apply outcome or adjust desires because " +
								"node being consumed is null.");
		}
	}
	
	
	// Finalize consumption after all outcomes are applied, and return
	// whether the node is the last one in the story
	public boolean finishConsumingNode()
	{
		boolean lastNode = false;
		
		if (m_nodeBeingConsumed != null)
		{
			lastNode = m_nodeBeingConsumed.isLastNode();
		}
		
		m_nodeBeingConsumed = null;
		
		return lastNode;
	}
	
	
	// Helper method to get all nodes that could potentially be presented to 
	// the user, used by node prioritizer
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
	
	
	// Returns an up-to-date list of the top available story nodes that
	// can be presented to a user
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
	
	
	// Reset all values so the story can be re-run
	public void reset(StoryState initialState)
	{
		m_nodeBeingConsumed = null;
		m_nodePrioritizer = new NodePrioritizer(this);
		m_storyState = initialState;
		
		for (StoryNode n : m_nodes)
		{
			n.resetNode();
		}
	}
}
