package evolvingnumbers;

import java.io.*;
import java.util.*;

public class Main {

    public static ArrayList<double[]> AGENT_INPUT = new ArrayList<>();
    public static int TRAINING_EPOCHES = 1000;
    
    public static void main(String[] args){

	int validData = importData();

	if(validData == 1){
	    System.out.println("The data has not been imported correctly.");
	    return;
	}

	trainOnData(TRAINING_EPOCHES);    

    }

    private static int importData(){

	BufferedReader br = null;
	String line = "";
	String delimiter = ",";
	int fileLineSize = 7;
	String[] fileLine = new String[fileLineSize];
	
	try{
	    br = new BufferedReader(new FileReader("./evolvingnumbers/table.csv"));
	    br.readLine();
	    while((line = br.readLine()) != null){
		fileLine = line.split(delimiter);
		double[] doubleline = new double[4];
		doubleline[0] = Double.parseDouble(fileLine[1]);
		doubleline[1] = Double.parseDouble(fileLine[2]);
		doubleline[2] = Double.parseDouble(fileLine[3]);
		doubleline[3] = Double.parseDouble(fileLine[4]);
		
		AGENT_INPUT.add(doubleline);
	    }
	} catch(Exception e){
	    e.printStackTrace();
	}
	
	if(AGENT_INPUT == null)
	    return 1;
	return 0;
	
    }

    private static void trainOnData(int epoches){

	
	
    }
}
