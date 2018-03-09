package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import processing.core.PApplet;
import storyEngine.PrioritizationType;
import storyEngine.Story;
import storyEngine.StoryState;
import storyEngine.analysis.ElementSpacingVisualizer;
import storyEngine.analysis.ObjectiveFunction;
import storyEngine.analysis.StoryRunSimulation;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.FunctionalDescription;
import storyEngine.storyNodes.NodeType;
import storyEngine.storyNodes.StoryNode;


// The goal with this class is to test whether story elements
// like theme and character are spaced out as desired.  A set
// of story scenes is generated such that there are many scenes
// tagged with each theme, character, and setting element.

public class TestElementSpacing extends PApplet
{
	private enum TestType
	{
		VisualizeOneRun,
		MonteCarloSimulation
	}
	
	private static final long serialVersionUID = -893063477402437731L;
	
	private static final int NUM_EACH_CATEGORY = 5; // how many different themes, characters, settings each
	private static final int NUM_NODES_PER_ELEMENT = 15; // how many nodes for each individual theme, etc
	private static final int MAX_PROMINENCE_VALUE = 3; // a random value between 1 and this number will be assigned
	private static final int NUM_TOP_CHOICES = 3; // how many of the top nodes are offered to players
	
	private static final boolean TEST_RANDOM_STORY = false; // true if story is randomly generated, false if story is read from existing file
	
	private static final PrioritizationType PRIORITIZATION_TYPE = PrioritizationType.eventBased; // just for visualizing one story
	
	private static final TestType TEST_TYPE = TestType.VisualizeOneRun;
	
	private static final boolean TEST_COMBO_ELEMENTS = true; // whether nodes should have multiple element tags
	
	private static final boolean TEST_RANDOM_CHOICES = true; // true when using random node choice rather than top
	
	private static ElementSpacingVisualizer VISUALIZER;
	
	private static Random RANDOM = null;
	
	
	////////////////////////////////////////////////////////
	
	private static StoryState getInitialState()
	{
		// Initial story state: set all values to 1(?)
		
		HashMap<String, Float> desires = new HashMap<String, Float>();
		for (int i=1; i <= NUM_EACH_CATEGORY; i++)
		{
			desires.put("theme" + i, 10.0f);
			desires.put("character" + i, 10.0f);
			desires.put("setting" + i, 10.0f);
		}
		
		return new StoryState(null, desires, null); 
	}
	
	////////////////////////////////////////////////////////
	
