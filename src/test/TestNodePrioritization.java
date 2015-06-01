package test;

import java.io.File;
import java.util.ArrayList;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import storyEngine.Story;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyElements.StoryElementCollection;
import storyEngine.storyNodes.StoryNode;

public class TestNodePrioritization
{
	// Here we are testing node prioritization - not applying outcome, etc 
	
	
	public static void main(String[] args)
	{
		///////////////////////////////////////////////////////////////////////////
		// Step 1: Set up a story element collection
		
		StoryElementCollection storyElements = new StoryElementCollection();
		
		storyElements.add(new StoryElement("heroTheme", "themes", "heroism", ElementType.quantifiable));
		storyElements.add(new StoryElement("friendshipTheme", "themes", "friendship", ElementType.quantifiable));
		storyElements.add(new StoryElement("betrayalTheme", "themes", "betrayal", ElementType.quantifiable));

		storyElements.add(new StoryElement("dogCharacter", "characters", "dog", ElementType.quantifiable));
		storyElements.add(new StoryElement("kittyCharacter", "characters", "kitty", ElementType.quantifiable));

		storyElements.add(new StoryElement("tension", "tension", "tension", ElementType.quantifiableStoryStateOnly));
		
		
		///////////////////////////////////////////////////////////////////////////
		// Step 2: Read the story data and validate it
		
		Story story = null;

		try
		{
			File result = new File(".\\testData\\testNodePrioritization\\story.xml");
			Serializer serializer = new Persister();
			story = serializer.read(Story.class, result);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		
		story.setElementCollection(storyElements);
		System.out.println("Story is valid: " + story.isValid(storyElements));
		
		
		///////////////////////////////////////////////////////////////////////////
		// Step 3: Get the first set of top scenes given to the user
		
		ArrayList<StoryNode> availableNodes = story.getCurrentSceneOptions();
		
		if (availableNodes == null)
		{
			System.err.println("There was a problem obtaining the available nodes.");
		}
		else
		{
			for (StoryNode n : availableNodes)
			{
				System.out.println(n.getTeaserText() + ": " + n.getEventText());
			}
		}
	}
}
