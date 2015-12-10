package storyEngine;

import java.util.ArrayList;
import java.util.HashMap;

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
	
	@Attribute(name="prioritizationType", required=false)
	protected PrioritizationType m_prioritizationType;
	
	@ElementList(name="storyNodes")
	protected ArrayList<StoryNode> m_nodes;
	
	@Element(name="startingNode", required=false)
	protected StoryNode m_startingNode;	
	
	@Element(name="initialStoryState")
	protected StoryState m_storyState;
	
	// Keep a reference to the original
	protected StoryState m_initialStoryState;
	
	// Stuff used for managing story progression
	protected NodePrioritizer m_nodePrioritizer;
	protected StoryNode m_nodeBeingConsumed;
	
	// Information that will be looked up often
	protected HashMap<String, Integer> m_numNodesWithElement;
	protected HashMap<String, Float> m_sumProminencesForNodesWithElement;
	protected float m_totalAllProminences;
	
	
	public Story(
			@Attribute(name="numTopScenesForUser") int numTopScenesForUser,
			@Attribute(name="prioritizationType", required=false) PrioritizationType prioritizationType,
			@ElementList(name="storyNodes") ArrayList<StoryNode> nodes,
			@Element(name="startingNode", required=false) StoryNode startingNode,
			@Element(name="initialStoryState") StoryState initStoryState)
	{
		m_numTopScenesForUser = numTopScenesForUser;
		
		m_prioritizationType = prioritizationType;
		if (m_prioritizationType == null) m_prioritizationType = PrioritizationType.physicsForcesAnalogy;
				
		m_nodes = nodes;
		m_startingNode = startingNode;
		
		m_initialStoryState = initStoryState;
		m_storyState = m_initialStoryState.clone();
		
		m_nodePrioritizer = new NodePrioritizer(this);
		
		m_nodeBeingConsumed = m_startingNode; // could be null
		
		calculateNumNodesWithElement();
		calculateSumProminencesWithElementAndTotal();
	}
	
	
	public ArrayList<StoryNode> getNodes() { return m_nodes; }
	public StoryNode getStartingNode() { return m_startingNode; }
	
	public int getNumTopScenesForUser() { return m_numTopScenesForUser; }
	
	public PrioritizationType getPrioritizationType() { return m_prioritizationType; }
	public void setPrioritizationType(PrioritizationType pType) { m_prioritizationType = pType; }
	
	public StoryElementCollection getElementCollection() { return m_elementCol; }
	public void setElementCollection(StoryElementCollection c) { m_elementCol = c; }
	
	public float getDesireForElement(String id)
	{
		return m_storyState.getValueForElement(id);
	}
	
	public float getLargestDesireValue()
	{
		return m_storyState.getLargestDesireValue();
	}
	
	public int getNumNodesWithElement(String id)
	{
		int num = 0;
		
		if (m_numNodesWithElement.containsKey(id))
		{
			num = m_numNodesWithElement.get(id);
		}
		
		return num;
	}
	
	public float getSumOfProminenceValuesForElement(String id)
	{
		float sum = 0;
		
		if (m_sumProminencesForNodesWithElement.containsKey(id))
		{
			sum = m_sumProminencesForNodesWithElement.get(id);
		}
		
		return sum;
	}
	
	public float getTotalProminenceValues()
	{
		return m_totalAllProminences;
	}
	
	public float getProminenceForMostRecentNodeWithElement(String elementID)
	{
		return m_storyState.getProminenceForMostRecentNodeWithElement(elementID);
	}
	
	public ArrayList<StoryNode> getScenesSeen()
	{
		return m_storyState.getScenesSeen();
	}
	
	
	public void printStoryState() { System.out.println(m_storyState); }
	
	
	/////////////////////////////////////////////////////////////
	
	
	protected void calculateNumNodesWithElement()
	{
		m_numNodesWithElement = new HashMap<String, Integer>();
		for (StoryNode node : m_nodes)
		{
			for (String id : node.getElementIDs())
			{
				int num = 1;
				if (m_numNodesWithElement.containsKey(id))
				{
					num += m_numNodesWithElement.get(id);
				}
				m_numNodesWithElement.put(id, num);
			}
		}
	}
	
	protected void calculateSumProminencesWithElementAndTotal()
	{
		m_sumProminencesForNodesWithElement = new HashMap<String, Float>();
		m_totalAllProminences = 0;
		
		for (StoryNode node : m_nodes)
		{
			for (String id : node.getElementIDs())
			{
				float sum = node.getProminenceValueForElement(id);
				m_totalAllProminences += sum;
				
				if (m_sumProminencesForNodesWithElement.containsKey(id))
				{
					sum += m_sumProminencesForNodesWithElement.get(id);
				}
				m_sumProminencesForNodesWithElement.put(id, sum);
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////
	
	
	public Story clone()
	{
		Story newStory = new Story(
				m_numTopScenesForUser,
				m_prioritizationType,
				m_nodes,
				m_startingNode,
				m_storyState); // <- story state gets cloned in constructor
		
		newStory.setElementCollection(m_elementCol);
		
		return newStory;
	}
	
	
	public Story cloneAndReset()
	{
		Story newStory = new Story(
				m_numTopScenesForUser,
				m_prioritizationType,
				m_nodes,
				m_startingNode,
				m_initialStoryState);
		
		newStory.setElementCollection(m_elementCol);
		
		newStory.reset();
		return newStory;
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
			m_nodePrioritizer.recalculateTopNodes();
			currentSceneOptions.addAll(m_nodePrioritizer.getTopNodes());
		}
		
		return currentSceneOptions;
	}
	
	
	// Reset all values so the story can be re-run
	public void reset()
	{
		m_nodeBeingConsumed = null;
		m_nodePrioritizer = new NodePrioritizer(this);
		
		m_storyState = m_initialStoryState.clone();
		
		for (StoryNode n : m_nodes)
		{
			n.resetNode();
		}
	}
}
