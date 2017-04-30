package evolvingnumbers;

import java.io.*;
import java.util.*;

public class Main {

    public static ArrayList<double[]> AGENT_INPUT = new ArrayList<>();
    public static ArrayList<double[]> AGENT_OUTPUT = new ArrayList<>();
    public static int TRAINING_EPOCHES = 10000;
    private static LSTM agent;
    private static Random r = new Random(1);
    private static int data_size = 4;
    private static int cell_blocks = 10;
    
    public static void main(String[] args){

	int validData = importData();

	if(validData == 1){
	    System.out.println("The data has not been imported correctly.");
	    return;
	}

	trainOnData(TRAINING_EPOCHES);

	for(int i = 0; i < AGENT_OUTPUT.size(); i++)
	    System.out.println(i + " " + Arrays.toString(AGENT_OUTPUT.get(i)));

	return;
    }

    // Read in data from a downloaded csv table containing candlestick values, use
    // the download data function on the Yahoo Finance website. 
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
	
	agent = new LSTM(r, data_size, data_size, cell_blocks);
	int input_count = 0;
	int input_max = AGENT_INPUT.size() - 1;
	boolean corrupted_state = false;
	
	//main training loop
	for(int i = 0; i < epoches; i++){
	    double[] input = AGENT_INPUT.get(input_count);
	    double[] expected_output = AGENT_INPUT.get(input_count + 1);
	    double[] output = new double[4];
	    
	    output = agent.Next(input, expected_output);
	    AGENT_OUTPUT.add(output);

	    double[] hidden_layer = agent.GetHiddenState();

	    for(int j = 0; j < hidden_layer.length; j ++){
		if(Math.abs(hidden_layer[j]) < .0000000000000000000000001){
		    hidden_layer[j] = 0;
		    corrupted_state = true;
		}
		else if(Math.abs(hidden_layer[j]) > 10000000000000000000000000.0){
		    hidden_layer[j] = hidden_layer[j] / 100000;
		    corrupted_state = true;
		}
	    }

	    if(corrupted_state == true){
		agent.SetHiddenState(hidden_layer);
		corrupted_state = false;
	    }

	    input_count ++;

	    if(input_count == input_max)
		input_count = 0;
	}//end of main training loop

	
    }
}
