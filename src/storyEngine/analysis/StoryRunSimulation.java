package storyEngine.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import storyEngine.Story;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyNodes.StoryNode;

// This class is used to simulate one complete run-through of
// a story and can be used to gather statistics about how
// nodes are arranged.

public class StoryRunSimulation
{
	public static enum ChoiceType
	{
		TopChoice,
		BottomChoice,
		RandomChoice,
		PreferencesListChoice,
	}
	
	protected static Random RANDOM = null;
	
	protected static boolean consumeNode(Story story, StoryNode node)
	{
		story.startConsumingNode(node);
		
		StoryNode consuming = story.getNodeBeingConsumed();
		
		if (consuming.getNumChoices() > 0)
		{
			int choiceIndex = RANDOM.nextInt(consuming.getNumChoices());
			consuming.setSelectedChoice(choiceIndex);
		}
		
		story.applyOutcomeAndAdjustQuantifiableValues();
		boolean success = story.finishConsumingNode();
		
		//System.out.println(ObjectiveFunction.forType(ObjectiveFunction.Type.MemoryModel, story).objectiveFunctionForStory());
		
		return success;
	}
	
	
	public static void walkthrough(Story story, ChoiceType choiceType, int percentNodesToConsume)
	{
		RANDOM = new Random();
		story.reset();
		
		boolean lastNode = false;
		
		// If there's a starting node, go there first
		if (story.getStartingNode() != null)
		{
			lastNode = consumeNode(story, story.getStartingNode());
		}

		// Loop through possible nodes until there aren't any
		// left, or until the last node in the story is reached
		
		ArrayList<StoryNode> possibleScenes = story.getCurrentSceneOptions();
		int stoppingNodeNum = percentNodesToConsume * story.getNodes().size() / 100;
		
		while (!lastNode && !possibleScenes.isEmpty() && story.getScenesSeen().size() < stoppingNodeNum)
		{
			int randomIndex = RANDOM.nextInt(possibleScenes.size());
			
			ArrayList<StoryElement> preferences = story.getElementCollection().getCopyOfElementList();
			Collections.shuffle(preferences);
			
			StoryNode nextNode = null;
			switch (choiceType)
			{
				case TopChoice:
					nextNode = possibleScenes.get(0);
					break;
				case RandomChoice:
					nextNode = possibleScenes.get(randomIndex);
					break;
				case BottomChoice:
					nextNode = possibleScenes.get(possibleScenes.size()-1);
					break;
				case PreferencesListChoice:
					nextNode = nodeWithHighestPreference(possibleScenes, preferences);
					break;
			}
						
			lastNode = consumeNode(story, nextNode);
			
			possibleScenes = story.getCurrentSceneOptions();
		}
	}
	
	private static StoryNode nodeWithHighestPreference(ArrayList<StoryNode> possibleScenes, ArrayList<StoryElement> preferences)
	{
		StoryNode node = null;
		int currentPreferenceIndex = 0;
		
		do
		{
			String preferredElementID = preferences.get(currentPreferenceIndex).getID();
			for (StoryNode candidateNode : possibleScenes)
			{
				if (candidateNode.featuresElement(preferredElementID))
				{
					node = candidateNode;
					break;
				}
			}
			currentPreferenceIndex++;
		} while (node == null && currentPreferenceIndex < preferences.size());
		
		return node;
	}
}
