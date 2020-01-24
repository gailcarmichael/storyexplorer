package storyEngine.storyElements;

import java.util.ArrayList;

public abstract class StoryElementWeightCurve
{
	
	public StoryElementWeightCurve()
	{
	}
	
	public abstract float getValueAt(int pos);
	
	
	///////////////////////////////////////////////////////
	
	
	public static class PiecewiseConstantWeightCurve extends StoryElementWeightCurve
	{
		private ArrayList<Integer> m_newValueStartsAt;
		private ArrayList<Float> m_values;
		private float m_defaultValue;

		public PiecewiseConstantWeightCurve(float defaultValue)
		{
			m_newValueStartsAt = new ArrayList<Integer>();
			m_values = new ArrayList<Float>();
			m_defaultValue = defaultValue;
		}
		
		public void addNewValue(int startAt, float value)
		{
			m_newValueStartsAt.add(startAt);
			m_values.add(value);
		}

		@Override
		public float getValueAt(int pos)
		{
			float value = m_defaultValue;
			
			if (!m_newValueStartsAt.isEmpty() && pos > m_newValueStartsAt.get(m_newValueStartsAt.size()-1))
			{
				value = m_values.get(m_values.size()-1);
			}
			else
			{
				for (int i=0; i < m_newValueStartsAt.size(); i++)
				{
					int prevRangeStart = i == 0 ? -1 : m_newValueStartsAt.get(i);
					
					if (pos < prevRangeStart)
					{
						value = m_values.get(i-1);
						break;
					}
				}
			}
			
			return value;
		}
		
	}
}