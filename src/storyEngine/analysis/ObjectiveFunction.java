package storyEngine.analysis;

import storyEngine.Story;

public class ObjectiveFunction
{
	public static float objectiveFunctionForElementAtNode(Story story, String elementID, int nodeIndex)
	{
		float ideal = FitnessFunctions.idealFitnessForElementAtNode(story, elementID, nodeIndex);
		float actual = FitnessFunctions.actualFitnessForElementAtNode(story, elementID, nodeIndex); 
		
		return (float)Math.pow(ideal-actual, 2);
	}
	
	public static float objectiveFunctionForAllElementsAtNode(Story story, int nodeIndex)
	{
		float result = 0;
		
		for (String id : story.getElementCollection().getDesireValueIDs())
		{
			result += objectiveFunctionForElementAtNode(story, id, nodeIndex);
		}
		
		return result;
	}
	
	public static float objectiveFunctionForStory(Story story)
	{
		float result = 0;
		
		final int NUM_NODES = story.getScenesSeen().size(); 
		for (int nodeIndex=0; nodeIndex < NUM_NODES; nodeIndex++)
		{
			result += objectiveFunctionForAllElementsAtNode(story, nodeIndex);
		}
		
		return result;
	}
}
