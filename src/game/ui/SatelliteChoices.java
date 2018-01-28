package game.ui;

import java.util.ArrayList;

import game.StoryExplorerGame;
import processing.core.PApplet;
import processing.core.PFont;

public class SatelliteChoices
{
	private StoryExplorerApplet m_parent;
	private SceneUI m_sceneUI;
	private StoryExplorerGame m_game;
	
	private int m_buttonHoverIndex;
	private SatelliteButton[] m_buttons;
	
	private final int BUTTON_PADDING = 50;
	private final int BUTTON_HEIGHT = 100;
	
	private final int BUTTON_FILL_COLOUR;
	private final int BUTTON_HOVER_COLOUR;
	private final int BUTTON_STROKE_COLOUR;
	private final int BUTTON_STROKE_WEIGHT = 2;
	
	private final PFont BUTTON_FONT;
	private final int BUTTON_TEXT_PADDING = 20;
	
	///////////////////////////
	
	private class SatelliteButton
	{
		int x, y, width, height;
		public SatelliteButton(int new_x, int new_y, int new_width, int new_height)
		{
			x = new_x;
			y = new_y;
			width = new_width;
			height = new_height;
		}
	}
	
	///////////////////////////
	
	SatelliteChoices(StoryExplorerApplet parent, SceneUI sceneUI, StoryExplorerGame game)
	{
		m_parent = parent;
		m_sceneUI = sceneUI;
		m_game = game;
		
		BUTTON_HOVER_COLOUR = m_parent.color(157, 232, 159);
		BUTTON_FILL_COLOUR = m_parent.color(209, 249, 210);
		BUTTON_STROKE_COLOUR = m_parent.color(0);
		
		BUTTON_FONT = m_parent.loadFont("AvenirNext-Medium-24.vlw");
		
		m_buttons = new SatelliteButton[m_game.getNumSatellitesToShow()];
		
		setupButtons();
	}
	
	///////////////////////////
	
	private void setupButtons()
	{
		int leftX = BUTTON_PADDING;
		int rightX = m_parent.width - BUTTON_PADDING;
		
		int bottomY = MetricsBar.BAR_HEIGHT + BUTTON_PADDING;
		int topY = m_parent.height - KernelsBar.BAR_HEIGHT - BUTTON_PADDING;
		
		// (topY - bottomY) = numButtons*buttonHeight + (numButtons-1)*(spaceBetween)
		
		int numButtons = m_game.getNumSatellitesToShow();
		int spaceBetween = ((topY-bottomY) - (numButtons*BUTTON_HEIGHT)) / (numButtons-1);
		int buttonWidth = (rightX-leftX);
		
		int currentY = bottomY;
		for (int buttonNum=0; buttonNum < numButtons; buttonNum++)
		{
			m_buttons[buttonNum] = new SatelliteButton(leftX, currentY, buttonWidth, BUTTON_HEIGHT);
			currentY += BUTTON_HEIGHT + spaceBetween;
		}
	}
	
	///////////////////////////
	
	void draw()
	{
		if (m_game.isDisplayingAScene()) return;
		
		m_parent.rectMode(PApplet.CORNER);
		
		m_parent.textFont(BUTTON_FONT);
		m_parent.textAlign(PApplet.CENTER, PApplet.CENTER);
		
		ArrayList<String> teaserText = m_game.getSatellitesTeaserText();
		int numButtons = Math.min(teaserText.size(), m_buttons.length);
		
		for (int buttonNum=0; buttonNum < numButtons; buttonNum++)
		{
			SatelliteButton button = m_buttons[buttonNum];
			
			///
			// Button
			
			if (buttonNum == m_buttonHoverIndex)
			{
				m_parent.fill(BUTTON_FILL_COLOUR);
			}
			else
			{
				m_parent.fill(BUTTON_HOVER_COLOUR);
			}
			
			m_parent.stroke(BUTTON_STROKE_COLOUR);
			m_parent.strokeWeight(BUTTON_STROKE_WEIGHT);
			
			m_parent.rect(button.x, button.y, button.width, button.height, 7);
			
			///
			// Button text

			m_parent.fill(0);
			
			m_parent.text(teaserText.get(buttonNum),
					      button.x + BUTTON_TEXT_PADDING, button.y + BUTTON_TEXT_PADDING, 
					      button.width - 2*BUTTON_TEXT_PADDING, button.height - 2*BUTTON_TEXT_PADDING);

		}
		
		
	}
	
	///////////////////////////
	
	void mouseMoved()
	{
		m_buttonHoverIndex = -1;
		
		if (m_game.isDisplayingAScene()) return;
		
		for (int i=0; i < m_buttons.length; i++)
		{
			SatelliteButton b = m_buttons[i];
			
			if (m_parent.mouseX >= b.x &&
			    m_parent.mouseX <= b.x + b.width&&
			    m_parent.mouseY >= b.y &&
			    m_parent.mouseY <= b.y + b.height)
			{
				m_buttonHoverIndex = i;
				break;
			}
		}
	}
	
	///////////////////////////
	
	boolean mouseClicked()
	{
		if (m_game.isDisplayingAScene()) return false;
		
		for (int i=0; i < m_buttons.length; i++)
		{
			SatelliteButton b = m_buttons[i];
			
			if (m_parent.mouseX >= b.x &&
			    m_parent.mouseX <= b.x + b.width&&
			    m_parent.mouseY >= b.y &&
			    m_parent.mouseY <= b.y + b.height)
			{
				m_game.consumeSatellite(i);
				m_sceneUI.prepareForNewScene();
				return true;
			}
		}
		
		return false;
	}
	
	///////////////////////////
}
