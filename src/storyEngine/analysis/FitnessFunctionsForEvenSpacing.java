package storyEngine.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import storyEngine.Story;
import storyEngine.storyNodes.StoryNode;

public class FitnessFunctionsForEvenSpacing
{
	Story m_story;
	HashMap<String, Float> m_kValues;
	HashMap<String, Float> m_prevNodeFitnessForElement;
	
	FitnessFunctionsForEvenSpacing(Story story)
	{
		m_story = story;
		m_kValues = new HashMap<String, Float>();
		m_prevNodeFitnessForElement = new HashMap<String, Float>(); 
	}
	
	
	// Calculates the fitness of the story seen so far up to nodeIndex, for one particular story element only
	float actualFitnessForElementAtNode(String elementID, int nodeIndex)
	{

		ArrayList<StoryNode> scenesSeen = m_story.getScenesSeen(); 
		
		float fitness = 0;
		
		if (nodeIndex > 0 && m_prevNodeFitnessForElement.containsKey(elementID))
		{
			fitness += m_prevNodeFitnessForElement.get(elementID);
		}
		
		fitness += scenesSeen.get(nodeIndex).getProminenceValueForElement(elementID);
		
		m_prevNodeFitnessForElement.put(elementID, fitness);
		
		return fitness;
	}
	
	
	// Returns the ideal fitness of an element at nodeIndex
    float idealFitnessForElementAtNode(String elementID, int nodeIndex)
	{
		return k_for_element(elementID) * (nodeIndex+1);
	}
	
    
	// Defines the coefficient k_i for a story element to be used in the ideal fitness calculation; currently,
	// this is static, but should eventually be dynamic (e.g. director's curves)
	private float k_for_element(String elementID)
	{		
		// Check if we've already calculated this value
		if (m_kValues.containsKey(elementID))
		{
			return m_kValues.get(elementID);
		}
		else
		{
			// Divide the sum of prominence values for the element by the sum of all prominence values
			float k_value = m_story.getSumOfProminenceValuesForElement(elementID) / (float)m_story.getTotalProminenceValues();
			m_kValues.put(elementID, k_value);
			return k_value;
		}
	}
}
