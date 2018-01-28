package storyEngine;

import java.util.ArrayList;
import java.util.Collections;

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
	
	
	void recalculateTopNodes(boolean satellitesOnly)
	{
		if (m_story.getPrioritizationType() == PrioritizationType.sumOfCategoryMaximums ||
			m_story.getPrioritizationType() == PrioritizationType.physicsForcesAnalogy  ||
			m_story.getPrioritizationType() == PrioritizationType.eventBased)
		{
			doLocalCalculation(satellitesOnly);
		}
		
		if (m_story.getPrioritizationType() == PrioritizationType.bestObjectiveFunction)
		{
			bestObjectiveFunction(satellitesOnly);
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
	
	protected void doLocalCalculation(boolean satellitesOnly)
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
			if (!satellitesOnly || node.isSatellite())
			{
				scores.add(new NodeIDAndScore(node, nodeScore));
			}
		}
		
		Collections.sort(scores); // sort according to score
		Collections.reverse(scores); // highest scoring first
		
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
	}
	
	
	///////////////////////////////////////////////////////////////////////////////

	protected void bestObjectiveFunction(boolean satellitesOnly)
	{
		final ArrayList<StoryNode> availableNodes = m_story.getAvailableNodes();
		final int numTopScenes = m_story.getNumTopScenesForUser();
		
		ObjectiveFunction objFunction = new ObjectiveFunction(m_story);
		objFunction.objectiveFunctionForStory(); // saves last result
		
		ArrayList<Float> bestScores = new ArrayList<Float>();
		m_topNodes.clear();
		
		for (StoryNode nextNode : availableNodes)
		{
			// Make a copy of the story and add a random new node from what's available
			// to see how it affects the score...the nodes with the best results will
			// be returned as the top choices
			
			Story copyOfStory = (Story)(m_story.clone());
			copyOfStory.getScenesSeen().add(nextNode);
			float scoreForCopyOfStory = objFunction.objectiveFunctionWithNewLastNode();
			
			if (m_topNodes.size() < numTopScenes)
			{
				// Just add blindly to the list until all slots are filled
				m_topNodes.add(nextNode);
				bestScores.add(scoreForCopyOfStory);
			}
			else
			{
				float maxScore = Collections.max(bestScores);
				int maxIndex = bestScores.indexOf(maxScore);
				
				// Replace a node if we have a score better than the max
				if (scoreForCopyOfStory < maxScore)
				{
					m_topNodes.set(maxIndex, nextNode);
					bestScores.set(maxIndex, scoreForCopyOfStory);
				}
			}
		}
	}
}

