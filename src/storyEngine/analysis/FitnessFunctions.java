package storyEngine.analysis;

import java.util.ArrayList;

import storyEngine.Story;
import storyEngine.storyNodes.StoryNode;

public class FitnessFunctions
{
	// Calculates the fitness of the story seen so far up to nodeIndex, for one particular story element only
	public static float actualFitnessForElementAtNode(Story story, String elementID, int nodeIndex)
	{
		float fitness = 0;
		
		ArrayList<StoryNode> scenesSeen = story.getScenesSeen(); 
		
		for (int i = 0; i <= Math.min(nodeIndex, scenesSeen.size()-1); i++)
		{
			StoryNode node = scenesSeen.get(i);
			fitness += node.getProminenceValueForElement(elementID);
		}
		
		return fitness;
	}
	
	// Returns the ideal fitness of an element at nodeIndex
	public static float idealFitnessForElementAtNode(Story story, String elementID, int nodeIndex)
	{
		return k_for_element(story, elementID, nodeIndex) * (nodeIndex+1);
	}
	
	// Defines the coefficient k_i for a story element to be used in the ideal fitness calculation; currently,
	// this is static, but should eventually be dynamic (e.g. director's curves)
	private static float k_for_element(Story story, String elementID, int nodeIndex)
	{
		// How many nodes feature this element? Divide that by the total number of nodes to get k
		// This is currently incorrect when nodes can have more than one element associated with them;
		// a node needs to be counted according to how many elements are featured
		//return story.getNumNodesWithElement(elementID) / (float)story.getNodes().size();
		
		// Divide the sum of prominence values for the element by the sum of all prominence values
		return story.getSumOfProminenceValuesForElement(elementID) / (float)story.getTotalProminenceValues();
	}
}
