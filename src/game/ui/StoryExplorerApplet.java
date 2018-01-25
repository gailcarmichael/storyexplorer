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
	
	///////////////////////////////////////////////////////////////////////////
	
	private int calculateWidth() { return 1200; }
	private int calculateHeight() { return 800; }
	
	public void setup()
	{
		// TODO: Set this up to load a specified story instead of test data
		Story story = Story.loadStoryFromFile("./testData/testElementSpacing/testSpacing.xml", "./testData/testElementSpacing/testSpacingElementCol.xml");
		m_game = new StoryExplorerGame(story);
		
		size(calculateWidth(), calculateHeight());
		
		m_metricsBar = new MetricsBar(this, m_game);
	}
	
	public void draw()
	{
		background(255);
		
		m_metricsBar.draw();
	}
	
	public void mouseClicked()
	{
		
	}
	
	///////////////////////////////////////////////////////////////////////////
	

	public static void main(String[] args) 
	{
		PApplet.main(StoryExplorerApplet.class.getCanonicalName());
	}

}
