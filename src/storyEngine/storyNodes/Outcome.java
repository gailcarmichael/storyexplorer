package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import storyEngine.StoryState;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElementCollection;

// An outcome specifies what happens after a choice is made in 
// a story node.  There is one outcome per choice.  Modifiers in
// an outcome change the story's state in some way.
public class Outcome
{
	@Element(name="text", required=false)
	protected String m_outcomeText;

	@ElementList(inline=true, required=false)
	protected ArrayList<QuantifiableModifier> m_quantifiableModifiers;

	@ElementList(inline=true, required=false)
	protected ArrayList<TagModifier> m_taggableModifiers;

	
	public Outcome()
	{
		this(null);
	}

	public Outcome(String outcomeText)
	{
		this(outcomeText, new ArrayList<QuantifiableModifier>(), new ArrayList<TagModifier>());	
	}

	public Outcome(
			String outcomeText,
			ArrayList<QuantifiableModifier> quantModifiers,
			ArrayList<TagModifier> taggableModifiers)
	{
		m_outcomeText = outcomeText;
		
		m_quantifiableModifiers = new ArrayList<Outcome.QuantifiableModifier>();
		if (quantModifiers != null) m_quantifiableModifiers.addAll(quantModifiers);
		
		m_taggableModifiers = new ArrayList<Outcome.TagModifier>();
		if (taggableModifiers != null) m_taggableModifiers.addAll(taggableModifiers);
	}
	
	
	public String getOutcomeText() { return m_outcomeText; }
	

	//////////////////////////////////////////////////////////////////////////////////////


	public void add(QuantifiableModifier m)
	{
		if (m != null)
		{
			m_quantifiableModifiers.add(m);
		}
	}

	public void add(TagModifier m)
	{
		if (m != null)
		{
			m_taggableModifiers.add(m);
		}
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	
	
	public boolean isValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		// Check quantifiable modifiers
		if (m_quantifiableModifiers != null)
		{
			for (QuantifiableModifier modifier : m_quantifiableModifiers)
			{
				if (!elements.hasElementWithID(modifier.getID()))
				{
					System.err.println("Quantifiable modifier is not valid because element" +
							" with id " + modifier.getID() + "  is not part of the element collection.");
					isValid = false;
				}
				else if (elements.getElementWithID(modifier.getID()).getType() != ElementType.quantifiable &&
						elements.getElementWithID(modifier.getID()).getType() != ElementType.quantifiableStoryStateOnly)
				{
					System.err.println("Quantifiable modifier is not valid because element" +
							" with id " + modifier.getID() + "  has type " + 
							elements.getElementWithID(modifier.getID()).getType());
					isValid = false;
				}
			}
		}
		
		
		// Check tag modifiers
		if (m_taggableModifiers != null)
		{
			for (TagModifier modifier : m_taggableModifiers)
			{
				if (!elements.hasElementWithID(modifier.getID()))
				{
					System.err.println("Taggable modifier is not valid because element" +
							" with id " + modifier.getID() + "  is not part of the element collection.");
					isValid = false;
				}
				else if (elements.getElementWithID(modifier.getID()).getType() != ElementType.taggable)
				{
					System.err.println("Taggable modifier is not valid because element" +
							" with id " + modifier.getID() + "  has type " + 
							elements.getElementWithID(modifier.getID()).getType());
					isValid = false;
				}
			}
		}
		
		
		return isValid;
	}

	//////////////////////////////////////////////////////////////////////////////////////
	
	
	void applyOutcome(StoryState state)
	{
		for (QuantifiableModifier modifier : m_quantifiableModifiers)
		{
			float value = state.getValueForElement(modifier.getID());
			
			if (modifier.getAbsolute())
			{
				value = modifier.getDelta();
			}
			else
			{
				value += modifier.getDelta();
			}
			
			state.setValueForElement(modifier.getID(), value);
		}
		
		
		for (TagModifier modifier : m_taggableModifiers)
		{
			if (modifier.getAction() == TagAction.add)
			{
				state.addTag(modifier.getID());
			}
			else if (modifier.getAction() == TagAction.remove)
			{
				state.removeTag(modifier.getID());
			}
		}
	}
	

	//////////////////////////////////////////////////////////////////////////////////////

	
	@Root(name="quantModifier")
	static public class QuantifiableModifier
	{
		@Attribute(name="id")
		protected String m_elementID;

		@Attribute(name="absolute")
		protected boolean m_absolute;

		@Text
		protected int m_delta;


		public QuantifiableModifier(
				@Attribute(name="id") String id, 
				@Attribute(name="absolute") boolean absolute,
				@Text int delta)
		{
			m_elementID = id;
			m_absolute = absolute;
			m_delta = delta;
		}
		
		
		String getID() { return m_elementID; }
		boolean getAbsolute() { return m_absolute; }
		int getDelta() { return m_delta; }
	}

	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	public static enum TagAction
	{
		add,
		remove
	}

	
	//////////////////////////////////////////////////////////////////////////////////////

	
	@Root(name="tagModifier")
	static public class TagModifier
	{
		@Attribute(name="id")
		protected String m_elementID;

		@Text
		protected TagAction m_action;


		public TagModifier(
				@Attribute(name="id") String id,
				@Text TagAction action)
		{
			m_elementID = id;
			m_action = action;
		}
		
		
		String getID() { return m_elementID; }
		TagAction getAction() { return m_action; }
	}
}