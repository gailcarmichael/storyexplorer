package game.ui;

import java.util.ArrayList;

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
	
	private static final int CHOICE_BUTTON_WIDTH_MIN = 150;
	private static final int CHOICE_BUTTON_WIDTH_MAX = 200;
	private static final int CHOICE_BUTTON_HEIGHT = 100;
	private static final int CHOICE_BUTTON_SPACE_BETWEEN = 20;
	
	private final PFont CHOICE_TEXT_FONT;
	
	private final int CHOICE_BUTTON_FILL;
	private final int CHOICE_BUTTON_HOVER_FILL;
	
	private ArrayList<ChoiceButton> m_choiceButtons;
	
	///////////////////////////
	
	protected class ChoiceButton
	{
		String m_text;
		int m_x, m_y;
		int m_width, m_height;
		
		ChoiceButton(String text, int x, int y, int width, int height)
		{
			m_text = text;
			m_x = x;
			m_y = y;
			m_width = width;
			m_height = height;
		}
		
		boolean pointInside(int pointX, int pointY)
		{
			return (pointX >= m_x && pointX <= m_x + m_width &&
					pointY >= m_y && pointY <= m_y + m_height);
		}
	}
	
	///////////////////////////
	
	SceneUI(StoryExplorerApplet parent, StoryExplorerGame game)
	{
		m_parent = parent;
		m_game = game;
		
		MAIN_BOX_FILL_COLOUR = m_parent.color(244, 250, 255);
		MAIN_BOX_STROKE_COLOUR = m_parent.color(204, 207, 209);
		
		SCENE_TEXT_FONT = m_parent.loadFont("AvenirNext-Medium-24.vlw");
		
		CHOICE_BUTTON_FILL = m_parent.color(255, 248, 188);
		CHOICE_BUTTON_HOVER_FILL = m_parent.color(255, 244, 150);
		
		CHOICE_TEXT_FONT = m_parent.loadFont("AvenirNext-Medium-14.vlw");
		
		setupChoiceButtons();
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
		
		if (m_game.getNumChoicesForCurrentNode() > 0)
		{
			drawEventTextWithChoices(boxX, boxY, boxWidth, boxHeight);
		}
		else
		{
			drawEventText(boxX, boxY, boxWidth, boxHeight);
		}
		
	}
	
	private void drawEventText(int boxX, int boxY, int boxWidth, int boxHeight)
	{
		m_parent.fill(0);
		m_parent.textAlign(PApplet.CENTER, PApplet.CENTER);
		m_parent.textFont(SCENE_TEXT_FONT);
		m_parent.text(m_game.getCurrentSceneText(), boxX, boxY, boxWidth, boxHeight);
	}
	
	private void drawEventTextWithChoices(int boxX, int boxY, int boxWidth, int boxHeight)
	{
		int textBoxHeight = boxHeight - CHOICE_BUTTON_HEIGHT - 2*MAIN_BOX_PADDING;
		drawEventText(boxX, boxY - CHOICE_BUTTON_HEIGHT/2 - MAIN_BOX_PADDING, boxWidth, textBoxHeight);
		
		drawChoiceButtons();
	}
	
	///////////////////////////
	
	private void setupChoiceButtons()
	{
		m_choiceButtons = new ArrayList<ChoiceButton>();
		
		ArrayList<String> choicesText = m_game.getChoicesText();
		int numChoices = choicesText.size();
		
		if (numChoices > 0)
		{
			int textBoxWidth = m_parent.width - (2*MAIN_BOX_PADDING);
			
			float allButtonWidth = textBoxWidth - ((numChoices + 1) * CHOICE_BUTTON_SPACE_BETWEEN);
			float width = allButtonWidth / numChoices;
			width = Math.min(width, CHOICE_BUTTON_WIDTH_MAX);
			width = Math.max(width, CHOICE_BUTTON_WIDTH_MIN);
			
			float allButtonAndSpacesWidth = (width * numChoices) + (CHOICE_BUTTON_SPACE_BETWEEN * (numChoices + 1));
			
			int x = (int)(textBoxWidth/2 - allButtonAndSpacesWidth/2 + CHOICE_BUTTON_SPACE_BETWEEN);
			int y = m_parent.height - KernelsBar.BAR_HEIGHT - 2*MAIN_BOX_PADDING - CHOICE_BUTTON_HEIGHT;
			
			for (int i=0; i < numChoices; i++)
			{
				ChoiceButton newB = new ChoiceButton(
						choicesText.get(i),
						x, y, 
						(int)width, CHOICE_BUTTON_HEIGHT);
				
				m_choiceButtons.add(newB);
				
				x += width + CHOICE_BUTTON_SPACE_BETWEEN;
			}
		}
	}
	
	private void drawChoiceButtons()
	{
		if (m_game.getNumChoicesForCurrentNode() > 0)
		{
			for (ChoiceButton b : m_choiceButtons)
			{
				m_parent.fill(CHOICE_BUTTON_FILL);
				if (b.pointInside(m_parent.mouseX, m_parent.mouseY))
				{
					m_parent.fill(CHOICE_BUTTON_HOVER_FILL);
				}
				
				m_parent.rectMode(PApplet.CORNER);
				m_parent.rect(b.m_x, b.m_y, b.m_width, b.m_height);
				
				if (b.m_text != null)
				{
					String text = b.m_text.replace("\n", "").replace("\r", "");
					
					m_parent.fill(0);
					m_parent.textFont(CHOICE_TEXT_FONT);
					m_parent.text(text, b.m_x, b.m_y, b.m_width * 0.95f, b.m_height * 0.95f);
				}
			}
		}
	}
	
	///////////////////////////
	
	void mouseClicked()
	{
		if (!m_game.displayAScene()) return;
	
		m_game.finishConsumingScene();
	}
}
