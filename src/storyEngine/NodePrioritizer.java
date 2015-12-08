package storyEngine;

import java.util.ArrayList;
import java.util.Collections;

import storyEngine.storyElements.StoryElementCollection;
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
	
	
	// Helper class that facilitates easy sorting of nodes according to score
	private class NodeIDAndScore implements Comparable<NodeIDAndScore>
	{
		StoryNode m_node;
		float m_score;
		PrioritizationType m_prioritizationType;
		
		public NodeIDAndScore(StoryNode node, float score, PrioritizationType pType)
		{
			m_node = node;
			m_score = score;
			m_prioritizationType = pType;
		}

		@Override
		public int compareTo(NodeIDAndScore other)
		{
			if (m_prioritizationType == PrioritizationType.sumOfCategoryMaximums ||
				m_prioritizationType == PrioritizationType.physicsAnalogy)
			{
				if (m_score > other.m_score) return 1;
				else if (m_score < other.m_score) return -1;
				else return 0;
			}
			else
			{
				System.err.println("Can't compare node priorities because prioritization type is invalid.");
				return 0;
			}
		}	
	}
	
	
	void recalculateTopNodes(StoryElementCollection elementCol)
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
			//System.out.println("\nNode with ID " + node.getID());
			float nodeScore = node.calculatePriorityScore(m_story, elementCol);
			scores.add(new NodeIDAndScore(node, nodeScore, m_story.getPrioritizationType()));
		}
		
		//System.out.println("\n----------------------------------------------\n");
		
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
}

