package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double score = 10.0;
		double curScore = 0;
		
		/*设置标答知识点*/
		Point point1 = new Point(0.4);
		Point point2 = new Point(0.3);
		Point point3 = new Point(0.3);	
		
		List<Point> points = new ArrayList<>();
		
		points.add(point1);
		points.add(point2);
		points.add(point3);
		
		
		//知识点1标答1、2
		String [] keyWords11 = {"测试用例","多","弱覆盖"};
		String sentence11 = "当测试用例数量过多的时候，优先考虑弱覆盖。";
		String [] keyWords12 = {"弱覆盖","测试用例","数量少","强覆盖","测试用例","数量多"};
		String sentence12 = "弱覆盖得到的测试用例数量少，而强覆盖得到的测试用例数量多。";
		StdAnswer stdAnswer11 = new StdAnswer(keyWords11,sentence11);
		point1.addStdAnswer(stdAnswer11);
		StdAnswer stdAnswer12 = new StdAnswer(keyWords12,sentence12);
		point1.addStdAnswer(stdAnswer12);
		
		//知识点2标答1、2
		String [] keyWords21 = {"测试覆盖","要求高","强覆盖"};
		String sentence21 = "当对测试覆盖的要求较高的时候，应采用强覆盖，否则，可使用弱覆盖。";
		String [] keyWords22 = {"弱覆盖","覆盖度","低","强覆盖","覆盖度","高"};
		String sentence22 = "弱覆盖覆盖度较低，而强覆盖覆盖度高。";
		StdAnswer stdAnswer21 = new StdAnswer(keyWords21,sentence21);
		point2.addStdAnswer(stdAnswer21);
		StdAnswer stdAnswer22 = new StdAnswer(keyWords22,sentence22);
		point2.addStdAnswer(stdAnswer22);
		
		//知识点3标答1、2
		String [] keyWords31 = {"时间效率","考虑","弱覆盖"};
		String sentence31 = "从时间效率来说，当时间较紧的时候，优先考虑弱覆盖。";
		String [] keyWords32 = {"弱覆盖","时间效率","高","强覆盖","时间效率","低"};
		String sentence32 = "弱覆盖时间效率高，强覆盖时间效率低。";
		StdAnswer stdAnswer31 = new StdAnswer(keyWords31,sentence31);
		point3.addStdAnswer(stdAnswer31);
		StdAnswer stdAnswer32 = new StdAnswer(keyWords32,sentence32);
		point3.addStdAnswer(stdAnswer32);
		
		
		/*设置同近义词*/
		Word w1 = stdAnswer21.quickKeyWords.get("要求高");
		Word w2 = stdAnswer31.quickKeyWords.get("考虑");
		Word w3 = stdAnswer11.quickKeyWords.get("测试用例");
		Word synw11 = new Word("用");
		Word synw12 = new Word("使用");
		Word synw21 = new Word("要求较高");
		Word synw31 = new Word("用例");
		List<Word> synonyms1 = new ArrayList<>();
		List<Word> synonyms2 = new ArrayList<>();
		List<Word> synonyms3 = new ArrayList<>();
		synonyms1.add(synw21);
		synonyms2.add(synw11);
		synonyms2.add(synw12);
		synonyms3.add(synw31);
		w1.setSynonyms(synonyms1);
		w2.setSynonyms(synonyms2);
		w3.setSynonyms(synonyms3);

		//计算得分
		try {
			Scanner sin = new Scanner(Paths.get("20180403_22.txt"),"UTF-8");
			String text1 = null;
			String text2 = null;
			StringBuffer buf = new StringBuffer(); 
			double curSim = 0;
			double sim = 0;
			double totalSim = 0;
			List<StdAnswer> StdAnswerList;
			while(sin.hasNext())
			{
				text1 = sin.nextLine();
				if(sin.hasNext())
				text2 = sin.nextLine();
				else
					text2 = "";
				for(Point p:points)
				{
					StdAnswerList = p.getStdAnswers();
					for(StdAnswer s:StdAnswerList)
					{
						curSim = computeWithOrder(s,text1+text2);
						if(curSim>sim)
							sim = curSim;
					}
					System.out.print(" "+sim+" ");
					totalSim = totalSim+sim*p.getWeight();
					sim = 0;
				}
				curScore = score*totalSim;
				BigDecimal b = new BigDecimal(curScore);  
				curScore = b.setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();  
				//curScore = (double)Math.round(curScore*100)/100;//保留两位小数
				buf.append(text1+text2+"   得分为："+curScore+"\n");
				System.out.printf("得分为:%6.2f\n",curScore);
				totalSim = 0;
			}
			sin.close();
			PrintWriter out = new PrintWriter("20180403_22evaluated.txt","UTF-8");
			out.write(buf.toString());
			out.close();
			}
		catch (IOException e) {
	            e.printStackTrace();
		}
	}
	
	public static double computeWithOrder(StdAnswer stdAnswer,String answer)
	{
		System.out.println("学生回答："+answer);
		double sim = 0;
		double wordSim = 0;
		double orderSim = 0;
		List<Word> keyWords = stdAnswer.getKeyWords();
		List<Word> comWords = new ArrayList<>();
		List<Word> reOrderWords2 = new ArrayList<>();
		Map<Word,Integer> wordVec = new HashMap<>();
		List<Word> synonyms = null;
		int i = 1;
		
		/*计算词型相似度*/
		for(Word w1:keyWords)
		{
			synonyms = w1.getSynonyms();
			if(KMPImp(answer,w1.getText())!=-1||checkSynonyms(synonyms,answer)!=-1)
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
		int loc = -1;
		int wordLoc;
		int synLoc;
		int k = 0;
		System.out.print("学生回答中共现词：");
		for(Word w2:comWords)
		{
			wordLoc = KMPImp(answer,w2.getText());
			if(wordLoc!=-1)
			loc = wordLoc;
			else
			{
				synLoc = checkSynonyms(w2.getSynonyms(),answer);
				if(synLoc!=-1)
					loc = synLoc;
			}
			w2.setLocation(loc);
			if(k==0)
				reOrderWords2.add(w2);
			if(k>0)
			{
			while(k>0&&w2.getLocation()<reOrderWords2.get(k-1).getLocation())
				k--;
			if(k+1>reOrderWords2.size())
				reOrderWords2.add(w2);
			else
				reOrderWords2.add(k,w2);//将词添加到排序表中合适的位置
			
			}
			k = reOrderWords2.size();
		}
		for(Word wc:comWords)
			System.out.print(wc.getText()+" loc:"+wc.getLocation()+"  ");
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
		//lenSim = 1-(double)Math.abs(answer.length()-stdAnswer.length())/(answer.length()+stdAnswer.length());
		
		if(wordSim>0.5)
		{
			if(orderSim<0.51)
				sim = 0;
			else
				sim = 0.4*wordSim+0.6*orderSim;			
		}
		else
			sim = 0.5*wordSim+0.5*orderSim;
		BigDecimal b = new BigDecimal(sim);  
		sim = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("当前得分点相似度："+sim);
		return sim;
	}
	
	public static int checkSynonyms(List<Word> synonyms,String answer)
	{
		int loc = -1;
		for(Word w:synonyms)
		{
			loc = KMPImp(answer,w.getText());
			if(loc!=-1)
			{
				return loc;
			}
			continue;
		}
		return loc;
	}
	
	public static double add(double v1,double v2){     
		BigDecimal b1 = new BigDecimal(Double.toString(v1));     
		BigDecimal b2 = new BigDecimal(Double.toString(v2));     
		return b1.add(b2).doubleValue();     
		}     
	
	public static double mul(double v1,double v2){     
		BigDecimal b1 = new BigDecimal(Double.toString(v1));     
		BigDecimal b2 = new BigDecimal(Double.toString(v2));     
		return b1.multiply(b2).doubleValue();     
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
