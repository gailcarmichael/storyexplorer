package storyEngine;

import java.util.ArrayList;

public class MemoryFunction
{
	private String m_elementID;
	private ArrayList<Float> m_memoryValueOverTime;
	private final float m_decayAmount = 0.5f; // will be a customizable function eventually
	
	public MemoryFunction(String elementID)
	{
		m_elementID = elementID;
		m_memoryValueOverTime = new ArrayList<Float>();
	}
	
	public String getElementID() { return m_elementID; }
	
	public Float getLastValue()
	{
		Float lastValue = 0.0f;
		
		if (!m_memoryValueOverTime.isEmpty())
		{
			lastValue = m_memoryValueOverTime.get(m_memoryValueOverTime.size()-1);
		}
		
		return lastValue;
	}
	
	public void timeStepFeaturingElement(float prominence)
	{
		m_memoryValueOverTime.add(getLastValue() + prominence);
	}
	
	public void timeStepNotFeaturingElement()
	{
		Float newValue = Math.max(0, getLastValue() - m_decayAmount);
		m_memoryValueOverTime.add(newValue);
	}
}
