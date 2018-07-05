package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import processing.core.PApplet;
import storyEngine.PrioritizationType;
import storyEngine.Story;
import storyEngine.analysis.ElementSpacingAnalysis;
import storyEngine.analysis.ElementSpacingVisualizer;
import storyEngine.analysis.ObjectiveFunction;
import storyEngine.analysis.RandomStory;
import storyEngine.analysis.StoryRunSimulation;
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
		MonteCarloSimulationOneStory,
		MonteCarloSimulationManyStories
	}
	
	private static final long serialVersionUID = -893063477402437731L;
	
	private static final int NUM_EACH_CATEGORY = 5; // how many different themes, characters, settings each
	private static final int NUM_NODES_PER_ELEMENT = 15; // how many nodes for each individual theme, etc
	private static final int MAX_PROMINENCE_VALUE = 5; // a random value between 1 and this number will be assigned

	private static final int NUM_TOP_CHOICES = 3; // how many of the top nodes are offered to players
	
	private static final boolean TEST_RANDOM_STORY = true; // true if story is randomly generated, false if story is read from existing file
	
	private static final PrioritizationType PRIORITIZATION_TYPE = PrioritizationType.sumOfCategoryMaximums; // just for visualizing one story
	
	private static final ObjectiveFunction.Type OBJECTIVE_FUNCTION_TYPE = ObjectiveFunction.Type.MemoryModel;
	
	private static final TestType TEST_TYPE = TestType.VisualizeOneRun;
	
	private static final boolean TEST_COMBO_ELEMENTS = true; // whether nodes should have multiple element tags
	
	private static final StoryRunSimulation.ChoiceType CHOICE_TYPE = StoryRunSimulation.ChoiceType.TopChoice;
	
	private static final int PERCENT_OF_NODES_TO_CONSUME = 30;
	
	private static ElementSpacingVisualizer VISUALIZER;
	private static boolean SHOW_MULTI_ELEMENT_NODES_IN_VISUALIZER = false;
	
	////////////////////////////////////////////////////////
	
	private static void runThroughStory(Story story, StoryRunSimulation.ChoiceType choiceType)
	{
		story.reset();
		
		StoryRunSimulation.walkthrough(story, choiceType, PERCENT_OF_NODES_TO_CONSUME);
		
		int numScenesSeen = story.getScenesSeen().size();
		
		try
		{
			String filename = "./testData/testElementSpacing/" + choiceType + "Results.txt";
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
		if (key == 'h')
		{
			ElementSpacingAnalysis spacingAnalysis = new ElementSpacingAnalysis(VISUALIZER.getStory());
			spacingAnalysis.printElementSpacingHistogram(true);
		}
		else
		{
			save("./testData/testElementSpacing/spacing" +
					CHOICE_TYPE +
					"-" + PRIORITIZATION_TYPE +
					".png");
		}
	}
	
	private static void doMonteCarloForPrioritizationTypesRandomStories(Story story, boolean randomizeStoryEachTime)
	{
		HashMap<PrioritizationType, ArrayList<Float>> objectiveFunctionResults = new HashMap<PrioritizationType, ArrayList<Float>>();
		
		for (int i=0; i < 1000; i++)
		{
			if (randomizeStoryEachTime)
			{
				story = 
					RandomStory.getRandomStory(
						NUM_EACH_CATEGORY, NUM_NODES_PER_ELEMENT,
						10, 
						MAX_PROMINENCE_VALUE, TEST_COMBO_ELEMENTS, 
						NUM_TOP_CHOICES, PRIORITIZATION_TYPE);
			}

			updateObjectiveFunctionResultsForMonteCarlo(story, objectiveFunctionResults, PrioritizationType.sumOfCategoryMaximums);	
			updateObjectiveFunctionResultsForMonteCarlo(story, objectiveFunctionResults, PrioritizationType.physicsForcesAnalogy);	
			updateObjectiveFunctionResultsForMonteCarlo(story, objectiveFunctionResults, PrioritizationType.eventBased);			
		}
		
		for (PrioritizationType prioritizationType : objectiveFunctionResults.keySet())
		{
			float total = 0;
			for (Float newResult : objectiveFunctionResults.get(prioritizationType))
			{
				total += newResult;
			}
			
			System.out.println("Objective function average for " + prioritizationType + ": " + (total / objectiveFunctionResults.get(prioritizationType).size()));
		}
		
	}
	
	private static void updateObjectiveFunctionResultsForMonteCarlo(
			Story story, 
			HashMap<PrioritizationType, ArrayList<Float>> objectiveFunctionResults, 
			PrioritizationType prioritizationType)
	{
		story.setPrioritizationType(prioritizationType);
		runThroughStory(story, CHOICE_TYPE);
		
		ArrayList<Float> results = objectiveFunctionResults.get(prioritizationType);
		if (results == null)
		{
			objectiveFunctionResults.put(prioritizationType, results = new ArrayList<Float>());
		}
		results.add(ObjectiveFunction.forType(OBJECTIVE_FUNCTION_TYPE, story).objectiveFunctionForStory());
	}
	
	
	public static void main(String[] args)
	{
		Story story = null;
		
		
		if (TEST_RANDOM_STORY)
		{
			story = RandomStory.getRandomStory(
					NUM_EACH_CATEGORY, NUM_NODES_PER_ELEMENT,
					10, 
					MAX_PROMINENCE_VALUE, TEST_COMBO_ELEMENTS, 
					NUM_TOP_CHOICES, PRIORITIZATION_TYPE);
			
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
			story = Story.loadStoryFromFile("./data/accidentalProphetStory.xml", "./data/accidentalProphetStoryElements.xml");
		}
		
		
		////
					
		
		////
		// Do a quick test by running through the story,
		// making random choices from available scenes or choosing the
		// top scene each time
		
		
		if (TEST_TYPE == TestType.MonteCarloSimulationOneStory)
		{
			doMonteCarloForPrioritizationTypesRandomStories(story, false);
		}
		else if (TEST_TYPE == TestType.MonteCarloSimulationManyStories)
		{
			doMonteCarloForPrioritizationTypesRandomStories(null, true);
		}
		else if (TEST_TYPE == TestType.VisualizeOneRun)
		{
			runThroughStory(story, CHOICE_TYPE);
			System.out.println("Objective function result: " + ObjectiveFunction.forType(OBJECTIVE_FUNCTION_TYPE, story).objectiveFunctionForStory());
		}
			
		
		////
		// Step 4: Visualize the spacing of elements
		
		if (TEST_TYPE == TestType.VisualizeOneRun)
		{
			TestElementSpacing.VISUALIZER = new ElementSpacingVisualizer(story, SHOW_MULTI_ELEMENT_NODES_IN_VISUALIZER);
			PApplet.main(TestElementSpacing.class.getCanonicalName());
		}
	}
}
