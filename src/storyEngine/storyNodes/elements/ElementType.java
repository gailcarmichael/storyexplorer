package storyEngine.storyNodes.elements;
 

public enum ElementType
{
	Quantifiable, // can attach a number to it
	QuantifiableStoryStateOnly, // can only be used in the story state (not with nodes)
	Taggable, // can tag nodes with it
}
