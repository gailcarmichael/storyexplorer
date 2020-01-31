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
import storyEngine.storyElements.StoryElementWeightCurve.PiecewiseConstantWeightCurve;
import storyEngine.storyNodes.FunctionalDescription;
import storyEngine.storyNodes.NodeType;
import storyEngine.storyNodes.StoryNode;

public class RandomStory
{
	private static Random RANDOM = new Random();
	
	public static enum WeightCurveStrategy
	{
		Constant,
		HalfHighHalfLowThenSwap
	}
	
	
	////////////////////////////////////////////////////////
	
	private static StoryState getInitialState(StoryElementCollection elementCollection, float initialValue)
	{	
		HashMap<String, Float> desires = new HashMap<String, Float>();
		
		for (String id : elementCollection.getDesireValueIDs())
		{
			desires.put(id, initialValue);
		}
		
		return new StoryState(null, desires, null); 
	}
	
	
	////////////////////////////////////////////////////////
	
	private static StoryNode createStoryNode(
			StoryElementCollection elementCollection,
			ArrayList<String> storyElementIDs,
			String nodeID, String nodeTeaserText,
			int maxProminenceValue)
	{	
		
		FunctionalDescription funcDesc = new FunctionalDescription();
		for (String elementID : storyElementIDs)
		{
			funcDesc.add(elementCollection, elementID, RANDOM.nextInt(maxProminenceValue) + 1);
		}
		
		return new StoryNode(
				/* id */  nodeID,
				/* type */ NodeType.satellite, 
				/* teaser text */ nodeTeaserText, 
				/* event text */ "-",
				/* functional desc */ funcDesc,
				/* prereq */ null,
				/* choices */ null);
	}
	
	
	////////////////////////////////////////////////////////
	
	private static void addElementToCollection(
			StoryElementCollection elementCollection, String id, String category, 
			WeightCurveStrategy curveStrategy, int numNodes)
	{
		elementCollection.add(
				new StoryElement(id, "themes", id, ElementType.quantifiable));
		
		if (curveStrategy == WeightCurveStrategy.Constant)
		{
			elementCollection.addWeightCurve(id, new PiecewiseConstantWeightCurve(1));
		}
		else if (curveStrategy == WeightCurveStrategy.HalfHighHalfLowThenSwap)
		{
			PiecewiseConstantWeightCurve highFirst = new PiecewiseConstantWeightCurve(1);
			highFirst.addNewValue(0, 1.5f);
			highFirst.addNewValue(numNodes/3, 0.5f);
			
			PiecewiseConstantWeightCurve lowFirst = new PiecewiseConstantWeightCurve(1);
			lowFirst.addNewValue(0, 0.5f);
			lowFirst.addNewValue(numNodes/3, 1.5f);
			
			elementCollection.addWeightCurve(id, RANDOM.nextBoolean() ? highFirst : lowFirst);
		}
	}
	
	private static StoryElementCollection generateStoryElementCollection(
			int numNodesTotal,
			int numSettings, int numThemes, int numCharacters,
			WeightCurveStrategy weightCurveStrategy)
	{
		StoryElementCollection elementCollection = new StoryElementCollection();
		
		for (int i=1; i <= numSettings; i++)
		{
			addElementToCollection(elementCollection, "setting" + i, "settings", weightCurveStrategy, numNodesTotal);
		}
		
		for (int i=1; i <= numThemes; i++)
		{
			addElementToCollection(elementCollection, "theme" + i, "themes", weightCurveStrategy, numNodesTotal);
		}
		
		for (int i=1; i <= numCharacters; i++)
		{
			addElementToCollection(elementCollection, "character" + i, "characters", weightCurveStrategy, numNodesTotal);
		}
		
		return elementCollection;
	}
	
	
	////////////////////////////////////////////////////////
	
	public static Story getRandomStory(
			HashMap<String, Integer> numElementsPerCategory,
			int initialValueForDesires,
			WeightCurveStrategy weightCurveStrategy,
			int numNodesTotal,
			float minNodesEachElementUsedIn,
			float maxNodesEachElementUsedIn,
			boolean chooseMaxRandomly,
			int maxElementsPerNode,
			int maxValueForProminence,
			int numTopChoices, 
			PrioritizationType prioritizationType)
	{
		Story story = null;
		
		
		////
		// Step 1: Generate a story element collection with generic story elements along with their weight curves
		
		StoryElementCollection elementCollection = 
				generateStoryElementCollection(
						numNodesTotal,
						numElementsPerCategory.getOrDefault("settings",  0),
						numElementsPerCategory.getOrDefault("themes",  0),
						numElementsPerCategory.getOrDefault("characters",  0),
						weightCurveStrategy);
		
		
		////
		// Step 2: Generate an initial story state with the given initial desire value
		
		StoryState initialState = getInitialState(elementCollection, initialValueForDesires);		
		
		
		////
		// Step 3: Create a set of scenes with different combinations of elements
		// in their functional descriptions; stick with satellites only for this test
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		
		ArrayList<ArrayList<String>> nodeElementLists = new ArrayList<ArrayList<String>>();
		for (int nodeNum=0; nodeNum < numNodesTotal; nodeNum++)
		{
			nodeElementLists.add(new ArrayList<String>());
		}
		
		int minNodes = (int)(numNodesTotal * minNodesEachElementUsedIn);
		int maxNodes = (int)(numNodesTotal * maxNodesEachElementUsedIn);
		
		for (String elementID : elementCollection.getIDs())
		{
			int numNodesElementAppearsIn = 0;
			
			int maxNodesThisElement = maxNodes;			
			if (chooseMaxRandomly)
			{
				maxNodesThisElement = minNodes + RANDOM.nextInt(maxNodes - minNodes + 1);
			}
			
			while (numNodesElementAppearsIn < maxNodesThisElement)
			{
				// Find a node with nothing in its element list if possible...
				boolean foundEmpty = false;
				for (ArrayList<String> elementList : nodeElementLists)
				{
					if (elementList.isEmpty())
					{
						elementList.add(elementID);
						numNodesElementAppearsIn++;
						foundEmpty = true;
						break;
					}
				}
				
				// ...otherwise, just pick randomly, ensuring we don't exceed the max number
				// of elements per node
				if (!foundEmpty)
				{
					int nodeIndex = RANDOM.nextInt(numNodesTotal);
					if (!nodeElementLists.get(nodeIndex).contains(elementID) &&
						nodeElementLists.get(nodeIndex).size() < maxElementsPerNode)
					{
						nodeElementLists.get(nodeIndex).add(elementID);
						numNodesElementAppearsIn++;
					}
				}
			}
		}
		
		// Create the nodes using their element lists
		for (int nodeNum=0; nodeNum < numNodesTotal; nodeNum++)
		{
			ArrayList<String> elementIDs = nodeElementLists.get(nodeNum);
			nodes.add(createStoryNode(elementCollection, elementIDs,
					"node" + nodeNum, String.join("-", elementIDs),
					maxValueForProminence));
		}
		
		
		////
		// Step 4: Construct the story object and test validity
		
		story = new Story(numTopChoices, prioritizationType, nodes, null /* <- no start node */, initialState);
		story.setElementCollection(elementCollection);
		
		if (!story.isValid(elementCollection)) System.err.println("Randomly generated story is not valid.");
		
		return story;
	}
}
