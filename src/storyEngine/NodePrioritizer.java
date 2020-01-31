package storyEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import storyEngine.analysis.ObjectiveFunction;
import storyEngine.analysis.ObjectiveFunctionForMemoryModel;
import storyEngine.storyNodes.StoryNode;

public class NodePrioritizer
{
	// The current list of the available nodes with the highest priority
	protected ArrayList<StoryNode> m_topNodes;
	
	// A reference to the Story to give access to all nodes
	protected Story m_story;
	
	
	NodePrioritizer(Story story)
	{
		m_story = story;
		m_topNodes = new ArrayList<StoryNode>();
	}
	
	
	ArrayList<StoryNode> getTopNodes() { return m_topNodes; }
	
	
	///////////////////////////////////////////////////////////////////////////////
	
	
	void recalculateTopNodes(boolean satellitesOnly)
	{
		ArrayList<NodeIDAndScore> scores = new ArrayList<NodeIDAndScore>();
		int numNodesToGet = 0;
		
		if (m_story.getPrioritizationType() == PrioritizationType.strawManRandom ||
			m_story.getPrioritizationType() == PrioritizationType.sumOfCategoryMaximums ||
			m_story.getPrioritizationType() == PrioritizationType.eventBased)
		{
			numNodesToGet = doLocalCalculation(satellitesOnly, scores);
		}
		
		else if (m_story.getPrioritizationType() == PrioritizationType.bestObjectiveFunction)
		{
			numNodesToGet = bestObjectiveFunction(satellitesOnly, scores);
		}
		
		m_topNodes.clear();
		boolean aTopNodeIsKernel = false;
		for (int i=0; i < scores.size(); i++)
		{
			m_topNodes.add(scores.get(i).m_node);
			if (m_topNodes.get(i).isKernel())
			{
				aTopNodeIsKernel = true;
			}
		}
		
		if (!satellitesOnly && !aTopNodeIsKernel)
		{
			// Search for the top scoring kernel, and if there is
			// one, switch it with the lowest scoring node from 
			// the top nodes list
			
			for (int i=numNodesToGet; i < scores.size(); i++)
			{
				StoryNode n = scores.get(i).m_node;
				if (n.isKernel())
				{
					m_topNodes.remove(m_topNodes.size()-1);
					m_topNodes.add(n);
					break;
				}
			}
		}
		
		if (!m_topNodes.isEmpty())
		{
			m_topNodes =  new ArrayList<StoryNode>(m_topNodes.subList(0, Math.min(numNodesToGet, m_topNodes.size())));
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////
	
	
	// Helper class that facilitates easy sorting of nodes according to score
	private class NodeIDAndScore implements Comparable<NodeIDAndScore>
	{
		protected StoryNode m_node;
		protected float m_score;
		
		public NodeIDAndScore(StoryNode node, float score)
		{
			m_node = node;
			m_score = score;
		}
		
		public boolean equals(Object o)
		{
			boolean result = false;
			if (o instanceof NodeIDAndScore)
			{
				result = m_node.getID().equals(((NodeIDAndScore)o).m_node.getID());
			}
			return result;
		}

		@Override
		public int compareTo(NodeIDAndScore other)
		{
			if (m_score > other.m_score) return 1;
			else if (m_score < other.m_score) return -1;
			else return 0;
		}	
	}
	
	protected int doLocalCalculation(boolean satellitesOnly, ArrayList<NodeIDAndScore> scores)
	{
		// - Ask the story for all available nodes
		// - Calculate the priority score for each node
		// - Sort the nodes according to priority
		// - Save references to the top nodes, ensuring there is at least one
		//   kernel in the top nodes if a kernel is available
		
		final ArrayList<StoryNode> availableNodes = m_story.getAvailableNodes(); 
		
		for (StoryNode node : availableNodes)
		{
			float nodeScore = node.calculatePriorityScore(m_story, m_story.getElementCollection());
			if (!satellitesOnly || node.isSatellite())
			{
				scores.add(new NodeIDAndScore(node, nodeScore));
			}
		}
		
		Collections.sort(scores); // sort according to score
		Collections.reverse(scores); // highest scoring first
		
		return Math.min(availableNodes.size(), m_story.getNumTopScenesForUser());
	}
	
	
	///////////////////////////////////////////////////////////////////////////////

	protected int bestObjectiveFunction(boolean satellitesOnly, ArrayList<NodeIDAndScore> scores)
	{
		final int NUM_TOP_SCENES = m_story.getNumTopScenesForUser();
		final int NUM_SCENES_TO_SAMPLE = 3;
		final int NUM_STORIES_TO_TRY = 10000;
		
		
		if (m_story.getAvailableNodes().isEmpty()) return 0;
		
		
		// Use this sorted map to find the best scoring stories. If two stories have the same score,
		// it doesn't matter which one we use, so it's ok to use the score as a key.
		TreeMap<Float, String> storyScores = new TreeMap<Float, String>();
		
		
		for (int i=0; i < NUM_STORIES_TO_TRY; i++)
		{
			// Get a random sample of nodes, add them to a copy of the story, and get the objective function
			// result for that story
	
			Story storyCopy = (Story)m_story.clone();
			
			ArrayList<StoryNode> nodesToSample = new ArrayList<StoryNode>(storyCopy.getAvailableNodes());
			Random random = new Random();
			
			int numSampledNodesConsumed=0;
			while (numSampledNodesConsumed < NUM_SCENES_TO_SAMPLE && !nodesToSample.isEmpty())
			{
				int index = random.nextInt(nodesToSample.size());
				StoryNode node = nodesToSample.get(index); 
				
				if (satellitesOnly && node.isKernel()) continue;
				
				storyCopy.startConsumingNode(node);
				storyCopy.applyOutcomeAndAdjustQuantifiableValues();
				storyCopy.finishConsumingNode();
				
				nodesToSample.remove(index);
				
				numSampledNodesConsumed++;
			}
			
			ObjectiveFunction objFunction = new ObjectiveFunctionForMemoryModel(storyCopy);
			float objFunctionResult = objFunction.objectiveFunctionForStory();
			
			//System.out.println(objFunctionResult);
			

			ArrayList<StoryNode> scenes = storyCopy.getScenesSeen();
			StoryNode nodeToOffer = scenes.get(scenes.size() - numSampledNodesConsumed);
			
			storyScores.put(objFunctionResult, nodeToOffer.getID());
		}
		
		for (Entry<Float, String> entry : storyScores.entrySet())
		{	
			NodeIDAndScore newIDAndScore = new NodeIDAndScore(m_story.nodeWithID(entry.getValue()), entry.getKey());
			
			if (scores.contains(newIDAndScore)) // avoid duplicate node IDs
			{
				continue;
			}
			
			scores.add(newIDAndScore);
		}
				
		return Math.min(NUM_TOP_SCENES, scores.size());
	}
}

