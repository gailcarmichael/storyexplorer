package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import storyEngine.StoryState;
import storyEngine.storyElements.ElementType;
import storyEngine.storyElements.StoryElementCollection;

// Each story node can have zero or one prerequisites. A
// prerequisite contains a collection of requirements, each
// of which must pass for the prerequisite to pass.
public class Prerequisite
{
	
	@ElementList(inline=true, required=false)
	protected ArrayList<QuantifiableElementRequirement> m_quantifiableRequirements;
	
	@ElementList(inline=true, required=false)
	protected ArrayList<TagRequirement> m_tagRequirements;
	
	@ElementList(inline=true, required=false)
	protected ArrayList<SceneRequirement> m_sceneRequirements;
	
	
	public Prerequisite()
	{
		m_quantifiableRequirements = new ArrayList<QuantifiableElementRequirement>();
		m_tagRequirements = new ArrayList<TagRequirement>();
		m_sceneRequirements = new ArrayList<SceneRequirement>();
	}
	
	public Prerequisite(
			ArrayList<QuantifiableElementRequirement> qRequirements,
			ArrayList<TagRequirement> tagRequirements,
			ArrayList<SceneRequirement> sceneRequirements)
	{
		this();
		if (qRequirements != null) m_quantifiableRequirements.addAll(qRequirements);
		if (tagRequirements != null) m_tagRequirements.addAll(tagRequirements);
		if (sceneRequirements != null) m_sceneRequirements.addAll(sceneRequirements);
	}
	
	
	public void add(QuantifiableElementRequirement r)
	{
		m_quantifiableRequirements.add(r);
	}
	
	public void add(TagRequirement r)
	{
		m_tagRequirements.add(r);
	}
	
	public void add(SceneRequirement r)
	{
		m_sceneRequirements.add(r);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	public boolean isValid(StoryElementCollection elements)
	{
		boolean isValid = true;
		
		// A prerequisite is valid if the quantifiable requirements
		// have ids for either type of quantifiable elements, and the
		// tag requirements have ids for taggable elements.
		
		// Currently, there are no checks for the scene ids.
		
		for (QuantifiableElementRequirement req : m_quantifiableRequirements)
		{
			if (!elements.hasElementWithID(req.getID()))
			{
				System.err.println("Prerequisite is not valid because element" +
						" with id " + req.getID() + "  is not part of the element collection.");
				isValid = false;
			}
			else if (elements.getElementWithID(req.getID()).getType() != ElementType.quantifiable &&
					elements.getElementWithID(req.getID()).getType() != ElementType.quantifiableStoryStateOnly)
			{
				System.err.println("Prerequisite is not valid because element" +
						" with id " + req.getID() + "  has type " + 
						elements.getElementWithID(req.getID()).getType());
				isValid = false;
			}
		}
		
		for (TagRequirement req : m_tagRequirements)
		{
			if (!elements.hasElementWithID(req.getID()))
			{
				System.err.println("Prerequisite is not valid because element" +
						" with id " + req.getID() + "  is not part of the element collection.");
				isValid = false;
			}
			else if (elements.getElementWithID(req.getID()).getType() != ElementType.taggable)
			{
				System.err.println("Prerequisite is not valid because element" +
						" with id " + req.getID() + "  has type " + 
						elements.getElementWithID(req.getID()).getType());
				isValid = false;
			}
		}
		
		return isValid;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	public boolean passes(StoryState storyState)
	{
		boolean passes = true;
		
		// Quantifiable prerequisites
		if (passes)
		{
			for (QuantifiableElementRequirement req : m_quantifiableRequirements)
			{
				if (!req.passes(storyState))
				{
					passes = false;
					break;
				}
			}
		}
		
		// Tag prerequisites
		if (passes)
		{
			for (TagRequirement req : m_tagRequirements)
			{
				if (!req.passes(storyState))
				{
					passes = false;
					break;
				}
			}
		}
		
		// Scene prerequisites
		if (passes)
		{
			for (SceneRequirement req : m_sceneRequirements)
			{
				if (!req.passes(storyState))
				{
					passes = false;
					break;
				}
			}
		}
		
		return passes;
	}
	

	//////////////////////////////////////////////////////////////////////////////////////

	public static enum BinaryRestriction
	{
		equal,
		lessThan,
		lessThanOrEqual,
		greaterThan,
		greaterThanOrEqual
	}

	public static enum ListRestriction
	{
		contains,
		doesNotContain
	}

	public static enum SceneRestriction
	{
		seen,
		notSeen
	}

	//////////////////////////////////////////////////////////////////////////////////////

	@Root(name="quantReq")
	public static class QuantifiableElementRequirement
	{
		@Attribute(name="id")
		protected String m_elementID;

		@Attribute(name="operator")
		protected BinaryRestriction m_operator;

		@Attribute(name="compareTo")
		protected int m_compareTo;


		public QuantifiableElementRequirement(
				@Attribute(name="id") String id,
				@Attribute(name="operator") BinaryRestriction operator,
				@Attribute(name="compareTo") int compareTo)
		{
			m_elementID = id;
			m_operator = operator;
			m_compareTo = compareTo;
		}
		
		public String getID() { return m_elementID; }
		public BinaryRestriction getOperator() { return m_operator; }
		public int getCompareValue() { return m_compareTo; }
		
		
		boolean passes(StoryState storyState)
		{
			boolean passes = false;
			
			float stateValue = storyState.getValueForElement(m_elementID);
			
			switch (m_operator)
			{
				case equal:
					passes = (stateValue == m_compareTo);
					break;

				case lessThan:
					passes = (stateValue < m_compareTo);
					break;

				case greaterThan:
					passes = (stateValue > m_compareTo);
					break;

				case lessThanOrEqual:
					passes = (stateValue <= m_compareTo);
					break;

				case greaterThanOrEqual:
					passes = (stateValue >= m_compareTo);
					break;

				default:
					break;
			}
			
			return passes;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	@Root(name="tagReq")
	public static class TagRequirement
	{
		@Attribute(name="id")
		protected String m_elementID;

		@Attribute(name="operator")
		protected ListRestriction m_operator;


		public TagRequirement(
				@Attribute(name="id") String id,
				@Attribute(name="operator") ListRestriction operator)
		{
			m_elementID = id;
			m_operator = operator;
		}
		
		public String getID() { return m_elementID; }
		public ListRestriction getOperator() { return m_operator; }
		
		
		boolean passes(StoryState storyState)
		{
			return (m_operator == ListRestriction.contains) ?
						storyState.taggedWithElement(m_elementID) :
						!storyState.taggedWithElement(m_elementID);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	@Root(name="sceneReq")
	public static class SceneRequirement
	{
		@Attribute(name="name") //TODO: This should be id rather than name (requires updating a lot of XML)
		protected String m_sceneID;

		@Attribute(name="operator")
		protected SceneRestriction m_operator;


		public SceneRequirement(
				@Attribute(name="name") String sceneID,
				@Attribute(name="operator") SceneRestriction operator)
		{
			m_sceneID = sceneID;
			m_operator = operator;
		}
		
		public String getSceneID() { return m_sceneID; }
		public SceneRestriction getOperator() { return m_operator; }
		
		
		boolean passes(StoryState storyState)
		{
			return (m_operator == SceneRestriction.seen) ?
					storyState.haveSeenScene(m_sceneID) :
					!storyState.haveSeenScene(m_sceneID);
		}
	}
}
