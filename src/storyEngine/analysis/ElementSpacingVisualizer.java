package storyEngine.analysis;

import java.util.ArrayList;

import processing.core.PApplet;
import storyEngine.Story;
import storyEngine.storyElements.ElementType;
import storyEngine.storyNodes.StoryNode;

// Use Processing to display the spacing between various elements
// by using the 'scenes seen' in the story

public class ElementSpacingVisualizer
{
	
	private Story m_story;
	
	private static final int SPACE_FOR_TEXT = 300;
	
	private static final int HOR_SPACE_BETWEEN = 10;
	private static final int VER_SPACE_BETWEEN = 20;
	
	private static final int NODE_DIAMETER = 16;
	
	private float m_windowHeight;
	private float m_windowWidth;
	
	private boolean m_showMultiElementNodes;
	
	
	public ElementSpacingVisualizer(Story story, boolean showMultiElementNodes)
	{
		m_story = story;
		m_showMultiElementNodes = showMultiElementNodes;
		
		ArrayList<StoryNode> nodes = m_story.getScenesSeen();
		int numElements = m_story.getElementCollection().getNumElementsPriorityCalc();
		
		m_windowWidth = nodes.size() * (NODE_DIAMETER + HOR_SPACE_BETWEEN) + HOR_SPACE_BETWEEN + SPACE_FOR_TEXT;
		
		m_windowHeight = (NODE_DIAMETER + VER_SPACE_BETWEEN) * numElements + VER_SPACE_BETWEEN;
	}
	
	
	///////////////////////////////////////////////////////////////////////////
	
	public Story getStory() { return m_story; }
	
	///////////////////////////////////////////////////////////////////////////

	
	public void setup(PApplet parent)
	{
		parent.size((int)m_windowWidth, (int)m_windowHeight);
		parent.ellipseMode(PApplet.CENTER);
	}
	
	
	public void draw(PApplet parent)
	{
		parent.background(255);
		
		// Draw a new row for each element. Along the row, go node by node. If the node
		// features that element, draw a circle.  Otherwise, skip the space.
		
		int elementNum = 0;
		for (String elementID : m_story.getElementCollection().getIDs())
		{
			if (m_story.getElementCollection().getElementWithID(elementID).getType() != ElementType.quantifiable)
			{
				continue;
			}
			
			float y = (NODE_DIAMETER + VER_SPACE_BETWEEN) * elementNum + VER_SPACE_BETWEEN;
			
			int nodeNum = 0;
			for (StoryNode node : m_story.getScenesSeen())
			{
			
				float x = SPACE_FOR_TEXT + (NODE_DIAMETER + HOR_SPACE_BETWEEN) * nodeNum + HOR_SPACE_BETWEEN + NODE_DIAMETER/2;
				
				if (m_showMultiElementNodes &&
						node.getElementIDs().size() > 1 &&
						elementNum == 0)
				{
					parent.pushStyle();
					parent.strokeWeight(4);
					parent.stroke(0,200,0);
					parent.line(x, 0, x, parent.height);
					parent.popStyle();
				}
				
				parent.noFill();
				parent.stroke(200);
				
				if (node.featuresElement(elementID))
				{
					if (elementNum % 4 == 0) parent.fill(0, 0, 240);
					else if (elementNum % 4 == 1) parent.fill(240,0,0);
					else if (elementNum % 4 == 2) parent.fill(240,240,0);
					else parent.fill(0,240,0);

					parent.noStroke();
					parent.ellipse(x, y, NODE_DIAMETER, NODE_DIAMETER);
					
					parent.noStroke();
				}
				parent.ellipse(x, y, NODE_DIAMETER, NODE_DIAMETER);
				
				parent.stroke(245);
				parent.line(0, y + NODE_DIAMETER/2 + VER_SPACE_BETWEEN/2, 
						m_windowWidth, y + NODE_DIAMETER/2 + VER_SPACE_BETWEEN/2);
				
				nodeNum++;
			}
			
			parent.fill(0);
			parent.textAlign(PApplet.LEFT, PApplet.CENTER);
			parent.textSize(NODE_DIAMETER);
			parent.text(elementID, HOR_SPACE_BETWEEN, y);
			
			elementNum++;
		}
	}
}
