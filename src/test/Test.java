package test;

import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.file.Paths;
import java.math.BigDecimal;


public class Test {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		double score = 10.0;
		double curScore = 0;
		
		String [] keyWords = {"语句覆盖","判定覆盖","条件覆盖","判定/条件覆盖","条件组合覆盖"
				,"修正的判定/条件覆盖"};		
		String sentence = "常见的覆盖指标包括：语句覆盖，判定覆盖，条件覆盖，判定/条件覆盖，"
				+ "条件组合覆盖，修正的判定/条件覆盖。";

		Point point1 = new Point(1.0);
		StdAnswer stdAnswer1 = new StdAnswer(keyWords,sentence);
		point1.addStdAnswer(stdAnswer1);
		
		/*设置同近义词*/
		Word w = stdAnswer1.quickKeyWords.get("判定/条件覆盖");
		Word w1 = stdAnswer1.quickKeyWords.get("语句覆盖");
		Word w2 = stdAnswer1.quickKeyWords.get("判定覆盖");
		Word synw1 = new Word("判定条件覆盖");
		Word synw2 = new Word("点覆盖");
		Word synw3 = new Word("边覆盖");
		Word synw4 = new Word("判定-条件覆盖");
		List<Word> synonyms1 = new ArrayList<>();
		List<Word> synonyms2 = new ArrayList<>();
		List<Word> synonyms3 = new ArrayList<>();
		synonyms1.add(synw1);
		synonyms1.add(synw4);
		synonyms2.add(synw2);
		synonyms3.add(synw3);
		w.setSynonyms(synonyms1);
		w1.setSynonyms(synonyms2);
		w2.setSynonyms(synonyms3);

