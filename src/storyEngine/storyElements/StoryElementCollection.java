package storyEngine.storyElements;

import java.util.ArrayList;
import java.util.HashMap;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.storyElements.StoryElementWeightCurve.PiecewiseConstantWeightCurve;


@Root
public class StoryElementCollection
{
	@ElementList(name="storyElements", inline=true)
	protected ArrayList<StoryElement> m_storyElements;
	
	
	// Contains optional element weight curves (values to be used as multipliers)
	// to change how important elements are in priority calculations
	protected HashMap<String, StoryElementWeightCurve> m_weightCurves;
	
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
		
		m_weightCurves = new HashMap<String, StoryElementWeightCurve>();
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<StoryElement> getCopyOfElementList()
	{
		return (ArrayList<StoryElement>) m_storyElements.clone();
	}
	
	public int getNumElementsPriorityCalc()
	{
		return getStoryElementsPriorityCalc().size();
	}
	
	
	public ArrayList<StoryElement> getStoryElementsPriorityCalc()
	{
		return m_storyElementsPriorityCalc;
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
	
	public ArrayList<String> getMemoryValueIDs()
	{
		return getDesireValueIDs();
	}
	
	public ArrayList<String> getElementsIDsInCategory(String category)
	{
		ArrayList<String> inCategory = new ArrayList<String>();
		
		for (StoryElement e : m_storyElements)
		{
			if (e.getCategory().equals(category))
			{
				inCategory.add(e.getID());
			}
		}
		
		return inCategory;
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
	
	public void addWeightCurve(String id, StoryElementWeightCurve curve)
	{
		m_weightCurves.put(id, curve);
	}
	
	public StoryElementWeightCurve getWeightCurve(String id)
	{
		if (m_weightCurves.containsKey(id))
		{
			return m_weightCurves.get(id);
		}
		else
		{
			return new PiecewiseConstantWeightCurve(1); // all default weight of 1
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object clone()
	{
		StoryElementCollection collection = new StoryElementCollection(
				m_storyElements);

		collection.m_weightCurves = (HashMap<String, StoryElementWeightCurve>) m_weightCurves.clone();
	
		return collection;
	}
}
