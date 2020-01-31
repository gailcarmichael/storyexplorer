package storyEngine;

import java.util.ArrayList;

public class MemoryFunction
{
	private String m_elementID;
	private ArrayList<Float> m_memoryValueOverTime;
	
	private final float m_decayAmount = 0.5f; // will be a customizable function eventually
	private final float m_initialMemoryValue = 0.0f; // should also be customizable per element
	
	public MemoryFunction(String elementID)
	{
		m_elementID = elementID;
		m_memoryValueOverTime = new ArrayList<Float>();
		m_memoryValueOverTime.add(m_initialMemoryValue);
	}
	
	public String getElementID() { return m_elementID; }
	
	public Float getValueAt(int pos)
	{
		Float lastValue = 0.0f;
		
		if (pos >= 0 && pos < m_memoryValueOverTime.size())
		{
			lastValue = m_memoryValueOverTime.get(pos);
		}
		else
		{
			System.err.println("Invalid index for obtaining a value from the memory function.");
		}
		
		return lastValue;
	}
	
	public Float getLastValue()
	{
		return getValueAt(m_memoryValueOverTime.size()-1);
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
	
	public String toString()
	{
		String result = "";
		
		result += m_elementID + "\n";
		
		for (Float value : m_memoryValueOverTime)
		{
			result += value + "\n";
		}
		
		return result;
	}
}
