package game;

import storyEngine.Story;

// For now, this class represents one specific game created for Global Game Jam.
// Later, it should be refactored so that it is general and the subclasses represent
// specific games.

public class StoryExplorerGame
{
	private Story m_story;

	///////////////////////
	
	public StoryExplorerGame(Story story)
	{
		m_story = (Story)story.clone();
		m_story.reset();
	}
	
	///////////////////////
	
	public String[] getMetricIconFilenames()
	{
		String[] filenames = {
			"../data/images/knife-fork.png",
			"../data/images/heart.png",
			"../data/images/cross.png",	
			"../data/images/cow.png",	
		};
		
		return filenames;
	}
	
	public int getMaxMetricValue(int metricIndex)
	{
		// TODO: Get proper value from game or story
		return 10;
	}
	
	public int getCurrentMetricValue(int metricIndex)
	{
		// TODO: Get proper value from game or story
		return 5;
	}
	
	///////////////////////
}