		//计算得分
		try {
			Scanner sin = new Scanner(Paths.get("20180403_1.txt"),"GB2312");
			String text = null;
			StringBuffer buf = new StringBuffer(); 
			List<StdAnswer> StdAnswerList = point1.getStdAnswers();
			double curSim = 0;
			double sim = 0;
			while(sin.hasNext())
			{
				text = sin.nextLine();
				System.out.println(text);
				for(StdAnswer s:StdAnswerList)
				{
					curSim = computeWithoutOrder(s,text);
					if(curSim>sim)
						sim = curSim;
				}
				curScore = score*sim; 
				BigDecimal b = new BigDecimal(curScore);  
				curScore = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();  
				//curScore = (double)Math.round(curScore*100)/100;//保留两位小数
				buf.append(text+"   得分为："+curScore+"\n");
				System.out.printf("得分为:%6.2f\n",curScore);
				sim = 0;
			}
			sin.close();
			PrintWriter out = new PrintWriter("20180403_1evaluated.txt","GB2312");
			out.write(buf.toString());
			out.close();
			}
		catch (IOException e) {
	            e.printStackTrace();
		}
		/*
		String text2 = "语句覆盖、判定覆盖、条件覆盖、判定/条件覆盖、条件组合覆盖、路径覆盖";
		curScore = score*computeWithoutOrder(point1,text2);
		System.out.printf("得分为:%6.2f",curScore);*/
	}
	
	//不考虑词序下的计算函数，类似填空题的题目
	public static double computeWithoutOrder(StdAnswer stdAnswer,String answer)
	{
		double sim = 0;
		double wordSim = 0;
		double lenSim = 0;
		List<Word> keyWords = stdAnswer.getKeyWords();
		List<Word> synonyms = null;
		String stdSentence = stdAnswer.getSentence();
		
		//计算词型相似度
		for(Word w1:keyWords)
		{
			synonyms = w1.getSynonyms();
			if(KMPImp(answer,w1.getText())!=-1||checkSynonyms(synonyms,answer))
				wordSim+=w1.getWeight();
		}
		
		//计算句长相似度
		lenSim = 1-(double)Math.abs(answer.length()-stdSentence.length())/(answer.length()+stdSentence.length());
		
		System.out.println("词型相似度为："+wordSim);
		System.out.println("句长相似度为："+lenSim);
		sim = 0.9*wordSim+0.1*lenSim;
		
		return sim;
	}
	
	//考虑词序的简单句子的回答
	public static double computeWithOrder(StdAnswer stdAnswer,String answer)
	{
		double sim = 0;
		double wordSim = 0;
		double lenSim = 0;
		double orderSim = 0;
		List<Word> keyWords = stdAnswer.getKeyWords();
		List<Word> comWords = new ArrayList<>();
		List<Word> reOrderWords2 = new ArrayList<>();
		Map<Word,Integer> wordVec = new HashMap<>();
		List<Word> synonyms = null;
		String stdSentence = stdAnswer.getSentence();
		int i = 1;
		
		/*计算词型相似度*/
		for(Word w1:keyWords)
		{
			synonyms = w1.getSynonyms();
			if(KMPImp(answer,w1.getText())!=-1||checkSynonyms(synonyms,answer))
			{
				comWords.add(w1);
				wordVec.put(w1, i++);
				wordSim+=w1.getWeight();
			}
		}
		
		/*计算词序相似度*/
		int vecLen = comWords.size();
		int [] v1 = new int [vecLen];
		int [] v2 = new int [vecLen];
		for(int j =1;j<=vecLen;j++)
		{
			v1[j-1] = j;//给标准向量赋值
		}
		int loc ;
		int k = 0;
		for(Word w2:comWords)
		{
			loc = KMPImp(answer,w2.getText());
			w2.setLocation(loc);
			if(k==0)
				reOrderWords2.add(w2);
			if(k>0)
			{
			while(k>0&&w2.getLocation()<comWords.get(k).getLocation())
				k--;
			reOrderWords2.add(k+1, w2);//将词添加到排序表中合适的位置
			}
			k = reOrderWords2.size();
		}
		int m = 0;
		for(Word w3:reOrderWords2)
		{
			v2[m++] = wordVec.get(w3);
		}
		//计算向量距离
		double distance = 0;
		double maxDistance = vecLen*vecLen/2;
		for(int x=0;x<vecLen;x++)
		{
			distance+=Math.abs(v1[x]-v2[x]);
		}
		if(vecLen==1)
			orderSim = 1;
		if(vecLen>1)
			orderSim = 1-distance/maxDistance;
		
		/*计算句长相似度*/
		lenSim = 1-(double)Math.abs(answer.length()-stdSentence.length())/(answer.length()+stdSentence.length());
		
		System.out.println("\n词型相似度为："+wordSim+"\t句长相似度为："+lenSim+"\t词序相似度为："+orderSim);
		if(wordSim>0.5)
		{
			sim = 0.5*wordSim+0.4*orderSim+0.1*lenSim;
		}
		else
			sim = 0.8*wordSim+0.1*orderSim+0.1*lenSim;
		
		return sim;
	}
	
	public static boolean checkSynonyms(List<Word> synonyms,String answer)
	{
		for(Word w2:synonyms)
			if(KMPImp(answer,w2.getText())!=-1)
			{
				return true;
			}
		return false;
	}
	
	public static List<String> getStringByKeyWords(String keyWords,String content){
	    List<String> list=new ArrayList<String>();
	    Matcher m=Pattern.compile("。(.*?"+keyWords+".*?)。").matcher(content);
	    while(m.find())
	        list.add(m.group(1));
	    return list;
	}
	
	private static int KMPImp(String source, String pattern) {
        int[] N=getN(pattern);
        int res=0;
        int sourceLength=source.length();
        int patternLength=pattern.length();
        for(int i=0;i<=(sourceLength-patternLength);){
            res++;
            String str=source.substring(i, i+patternLength);//要比较的字符串
            //p(str);
            int count=getNext(pattern, str,N);
            //p("移动"+count+"步");
            if(count==0){
                return res;
            }
            i=i+count;
        }
        return -1;
    }
	
	private static int getNext(String pattern,String str,int[] N) {
        int n = pattern.length();
        char v1[] = str.toCharArray();
        char v2[] = pattern.toCharArray();
        int x = 0;
        while (n-- != 0) {
            if (v1[x] != v2[x]){
                if(x==0){
                    return 1;//如果第一个不相同，移动1步
                }
                return x-N[x-1];//x:第一次出现不同的索引的位置，即j
            }
            x++;
        }
        return 0;
    }
	
	private static int[] getN(String pattern) {
        char[] pat=pattern.toCharArray();
        int j=pattern.length()-1;
        int[] N=new int[j+1];
        for(int i=j;i>=2;i--){
            N[i-1]=getK(i,pat);
        }
        /*for(int a:N)
            p(a);*/
        return N;
    }
	
	private static int getK(int j, char[] pat) {
        int x=j-2;
        int y=1;
        while (x>=0 && compare(pat, 0, x, y, j-1)) {
            x--;
            y++;
        }
        return x+1;
    }
	
	private static boolean compare(char[] pat,int b1,int e1,int b2,int e2){
        int n = e1-b1+1;
        while (n-- != 0) {
            if (pat[b1] != pat[b2]){
                return true;
            }
            b1++;
            b2++;
        }
        return false;
    }
	
	public static void p(Object obj) {
        System.out.println(obj);
    }

}
