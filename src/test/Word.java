package test;

import java.util.Collections;
import java.util.List;

public class Word {
	private String text;//词本身
	private double weight;//权重
	private List<Word> synonyms = null;//同近义词
	private int location;
	
	public Word(String text) { this.text = text;}
	
	public Word(String text,double weight)
	{
		this.text = text;
		this.weight = weight;
	}
	
	public void setText(String text) { this.text = text;}
	
	public String getText() { return text;}
	
	public void setWeight(double weight) { this.weight = weight;}
	
	public double getWeight() { return weight;}
	
	public void setSynonyms(List<Word> synonyms)
	{
		if(synonyms!=null)
			this.synonyms = synonyms;
	}
	
	public List<Word> getSynonyms()
	{
		if(synonyms==null){
            return Collections.emptyList();
        }
        return synonyms;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}
	
	

}
