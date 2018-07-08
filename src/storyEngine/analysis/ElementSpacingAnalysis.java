package storyEngine.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import storyEngine.Story;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyNodes.StoryNode;

public class ElementSpacingAnalysis
{
	private Story m_story;

	private SortedMap<String, SortedMap<Integer, Integer>> m_spacingHist;
	
	
	////////////////////////////////
	
	
	public ElementSpacingAnalysis(Story story)
	{
		m_story = story;
		
		m_spacingHist = new TreeMap<String, SortedMap<Integer, Integer>>();
		setupElementsInHistogram();
	}
	
	
	private void setupElementsInHistogram()
	{
		for (StoryElement e : m_story.getElementCollection().getStoryElementsPriorityCalc())
		{
			m_spacingHist.put(e.getID(), new TreeMap<Integer, Integer>());
		}
	}
	
	
	////////////////////////////////
	
	
	public SortedMap<String, SortedMap<Integer, Integer>> getElementSpacingHistogram()
	{
		return m_spacingHist;
	}
	
	
	public int getMaxSpacingForElement(String elementID)
	{
		int spacing = 0;
		
		SortedMap<Integer, Integer> histForElement = m_spacingHist.get(elementID); 
		if (histForElement != null)
		{
			spacing = histForElement.lastKey();
		}
		
		return spacing;
	}
	
	
	public int getMinSpacingForElement(String elementID)
	{
		int spacing = 0;
		
		SortedMap<Integer, Integer> histForElement = m_spacingHist.get(elementID); 
		if (histForElement != null)
		{
			spacing = histForElement.firstKey();
		}
		
		return spacing;
	}

	
	////////////////////////////////
	
	
	public void recalculateSpacingHistogram()
	{
		HashMap<String, Integer> lastSceneNumberForElement = new HashMap<String, Integer>();
		
		ArrayList<StoryNode> scenes = m_story.getScenesSeen();
		
		
		for (int sceneNum=0; sceneNum < scenes.size(); sceneNum++) // walk through all the scenes seen in a story
		{
			StoryNode node = scenes.get(sceneNum);
			for (String elementID : node.getElementIDs()) // look at each element in the node
			{
				if (m_spacingHist.containsKey(elementID)) // only look at story elements with a desire value
				{
					// Track the number of scenes it has been since we last saw this particular element
					
					SortedMap<Integer, Integer> histForElement = m_spacingHist.get(elementID);
					
					int timeSince;
					if (lastSceneNumberForElement.get(elementID) != null)
					{
						timeSince = sceneNum - lastSceneNumberForElement.get(elementID) - 1;
					}
					else
					{
						timeSince = sceneNum; // on the first encounter, space between is all nodes so far
					}
					lastSceneNumberForElement.put(elementID, sceneNum);
					
					Integer countForTimeSince = histForElement.get(timeSince);
					if (countForTimeSince != null)
					{
						histForElement.put(timeSince, (countForTimeSince + 1));
					}
					else
					{
						histForElement.put(timeSince, 1);
					}
				}
			}
		}
	}

	
	////////////////////////////////
	
	

	public void printElementSpacingHistogram()
	{
		printElementSpacingHistogram(false);
	}
	
	public void printElementSpacingHistogram(boolean recalculateFirst)
	{
		if (recalculateFirst) recalculateSpacingHistogram();
		
		SortedMap<Integer, Integer> collectiveHistogram = new TreeMap<Integer, Integer>();
		
		for (String elementID : m_spacingHist.keySet())
		{
			System.out.println("\n" + elementID + ":\n");

			SortedMap<Integer, Integer> histForElement = m_spacingHist.get(elementID);
	
			int max = getMaxSpacingForElement(elementID);
			for (int spacing=0; spacing <= max; spacing++)
			{
				int count = 0;
				if (histForElement.containsKey(spacing))
				{
					count = histForElement.get(spacing);
				}
				System.out.printf("\tSpacing of " + spacing + ":\t" + "%3d", count);
				System.out.print(" " + String.join("", Collections.nCopies(count, "*")) + "\n");
				
				if (collectiveHistogram.containsKey(spacing))
				{
					collectiveHistogram.put(spacing, collectiveHistogram.get(spacing) + count);
				}
				else
				{
					collectiveHistogram.put(spacing, count);
				}
			}

			System.out.println("\n\tMin space value: " + getMinSpacingForElement(elementID));
			System.out.println("\tMax space value: " + getMaxSpacingForElement(elementID));
		}
		
		System.out.println("\nSpacing over all elements: \n");
		for (Integer spacing : collectiveHistogram.keySet())
		{
			int count = collectiveHistogram.get(spacing);
			System.out.printf("\tSpacing of " + spacing + ":\t" + "%3d", count);
			System.out.print(" " + String.join("", Collections.nCopies(count, "*")) + "\n");
		}
	}
}
