package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

import storyEngine.storyNodes.elements.StoryElement;
import storyEngine.storyNodes.elements.Terrain;
import storyEngine.storyNodes.elements.Weather;

// Each story node can have zero or one prerequisites. A
// prerequisite contains a collection of requirements, each
// of which must pass for the prerequisite to pass.
public class Prerequisite
{
	
	@ElementList(inline=true, required=false)
	protected ArrayList<ElementRequirement> m_elementRequirements;
	
	@ElementList(inline=true, required=false)
	protected ArrayList<TerrainRequirement> m_terrainRequirements;
	
	@ElementList(inline=true, required=false)
	protected ArrayList<WeatherRequirement> m_weatherRequirements;
	
	@ElementList(inline=true, required=false)
	protected ArrayList<SceneRequirement> m_sceneRequirements;
	
	
	public Prerequisite()
	{
		m_elementRequirements = new ArrayList<ElementRequirement>();
		m_terrainRequirements = new ArrayList<TerrainRequirement>();
		m_weatherRequirements = new ArrayList<WeatherRequirement>();
		m_sceneRequirements = new ArrayList<SceneRequirement>();
	}
	
	public Prerequisite(
			ArrayList<ElementRequirement> elementRequirements,
			ArrayList<TerrainRequirement> terrainRequirements,
			ArrayList<WeatherRequirement> weatherRequirements,
			ArrayList<SceneRequirement> sceneRequirements)
	{
		this();
		if (elementRequirements != null) m_elementRequirements.addAll(elementRequirements);
		if (terrainRequirements != null) m_terrainRequirements.addAll(terrainRequirements);
		if (weatherRequirements != null) m_weatherRequirements.addAll(weatherRequirements);
		if (sceneRequirements != null) m_sceneRequirements.addAll(sceneRequirements);
	}
	
	
	public void add(ElementRequirement r)
	{
		m_elementRequirements.add(r);
	}
	
	public void add(TerrainRequirement r)
	{
		m_terrainRequirements.add(r);
	}
	
	public void add(WeatherRequirement r)
	{
		m_weatherRequirements.add(r);
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

	public static class ElementRequirement
	{
		@Attribute(name="name")
		protected StoryElement m_element;

		@Attribute(name="specifier", required=false)
		protected String m_name;

		@Attribute(name="operator")
		protected BinaryRestriction m_operator;

		@Attribute(name="compareTo")
		protected int m_compareTo;


		public ElementRequirement(
				@Attribute(name="name") StoryElement element,
				@Attribute(name="specifier", required=false) String name,
				@Attribute(name="operator") BinaryRestriction operator,
				@Attribute(name="compareTo") int compareTo)
		{
			m_element = element;
			m_name = name;
			m_operator = operator;
			m_compareTo = compareTo;
		}

		public ElementRequirement(
				@Attribute(name="name") StoryElement element,
				@Attribute(name="operator") BinaryRestriction operator,
				@Attribute(name="compareTo") int compareTo)
		{
			this(element, null, operator, compareTo);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	public static class TerrainRequirement
	{
		@Attribute(name="name")
		protected Terrain m_terrain;

		@Attribute(name="operator")
		protected ListRestriction m_operator;


		public TerrainRequirement(
				@Attribute(name="name") Terrain terrain,
				@Attribute(name="operator") ListRestriction operator)
		{
			m_terrain = terrain;
			m_operator = operator;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	public static class WeatherRequirement
	{
		@Attribute(name="name")
		protected Weather m_weather;

		@Attribute(name="operator")
		protected ListRestriction m_operator;


		public WeatherRequirement(
				@Attribute(name="name") Weather weather,
				@Attribute(name="operator") ListRestriction operator)
		{
			m_weather = weather;
			m_operator = operator;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

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
