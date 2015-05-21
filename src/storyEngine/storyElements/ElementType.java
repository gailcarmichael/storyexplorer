package storyEngine.storyElements;
 

public enum ElementType
{
	quantifiable, // can attach a number to it
	quantifiableStoryStateOnly, // can only be used in the story state (not with nodes)
	taggable, // can tag nodes with it
}
