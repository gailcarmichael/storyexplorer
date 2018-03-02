package game.ui;

import game.StoryExplorerGame;
import processing.core.PConstants;
import processing.core.PImage;

public class MetricsBar
{
	private StoryExplorerApplet m_parent;
	private StoryExplorerGame m_game;
	
	final static int BAR_HEIGHT = 120;
	
	private final int BAR_BACKGROUND_COLOUR;

	private final PImage[] METRIC_ICONS;
	private final int TOTAL_ICON_WIDTH;
	
	private final int METRIC_IMAGE_SPACING = 20;
	
	///////////////////////////
	
	MetricsBar(StoryExplorerApplet parent, StoryExplorerGame game)
	{
		m_parent = parent;
		m_game = game;
		
		BAR_BACKGROUND_COLOUR = m_parent.color(206, 225, 255);
		
		String [] iconFilenames = m_game.getMetricIconFilenames();
		METRIC_ICONS = new PImage[iconFilenames.length];
		
		int totalWidth = 0;
		for (int i=0; i < iconFilenames.length; i++)
		{
			METRIC_ICONS[i] = m_parent.loadImage(iconFilenames[i]);
			METRIC_ICONS[i].resize(0, (int)(0.5*BAR_HEIGHT));
			totalWidth += METRIC_ICONS[i].width + METRIC_IMAGE_SPACING;
		}
		TOTAL_ICON_WIDTH = totalWidth - METRIC_IMAGE_SPACING;
	}
	
	///////////////////////////
	
	void draw()
	{
		m_parent.fill(BAR_BACKGROUND_COLOUR);
		m_parent.noStroke();
		m_parent.rectMode(PConstants.CORNER);
		m_parent.rect(0, 0, m_parent.getWidth(), BAR_HEIGHT);
		
		int iconX = (m_parent.getWidth() / 2) - (TOTAL_ICON_WIDTH / 2);
		int iconY = BAR_HEIGHT/2;
		
		m_parent.imageMode(PConstants.CENTER);
		for (int i=0; i < METRIC_ICONS.length; i++)
		{
			PImage img = METRIC_ICONS[i];
			
			float metricPercent = (float)m_game.getCurrentMetricValue(i) / (float)m_game.getMaxMetricValue(i);
			maskMetricIcon(img, metricPercent);
			
			m_parent.image(img, iconX, iconY);
			iconX += img.width + METRIC_IMAGE_SPACING;
		}
	}
	
	private void maskMetricIcon(PImage iconImage, float percentFull)
	{
		int rowToStartSolid = iconImage.height - (int)(iconImage.height * percentFull); 
		iconImage.loadPixels();
		
		for (int i=0; i < iconImage.pixels.length; i++)
		{
			int rowNum = i / iconImage.height;
			
			if(m_parent.alpha(iconImage.pixels[i]) > 10 && rowNum < rowToStartSolid)
			{
				float r = m_parent.red(iconImage.pixels[i]);
				float g = m_parent.green(iconImage.pixels[i]);
				float b = m_parent.blue(iconImage.pixels[i]);
				
				iconImage.pixels[i] = m_parent.color(r,g,b,40);
			}
		}
		
		iconImage.updatePixels();
	}
}
