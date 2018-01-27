package game.ui;

import game.StoryExplorerGame;
import processing.core.PApplet;
import storyEngine.Story;

public class StoryExplorerApplet extends PApplet
{
	private static final long serialVersionUID = 8907928995124251242L;
	
	///////////////////////////////////////////////////////////////////////////
	
	// Model
	private StoryExplorerGame m_game;
	
	// UI Stuff
	private MetricsBar m_metricsBar;
	private KernelsBar m_kernelsBar;
	private SceneUI m_sceneUI;
	
	///////////////////////////////////////////////////////////////////////////
	
	private int calculateWidth() { return 1200; }
	private int calculateHeight() { return 800; }
	
	public void setup()
	{
		// TODO: Set this up to load a specified story instead of test data
		Story story = Story.loadStoryFromFile("./testData/testSimpleXML/testSimple.xml", "./testData/testSimpleXML/testStoryElements.xml");
		m_game = new StoryExplorerGame(story);
		
		size(calculateWidth(), calculateHeight());
		
		m_metricsBar = new MetricsBar(this, m_game);
		m_kernelsBar = new KernelsBar(this, m_game);
		m_sceneUI = new SceneUI(this, m_game);
	}
	
	public void draw()
	{
		background(255);
		
		m_metricsBar.draw();
		m_kernelsBar.draw();
		m_sceneUI.draw();
	}
	
	public void mouseClicked()
	{
		m_sceneUI.mouseClicked();
		m_kernelsBar.mouseClicked();
	}
	
	public void mouseMoved()
	{
		m_kernelsBar.mouseMoved();
	}
	
	///////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) 
	{
		PApplet.main(StoryExplorerApplet.class.getCanonicalName());
	}

}
