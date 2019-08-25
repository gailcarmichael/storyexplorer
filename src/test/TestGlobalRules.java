package test;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import storyEngine.GlobalRule;
import storyEngine.GlobalRule.NodeFilter;
import storyEngine.GlobalRule.NodesAffected;
import storyEngine.GlobalRule.StoryStateFilter;

public class TestGlobalRules
{
	public static void main(String[] args)
	{
		Serializer serializer = new Persister();
		
		//////////////////////////////////////////////////////////////////
		
		NodeFilter nodeFilter = new NodeFilter(
				GlobalRule.ListTypeForNodes.nodeIDs, 
				"nodeID1,nodeID2,nodeID3",
				GlobalRule.HowMany.anyOf,
				GlobalRule.NodeState.seen);
		
		
		NodesAffected nodesAffected = new NodesAffected(
				GlobalRule.ListTypeForNodes.nodeIDs, 
				"nodeID4",
				GlobalRule.HowMany.allOf);
		
		GlobalRule nodeFilterRule = new GlobalRule("nodeFilterRule", nodeFilter, nodesAffected);
		
		//
		
		StoryStateFilter storyStateFilter = new StoryStateFilter(
				"tag1,tag2,tag3",
				GlobalRule.HowMany.allOf,
				GlobalRule.TagState.notPresent);
		
		NodesAffected nodesAffected2 = new NodesAffected(
				GlobalRule.ListTypeForNodes.nodeIDs, 
				"nodeID4,nodeID5",
				GlobalRule.HowMany.anyOf);
		
		GlobalRule storyStateFilterRule = new GlobalRule("storyStateFilterRule", storyStateFilter, nodesAffected);
		
		//////////////////////////////////////////////////////////////////
		
		try
		{
			File result = new File("./testData/testGlobalRules/testGlobalRules1a.xml");
			serializer.write(nodeFilterRule, result);
			
			GlobalRule readRule = 
					serializer.read(GlobalRule.class, result);
			
			System.out.println(readRule);
			System.out.println("Valid: " + readRule.isValid());
			
			File result2 = new File("./testData/testGlobalRules/testGlobalRules1b.xml");
			
			serializer.write(readRule, result2);
			
			///
			
			result = new File("./testData/testGlobalRules/testGlobalRules2a.xml");
			serializer.write(storyStateFilterRule, result);
			
			readRule = 
					serializer.read(GlobalRule.class, result);
			
			System.out.println("\n\n" + readRule);
			System.out.println("Valid: " + readRule.isValid());
			
			result2 = new File("./testData/testGlobalRules/testGlobalRules2b.xml");
			
			serializer.write(readRule, result2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
