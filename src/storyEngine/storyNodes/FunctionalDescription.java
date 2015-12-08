package storyEngine.storyNodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import storyEngine.Story;
import storyEngine.StoryState;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElement;
import storyEngine.storyElements.StoryElementCollection;

public class FunctionalDescription
{
	// This class exists only to make the XML nicer
	protected static class Tag
	{
		@Attribute(name="id")
		String m_tag;
		Tag(@Attribute(name="id") String tag) { m_tag = tag; }
		
		public String toString() { return m_tag; }
	}
	
	
	@ElementMap(required=false, inline=true, entry="prominence", key="id", attribute=true)
	protected HashMap<String, Integer> m_elementProminences;
	
	@ElementList(required=false, inline=true)
	protected ArrayList<Tag> m_elementTags;
	
	
	public FunctionalDescription()
	{
		m_elementProminences = new HashMap<String, Integer>();
		m_elementTags = new ArrayList<Tag>();
	}
	
	
	public FunctionalDescription(
			HashMap<String, Integer> prominences,
			ArrayList<Tag> tags)
	{
		m_elementProminences = prominences;
		m_elementTags = tags;
	}
	
	
	public void add(StoryElementCollection c, String elementID, int prominence)
	{	
		StoryElement e = c.getElementWithID(elementID);
		
		// Only quantifiable story elements that are not story-state-only can
		// be added a node's functional description
		if (e != null && e.getType() == ElementType.quantifiable)
		{
			m_elementProminences.put(elementID, prominence);
		}
		else
		{
			System.err.println("Could not add id " + elementID + " to functional" +
					           " description because it does not exist, or because it" +
					           " is not quantifiable.");
		}
	}
	
	
	public void add(StoryElementCollection c, String elementID)
	{
		StoryElement e = c.getElementWithID(elementID);
		
		// Only quantifiable story elements that are not story-state-only can
		// be added a node's functional description
		if (e != null && e.getType() == ElementType.taggable)
		{
			m_elementTags.add(new Tag(elementID));
		}
		else
		{
			System.err.println("Could not add id " + elementID + " to functional" +
					           " description because it does not exist, or because it" +
					           " is quantifiable when it shouldn't be.");
		}
	}
	
	
	boolean featuresElement(String id)
	{
		return (m_elementProminences.containsKey(id) || m_elementTags.contains(id));
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	public boolean isValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		// A functional description is valid if each element prominence
		// is of type Quantifiable, and each tag is of type Taggable.
		
		if (m_elementProminences != null)
		{
			for (String id : m_elementProminences.keySet())
			{
				if (!elements.hasElementWithID(id))
				{
					System.err.println("FunctionalDescription is not valid because element" +
							" with id " + id + "  is not part of the element collection.");
					isValid = false;
				}
				else if (elements.getElementWithID(id).getType() !=
						ElementType.quantifiable)
				{
					System.err.println("FunctionalDescription is not valid because element" +
							" with id " + id + "  is not " + ElementType.quantifiable);
					isValid = false;
				}
			}
			
			for (Tag tag : m_elementTags)
			{
				String id = tag.m_tag;
				
				if (!elements.hasElementWithID(id))
				{
					System.err.println("FunctionalDescription is not valid because element" +
							" with id " + id + "  is not part of the element collection.");
					isValid = false;
				}
				else if (elements.getElementWithID(id).getType() !=
						ElementType.taggable)
				{
					System.err.println("FunctionalDescription is not valid because element" +
							" with id " + id + "  is not " + ElementType.taggable);
					isValid = false;
				}
			}
		}
		
		return isValid;
	}
	
	
	////////////////////////////////////////////////////////////////
	
	public float getProminenceValueForElement(String elementID)
	{
		float prominence = 0;
		
		if (m_elementProminences.containsKey(elementID))
		{
			prominence = m_elementProminences.get(elementID);
		}

		return prominence;
	}
	
