package test;

import org.apdplat.word.*;
//import org.apdplat.word.analysis.*;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import java.util.*;
//import  java.nio.file.*;
//import java.io.*;

public class HelloWord {

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		/*
		String text1 = "我爱购物";
		String text2 = "我爱读书";
		String text3 = "他是黑客";
		String text4 = "我不爱读书";*/
		//TextSimilarity textSimilarity = new SimpleTextSimilarity();

		String text1 = "常见的覆盖指标包括：语句覆盖，判定覆盖，条件覆盖，判定/条件覆盖，"
				+ "条件组合覆盖，修正的判定/条件覆盖。";
		String text2 = "1、语句覆盖 2、判定覆盖 3、条件覆盖 4、判定/条件覆盖 "
				+ "5、条件组合覆盖 6、点覆盖 7、边覆盖 8、路径覆盖。";
		boolean ordered = true;
		Scanner sin = new Scanner(System.in);
		System.out.println("该题目需要考虑词序吗？yes/no");
		if(sin.nextLine().equals("no"))
			ordered = false;
		sin.close();
		double score = 0;
		if(ordered)
			score = computeWithOrder(text1,text2);
		else
			score = computeWithoutOrder(text1,text2);
		System.out.printf("得分为：%6.2f", score);
	}
		
	
	public static double computeWithOrder(String stdAnswer,String answer)
	{
		
		double score = 10;
		double curScore = 0;
		double wordSim = 0;//词型相似度
		double lenSim = 0;//句长相似度
		double sim = 0;
		//分词
		List<Word> words1 = WordSegmenter.segWithStopWords(stdAnswer,SegmentationAlgorithm.FullSegmentation);
		List<Word> words2 = WordSegmenter.segWithStopWords(answer,SegmentationAlgorithm.FullSegmentation);
		
		//去重
		List<String> word1List = new ArrayList<>();
		List<String> word2List = new ArrayList<>();
		for(Word w1:words1)
			if(!word1List.contains(w1.getText()))
			word1List.add(w1.getText());
		for(Word w2:words2)
			if(!word2List.contains(w2.getText()))
			word2List.add(w2.getText());
		
		int w1Len = words1.size();
		int w2Len = words2.size();

		Map<String,Integer> s1 = new HashMap<>();//单词向量映射
		int i = 1;
		for(String w1:word1List)
		{
			if(!s1.containsKey(w1)&&word2List.contains(w1))
				s1.put(w1, i++);
		}
		int vectorLen = s1.size();//共有关键词数量
		lenSim = 1.0-Math.abs(w1Len-w2Len)/(double)w1Len;//计算句长相似度
		wordSim = vectorLen/(double)word1List.size();//计算词型相似度

		int [] v1 = new int[vectorLen];
		int [] v2 = new int[vectorLen];
		for(int j = 1;j<=vectorLen;j++)
			v1[j-1] = j;
		int k = 0;
		for(String w2:word2List)
		{
			if(s1.containsKey(w2))
				v2[k++] = s1.get(w2);
		}
		
		System.out.println(word1List);
		System.out.println(word2List);
		System.out.println("共有词有：");
		System.out.println(s1);
		for(int x:v1)
			System.out.print(x+"\t");
		System.out.println();
		for(int y:v2)
			System.out.print(y+"\t");
		
		//计算向量距离和词序相似度
		double distance = 0;
		double maxDistance = vectorLen*vectorLen/2;
		double d_sim = 0;
		for(int x=0;x<vectorLen;x++)
		{
			distance+=Math.abs(v1[x]-v2[x]);
		}
		if(vectorLen==1)
			d_sim = 1;
		if(vectorLen>1)
			d_sim = 1-distance/maxDistance;
		System.out.println("\n词型相似度为："+wordSim+"\t句长相似度为："+lenSim+"\t词序相似度为："+d_sim);
		if(wordSim>0.5)
		{
			sim = 0.5*wordSim+0.4*d_sim+0.1*lenSim;
		}
		else
			sim = 0.8*wordSim+0.1*d_sim+0.1*lenSim;
		curScore = score*sim;
		return curScore;
	}
	
	public static double computeWithoutOrder(String stdAnswer,String answer)
	{
		double score = 10;
		double curScore = 0;
		double wordSim = 0;//词型相似度
		double lenSim = 0;//句长相似度
		double sim = 0;
		
		//分词
		List<Word> words1 = WordSegmenter.segWithStopWords(stdAnswer,SegmentationAlgorithm.FullSegmentation);
		List<Word> words2 = WordSegmenter.segWithStopWords(answer,SegmentationAlgorithm.FullSegmentation);
		int w1Len = words1.size();
		int w2Len = words2.size();
		
		words1.retainAll(words2);
		System.out.println(words1);
				
		int vectorLen = words1.size();//共有关键词数量
		lenSim = 1.0-Math.abs(w1Len-w2Len)/(double)w1Len;//计算句长相似度
		System.out.println("句长相似度："+lenSim);
		wordSim = vectorLen/(double)w1Len;//计算词型相似度
		System.out.println("词型相似度："+wordSim);
		
		sim = 0.9*wordSim+0.1*lenSim;//相似度公式
		System.out.println("总相似度："+sim);
		curScore = score*sim;
		
		return curScore;
	}
}
