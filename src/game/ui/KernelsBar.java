package game.ui;

import game.StoryExplorerGame;
import processing.core.PConstants;

public class KernelsBar
{
	private StoryExplorerApplet m_parent;
	private StoryExplorerGame m_game;
	
	final static int BAR_HEIGHT = 50;
	
	private final int BAR_BACKGROUND_COLOUR;
	
	private final int CIRCLE_SPACING = 15;
	private final int CIRCLE_SIZE = BAR_HEIGHT-CIRCLE_SPACING*2;
	
	private final int CIRCLE_UNCONSUMED_FILL_COLOUR;
	private final int CIRCLE_CONSUMED_FILL_COLOUR;
	private final int CIRCLE_NEXT_FILL_COLOUR;
	
	private final int CIRCLE_STROKE_COLOUR;
	private final int CIRCLE_CONSUMING_STROKE_COLOUR;
	private final int CIRCLE_STROKE_WEIGHT = 2;
	private final int CIRCLE_HOVER_STROKE_WEIGHT = 3;
	
	private int m_circleHoverIndex;
	private int m_circleConsumingIndex;
	
	///////////////////////////
	
	protected class KernelCircle
	{
		int x, y;
		KernelCircle(int new_x, int new_y)
		{
			x = new_x;
			y = new_y;
		}
	}
	private final KernelCircle[] KERNEL_CIRCLES;
	
	///////////////////////////
	
	KernelsBar(StoryExplorerApplet parent, StoryExplorerGame game)
	{
		m_parent = parent;
		m_game = game;
		
		BAR_BACKGROUND_COLOUR = m_parent.color(245);
		
		CIRCLE_UNCONSUMED_FILL_COLOUR = m_parent.color(228, 211, 255);
		CIRCLE_CONSUMED_FILL_COLOUR = m_parent.color(152, 105, 229);
		CIRCLE_NEXT_FILL_COLOUR = m_parent.color(255);
		
		CIRCLE_STROKE_COLOUR = m_parent.color(0);
		CIRCLE_CONSUMING_STROKE_COLOUR = m_parent.color(152, 105, 229);
		
		int numCircles = m_game.getNumKernels();
		
		int allCirclesWidth = numCircles*CIRCLE_SIZE + (numCircles-1)*CIRCLE_SPACING;
		int startX = m_parent.width/2 - allCirclesWidth/2;
		int startY = m_parent.height - BAR_HEIGHT + CIRCLE_SPACING + CIRCLE_SIZE/2;
		
		KERNEL_CIRCLES = new KernelCircle[numCircles];
		for (int i=0; i < KERNEL_CIRCLES.length; i++)
		{
			KERNEL_CIRCLES[i] = new KernelCircle(startX, startY);
			startX += CIRCLE_SIZE + CIRCLE_SPACING;
		}
		
		m_circleHoverIndex = -1;
		m_circleConsumingIndex = -1;
	}
	
	///////////////////////////
	
	void draw()
	{
		m_parent.fill(BAR_BACKGROUND_COLOUR);
		m_parent.noStroke();
		m_parent.rectMode(PConstants.CORNER);
		m_parent.rect(0, m_parent.getHeight()-BAR_HEIGHT, m_parent.getWidth(), m_parent.getHeight());
		
		for (int i=0; i < KERNEL_CIRCLES.length; i++)
		{
			KernelCircle c = KERNEL_CIRCLES[i];
			
			if (i < m_game.getNumKernelsConsumed())
			{
				m_parent.fill(CIRCLE_CONSUMED_FILL_COLOUR);
			}
			else if (i == m_game.getNumKernelsConsumed())
			{
				m_parent.fill(CIRCLE_NEXT_FILL_COLOUR);
			}
			else
			{
				m_parent.fill(CIRCLE_UNCONSUMED_FILL_COLOUR);
			}
			
			if (m_circleHoverIndex == i)
			{
				m_parent.strokeWeight(CIRCLE_HOVER_STROKE_WEIGHT);
			}
			else
			{
				m_parent.strokeWeight(CIRCLE_STROKE_WEIGHT);
			}
			
			if (i == m_circleConsumingIndex)
			{
				m_parent.stroke(CIRCLE_CONSUMING_STROKE_COLOUR);
			}
			else
			{
				m_parent.stroke(CIRCLE_STROKE_COLOUR);
			}
			
			m_parent.ellipse(c.x, c.y, CIRCLE_SIZE, CIRCLE_SIZE);
		}
	}
	
	///////////////////////////
	
	void mouseMoved()
	{		
		m_circleHoverIndex = -1;
		
		if (m_parent.mouseY < m_parent.getHeight() - BAR_HEIGHT) return;
		
		for (int i=0; i < KERNEL_CIRCLES.length; i++)
		{
			KernelCircle c = KERNEL_CIRCLES[i];
			
			if (m_parent.mouseX >= c.x - CIRCLE_SIZE/2 &&
			    m_parent.mouseX <= c.x + CIRCLE_SIZE/2 &&
			    m_parent.mouseY >= c.y - CIRCLE_SIZE/2 &&
			    m_parent.mouseY <= c.y + CIRCLE_SIZE/2)
			{
				if (i == m_game.getNumKernelsConsumed())
				{
					m_circleHoverIndex = i;
					break;
				}
			}
		}
	}
	
	///////////////////////////
	
	void mouseClicked()
	{
		if (m_parent.mouseY < m_parent.getHeight() - BAR_HEIGHT) return;
		if (!m_game.canChooseAKernel()) return;
		
		for (int i=0; i < KERNEL_CIRCLES.length; i++)
		{
			KernelCircle c = KERNEL_CIRCLES[i];
			
			if (m_parent.mouseX >= c.x - CIRCLE_SIZE/2 &&
			    m_parent.mouseX <= c.x + CIRCLE_SIZE/2 &&
			    m_parent.mouseY >= c.y - CIRCLE_SIZE/2 &&
			    m_parent.mouseY <= c.y + CIRCLE_SIZE/2)
			{
				if (i == m_game.getNumKernelsConsumed())
				{
					m_game.consumeNextKernel();
					m_circleConsumingIndex = i;
					break;
				}
			}
		}
	}
}
