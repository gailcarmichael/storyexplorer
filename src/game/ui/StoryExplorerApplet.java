package game.ui;

import game.AccidentalProphetGame;
import game.StoryExplorerGame;
import processing.core.PApplet;

public class StoryExplorerApplet extends PApplet
{
	private static final long serialVersionUID = 8907928995124251242L;
	
	///////////////////////////////////////////////////////////////////////////
	
	// Model
	private StoryExplorerGame m_game;
	
	// UI Stuff
	private MetricsBar m_metricsBar;
	private KernelsBar m_kernelsBar;
	private SatelliteChoices m_satelliteChoices;
	private SceneUI m_sceneUI;
	
	///////////////////////////////////////////////////////////////////////////
	
	private int calculateWidth() { return 1200; }
	private int calculateHeight() { return 800; }
	
	public void setup()
	{		
		m_game = new AccidentalProphetGame();
		
		size(calculateWidth(), calculateHeight());
		
		m_sceneUI = new SceneUI(this, m_game);
		
		m_metricsBar = new MetricsBar(this, m_game);
		m_kernelsBar = new KernelsBar(this, m_sceneUI, m_game);
		m_satelliteChoices = new SatelliteChoices(this, m_sceneUI, m_game);
	}
	
	public void draw()
	{
		background(255);
		
		m_metricsBar.draw();
		m_kernelsBar.draw();
		
		m_satelliteChoices.draw();
		
		m_sceneUI.draw();
	}
	
	public void mouseClicked()
	{
		boolean handledSceneClick = m_sceneUI.mouseClicked();
		
		if (!handledSceneClick) handledSceneClick = m_kernelsBar.mouseClicked();
		if (!handledSceneClick) handledSceneClick = m_satelliteChoices.mouseClicked();
	}
	
	public void mouseMoved()
	{
		m_kernelsBar.mouseMoved();
		m_satelliteChoices.mouseMoved();
	}
	
	///////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) 
	{
		PApplet.main(StoryExplorerApplet.class.getCanonicalName());
	}

}
