package storyEngine.analysis;

import storyEngine.Story;

public class ObjectiveFunction
{	
	protected Story m_story;
	protected FitnessFunctions m_fitness;
	
	float m_lastResult;
	
	public ObjectiveFunction(Story story)
	{
		m_story = story;
		m_fitness = new FitnessFunctions(story);
		m_lastResult = 0;
	}
	
	private float objectiveFunctionForElementAtNode(String elementID, int nodeIndex)
	{
		float ideal = m_fitness.idealFitnessForElementAtNode(elementID, nodeIndex);
		float actual = m_fitness.actualFitnessForElementAtNode(elementID, nodeIndex); 
		
		return (float)Math.pow(ideal-actual, 2);
	}
	
	private float objectiveFunctionForAllElementsAtNode(int nodeIndex)
	{
		float result = 0;
		
		for (String id : m_story.getElementCollection().getDesireValueIDs())
		{

			result += objectiveFunctionForElementAtNode(id, nodeIndex);
		}
		
		return result;
	}
	
	public float objectiveFunctionWithNewLastNode()
	{
		int lastIndex = m_story.getScenesSeen().size()-1;
		if (lastIndex >= 0)
			m_lastResult += objectiveFunctionForAllElementsAtNode(lastIndex);
		return m_lastResult;
	}
	
	public float objectiveFunctionForStory()
	{
		float m_lastResult = 0;
		
		final int NUM_NODES = m_story.getScenesSeen().size(); 
		for (int nodeIndex=0; nodeIndex < NUM_NODES; nodeIndex++)
		{
			m_lastResult += objectiveFunctionForAllElementsAtNode(nodeIndex);
		}
		 
		return m_lastResult;
	}
}
