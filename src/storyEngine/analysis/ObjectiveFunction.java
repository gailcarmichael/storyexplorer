package storyEngine.analysis;

import storyEngine.Story;

public abstract class ObjectiveFunction
{	
	
	public static enum Type
	{
		EvenSpacing,
		MemoryModel
	}	
	
	protected Story m_story;
	
	public ObjectiveFunction(Story story)
	{
		m_story = story;
	}
	
	public abstract float objectiveFunctionForStory();
	
	public static ObjectiveFunction forType(Type type, Story story)
	{
		ObjectiveFunction f = null;
		
		switch (type)
		{
			case EvenSpacing:
				f = new ObjectiveFunctionForEvenSpacing(story);
				break;
			case MemoryModel:
				f = new ObjectiveFunctionForMemoryModel(story);
				break;
		}
				
		return f;
	}
}
