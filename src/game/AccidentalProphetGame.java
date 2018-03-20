package game;

import storyEngine.Story;

public class AccidentalProphetGame extends StoryExplorerGame
{
	public AccidentalProphetGame()
	{
		super(Story.loadStoryFromFile(
				"./data/accidentalProphetStory.xml", 
				"./data/accidentalProphetStoryElements.xml"));
	}
	
	///////////////////////
	
	public String[] getMetricIconFilenames()
	{	
		String[] filenames = {
			"../data/images/currency.png",
			"../data/images/charity.png",
			"../data/images/morality.png",
			"../data/images/trust.png",
			"../data/images/superstition.png",
		};
		
		return filenames;
	}
	
	public String[] getMetricIDs()
	{
		String[] ids = {
			"wealthMetric",
			"charityMetric",
			"moralityMetric",
			"trustMetric",
			"superstitionMetric"
		};
		
		return ids;
	}
	
	public int getMaxMetricValue(int metricIndex)
	{
		return 10;
	}
}