	private static void runThroughStory(Story story, boolean random)
	{
		story.reset();
		
		String keyword;
		
		if (random)
		{
			StoryRunSimulation.randomWalkthrough(story);
			keyword = "random";
		}
		else
		{
			StoryRunSimulation.topSceneWalkthrough(story);
			keyword = "topScene";
		}
		
		int numScenesSeen = story.getScenesSeen().size();
		
		try
		{
			String filename = "./testData/testElementSpacing/" + keyword + "Results.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			writer.write("-----\nTop Scene Walkthrough Results\n-----\n");
			writer.write("(" + story.getNodes().size() + " scenes total, " + numScenesSeen + " seen)\n");
			
			for (StoryNode scene : story.getScenesSeen())
			{
				writer.write("\t" + scene.getID() + "\t");
				if (scene.getID().length() < 12) writer.write("\t");
				writer.write(scene.getTeaserText() + "\n");
			}
			
			writer.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////
	
	
	public void setup()
	{
		VISUALIZER.setup(this);
	}
	

	public void draw()
	{
		VISUALIZER.draw(this);
	}
	
	
	public void keyPressed()
	{
		save("./testData/testElementSpacing/spacing" +
				(TEST_RANDOM_CHOICES ? "-random" : "-topScene") +
				"-" + PRIORITIZATION_TYPE +
				".png");
	}
	
	
	////////////////////////////////////////////////////////
	
	
	private static StoryNode createStoryNode(
			StoryElementCollection elementCollection, String category, int catNum, int withinCatNum)
	{
		int prominence = RANDOM.nextInt(MAX_PROMINENCE_VALUE) + 1;
		
		FunctionalDescription funcDesc = new FunctionalDescription();
		funcDesc.add(elementCollection, category + catNum, prominence);
		
		if (TEST_COMBO_ELEMENTS)
		{
			while (RANDOM.nextBoolean())
			{
				addRandomElementToFuncDesc(elementCollection, funcDesc);
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
	
	
	private static void addRandomElementToFuncDesc(StoryElementCollection col, FunctionalDescription funcDesc)
	{
		// Pick a random element from the collection, and then give it a random prominence between 1 and 3
		
		String id = col.getIDs().get(RANDOM.nextInt(col.getIDs().size()));
		funcDesc.add(col, id, RANDOM.nextInt(MAX_PROMINENCE_VALUE) + 1);
	}
	
	private static void doMonteCarloForPrioritizationType(Story story, PrioritizationType type)
	{
		story.setPrioritizationType(type);
		
		ArrayList<Float> objectiveFunctionResults = new ArrayList<Float>();
		
		for (int i=0; i < 1000; i++)
		{
			runThroughStory(story, TEST_RANDOM_CHOICES);
			objectiveFunctionResults.add(new ObjectiveFunction(story).objectiveFunctionForStory());
		}
		
		float total = 0;
		for (float f : objectiveFunctionResults) { total += f; }
		
		System.out.println("Objective function average for " + story.getPrioritizationType() + ": " + total / objectiveFunctionResults.size());
	}
	
	
	public static void main(String[] args)
	{		
		RANDOM = new Random();
		
		
		StoryElementCollection elementCollection = null;
		Story story = null;
		
		
		if (TEST_RANDOM_STORY)
		{
			////
			// Step 1: Generate a story collection with generic story elements
			
			elementCollection = new StoryElementCollection();
			
			for (int i=1; i <= NUM_EACH_CATEGORY; i++)
			{
				elementCollection.add(
						new StoryElement("theme" + i, "themes", "theme" + i, ElementType.quantifiable));
				
				elementCollection.add(
						new StoryElement("character" + i, "characters", "character" + i, ElementType.quantifiable));
			
				elementCollection.add(
						new StoryElement("setting" + i, "settings", "setting" + i, ElementType.quantifiable));
				
			}
			
			// Step 2: Create a story with generic scenes and validate it
			
			StoryState initialState = getInitialState();		
			
			// Create a set of scenes with different combinations of elements
			// in their functional descriptions; stick with satellites only
			// for this test
			
			ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
			
			// Nodes tagged with just one element
			
			for (int i=1; i <= NUM_EACH_CATEGORY; i++)
			{
				for (int j=0; j < NUM_NODES_PER_ELEMENT; j++)
				{
					nodes.add(createStoryNode(elementCollection, "theme", i, j));
					nodes.add(createStoryNode(elementCollection, "character", i, j));
					nodes.add(createStoryNode(elementCollection, "setting", i, j));
				}
			}
			
			
			// Construct the story object and test validity
			
			story = new Story(NUM_TOP_CHOICES, PRIORITIZATION_TYPE, nodes, null /* <- no start node */, initialState);
			story.setElementCollection(elementCollection);
			System.out.println("Test story is valid: " + story.isValid(elementCollection));
			
			
			// Export to XML for easy double checking of story generation
			
			try
			{
				Serializer serializer = new Persister();
				File result = new File("./testData/testElementSpacing/testSpacing.xml");
				serializer.write(story, result);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			story = Story.loadStoryFromFile("./data/ggjGameStory.xml", "./data/ggjGameStoryElements.xml");
		}
		
		
		////
			
					
		
		////
		// Part 2: Do a quick test by running through the story,
		// making random choices from available scenes or choosing the
		// top scene each time
		
		
		if (TEST_TYPE == TestType.MonteCarloSimulation)
		{
			
			doMonteCarloForPrioritizationType(story, PrioritizationType.sumOfCategoryMaximums);
			doMonteCarloForPrioritizationType(story, PrioritizationType.physicsForcesAnalogy);
			doMonteCarloForPrioritizationType(story, PrioritizationType.eventBased);
			doMonteCarloForPrioritizationType(story, PrioritizationType.bestObjectiveFunction); // <- too slow
		}
		else if (TEST_TYPE == TestType.VisualizeOneRun)
		{
			runThroughStory(story, TEST_RANDOM_CHOICES);
			System.out.println("Objective function result: " + new ObjectiveFunction(story).objectiveFunctionForStory());
		}
			
		
		////
		// Step 4: Visualize the spacing of elements
		
		if (TEST_TYPE == TestType.VisualizeOneRun)
		{
			TestElementSpacing.VISUALIZER = new ElementSpacingVisualizer(story);
			PApplet.main(TestElementSpacing.class.getCanonicalName());
		}
	}
}
