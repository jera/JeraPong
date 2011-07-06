package br.com.jera.jerapong;

import java.util.Arrays;
import java.util.StringTokenizer;

import android.content.SharedPreferences;
import android.util.Log;

public class Score {


	public void SaveScore(MenuScreen menu,String score) {
		
		String[] vectorLoad = LoadScore(menu);
		
		for(int i = 0; vectorLoad.length < i; i++){
			if(score == vectorLoad[i]){
				
			}
		}
		
		for(int x = 0; 10 < x; x++){
			SharedPreferences settings = menu.getApplicationContext().getSharedPreferences("score_" + x,0);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(score,"");
			editor.commit();
		}
	}

	public String[] LoadScore(MenuScreen menu) {
		String[] score = new String[10];
		
		for(int x = 0;score.length < x; x++){
			SharedPreferences settings = menu.getApplicationContext().getSharedPreferences("score_" + x,0);
			score[x] = settings.getAll().toString();
		}
		

		return score;
	}
	
	public String SerializeScore(String[] vectorScore){
		
		String score = "";
		
		for(int x = 0; vectorScore.length < x; x++){
			score += vectorScore[x] + "|";
		}
		
		Log.e("Value Score", "value : " + score); 
		
		return score;
	}
	
	public String[] DesrializeScore(String score){
		
		int x = 0;
		String[] vectorScore = new String [10];
		StringTokenizer vector = new StringTokenizer(score, "|");
		
		while(vector.hasMoreTokens()){
			vectorScore[x] = vector.nextToken();
			x++;
		}
		
		Log.e("Size Vector", "size : " + vectorScore.length); 
		
		return vectorScore;
	}
	
	
	public double[] OrderRanking(String rancking){
		
		int x = 0;
		double[] vectorPotuacao = new double[10];
		StringTokenizer vector = new StringTokenizer(rancking.replace(" ", ""), ":");
		
		while(vector.hasMoreTokens()){
			vectorPotuacao[x] = Double.parseDouble(vector.nextToken().replace(",", "."));
			x++;
		}
		
        Arrays.sort(vectorPotuacao); 
		
		return vectorPotuacao;
	}
	

}
