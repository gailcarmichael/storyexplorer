package game.ui;

import game.StoryExplorerGame;
import processing.core.PApplet;
import processing.core.PFont;

public class SceneUI
{
	private StoryExplorerApplet m_parent;
	private StoryExplorerGame m_game;
	
	private final int MAIN_BOX_PADDING = 25;
	private final int MAIN_BOX_FILL_COLOUR;
	private final int MAIN_BOX_STROKE_COLOUR;
	private final int MAIN_BOX_STROKE_WEIGHT = 2;
	
	private final PFont SCENE_TEXT_FONT;
	
	///////////////////////////
	
	SceneUI(StoryExplorerApplet parent, StoryExplorerGame game)
	{
		m_parent = parent;
		m_game = game;
		
		MAIN_BOX_FILL_COLOUR = m_parent.color(244, 250, 255);
		MAIN_BOX_STROKE_COLOUR = m_parent.color(204, 207, 209);
		
		SCENE_TEXT_FONT = m_parent.loadFont("AvenirNext-Medium-24.vlw");
	}
	
	///////////////////////////
	
	void draw()
	{
		if (!m_game.displayAScene()) return;
		
		int boxX = MAIN_BOX_PADDING;
		int boxY = MetricsBar.BAR_HEIGHT + MAIN_BOX_PADDING;
		int boxWidth = m_parent.width - 2*MAIN_BOX_PADDING;
		int boxHeight = m_parent.height- MetricsBar.BAR_HEIGHT - KernelsBar.BAR_HEIGHT - 2*MAIN_BOX_PADDING;
		
		////
		// Background box
		
		m_parent.stroke(MAIN_BOX_STROKE_COLOUR);
		m_parent.strokeWeight(MAIN_BOX_STROKE_WEIGHT);
		
		m_parent.fill(MAIN_BOX_FILL_COLOUR);
		
		m_parent.rectMode(PApplet.CORNER);
		m_parent.rect(boxX, boxY, boxWidth, boxHeight);

		////
		// Event text
		
		m_parent.fill(0);
		m_parent.textAlign(PApplet.CENTER, PApplet.CENTER);
		m_parent.textFont(SCENE_TEXT_FONT);
		m_parent.text(m_game.getCurrentSceneText(), boxX, boxY, boxWidth, boxHeight);
	}
	
	///////////////////////////
	
	void mouseClicked()
	{
		if (!m_game.displayAScene()) return;
	
		m_game.finishConsumingScene();
	}
}
