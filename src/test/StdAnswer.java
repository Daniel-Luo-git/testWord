package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StdAnswer {
	private List<Word> keyWords;
	private String sentence;
	public Map<String,Word>  quickKeyWords = new HashMap<>();
	
	public StdAnswer(String [] keyWords,String sentence)
	{
		this.setKeyWords(keyWords);
		this.sentence = sentence;
	}
	
	public void setKeyWords(List<Word> keyWords)
	{
		if(keyWords!=null)
			this.keyWords = keyWords;
	}
	
	public List<Word> getKeyWords()
	{
		if(keyWords==null)
			return Collections.emptyList();
		return keyWords;
	}
	
	public void setKeyWords(String [] keyWords)
	{
		int len = keyWords.length;
		double aWeight = 1.0/len;
		List<Word> keyWordList = new ArrayList<>();
		
		for(String s:keyWords)
		{
			Word w1 = new Word(s,aWeight);
			quickKeyWords.put(s, w1);
			keyWordList.add(w1);
		}
		this.keyWords = keyWordList;
	}	
	
	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public void autoWordsWeight()
	{
		if(keyWords!=null)
		{
			int length = keyWords.size();
			double averageWeight = 1.0/length;//平均分配权重
			for(Word x:keyWords)
			{
				x.setWeight(averageWeight);
			}
		}
		else System.out.println("关键词为空！");
	}


}
