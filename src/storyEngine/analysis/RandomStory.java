package storyEngine.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import storyEngine.PrioritizationType;
import storyEngine.Story;
import storyEngine.StoryState;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.FunctionalDescription;
import storyEngine.storyNodes.NodeType;
import storyEngine.storyNodes.StoryNode;

public class RandomStory
{
	private static Random RANDOM = new Random();
	
	////////////////////////////////////////////////////////
	
	private static StoryState getInitialState(int numEachCategory, float initialValue)
	{	
		HashMap<String, Float> desires = new HashMap<String, Float>();
		for (int i=1; i <= numEachCategory; i++)
		{
			desires.put("theme" + i, initialValue);
			desires.put("character" + i, initialValue);
			desires.put("setting" + i, initialValue);
		}
		
		return new StoryState(null, desires, null); 
	}
	
	
	////////////////////////////////////////////////////////
	
	
	private static StoryNode createStoryNode(
			StoryElementCollection elementCollection,
			String category, int catNum, int withinCatNum,
			int maxProminenceValue,
			boolean testComboElements)
	{	
		int prominence = RANDOM.nextInt(maxProminenceValue) + 1;
		
		FunctionalDescription funcDesc = new FunctionalDescription();
		funcDesc.add(elementCollection, category + catNum, prominence);
		
		if (testComboElements)
		{
			while (RANDOM.nextBoolean())
			{
				addRandomElementToFuncDesc(elementCollection, funcDesc, maxProminenceValue);
			}
		}
		
		return new StoryNode(
				/* id */ category + catNum + "-" + (withinCatNum+1),
				/* type */ NodeType.satellite, 
				/* teaser text */ category + catNum + " (" + prominence + ")", 
				/* event text */ "-",
				/* functional desc */ funcDesc,
				/* prereq */ null,
				/* choices */ null);
	}
	
	
	////////////////////////////////////////////////////////
	
	
	private static void addRandomElementToFuncDesc(
			StoryElementCollection col, FunctionalDescription funcDesc, int maxProminenceValue)
	{
		// Pick a random element from the collection, and then give it a random prominence between 1 and 3
		
		String id = col.getIDs().get(RANDOM.nextInt(col.getIDs().size()));
		funcDesc.add(col, id, RANDOM.nextInt(maxProminenceValue) + 1);
	}
	
	
	////////////////////////////////////////////////////////
	
	
	public static Story getRandomStory(
			int numElementsEachCategory, int numNodesPerElement, 
			int initialValueForDesires,
			int maxValueForProminence, boolean allowComboProminence,
			int numTopChoices, PrioritizationType prioritizationType)
	{
		Story story = null;
		
		////
		// Step 1: Generate a story collection with generic story elements
		
		StoryElementCollection elementCollection = new StoryElementCollection();
		
		for (int i=1; i <= numElementsEachCategory; i++)
		{
			elementCollection.add(
					new StoryElement("theme" + i, "themes", "theme" + i, ElementType.quantifiable));
			
			elementCollection.add(
					new StoryElement("character" + i, "characters", "character" + i, ElementType.quantifiable));
		
			elementCollection.add(
					new StoryElement("setting" + i, "settings", "setting" + i, ElementType.quantifiable));
			
		}
		
		// Step 2: Create a story with generic scenes and validate it
		
		StoryState initialState = RandomStory.getInitialState(numElementsEachCategory, initialValueForDesires);		
		
		// Create a set of scenes with different combinations of elements
		// in their functional descriptions; stick with satellites only
		// for this test
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		
		// Nodes tagged with just one element
		
		for (int i=1; i <= numElementsEachCategory; i++)
		{
			for (int j=0; j < numNodesPerElement; j++)
			{
				nodes.add(RandomStory.createStoryNode(elementCollection, "theme", i, j, maxValueForProminence, allowComboProminence));
				nodes.add(RandomStory.createStoryNode(elementCollection, "character", i, j, maxValueForProminence, allowComboProminence));
				nodes.add(RandomStory.createStoryNode(elementCollection, "setting", i, j, maxValueForProminence, allowComboProminence));
			}
		}
		
		
		// Construct the story object and test validity
		
		story = new Story(numTopChoices, prioritizationType, nodes, null /* <- no start node */, initialState);
		story.setElementCollection(elementCollection);
		
		if (!story.isValid(elementCollection)) System.err.println("Randomly generated story is not valid.");
		
		return story;
	}
}
