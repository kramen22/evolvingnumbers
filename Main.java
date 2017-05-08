package evolvingnumbers;

import java.io.*;
import java.util.*;

public class Main {

    public static final boolean DEBUG = false;
    public static ArrayList<double[]> AGENT_INPUT = new ArrayList<>();
    public static ArrayList<double[]> AGENT_OUTPUT = new ArrayList<>();
    public static int TRAINING_EPOCHES = 100000000;
    private static LSTM agent;
    private static Random r = new Random(1);
    private static int data_size = 4;
    private static int cell_blocks = 10;
    
    public static void main(String[] args){

	int validData = importData();

	// Attempt to read the data and then check the return value, return on failure.
	if(validData == 1){
	    System.out.println("The data has not been imported correctly.");
	    return;
	}

	// LSTM throws exception when calling next and on creation, both occur in trainOnData
	try{
	trainOnData(TRAINING_EPOCHES);
	} catch(Exception e){
	    e.printStackTrace();
	}
	if(DEBUG == true){
	    for(int i = 0; i < AGENT_OUTPUT.size(); i++){
		System.out.println(i + " " + Arrays.toString(AGENT_OUTPUT.get(i)));
		System.out.println("Actual " + Arrays.toString(AGENT_INPUT.get(i % AGENT_INPUT.size())));
		printError(AGENT_INPUT.get(i % AGENT_INPUT.size()), AGENT_OUTPUT.get(i));

	    }
	}
	
	return;
    }

    // A simple percentage error between two same size arrays of doubles, the input and output of the LSTM
    private static void printError(double[] input, double[] output){
	double[] error = new double[input.length];

	for(int i = 0; i < input.length; i++){
	    error[i] = Math.abs( (input[i] - output[i]) / output[i]) * 100;
	}
	    
	System.out.println("Error = " + Arrays.toString(error));
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

    private static void trainOnData(int epoches) throws Exception {
	
	agent = new LSTM(r, data_size, data_size, cell_blocks);
	int input_count = 0;
	int input_max = AGENT_INPUT.size() - 1;
	boolean corrupted_state = false;
	//boolean validation = false;
	int success_count = 0;
	
	//main training loop
	for(int i = 0; i < epoches; i++){
	    double[] input = AGENT_INPUT.get(input_count);
	    double[] expected_output = AGENT_INPUT.get(input_count + 1);
	    double[] output = new double[4];

	    //if(validation == false){
		output = agent.Next(input, expected_output);
		AGENT_OUTPUT.add(output);
		//}

		//else if(validation == true){
		//output = agent.Next(input);
		//AGENT_OUTPUT.add(output);
		//}
	    

	    if(DEBUG == true)
		System.out.println(Arrays.toString(output));

	    double[] hidden_layer = agent.GetParameters();

	    for(int j = 0; j < hidden_layer.length; j ++){
		if(Math.abs(hidden_layer[j]) < .000000000000000000001){
		    hidden_layer[j] = 0;
		    corrupted_state = true;
		    //System.out.println("Small weight clipped");
		}
		else if(Math.abs(hidden_layer[j]) > 10000000000000000000000000.0){
		    hidden_layer[j] = hidden_layer[j] / 100000;
		    corrupted_state = true;
		    System.out.println("Large weight clipped");
		}
	    }

	    if((i % 10000) == 0){
		System.out.print(i + " ");
		printError(input, output);
	    }
	    //if(validation == true)
		//	validation = false;
	    //else
		//	validation = true;
	    

	    if(DEBUG == true){
		if((i % 100000) == 0){
		    double[] parameters = agent.GetParameters();
		    System.out.println(Arrays.toString(parameters));
		}
	    }

	    if(DEBUG == true){
		if((i % AGENT_INPUT.size()) == 0)
		    System.out.println("Reached the end of the data set");
	    }
	    
	    if(corrupted_state == true){
		agent.SetParameters(hidden_layer);
		corrupted_state = false;
	    }

	    input_count ++;

	    if(input_count == input_max)
		input_count = 0;

	    int total = 0;
	    for(int j = 0; j < output.length; j++){
		total += output[j];
	    }
	    if((total/output.length) < 1){
		success_count++;
		if(success_count > 10){
		    System.out.println("lo hicimos");
		    return;
		}
	    }
	    else if (success_count > 0)
		success_count = 0;
	    
	}//end of main training loop

	
    }
}
