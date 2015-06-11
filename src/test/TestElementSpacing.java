package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import storyEngine.Story;
import storyEngine.StoryState;
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

public class TestElementSpacing
{

	private static final int NUM_EACH_ELEMENT = 4; // how many different themes, characters, settings each
	private static final boolean TEST_COMBO_ELEMENTS = false; // whether nodes should have multiple element tags
	
	////////////////////////////////////////////////////////
	
	private static StoryState getInitialState()
	{
		// Initial story state: set all values to 1
		
		HashMap<String, Float> desires = new HashMap<String, Float>();
		for (int i=1; i <= NUM_EACH_ELEMENT; i++)
		{
			desires.put("theme" + i, 1.0f);
			desires.put("character" + i, 1.0f);
			desires.put("setting" + i, 1.0f);
		}
		
		return new StoryState(null, desires, null); 
	}
	
	////////////////////////////////////////////////////////
	
	private static void runThroughStory(Story story, boolean random)
	{
		int numScenesSeen;
		String keyword;
		
		if (random)
		{
			numScenesSeen = StoryRunSimulation.randomWalkthrough(story);
			keyword = "random";
		}
		else
		{
			numScenesSeen = StoryRunSimulation.topSceneWalkthrough(story);
			keyword = "topScene";
		}
		
		try
		{
			String filename = ".\\testData\\testElementSpacing\\" + keyword + "Results.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			
			writer.write("-----\nTop Scene Walkthrough Results\n-----\n");
			writer.write("(" + story.getNodes().size() + " scenes total, " + numScenesSeen + " total)\n");
			
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
	
	public static void main(String[] args)
	{		
		////
		// Step 1: Generate a story collection with generic story elements
		
		StoryElementCollection elementCollection = new StoryElementCollection();
		
		for (int i=1; i <= NUM_EACH_ELEMENT; i++)
		{
			elementCollection.add(
					new StoryElement("theme" + i, "themes", "theme" + i, ElementType.quantifiable));
			
			elementCollection.add(
					new StoryElement("character" + i, "characters", "character" + i, ElementType.quantifiable));
		
			elementCollection.add(
					new StoryElement("setting" + i, "settings", "setting" + i, ElementType.quantifiable));
		}
		
		
		
		////
		// Step 2: Create a story with generic scenes and validate it
		
		StoryState initialState = getInitialState();		
		
		// Create a set of scenes with different combinations of elements
		// in their functional descriptions; stick with satellites only
		// for this test
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		
		// Nodes tagged with just one element
		
		for (int i=1; i <= NUM_EACH_ELEMENT; i++)
		{
			for (int j=0; j < 6; j++)
			{
				FunctionalDescription funcDesc = new FunctionalDescription();
				funcDesc.add(elementCollection, "theme" + i, j/2 + 1);
				
				nodes.add(new StoryNode(
						/* id */ "theme" + i + "-" + (j+1),
						/* type */ NodeType.satellite, 
						/* teaser text */ "theme" + i + " (" + (j/2 + 1) + ")", 
						/* event text */ "-",
						/* functional desc */ funcDesc,
						/* prereq */ null,
						/* choices */ null));
				
				funcDesc = new FunctionalDescription();
				funcDesc.add(elementCollection, "character" + i, j/2 + 1);
				
				nodes.add(new StoryNode(
						/* id */ "character" + i + "-" + (j+1),
						/* type */ NodeType.satellite, 
						/* teaser text */ "character" + i + " (" + (j/2 + 1) + ")", 
						/* event text */ "-",
						/* functional desc */ funcDesc,
						/* prereq */ null,
						/* choices */ null));
				
				funcDesc = new FunctionalDescription();
				funcDesc.add(elementCollection, "setting" + i, j/2 + 1);
				
				nodes.add(new StoryNode(
						/* id */ "setting" + i + "-" + (j+1),
						/* type */ NodeType.satellite, 
						/* teaser text */ "setting" + i + " (" + (j/2 + 1) + ")", 
						/* event text */ "-",
						/* functional desc */ funcDesc,
						/* prereq */ null,
						/* choices */ null));
			}
		}
		
		
		if (TEST_COMBO_ELEMENTS)
		{
			
		}
		
		
		// Construct the story object and test validity
		
		Story story = new Story(4, nodes, null /* <- no start node */, initialState);
		story.setElementCollection(elementCollection);
		System.out.println("Test story is valid: " + story.isValid(elementCollection));
		
		
		// Export to XML for easy double checking of story generation
		
		try
		{
			Serializer serializer = new Persister();
			File result = new File(".\\testData\\testElementSpacing\\testSpacing.xml");
			serializer.write(story, result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
					
		
		////
		// Step 3: Do a quick test by running through the story twice,
		// making random choices from available scenes or choosing the
		// top scene each time
		
		runThroughStory(story, false);
		story.reset(getInitialState());
		runThroughStory(story, true);
		
		
		////
		// Step 4: Run through the story many times and report statistics
		// on how far apart elements are
	}
}
