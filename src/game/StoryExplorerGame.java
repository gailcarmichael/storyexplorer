package game;

import java.util.ArrayList;

import storyEngine.Story;
import storyEngine.storyNodes.StoryNode;

// In this version of the game:
// - kernels can't branch, and must have prerequisites that enforce linear progression
// - there is currently no regard for a node tagged as first, nor last

public abstract class StoryExplorerGame
{
	private Story m_story;
	
	private ArrayList<StoryNode> m_currentSatellites;
	
	private boolean m_showingOutcomeText;

	///////////////////////
	
	protected StoryExplorerGame(Story story)
	{
		m_story = (Story)story.clone();
		m_story.reset();
		
		refreshCurrentSatellites();
		
		m_showingOutcomeText = false;
	}
	
	///////////////////////
	
	public abstract String[] getMetricIconFilenames();
	public abstract String[] getMetricIDs();
	public abstract int getMaxMetricValue(int metricIndex);
	
	public int getCurrentMetricValue(int metricIndex)
	{
		float metricValue = m_story.getStoryStateOnlyElementValue(getMetricIDs()[metricIndex]);
		metricValue = Math.max(0, metricValue);
		metricValue = Math.min(getMaxMetricValue(metricIndex), metricValue);
		return (int)metricValue;
	}
	
	///////////////////////
	
	public int getNumKernels() { return m_story.getNumKernels(); }
	public int getNumKernelsConsumed() { return m_story.getNumKernelsConsumed(); }
	
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
	
	///////////////////////
	
	private void refreshCurrentSatellites()
	{
		m_currentSatellites = m_story.getCurrentSceneOptions(true); // don't include top kernel
	}
	
	public int getNumSatellitesToShow()
	{
		return m_story.getNumTopScenesForUser();
	}
	
	public ArrayList<String> getSatellitesTeaserText()
	{
		ArrayList<String> textList = new ArrayList<String>();
		
		for (StoryNode s : m_currentSatellites)
		{
			textList.add(s.getTeaserText());
		}
		
		return textList;
	}
	
	public void consumeSatellite(int index)
	{
		if (index < 0 || index >= m_currentSatellites.size())
		{
			System.err.println("Game failed to consume satellite because " + index + " is not a valid index");
		}
		else
		{
			m_story.startConsumingNode(m_currentSatellites.get(index));
		}
	}
	
	///////////////////////
	
	public boolean isDisplayingAScene()
	{
		return m_story.getNodeBeingConsumed() != null;
	}
	
	public String getCurrentSceneText()
	{
		String text = "";
		StoryNode currentNode = m_story.getNodeBeingConsumed();
		
		if (currentNode == null)
		{
			System.err.println("Game cannot provide current scene text because no node is being consumed.");
		}
		else
		{
			text = currentNode.getEventText();
		}
		
		return text;
	}
	
	public String getCurrentSceneOutcomeText()
	{
		String text = null;
		StoryNode currentNode = m_story.getNodeBeingConsumed();
		
		if (currentNode == null)
		{
			System.err.println("Game cannot provide current scene text because no node is being consumed.");
		}
		else
		{
			text = currentNode.getOutcomeTextForSelectedChoice();
		}
		
		return text;
	}
	
	///////////////////////
	
	public int getNumChoicesForCurrentNode()
	{
		int numChoices = 0;
		
		if (m_story.getNodeBeingConsumed() != null)
		{
			numChoices = m_story.getNodeBeingConsumed().getNumChoices();
		}
		
		return numChoices;
	}
	
	public ArrayList<String> getChoicesText()
	{
		ArrayList<String> choicesText = new ArrayList<String>();
		
		for (int i=0; i < getNumChoicesForCurrentNode(); i++)
		{
			choicesText.add(m_story.getNodeBeingConsumed().getTextForChoice(i));
		}
		
		return choicesText;
	}
	
	public void applyChoice(int index)
	{
		if (m_story.getNodeBeingConsumed() == null) return; 
		
		if (index < 0) index = 0; // for scenes with one choice but no button
		m_story.getNodeBeingConsumed().setSelectedChoice(index);
	}
	
	public void moveSceneForward()
	{
		if (!currentlyShowingOutcome() && currentNodeHasOutcomeToShow())
		{
			setShowingOutcome(true);
		}
		else if (currentlyShowingOutcome())
		{
			finishConsumingScene();
		}
		else
		{
			applyChoice(0);
			setShowingOutcome(currentNodeHasOutcomeToShow());
			if (!currentlyShowingOutcome()) finishConsumingScene();
		}
	}
	
	///////////////////////
	
	public boolean currentlyShowingOutcome() { return m_showingOutcomeText; }
	private void setShowingOutcome(boolean showingOutcome) { m_showingOutcomeText = showingOutcome; }
	
	public boolean currentNodeHasOutcomeToShow() 
	{
		if (m_story.getNodeBeingConsumed() == null)
		{
			return false;
		}
		else
		{
			String outcomeText = m_story.getNodeBeingConsumed().getOutcomeTextForSelectedChoice();
			return (getNumChoicesForCurrentNode() > 1) &&
					(outcomeText != null) &&
					(!outcomeText.isEmpty());
		}
	}
	
	///////////////////////
	
	public void finishConsumingScene()
	{
		m_showingOutcomeText = false;
		
		if (m_story.getNodeBeingConsumed() == null) return; 
		
		m_story.applyOutcomeAndAdjustQuantifiableValues();
		m_story.finishConsumingNode();
		
		refreshCurrentSatellites();
	}
	
	///////////////////////
}
