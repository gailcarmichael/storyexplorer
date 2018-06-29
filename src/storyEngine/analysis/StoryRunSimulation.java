package storyEngine.analysis;

import java.util.ArrayList;
import java.util.Random;

import storyEngine.Story;
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
		RandomChoice
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
		return story.finishConsumingNode();
	}
	
	
	public static void walkthrough(Story story, ChoiceType choiceType)
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
		
		while (!lastNode && !possibleScenes.isEmpty())
		{
			int randomIndex = RANDOM.nextInt(possibleScenes.size());
			
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
			}
						
			lastNode = consumeNode(story, nextNode);
			
			possibleScenes = story.getCurrentSceneOptions();
		}
	}
}
