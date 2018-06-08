package storyEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

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
	
	protected int m_numKernels;
	protected int m_numKernelsConsumed;
	
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
		
		for (StoryNode node : m_nodes) { if (node.isKernel()) m_numKernels++; }
		m_numKernelsConsumed = 0;
		
		m_startingNode = startingNode;
		
		m_initialStoryState = initStoryState.clone();
		m_storyState = m_initialStoryState.clone();
		
		m_nodePrioritizer = new NodePrioritizer(this);
		
		m_nodeBeingConsumed = m_startingNode; // could be null
		
		calculateNumNodesWithElement();
		calculateSumProminencesWithElementAndTotal();
	}
	
	
	public ArrayList<StoryNode> getNodes() { return m_nodes; }
	public int getNumKernels() { return m_numKernels; }
	public int getNumKernelsConsumed() { return m_numKernelsConsumed; }
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
	
	public float getStoryStateOnlyElementValue(String id)
	{
		return m_storyState.getValueForElement(id);
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
	
	
	public static Story loadStoryFromFile(String storyFilename, String elementCollectionFilename)
	{
		Story story = null;
		StoryElementCollection col = null;
		Serializer serializer = new Persister();
		
		try
		{
			File collectionFile = new File(elementCollectionFilename);
			col = serializer.read(StoryElementCollection.class, collectionFile);
			
			try
			{
				File storyFile = new File(storyFilename);
				story = serializer.read(Story.class, storyFile);
				story.setElementCollection(col);
			}
			catch (Exception e)
			{
				System.err.println("Loading story from " + storyFilename + " failed.\n" + e.getMessage());
			}
		}
		catch (Exception e)
		{
			System.err.println("Loading story element collection from " + elementCollectionFilename + " failed.\n" + e.getMessage());
		}
		
		return story;
	}
	
	
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
				float totalProminence = node.getProminenceValueForElement(id);
				m_totalAllProminences += totalProminence;
				
				if (m_sumProminencesForNodesWithElement.containsKey(id))
				{
					totalProminence += m_sumProminencesForNodesWithElement.get(id);
				}
				m_sumProminencesForNodesWithElement.put(id, totalProminence);
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////
	
	
	public Object clone()
	{
		@SuppressWarnings("unchecked")
		
		Story newStory = new Story(
				m_numTopScenesForUser,
				m_prioritizationType,
				(ArrayList<StoryNode>)m_nodes.clone(),
				m_startingNode,
				m_storyState); // <- story state gets cloned in constructor
		
		newStory.setElementCollection((StoryElementCollection)m_elementCol.clone());
		
		return newStory;
	}
	
	
	public Story cloneAndReset()
	{
		Story newStory = (Story)clone();
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
	public void applyOutcomeAndAdjustQuantifiableValues()
	{
		if (m_nodeBeingConsumed != null)
		{
			m_nodeBeingConsumed.applyOutcomeForSelectedChoice(m_storyState, m_elementCol);
			m_nodeBeingConsumed.resetRelevantDesireValuesInStoryState(m_storyState);
			m_storyState.increaseDesireValues();
			
			m_storyState.adjustMemoryValues(m_nodeBeingConsumed, m_elementCol);			
		}
		else
		{
			System.err.println("Could not apply outcome or adjust quantifiable values because " +
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
			if (m_nodeBeingConsumed.isKernel()) m_numKernelsConsumed++;
		}
		
		m_nodeBeingConsumed = null;
		
		return lastNode;
	}
	
	
	// Helper methods to get all nodes that could potentially be presented to 
	// the user; used by node prioritizer and to get available kernels
	ArrayList<StoryNode> getAvailableNodes() { return getAvailableNodes(false, false); }
	private ArrayList<StoryNode> getAvailableNodes(boolean kernelsOnly, boolean satellitesOnly)
	{
		ArrayList<StoryNode> availableNodes = new ArrayList<StoryNode>();
		
		for (StoryNode node : m_nodes)
		{
			if (!node.isConsumed() && node.passesPrerequisite(m_storyState)
					&& (!kernelsOnly || node.isKernel())
					&& (!satellitesOnly || node.isSatellite()))
			{
				availableNodes.add(node);
			}
		}
		
		return availableNodes;
	}
	
	
	// Returns a collection of available kernel nodes
	public ArrayList<StoryNode> getAvailableKernelNodes()
	{
		return getAvailableNodes(true, false);
	}
	
	// Returns a collection of available satellite nodes
		public ArrayList<StoryNode> getAvailableSatelliteNodes()
		{
			return getAvailableNodes(false, true);
		}
	
	
	////
	// Returns an up-to-date list of the top available story nodes that
	// can be presented to a user...default is to include a kernel
	
	public ArrayList<StoryNode> getCurrentSceneOptions()
	{
		return getCurrentSceneOptions(false);
	}
	
	public ArrayList<StoryNode> getCurrentSceneOptions(boolean satellitesOnly)
	{
		ArrayList<StoryNode> currentSceneOptions = new ArrayList<StoryNode>();
		
		if (m_elementCol == null)
		{
			System.err.println("Story could not return current scene options because the story"
					+ " element collection is not available.");
		}
		else
		{
			m_nodePrioritizer.recalculateTopNodes(satellitesOnly);
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
		
		m_numKernelsConsumed = 0;
	}
}
