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
import storyEngine.storyNodes.elements.ElementProminence;
import storyEngine.storyNodes.elements.StoryElement;
import storyEngine.storyNodes.elements.Terrain;
import storyEngine.storyNodes.elements.Weather;

public class TestSimpleXML
{
	public static void main(String[] args)
	{
		Serializer serializer = new Persister();
		
		Outcome o = new Outcome("outcome text");
		o.add(new Outcome.ElementModifier(StoryElement.Character, "Dawg", false, -1));
		o.add(new Outcome.WeatherModifier(Weather.Cloudy, Outcome.Action.add));
		
		ArrayList<Choice> choices = new ArrayList<Choice>();
		Choice c1 = new Choice("choice text");
		c1.setOutcome(o);
		choices.add(c1);
		
		FunctionalDescription funcDesc = new FunctionalDescription();
		funcDesc.add(new ElementProminence(StoryElement.Character, "Dawg", 3));
		funcDesc.add(new ElementProminence(StoryElement.Tension, 5));
		funcDesc.add(Weather.Snowing);
		funcDesc.add(Weather.Windy);
		funcDesc.add(Terrain.Coast);
		
		Prerequisite prereq = new Prerequisite();
		prereq.add(new Prerequisite.ElementRequirement(
				StoryElement.Character, "Hot",  Prerequisite.BinaryRestriction.greaterThan, 4));
		prereq.add(new Prerequisite.WeatherRequirement(
				Weather.Hailing, Prerequisite.ListRestriction.contains));
		prereq.add(new Prerequisite.SceneRequirement(
				"FirstScene", Prerequisite.SceneRestriction.sceneNotSeen));
		
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		nodes.add(new StoryNode("node1", "node 1 teaser", "node 1 event", funcDesc, prereq, choices));
		nodes.add(new StoryNode("node2", "node 2 teaser", "node 3 event", funcDesc, choices));
		nodes.add(new StoryNode("node3", "node 2 teaser", "node 3 event", funcDesc, choices));
		
		Story story = new Story(nodes);
		
		try
		{
			File result = new File(".\\testOutput\\testSimple.xml");
			
			serializer.write(story, result);
			
			Story readStory = serializer.read(Story.class, result);
			
			for (StoryNode n : readStory.getNodes())
			{
				System.out.println(n.getID());
			}
			
			File result2 = new File("example2.xml");
			
			serializer.write(readStory, result2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
