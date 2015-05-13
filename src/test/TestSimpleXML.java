package test;

import java.io.File;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import storyEngine.Story;
import storyEngine.storyNodes.Choice;
import storyEngine.storyNodes.FunctionalDescription;
import storyEngine.storyNodes.Outcome;
import storyEngine.storyNodes.Prerequisite;
import storyEngine.storyNodes.StoryNode;
import storyEngine.storyNodes.elements.ElementType;
import storyEngine.storyNodes.elements.StoryElement;
import storyEngine.storyNodes.elements.StoryElementCollection;

public class TestSimpleXML
{
	public static void main(String[] args)
	{
		Serializer serializer = new Persister();
		
		//////////////////////////////////////////////////////////////////
		
		StoryElementCollection elements = new StoryElementCollection();
		
		elements.add(new StoryElement("heroTheme", "themes", "heroism", ElementType.Quantifiable));
		elements.add(new StoryElement("friendshipTheme", "themes", "friendship", ElementType.Quantifiable));

		elements.add(new StoryElement("dawgCharacter", "characters", "dawg", ElementType.Quantifiable));
		elements.add(new StoryElement("kittyCharacter", "characters", "kitty", ElementType.Quantifiable));

		elements.add(new StoryElement("tension", "tension", "tension", ElementType.QuantifiableStoryStateOnly));

		elements.add(new StoryElement("openWaterTerrain", "terrains", "openWater", ElementType.Taggable));
		elements.add(new StoryElement("mountainTerrain", "terrains", "mountains", ElementType.Taggable));

		elements.add(new StoryElement("sunnyWeather", "weather", "sunny", ElementType.Taggable));
		elements.add(new StoryElement("rainyWeather", "weather", "rainy", ElementType.Taggable));

		
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
		
		Outcome o = new Outcome("outcome text");
		o.add(new Outcome.QuantifiableModifier("dawgCharacter", false, -1));
		o.add(new Outcome.TaggableModifier("sunnyWeather", Outcome.TagAction.add));
		
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
				"FirstScene", Prerequisite.SceneRestriction.sceneNotSeen));
		
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		nodes.add(new StoryNode("node1", "node 1 teaser", "node 1 event", funcDesc, prereq, choices));
		nodes.add(new StoryNode("node2", "node 2 teaser", "node 3 event", funcDesc, choices));
		nodes.add(new StoryNode("node3", "node 2 teaser", "node 3 event", funcDesc, choices));
		
		Story story = new Story(nodes);
		
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
