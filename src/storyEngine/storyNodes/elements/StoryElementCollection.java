package storyEngine.storyNodes.elements;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root
public class StoryElementCollection
{
	@ElementList(name="storyElements", inline=true)
	protected ArrayList<StoryElement> m_storyElements;
	
	
	// Used to easily retrieve elements involved with priority
	// calculations
	protected ArrayList<StoryElement> m_storyElementsPriorityCalc;
	
	
	public StoryElementCollection()
	{
		this(new ArrayList<StoryElement>());
	}
	
	public StoryElementCollection(ArrayList<StoryElement> storyElements)
	{
		m_storyElements = new ArrayList<StoryElement>();
		m_storyElementsPriorityCalc = new ArrayList<StoryElement>();
		
		for (StoryElement e : storyElements)
		{
			add(e);
		}
	}
	
	
	public void printStoryElements()
	{
		for (StoryElement e : m_storyElements)
		{
			System.out.println(e);
		}
	}
	
	
	public StoryElement getElementWithID(String id)
	{
		StoryElement elementWithID = null;
		
		for (StoryElement e : m_storyElements)
		{
			if (e.getID().equals(id))
			{
				elementWithID = e;
				break;
			}
		}
		
		return elementWithID;
	}
	
	
	public boolean add(StoryElement e)
	{
		boolean success = false;
		
		if (getElementWithID(e.getID()) == null)
		{
			m_storyElements.add(e);
			
			if (e.getType() == ElementType.Quantifiable)
			{
				m_storyElementsPriorityCalc.add(e);
			}
		}
		else
		{
			System.err.println("Could not add element with id " + e.getID() +
					           " to collection because it already exists.");
		}
		
		return success;
	}
}
