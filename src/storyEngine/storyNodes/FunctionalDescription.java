package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;

import storyEngine.storyNodes.elements.ElementProminence;
import storyEngine.storyNodes.elements.Terrain;
import storyEngine.storyNodes.elements.Weather;

public class FunctionalDescription
{
	@ElementList(name="elementProminencies", required=false, inline=true)
	protected ArrayList<ElementProminence> m_elementProminencies;
	
	@ElementList(name="terrains", inline=true, required=false)
	protected ArrayList<Terrain> m_terrains;
	
	@ElementList(name="weather", inline=true, required=false)
	protected ArrayList<Weather> m_weather;	
	
	
	public FunctionalDescription()
	{
		this(null, null, null);
	}
	
	public FunctionalDescription(
			ArrayList<ElementProminence> prominencies,
			ArrayList<Terrain> terrains,
			ArrayList<Weather> weather)
	{
		m_elementProminencies = prominencies;
		m_terrains = terrains;
		m_weather = weather;
	}
}
