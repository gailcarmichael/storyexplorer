package storyEngine.storyNodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

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
	
}
