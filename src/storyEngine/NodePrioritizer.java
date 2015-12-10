package storyEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import storyEngine.analysis.ObjectiveFunction;
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
	
	
	void recalculateTopNodes()
	{
		if (m_story.getPrioritizationType() == PrioritizationType.sumOfCategoryMaximums ||
			m_story.getPrioritizationType() == PrioritizationType.physicsForcesAnalogy  ||
			m_story.getPrioritizationType() == PrioritizationType.eventBased)
		{
			doLocalCalculation();
		}
		
		if (m_story.getPrioritizationType() == PrioritizationType.bestObjectiveFunction)
		{
			randomSubsetBestObjectiveFunction();
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

		@Override
		public int compareTo(NodeIDAndScore other)
		{
			if (m_score > other.m_score) return 1;
			else if (m_score < other.m_score) return -1;
			else return 0;
		}	
	}
	
	protected void doLocalCalculation()
	{
		// - Ask the story for all available nodes
		// - Calculate the priority score for each node
		// - Sort the nodes according to priority
		// - Save references to the top nodes, ensuring there is at least one
		//   kernel in the top nodes if a kernel is available
		
		final ArrayList<StoryNode> availableNodes = m_story.getAvailableNodes();
		
		final int numNodesToGet = Math.min(availableNodes.size(), m_story.getNumTopScenesForUser()); 
		
		ArrayList<NodeIDAndScore> scores = new ArrayList<NodeIDAndScore>();
		for (StoryNode node : availableNodes)
		{
			float nodeScore = node.calculatePriorityScore(m_story, m_story.getElementCollection());
			scores.add(new NodeIDAndScore(node, nodeScore));
		}
		
		Collections.sort(scores); // sort according to score
		Collections.reverse(scores); // highest scoring first
		
		m_topNodes.clear();
		boolean aTopNodeIsKernel = false;
		for (int i=0; i < numNodesToGet; i++)
		{
			m_topNodes.add(scores.get(i).m_node);
			if (m_topNodes.get(i).isKernel())
			{
				aTopNodeIsKernel = true;
			}
		}
		
		if (!aTopNodeIsKernel)
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
	}
	
	
	///////////////////////////////////////////////////////////////////////////////

	// This needs more testing/development - running it is currently very slow (I'm not even sure if it ever finishes)
	
	protected void randomSubsetBestObjectiveFunction()
	{
		// - Ask the story for all available nodes
		// - Try a variety of random configurations of the next X nodes, calculating the 'score' of each
		//   (the score mechanism being determined by the prioritization type)
		// - Pick the configuration with the best score
		// - Save references to the top nodes, ensuring there is at least one
		//   kernel in the top nodes if a kernel is available
		
		final ArrayList<StoryNode> availableNodes = m_story.getAvailableNodes();
		final int numNodesToGet = Math.min(availableNodes.size(), m_story.getNumTopScenesForUser());
		final Random random = new Random();
		
		Story bestStory = null;
		float bestScore = -1;
		
		int originalNumScenesSeen = m_story.getScenesSeen().size();
		
		for (int i=0; i < 100; i++)
		{
			// Create a temporary story where we can put a bunch of random nodes and then test
			// whether the result has a better objective score
			
			ArrayList<StoryNode> copyOfAvailableNodes = new ArrayList<StoryNode>(availableNodes);
			Collections.shuffle(copyOfAvailableNodes, random);
			
			Story copyOfStory = m_story.clone();
			copyOfStory.getScenesSeen().addAll(copyOfAvailableNodes.subList(0, numNodesToGet));
			
			float scoreForCopyOfStory = ObjectiveFunction.objectiveFunctionForStory(copyOfStory);
			
			if (bestStory == null || scoreForCopyOfStory < bestScore)
			{
				bestStory = copyOfStory;
				bestScore = scoreForCopyOfStory;
			}
		}
		
		m_topNodes.addAll(bestStory.getScenesSeen().subList(
				originalNumScenesSeen,
				bestStory.getScenesSeen().size()));
	}
}

