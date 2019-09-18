package storyEngine;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import storyEngine.storyNodes.StoryNode;

public class GlobalRule
{	
	public static enum HowMany { allOf, anyOf }
	public static enum ListTypeForNodes { tags, storyElements, nodeIDs }
	public static enum NodeState { seen, notSeen }
	public static enum TagState { present, notPresent }
	
	@Attribute(name="id", required=false)
	protected String m_id;
	
	//
	
	@Element(name="nodeFilter", required=false)
	protected NodeFilter m_nodeFilter;
	
	@Element(name="storyStateFilter", required=false)
	protected StoryStateFilter m_storyStateFilter;
	
	//
	
	@Element(name="nodesAffected")
	protected NodesAffected m_nodesAffectedFilter;
	
	///////////////////////////////////////////
	
	protected GlobalRule(
			@Attribute(name="id", required=false) String id,
			@Element(name="nodeFilter", required=false) NodeFilter nodeFilter,
			@Element(name="storyStateFilter", required=false) StoryStateFilter storyStateFilter,
			@Element(name="nodesAffected") NodesAffected nodesAffectedFilter)
	{
		m_id = id;
		m_nodeFilter = nodeFilter;
		m_storyStateFilter = storyStateFilter;
		m_nodesAffectedFilter = nodesAffectedFilter;
	}
	
	public GlobalRule(
			@Attribute(name="id", required=false) String id,
			@Element(name="nodeFilter", required=false) NodeFilter nodeFilter,
			@Element(name="nodesAffected") NodesAffected nodesAffectedFilter)
	{
		this(id, nodeFilter, null, nodesAffectedFilter);
	}
	
	public GlobalRule(
			@Attribute(name="id", required=false) String id,
			@Element(name="storyStateFilter", required=false) StoryStateFilter storyStateFilter,
			@Element(name="nodesAffected") NodesAffected nodesAffectedFilter)
	{
		this(id, null, storyStateFilter, nodesAffectedFilter);
	}
	
	public GlobalRule(
			@Element(name="nodeFilter", required=false) NodeFilter nodeFilter,
			@Element(name="nodesAffected") NodesAffected nodesAffectedFilter)
	{
		this(null, nodeFilter, null, nodesAffectedFilter);
	}
	
	public GlobalRule(
			@Element(name="storyStateFilter", required=false) StoryStateFilter storyStateFilter,
			@Element(name="nodesAffected") NodesAffected nodesAffectedFilter)
	{
		this(null, null, storyStateFilter, nodesAffectedFilter);
	}
	
	//
	
	public boolean appliesToNode(StoryNode node)
	{
		return m_nodesAffectedFilter.m_itemList.contains(node.getID());
	}
	
	public boolean passes(StoryState storyState)
	{
		return true;//TODO: global rule passes?
	}
	
	//
	
	public boolean isValid()
	{
		boolean valid = true;
		
		if (m_nodeFilter == null && m_storyStateFilter == null)
		{
			valid = false;
		}
		
		if (m_nodeFilter != null && !m_nodeFilter.isValid())
		{
			valid = false;
		}
		
		if (m_storyStateFilter != null && !m_storyStateFilter.isValid())
		{
			valid = false;
		}
		
		if (m_nodesAffectedFilter == null || !m_nodesAffectedFilter.isValid())
		{
			valid = false;
		}
		
		return valid;
	}
	
	//
	
	public String toString()
	{
		String result = "";
		
		result += "Global rule";
		if (m_id != null && !m_id.isEmpty())
		{
			result += " with id " + m_id;
		}
		result += ":";
		
		if (m_nodeFilter != null)
		{
			result += "\n" + m_nodeFilter;
		}
		else if (m_storyStateFilter != null)
		{
			result += "\n" + m_storyStateFilter;
		}
		
		result += "\n" + m_nodesAffectedFilter;
		
		return result;
	}
	
	///////////////////////////////////////////
	
	public static class NodeFilter
	{	
		@Attribute(name="checkNodesUsing")
		protected ListTypeForNodes m_filterNodeListType;
		
		@Attribute(name="listToCheck")
		protected String m_itemList; // needs to be comma-delimited list
		
		@Attribute(name="howManyRequired")
		protected HowMany m_howManyItemsRequired;
		
		@Attribute(name="nodeState")
		protected NodeState m_nodeState;
		
		public NodeFilter(
				@Attribute(name="checkNodesUsing") ListTypeForNodes filterNodeListType,
				@Attribute(name="listToCheck") String itemList,
				@Attribute(name="howManyRequired") HowMany howManyItemsRequired,
				@Attribute(name="nodeState") NodeState nodeState)
		{
			m_filterNodeListType = filterNodeListType;
			m_itemList = itemList;
			m_howManyItemsRequired = howManyItemsRequired;
			m_nodeState = nodeState;
		}
		
