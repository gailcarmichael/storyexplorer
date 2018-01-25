package game;

import storyEngine.Story;

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
