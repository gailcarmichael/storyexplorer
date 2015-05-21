package test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import storyEngine.Story;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.Choice;
import storyEngine.storyNodes.FunctionalDescription;
import storyEngine.storyNodes.NodeType;
import storyEngine.storyNodes.Outcome;
import storyEngine.storyNodes.Prerequisite;
import storyEngine.storyNodes.StoryNode;
import storyEngine.storyState.StoryState;

public class TestSimpleXML
{
	public static void main(String[] args)
	{
		Serializer serializer = new Persister();
		
		//////////////////////////////////////////////////////////////////
		
		StoryElementCollection elements = new StoryElementCollection();
		
		elements.add(new StoryElement("heroTheme", "themes", "heroism", ElementType.quantifiable));
		elements.add(new StoryElement("friendshipTheme", "themes", "friendship", ElementType.quantifiable));

		elements.add(new StoryElement("dawgCharacter", "characters", "dawg", ElementType.quantifiable));
		elements.add(new StoryElement("kittyCharacter", "characters", "kitty", ElementType.quantifiable));

		elements.add(new StoryElement("tension", "tension", "tension", ElementType.quantifiableStoryStateOnly));

		elements.add(new StoryElement("openWaterTerrain", "terrains", "openWater", ElementType.taggable));
		elements.add(new StoryElement("mountainTerrain", "terrains", "mountains", ElementType.taggable));

		elements.add(new StoryElement("sunnyWeather", "weather", "sunny", ElementType.taggable));
		elements.add(new StoryElement("rainyWeather", "weather", "rainy", ElementType.taggable));

		
		//////////////////////////////////////////////////////////////////
		
		try
		{
			File result = new File(".\\testOutput\\testStoryElements.xml");
			
			serializer.write(elements, result);
			
			StoryElementCollection readStoryElements = 
					serializer.read(StoryElementCollection.class, result);
			
			readStoryElements.printStoryElements();
			
			File result2 = new File(".\\testOutput\\testStoryElements2.xml");
			
			serializer.write(readStoryElements, result2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//////////////////////////////////////////////////////////////////
		
		HashMap<String, Integer> values = new HashMap<String, Integer>();
		values.put("tension", 3);
		
		HashMap<String, Integer> desires = new HashMap<String, Integer>();
		desires.put("heroTheme", 1);
		desires.put("friendshipTheme", 1);
		desires.put("dawgCharacter", 1);
		desires.put("kittyCharacter", 1);
		
		StoryState initStoryState = new StoryState(values, desires);
		
		//////////////////////////////////////////////////////////////////
		
		Outcome o = new Outcome("outcome text");
		o.add(new Outcome.QuantifiableModifier("dawgCharacter", false, -1));
		o.add(new Outcome.TagModifier("sunnyWeather", Outcome.TagAction.add));
		
		ArrayList<Choice> choices = new ArrayList<Choice>();
		Choice c1 = new Choice("choice text");
		c1.setOutcome(o);
		choices.add(c1);
		
		FunctionalDescription funcDesc = new FunctionalDescription();
		funcDesc.add(elements, "dawgCharacter", 3);
		funcDesc.add(elements, "sunnyWeather");
		
		Prerequisite prereq = new Prerequisite();
		
		prereq.add(new Prerequisite.QuantifiableElementRequirement(
				"kittyCharacter",  Prerequisite.BinaryRestriction.greaterThan, 4));
		
		prereq.add(new Prerequisite.TagRequirement(
				"rainyWeather", Prerequisite.ListRestriction.contains));
		
		prereq.add(new Prerequisite.SceneRequirement(
				"FirstScene", Prerequisite.SceneRestriction.notSeen));
		
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		nodes.add(new StoryNode("node1", NodeType.kernel, "node 1 teaser", "node 1 event", funcDesc, prereq, choices));
		nodes.add(new StoryNode("node2", NodeType.satellite, "node 2 teaser", "node 3 event", funcDesc, choices));
		nodes.add(new StoryNode("node3", NodeType.satellite, "node 2 teaser", "node 3 event", funcDesc, choices));
		
		Story story = new Story(nodes, initStoryState);
		
		System.out.println("Test story is valid: " + story.storyIsValid(elements));
		
		//////////////////////////////////////////////////////////////////
		
		try
		{
			File result = new File(".\\testOutput\\testSimple.xml");
			
			serializer.write(story, result);
			
			Story readStory = serializer.read(Story.class, result);
			
			for (StoryNode n : readStory.getNodes())
			{
				System.out.println(n.getID());
			}
			
			File result2 = new File(".\\testOutput\\testSimple2.xml");
			
			serializer.write(readStory, result2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		//////////////////////////////////////////////////////////////////
	}
}
