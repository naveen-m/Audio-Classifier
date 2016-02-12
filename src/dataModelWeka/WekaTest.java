/*Authors : Naveen Mohan, Usha Basannappa gowda and Pooja Shetty
*Description: This class reads three audio features Spectral Centroid Overall, Standard Deviation 
*			   Spectral Flux Overall Average and Zero Crossings Overall Standard Deviation. 
*			   It uses PART classifiers to make decision tree.This class uses first 2/3rd of 
*			   data sent to train the system and next 1/3rd as the test data.It returns string 
*			   which is concatenated result of predicted output and ground truth value for all 
*			   the test data the output string also contains precision and recall values for both 
*			   music and speech
*/

package dataModelWeka;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.FastVector;
import weka.core.Instances;
import weka.classifiers.Evaluation;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;


public class WekaTest {
	
	public String outString;
	
	//readDataFile
	//Input : This function takes filename as the input parameter
	//Output: This function returns buffer reader for the file
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}


	/* getResults
	* Input : This function takes test data file names as input
	* Output : This function returns string which is concatenated result of predicted output and ground truth value for all the test data
	* the output string also contains precision and recall values for both music and speech
	*/
	
	public String getResults(String[] testfileNames) throws Exception {
		
		BufferedReader datafile = readDataFile("weka.txt");
		
		String modelOutput, groundTruthLabel;	
		String nametomodel, modeltoground;
		int testcount =0;
		
		//Create instance for each data
		Instances data = new Instances(datafile);
		data.setClassIndex(data.numAttributes() - 1);
		
		//Take first 2/3rd as train data and rest 1/3rd as test data
		int trainSize = (int) Math.round(data.numInstances() * 0.67);
		int testSize = data.numInstances() - trainSize;
		Instances train = new Instances(data, 0, trainSize);
		Instances test = new Instances(data, trainSize, testSize);
		
		//Selecting part classifier to make decision tree and building the decision tree for train data
		Classifier cls = 
		new PART();
		//DecisionStump();
		//new J48();
		//new PART(), 
		//new DecisionTable(),//decision table majority classifier
		//new DecisionStump() //one-level decision tree
		cls.buildClassifier(train);
		Evaluation eval = new Evaluation(train);
		
		//Evaluate the result for test data
		double d1[] = eval.evaluateModel(cls, test, new Object[] { });
		FastVector predictions = new FastVector();
		predictions.appendElements(eval.predictions());	 
		String outString="Format:\n\nFilename            Modeloutput          Groundtruthlabel\n";
		
		boolean isPredict;
		boolean groundTruth;
		
		
		//Iterating through all the test data predictions
		for (int i = 0; i < predictions.size(); i++) {
			
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			
			//Assigning predicted and groundtruth values based on classifier result
			if(np.predicted()==0.0)
			{
				isPredict = true;
			}
			else
			{
				isPredict = false;
			}
			
			if(np.actual()==0.0)
			{
				groundTruth = true;
			}
			else
			{
				groundTruth = false;
			}
			
			
			//Music is identified as true in data
			//Hence identifying if Music or speech bases on isPredict and groundtruth boolean values
			if(isPredict)
			modelOutput = "Music";
			else
			modelOutput = "Speech";
			
			if(groundTruth)
			groundTruthLabel = "Music";
			else
			groundTruthLabel = "Speech";
			
			//testFileNames array can have null values
			//Incrementing until non null value is obtained
			while(testfileNames[testcount] == null)
			testcount++;
			
			/*if(testfileNames[testcount].contains("mu"))
							groundTruthLabel = "Music";
						else
							groundTruthLabel = "Speech";*/
			if(testfileNames[testcount].contains("mu"))
			nametomodel = "		                         ";
			else
			nametomodel = "		                          ";
			
			
			modeltoground = (modelOutput.equalsIgnoreCase("Music"))?"		                            	":"	                         ";
			
			//Obtaining string which is concatenated result of predicted output and ground truth value for all the test data
			//the output string also contains precision and recall values for both music and speech
			outString+=testfileNames[testcount]+nametomodel+modelOutput+modeltoground+groundTruthLabel+"\n";
			
			testcount++;
		}
		
		//Obtaining confusion matrix
		double [][] ConfusionMatrix = eval.confusionMatrix();
		String string1 = eval.toMatrixString();
		
		//Calculating precision and recall for both Music and Speech
		double musicPre = (ConfusionMatrix[0][0]/(ConfusionMatrix[0][0]+ConfusionMatrix[1][0]));
		double musicRec = (ConfusionMatrix[0][0]/(ConfusionMatrix[0][0]+ConfusionMatrix[0][1]));
		double speechPre = (ConfusionMatrix[1][1]/(ConfusionMatrix[1][1]+ConfusionMatrix[0][1]));
		double speechRec = (ConfusionMatrix[1][1]/(ConfusionMatrix[1][1]+ConfusionMatrix[1][0]));
		
		//appending precision and recall values to the output string
		String str ="\nMUSIC\n\nPrecession - "+musicPre+"\n"+"Recall - "+musicRec+"\n"+"\nSPEECH:\n\nPrecission - "
		+speechPre+"\n"+"Recall - "+speechRec+"\n";
		outString+=str;
		
		//Return the string value
		return outString;
	}
}
