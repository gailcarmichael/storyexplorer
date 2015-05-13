package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

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
		sceneSeen,
		sceneNotSeen
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
	}

	//////////////////////////////////////////////////////////////////////////////////////

	@Root(name="sceneReq")
	public static class SceneRequirement
	{
		@Attribute(name="name")
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
	}
}
