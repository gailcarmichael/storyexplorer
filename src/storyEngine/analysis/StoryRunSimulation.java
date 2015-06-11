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
		
		story.applyOutcomeAndAdjustDesires();
		return story.finishConsumingNode();
	}
	
	
	public static int randomWalkthrough(Story story)
	{
		return walkthrough(story, true);
	}
	
	
	public static int topSceneWalkthrough(Story story)
	{
		return walkthrough(story, false);
	}
	
	
	protected static int walkthrough(Story story, boolean random)
	{
		if (RANDOM == null) RANDOM = new Random();
		
		boolean lastNode = false;
		int numScenesSeen = 0;
		
		// If there's a starting node, go there first
		if (story.getStartingNode() != null)
		{
			lastNode = consumeNode(story, story.getStartingNode());
			numScenesSeen++;
		}

		// Loop through possible nodes until there aren't any
		// left, or until the last node in the story is reached
		
		ArrayList<StoryNode> possibleScenes = story.getCurrentSceneOptions();
		
		while (!lastNode && !possibleScenes.isEmpty())
		{
			StoryNode nextNode = 
					random ?
						possibleScenes.get(RANDOM.nextInt(possibleScenes.size())) :
						possibleScenes.get(0);
			
			lastNode = consumeNode(story, nextNode);
			numScenesSeen++;
			
			possibleScenes = story.getCurrentSceneOptions();
		}
		
		return numScenesSeen;
	}
}
