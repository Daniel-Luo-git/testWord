package test;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class Point {
	private List<StdAnswer> StdAnswerList = null;//标答表
	private double weight;//权重
	private String negativeDescr;//否定表述
	
	public Point(List<StdAnswer> keyWords,double weight)
	{
		this.StdAnswerList = keyWords;
		this.weight = weight;
	}
	
	public Point(double weight)
	{
		this.weight = weight;
	}
	
	public List<StdAnswer> getStdAnswers()
	{
		if(StdAnswerList==null)
			return Collections.emptyList();
		return StdAnswerList;
	}
	
	public void setStdAnswers(List<StdAnswer> StdAnswerList)
	{
		if(StdAnswerList!=null)
			this.StdAnswerList = StdAnswerList;
	}
	
	public void addStdAnswer(StdAnswer stdAnswer)
	{
		if(StdAnswerList==null)
			StdAnswerList = new ArrayList<>();
		if(stdAnswer!=null)
			StdAnswerList.add(stdAnswer);
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getNegativeDescr() {
		return negativeDescr;
	}

	public void setNegativeDescr(String negativeDescr) {
		this.negativeDescr = negativeDescr;
	}
}
