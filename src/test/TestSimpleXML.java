package test;

import java.io.File;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import storyEngine.Story;
import storyEngine.storyNodes.Choice;
import storyEngine.storyNodes.FunctionalDescription;
import storyEngine.storyNodes.StoryNode;
import storyEngine.storyNodes.elements.ElementProminence;
import storyEngine.storyNodes.elements.StoryElements;
import storyEngine.storyNodes.elements.Weather;

public class TestSimpleXML
{
	public static void main(String[] args)
	{
		Serializer serializer = new Persister();
		
		ArrayList<Choice> choices = new ArrayList<Choice>();
		choices.add(new Choice("choice text"));
		
		
		ArrayList<ElementProminence> prominencies = new ArrayList<ElementProminence>();
		prominencies.add(new ElementProminence(StoryElements.Character, "Dawg", 3));
		
		ArrayList<Weather> weather = new ArrayList<Weather>();
		weather.add(Weather.Snowing);
		
		FunctionalDescription funcDesc = new FunctionalDescription(prominencies, null, weather);
		
		ArrayList<StoryNode> nodes = new ArrayList<StoryNode>();
		nodes.add(new StoryNode("node1", "node 1 teaser", "node 1 event", choices, funcDesc));
		nodes.add(new StoryNode("node2", "node 2 teaser", "node 3 event", choices, funcDesc));
		nodes.add(new StoryNode("node3", "node 2 teaser", "node 3 event", choices, funcDesc));
		
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