		private String[] items() { return m_itemList.split("[, ]+"); }
		
		private boolean itemPresent(StoryState storyState, String item)
		{
			switch(m_filterNodeListType)
			{
				case tags:
				case storyElements:
					return storyState.haveSeenSceneWithFeature(item);
				case nodeIDs:
					return storyState.haveSeenScene(item);
				default:
					return false;
			}
		}
		
		public boolean passes(StoryState storyState)
		{
			boolean passes = false;
			
			if (m_howManyItemsRequired == HowMany.allOf)
			{
				passes = true;
				for (String item : items())
				{
					boolean itemPresent = itemPresent(storyState, item);
					if ((m_nodeState == NodeState.seen && !itemPresent) ||
						(m_nodeState == NodeState.notSeen && itemPresent))
					{
						passes = false;
						break;
					}
				}
			}
			
			else if (m_howManyItemsRequired == HowMany.anyOf)
			{
				passes = false;
				for (String item : items())
				{
					boolean itemPresent = itemPresent(storyState, item);
					if ((m_nodeState == NodeState.seen && itemPresent) ||
						(m_nodeState == NodeState.notSeen && !itemPresent))
					{
						passes = true;
						break;
					}
				}
			}
			
			return passes;
		}
		
		public boolean isValid()
		{
			return items().length > 0;
		}
		
		public String toString()
		{
			return "\tChecking whether " + m_howManyItemsRequired
					+ " list of type " + m_filterNodeListType 
					+ " has been " + m_nodeState + ": "
					+ m_itemList.replaceAll(",", " ");
		}
	}
	
	//
	
	public static class StoryStateFilter
	{
		@Attribute(name="tagsToCheck")
		protected String m_tagList; // needs to be comma-delimited list
		
		@Attribute(name="howManyRequired")
		protected HowMany m_howManyItemsRequired;
		
		@Attribute(name="tagState")
		protected TagState m_tagState;
		
		public StoryStateFilter(
				@Attribute(name="tagsToCheck")  String tagList,
				@Attribute(name="howManyRequired") HowMany howManyItemsRequired,
				@Attribute(name="tagState") TagState tagState)
		{
			m_tagList = tagList;
			m_howManyItemsRequired = howManyItemsRequired;
			m_tagState = tagState;
		}
		
		private String[] tags() { return m_tagList.split("[, ]+"); }
		
		public boolean passes(StoryState storyState)
		{
			boolean passes = false;
			
			if (m_howManyItemsRequired == HowMany.allOf)
			{
				passes = true;
				for (String item : tags())
				{
					boolean itemPresent = storyState.taggedWithElement(item);
					if ((m_tagState == TagState.present && !itemPresent) ||
						(m_tagState == TagState.notPresent && itemPresent))
					{
						passes = false;
						break;
					}
				}
			}
			
			else if (m_howManyItemsRequired == HowMany.anyOf)
			{
				passes = false;
				for (String item : tags())
				{
					boolean itemPresent = storyState.taggedWithElement(item);
					if ((m_tagState == TagState.present && itemPresent) ||
						(m_tagState == TagState.notPresent && !itemPresent))
					{
						passes = true;
						break;
					}
				}
			}
			
			return passes;
		}
		
		public boolean isValid()
		{
			return m_tagList.split(",").length > 0;
		}
		
		public String toString()
		{
			return "\tChecking whether " + m_howManyItemsRequired
					+ " tags are " + m_tagState + " in story state: "
					+ m_tagList.replaceAll(",", " ");
		}
	}
	
	// 
	
	public static class NodesAffected
	{	
		@Attribute(name="checkNodesUsing")
		protected ListTypeForNodes m_filterNodeListType;
		
		@Attribute(name="listToCheck")
		protected String m_itemList; // needs to be comma-delimited list
		
		@Attribute(name="howManyRequired")
		protected HowMany m_howManyItemsRequired;
		
		public NodesAffected(
				@Attribute(name="checkNodesUsing") ListTypeForNodes filterNodeListType,
				@Attribute(name="listToCheck") String itemList,
				@Attribute(name="howManyRequired") HowMany howManyItemsRequired)
		{
			m_filterNodeListType = filterNodeListType;
			m_itemList = itemList;
			m_howManyItemsRequired = howManyItemsRequired;
		}
		
		public boolean isValid()
		{
			return m_itemList.split(",").length > 0;
		}
		
		public String toString()
		{
			return "\tNodes the rule applies to include those that have " + m_howManyItemsRequired
					+ " the list of type " + m_filterNodeListType + ": "
					+ m_itemList.replaceAll(",", " ");
		}
	}
}