	public ArrayList<String> getElementIDs()
	{
		ArrayList<String> elementIDs = new ArrayList<String>();
		
		elementIDs.addAll(m_elementProminences.keySet());
		
		for (Tag t : m_elementTags)
		{
			elementIDs.add(t.toString());
		}
		
		return elementIDs;
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	public float calculatePriorityScore(Story story, StoryElementCollection elementCol)
	{
		switch (story.getPrioritizationType())
		{
			case sumOfCategoryMaximums:
				return calculateSumOfCategoryMaximums(story, elementCol);
				
			case physicsAnalogy:
				return calculatePhysicsAnalogyScore(story, elementCol);

			default:
				System.err.println("Cannot calculate priority score because " + story.getPrioritizationType() +
						" is not a valid prioritization type.");
				return -1;
		}
	}
	
	protected float calculatePhysicsAnalogyScore(Story story, StoryElementCollection elementCol)
	{		
		// Each story element represented in the node will get a score that represents attraction (+ve)
		// or repulsion (-ve).
		
		// The default behavior of a node is that story elements that have been seen recently should
		// cause a repulsion while elements not seen in some time should cause an attraction; either
		// should be proportional to the distance between the empty slot and the last node that 
		// reflected the story element. Forces values for all the story elements will be summed and 
		// this will become the score of the node.

		
		//final float MAX_DESIRE = story.getLargestDesireValue();
		
		float nodeScore = 0;
		
		for (String id : m_elementProminences.keySet())
		{			
			StoryElement el = elementCol.getElementWithID(id);
			
			//System.out.print("\t" + el.getName() + ":\t");
			//if (el.getName().length() < 8) System.out.print("\t");
			
			if (el != null && el.hasDesireValue())
			{
				float mostRecentProminence = story.getProminenceForMostRecentNodeWithElement(id);
				
				// If there were no other nodes that have shown this element, we still want to use the desire
				// and the new node's prominence to calculate a force
				if (mostRecentProminence <= 0) mostRecentProminence = 1;
				
				float newProminence = m_elementProminences.get(id);
				
				float desire = story.getDesireForElement(id);
				
				float force = (newProminence * mostRecentProminence * desire);
				
				if (desire <= 5)
				{
					// If the element has been seen recently, the node should be 
					// strongly repelled on this element
					force *= -1;
				}
				
				nodeScore += force;
				
				/*System.out.println(
						mostRecentProminence + " \t" +
						newProminence + " \t" +
						desire + " \t" +
						force + " \t");*/
			}
			//else System.out.println("-");
		}
		
		//System.out.println("Total score: " + nodeScore + "\n");
		
		return nodeScore;
	}
	
	protected float calculateSumOfCategoryMaximums(Story story, StoryElementCollection elementCol)
	{
		float nodeScore = 0;
		
		// Walk through each element in the description. Check whether the element
		// has a desire value in the story state.  If so, multiply the desire by
		// the element's prominence in the node.  Keep track of the highest score
		// in each element category.  Sum the highest scores together.
		
		HashMap<String, Float> categoryScores = new HashMap<String, Float>();
		
		for (String id : m_elementProminences.keySet())
		{
			StoryElement el = elementCol.getElementWithID(id);
			
			if (el != null && el.hasDesireValue())
			{
				float elementScore = m_elementProminences.get(id) *
						             story.getDesireForElement(id);
				
				if (!categoryScores.containsKey(el.getCategory()) || 
					categoryScores.get(el.getCategory()) < elementScore)
				{
					categoryScores.put(el.getCategory(), elementScore);
				}
				
			}
		}
		
		for (Float categoryScore : categoryScores.values())
		{
			nodeScore += categoryScore;
		}
		
		return nodeScore;
	}
	
	
	////////////////////////////////////////////////////////////////
	
	
	void resetDesireValues(StoryState state)
	{
		for (String id : m_elementProminences.keySet())
		{
			if (state.isDesireValue(id))
			{
				state.resetDesireValue(id);
			}
		}
	}
	
}
