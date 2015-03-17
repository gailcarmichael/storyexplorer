package storyEngine.storyNodes;

import java.util.ArrayList;

import org.simpleframework.xml.ElementList;

import storyEngine.storyNodes.elements.ElementProminence;
import storyEngine.storyNodes.elements.Terrain;
import storyEngine.storyNodes.elements.Weather;

public class FunctionalDescription
{
	@ElementList(required=false, inline=true)
	protected ArrayList<ElementProminence> m_elementProminencies;
	
	@ElementList(name="terrains", inline=true, required=false)
	protected ArrayList<Terrain> m_terrains;
	
	@ElementList(name="weather", inline=true, required=false)
	protected ArrayList<Weather> m_weather;	
	
	
	public FunctionalDescription()
	{
		m_elementProminencies = new ArrayList<ElementProminence>();
		m_terrains = new ArrayList<Terrain>();
		m_weather = new ArrayList<Weather>();
	}
	
	public FunctionalDescription(
			ArrayList<ElementProminence> prominencies,
			ArrayList<Terrain> terrains,
			ArrayList<Weather> weather)
	{
		this();
		m_elementProminencies.addAll(prominencies);
		m_terrains.addAll(terrains);
		m_weather.addAll(weather);
	}
	
	public void add(ElementProminence ep)
	{
		if (ep != null)
		{
			m_elementProminencies.add(ep);
		}
	}
	
	public void add(Terrain t)
	{
		if (t != null)
		{
			m_terrains.add(t);
		}
	}
	
	public void add(Weather w)
	{
		if (w != null)
		{
			m_weather.add(w);
		}
	}
}
