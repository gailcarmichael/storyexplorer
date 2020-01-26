package storyEngine.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import storyEngine.MemoryFunction;
import storyEngine.Story;
import storyEngine.storyElements.StoryElementWeightCurve;
import storyEngine.storyNodes.StoryNode;

public class ObjectiveFunctionForMemoryModel extends ObjectiveFunction
{
	private static final float DESIRED_THRESHOLD = 3;
	
	public ObjectiveFunctionForMemoryModel(Story story)
	{
		super(story);
	}

	public float objectiveFunctionForStory()
	{
		HashMap<String, ArrayList<Float>> memoryDesiredThreshDiff = new HashMap<String, ArrayList<Float>>();

		ArrayList<StoryNode> scenesSeen = m_story.getScenesSeen();
		ArrayList<String> elementIDs = m_story.getElementCollection().getMemoryValueIDs();
		
		///
		// Step 1: Collect data on  |min(0, M_i(t)-D_i(t))|  values
		for (int sceneNum=0; sceneNum < scenesSeen.size(); sceneNum++)
		{
			for (String elementID : elementIDs)
			{
				updateMemoryDesiredThreshDiff(elementID, sceneNum, memoryDesiredThreshDiff);
			}
		}
		
		///
		// Step 2: Compute Z_i(t) for each element
		HashMap<String,Float> Z_i = get_z_i_values(memoryDesiredThreshDiff);
		
		///
		// Step 3: Sum up W_i*Z_i
		float sum = 0;
		
		//for (Float value : Z_i.values())
		for (String elementID : Z_i.keySet())
		{
			
			sum += Z_i.get(elementID);
		}
		
		return sum;
	}
	
	
	
	private float updateMemoryDesiredThreshDiff(
			String elementID, int t, HashMap<String, ArrayList<Float>> diffsSoFar)
	{
		ArrayList<Float> resultListSoFar = diffsSoFar.get(elementID);
		if (resultListSoFar == null)
		{
			if (t != 0) System.err.println("updateMemoryDesiredThreshDiff: " + elementID + " doesn't have any results when it should");
			diffsSoFar.put(elementID, resultListSoFar = new ArrayList<Float>());
		}
		
		MemoryFunction memoryFunction = m_story.getMemoryFunctionForElement(elementID);
		
		StoryElementWeightCurve weightCurve = m_story.getElementCollection().getWeightCurve(elementID);
		float elementWeight = weightCurve.getValueAt(t);
		
		float newResult = Math.abs(Math.min(0, memoryFunction.getValueAt(t) - (elementWeight*DESIRED_THRESHOLD)));
		resultListSoFar.add(newResult);
		
		return newResult;
	}
	
	
	
	private HashMap<String,Float> get_z_i_values(HashMap<String, ArrayList<Float>> memoryDesiredThreshDiff)
	{
		HashMap<String,Float> Z_i = new HashMap<String,Float>();
		for (String elementID : m_story.getElementCollection().getMemoryValueIDs())
		{
			float integral = 0;
			ArrayList<Float> memoryDesiredThreshDiffs = memoryDesiredThreshDiff.get(elementID);
			if (memoryDesiredThreshDiffs != null)
			{
				for (Float diff : memoryDesiredThreshDiffs)
				{
					integral += diff;
				}
			}
			Z_i.put(elementID, integral);
		}
		return Z_i;
	}
}
