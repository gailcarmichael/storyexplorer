package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Text;

import storyEngine.storyNodes.elements.StoryElement;
import storyEngine.storyNodes.elements.Terrain;
import storyEngine.storyNodes.elements.Weather;

// An outcome specifies what happens after a choice is made in 
// a story node.  There is one outcome per choice.  Modifiers in
// an outcome change the story's state in some way.
public class Outcome
{
	@Element(name="text")
	protected String m_outcomeText;

	@ElementList(inline=true, required=false)
	protected ArrayList<ElementModifier> m_elementModifiers;

	@ElementList(inline=true, required=false)
	protected ArrayList<TerrainModifier> m_terrainModifiers;

	@ElementList(inline=true, required=false)
	protected ArrayList<WeatherModifier> m_weatherModifiers;


	public Outcome(String outcomeText)
	{
		m_outcomeText = outcomeText;
		m_elementModifiers = new ArrayList<ElementModifier>();
		m_terrainModifiers = new ArrayList<TerrainModifier>();
		m_weatherModifiers = new ArrayList<WeatherModifier>();		
	}

	public Outcome(
			@Element(name="text") String outcomeText,
			@ElementList(inline=true, required=false) ArrayList<ElementModifier> elementModifiers,
			@ElementList(inline=true, required=false) ArrayList<TerrainModifier> terrainModifiers,
			@ElementList(inline=true, required=false) ArrayList<WeatherModifier> weatherModifiers)
	{
		this(outcomeText);
		if (elementModifiers != null) m_elementModifiers.addAll(elementModifiers);
		if (terrainModifiers != null) m_terrainModifiers.addAll(terrainModifiers);
		if (weatherModifiers != null) m_weatherModifiers.addAll(weatherModifiers);
	}


	public void add(ElementModifier m)
	{
		if (m != null)
		{
			m_elementModifiers.add(m);
		}
	}

	public void add(WeatherModifier m)
	{
		if (m != null)
		{
			m_weatherModifiers.add(m);
		}
	}

	public void add(TerrainModifier m)
	{
		if (m != null)
		{
			m_terrainModifiers.add(m);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////
	
	public static enum Action
	{
		add,
		remove
	}

	//////////////////////////////////////////////////////////////////////////////////////

	static public class ElementModifier
	{
		@Attribute(name="name")
		protected StoryElement m_element;

		@Attribute(name="specifier", required=false)
		protected String m_name;

		@Attribute(name="absolute")
		protected boolean m_absolute;

		@Text
		protected int m_delta;


		public ElementModifier(
				@Attribute(name="name") StoryElement element, 
				@Attribute(name="specifier") String name,
				@Attribute(name="absolute") boolean absolute,
				@Text int delta)
		{
			m_element = element;
			m_name = name;
			m_absolute = absolute;
			m_delta = delta;
		}

		public ElementModifier(
				@Attribute(name="name") StoryElement element, 
				@Text int prominence,
				@Attribute(name="absolute") boolean absolute)
		{
			this(element, null, absolute, prominence);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	static public class TerrainModifier
	{
		@Attribute(name="name")
		protected Terrain m_terrain;

		@Text
		protected Action m_action;


		public TerrainModifier(
				@Attribute(name="name") Terrain terrain,
				@Text Action action)
		{
			m_terrain = terrain;
			m_action = action;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////

	static public class WeatherModifier
	{
		@Attribute(name="name")
		protected Weather m_weather;

		@Text
		protected Action m_action;


		public WeatherModifier(
				@Attribute(name="name") Weather weather,
				@Text Action action)
		{
			m_weather = weather;
			m_action = action;
		}
	}
}