package game;

import java.util.ArrayList;

import storyEngine.Story;
import storyEngine.storyNodes.StoryNode;

// For now, this class represents one specific game created for Global Game Jam.
// Later, it should be refactored so that it is general and the subclasses represent
// specific games.

// In this version of the game:
// - kernels can't branch, and must have prerequisites that enforce linear progression

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
	
	public int getNumKernels() { return m_story.getNumKernels(); }
	public int getNumKernelsConsumed() { return m_story.getNumKernelsConsumed(); }
	
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
	
	public boolean canChooseAKernel()
	{
		return m_story.getNodeBeingConsumed() == null;
	}
	
	public void consumeNextKernel()
	{
		ArrayList<StoryNode> kernels = m_story.getAvailableKernelNodes();
		
		if (kernels.size() != 1)
		{
			System.err.println("Game failed to consume next kernel node because there isn't exactly one such node currently available.");
		}
		else
		{
			m_story.startConsumingNode(kernels.get(0));
		}
	}
}
