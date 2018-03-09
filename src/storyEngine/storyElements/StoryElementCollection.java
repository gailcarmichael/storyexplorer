package storyEngine.storyElements;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root
public class StoryElementCollection
{
	@ElementList(name="storyElements", inline=true)
	protected ArrayList<StoryElement> m_storyElements;
	
	
	// Used to easily retrieve elements involved with priority
	// calculations (contains references to some of the elements
	// in m_storyElements)
	protected ArrayList<StoryElement> m_storyElementsPriorityCalc;
	
	
	public StoryElementCollection()
	{
		this(new ArrayList<StoryElement>());
	}
	
	public StoryElementCollection(@ElementList(name="storyElements", inline=true) ArrayList<StoryElement> storyElements)
	{
		m_storyElements = new ArrayList<StoryElement>();
		m_storyElementsPriorityCalc = new ArrayList<StoryElement>();
		
		for (StoryElement e : storyElements)
		{
			add(e);
		}
	}
	
	
	public int getNumElementsPriorityCalc()
	{
		return m_storyElementsPriorityCalc.size();
	}
	
	
	public void printStoryElements()
	{
		for (StoryElement e : m_storyElements)
		{
			System.out.println(e);
		}
	}
	
	
	public boolean hasElementWithID(String id)
	{
		return getElementWithID(id) != null;
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
	
	
	public ArrayList<String> getIDs()
	{
		ArrayList<String> ids = new ArrayList<String>();
		
		for (StoryElement el : m_storyElements)
		{
			if (ids.contains(el.getID()))
			{
				System.err.println("getIDs found duplicate story element id: " + el.getID());
			}
			else
			{
				ids.add(el.getID());
			}
		}
		
		return ids;
	}
	
	
	public ArrayList<String> getDesireValueIDs()
	{
		ArrayList<String> ids = new ArrayList<String>();
		
		for (StoryElement el : m_storyElements)
		{
			if (ids.contains(el.getID()))
			{
				System.err.println("getIDs found duplicate story element id: " + el.getID());
			}
			else if (el.hasDesireValue())
			{
				ids.add(el.getID());
			}
		}
		
		return ids;
	}
	
	
	public boolean add(StoryElement e)
	{
		boolean success = false;
		
		if (getElementWithID(e.getID()) == null)
		{
			m_storyElements.add(e);
			
			if (e.getType() == ElementType.quantifiable)
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
	
	public Object clone()
	{
		StoryElementCollection collection = new StoryElementCollection(
				m_storyElements);
		
		return collection;
	}
}
