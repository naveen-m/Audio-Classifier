/*
 * Description : This class contains the implementation to read the output of the JAudio library
 * 				 file "feature_values_1.xml" and extract the necessary feature values for the 
 * 				 following 3 features,
 * 					1. Spectral Centroid 
 * 					2. Spectral Centroid 
 * 					3. Zero Crossings
 * 				 Then, it writes the feature values in a separate file "weka.txt" which is the 
 * 				 required format for WEKA to process the data.
 */
package dataModelWeka;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.FileWriter;
import java.io.BufferedWriter;

public class XmlReader {

	/* read()
	 * Input  - void
	 * Output - returns an array of strings in the same order as the set of test data 
	 * 			filenames being written to weka for processing, so that they can be later 
	 * 			used for display. Also, write the features in the order such that 1st two-third 
	 * 			feature rows of the file ("weka.txt") corresponds to the training data and the 
	 * 			remaining is test data, since it is based on this order WEKA parses and processes 
	 * 			the file.
	 */
	public String[] read() {

		String[] audiosetNames;
		String[] testFileNames = new String[21];

		try {
			// Accessing the required files for processing
			File file = new File("feature_values_1.xml");
			File wekafile=new File("weka.txt");
			
			if (!wekafile.exists()) {
				wekafile.createNewFile();
			}
			
			//FileWriters for writing the values in the WEA=KA format
			FileWriter fw = new FileWriter(wekafile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			/* The standard format for WEKA processing, defining the features and 
			 * attributes, especially @attribute ismusic {yes, no} - which indicates
			 * the 	WEKA processor for the groundtruth label later on
			 */
			bw.write("@relation  weka");
			bw.newLine();
			bw.write("@attribute SpectralCentroidOverallStandardDeviation numeric");
			bw.newLine();
			bw.write("@attribute SpectralFluxOverallAverage numeric");
			bw.newLine();
			bw.write("@attribute ZeroCrossingsOverallStandardDeviation numeric");
			bw.newLine();
			bw.write("@attribute ismusic {yes, no}");
			bw.newLine();
			bw.write("@data");
			bw.newLine();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();

			NodeList dataSetLst = doc.getElementsByTagName("data_set");
			audiosetNames = new String[dataSetLst.getLength()];

			NodeList featureList = doc.getElementsByTagName("feature");

			String[] musicFeat1 = new String[21];
			String[] musicFeat2 = new String[21];
			String[] musicFeat3 = new String[21];
			String[] musicFileNames = new String[21];

			String[] speechFeat1 = new String[21];
			String[] speechFeat2 = new String[21];
			String[] speechFeat3 = new String[21];
			String[] speechFileNames = new String[21];

			int noOfMusic = 0;
			int noOfSpeech = 0;
			
			/* Parsing the xml document output of jAudio to extract the required
			 * feature values, appropriately
			 */
			for (int s = 0; s < dataSetLst.getLength(); s++) {
				
				Node dataSet = dataSetLst.item(s);
				Element dael= (Element) dataSet;
				NodeList fname = dael.getElementsByTagName("data_set_id");
				
				Element fnameEl = (Element) fname.item(0);
				NodeList fnameNode = fnameEl .getChildNodes();
				String f= ((Node) fnameNode.item(0)).getNodeValue();
				
				audiosetNames[s] = f.substring(f.lastIndexOf('\\')+1);
				
				int index=f.indexOf('.');

				char[]filen=f.toCharArray();
				index--;
				
				// Logic to get only the filename from the absolute path
				while(filen[index]!='\\')
				{
					index=index-1;
				}
				
				int j=0;
				System.out.println(filen);
				index++;
				
				int start=index;
				start=start+2;
				String FileNOStr="";
				
				while(filen[start]!='.')
				{
					FileNOStr+=filen[start];
					start++;
				}
				
				int FileNo = Integer.parseInt(FileNOStr);
								
				if(filen[index]=='m')
				{
					noOfMusic++;
					musicFileNames[FileNo] = filen[index] +""+filen[index+1] + filen[index+2] + filen[index+3];
					if (dataSet.getNodeType() == Node.ELEMENT_NODE) {
						
						for (int fIndex = 0 + (s*featureList.getLength()/dataSetLst.getLength()) ; fIndex < featureList.getLength()/dataSetLst.getLength()+ (s*featureList.getLength()/dataSetLst.getLength()); fIndex++) {
							
							Node feature = featureList.item(fIndex);
							
							if (feature.getNodeType() == Node.ELEMENT_NODE) {
								
								Element featureElement= (Element) feature;
								NodeList name = featureElement.getElementsByTagName("name");
								
								Element nameElement = (Element) name.item(0);
								NodeList nameNode = nameElement .getChildNodes();
								
								String b= ((Node) nameNode.item(0)).getNodeValue();
								
								if(b.equals("Spectral Centroid Overall Standard Deviation"))
								{
									NodeList v = featureElement.getElementsByTagName("v");
									Element valueElement = (Element) v.item(0);
									NodeList valueNode = valueElement .getChildNodes();
									String c= ((Node) valueNode .item(0)).getNodeValue();
									
									musicFeat1[FileNo]=c;
								}
								
								if(b.equals("Spectral Flux Overall Average"))
								{
									NodeList v = featureElement.getElementsByTagName("v");
									Element valueElement = (Element) v.item(0);
									NodeList valueNode = valueElement .getChildNodes();
									String c= ((Node) valueNode .item(0)).getNodeValue();
									musicFeat2[FileNo]=c;
								}
								
								if(b.equals("Zero Crossings Overall Standard Deviation"))
								{
									NodeList v = featureElement.getElementsByTagName("v");
									Element valueElement = (Element) v.item(0);
									NodeList valueNode = valueElement .getChildNodes();
									String c= ((Node) valueNode .item(0)).getNodeValue();
									musicFeat3[FileNo]=c;
								}
							}
						}
					}
				}
				
				else
				{
					if (dataSet.getNodeType() == Node.ELEMENT_NODE) {

						noOfSpeech++;
						speechFileNames[FileNo] = filen[index] +""+filen[index+1] + filen[index+2] + filen[index+3];
						
						for (int fIndex = 0 + (s*featureList.getLength()/dataSetLst.getLength()) ; fIndex < featureList.getLength()/dataSetLst.getLength()+ (s*featureList.getLength()/dataSetLst.getLength()); fIndex++) {
							
							Node feature = featureList.item(fIndex);
							
							if (feature.getNodeType() == Node.ELEMENT_NODE) {
								
								Element featureElement= (Element) feature;
								NodeList name = featureElement.getElementsByTagName("name");

								Element nameElement = (Element) name.item(0);
								NodeList nameNode = nameElement .getChildNodes();
								String b= ((Node) nameNode.item(0)).getNodeValue();
								
								if(b.equals("Spectral Centroid Overall Standard Deviation"))
								{
									NodeList v = featureElement.getElementsByTagName("v");
									Element valueElement = (Element) v.item(0);
									
									NodeList valueNode = valueElement .getChildNodes();
									String c= ((Node) valueNode .item(0)).getNodeValue();

									speechFeat1[FileNo]=c;
								}
								
								if(b.equals("Spectral Flux Overall Average"))
								{
									NodeList v = featureElement.getElementsByTagName("v");
									Element valueElement = (Element) v.item(0);
									
									NodeList valueNode = valueElement .getChildNodes();
									String c= ((Node) valueNode .item(0)).getNodeValue();
									
									speechFeat2[FileNo]=c;
								}
								
								if(b.equals("Zero Crossings Overall Standard Deviation"))
								{
									NodeList v = featureElement.getElementsByTagName("v");
									Element valueElement = (Element) v.item(0);
									NodeList valueNode = valueElement .getChildNodes();
									String c= ((Node) valueNode .item(0)).getNodeValue();
									speechFeat3[FileNo]=c;
								}
							}
						}
					}
				  }	
				}	
			
			/* Logic for equally splitting up the music and audio files in the 
			 * training data set which must be two-3rd of the input data from user.
			 */
			int noOfSpeechToInclude = (int) Math.round((dataSetLst.getLength()/3));
			int noOfMusicToInclude = (int) Math.round((dataSetLst.getLength()*0.67)) - noOfSpeechToInclude;
		
			noOfMusicToInclude = (noOfMusic < noOfMusicToInclude)? noOfMusic : noOfMusicToInclude;
			noOfSpeechToInclude = (noOfSpeech < noOfSpeechToInclude)? noOfSpeech : noOfSpeechToInclude;

			int includedNoOfMusic = 0;
			int includedNoOfSpeech = 0;
			
			int i,j;
			int k =0 ;
			
			// Music files in the training dataset are written first
			for(i = 0;((i<20)&&(includedNoOfMusic<noOfMusicToInclude));i++)
			{
				if(musicFeat1[i+1]!= null)
				{
					bw.write(musicFeat1[i+1]+",");
					bw.write(" "+musicFeat2[i+1]+",");
					bw.write(" "+musicFeat3[i+1]+","); 
					bw.write(" "+"yes");
					bw.newLine();
					includedNoOfMusic++;

				}
			}
			
			// Speech files in the training dataset are written next
			for(j = 0;j<20&&includedNoOfSpeech<noOfSpeechToInclude;j++)
			{
				if(speechFeat1[j+1]!= null)
				{
					bw.write(speechFeat1[j+1]+",");
					bw.write(" "+speechFeat2[j+1]+",");
					bw.write(" "+speechFeat3[j+1]+","); 
					bw.write(" "+"no");
					bw.newLine();
					includedNoOfSpeech++;

				}
			}
			
			// Remaining test data (both music & speech are written last in weka.txt file in respective order
			for(;i<20;i++)
			{
				if(musicFeat1[i+1]!= null)
				{
					bw.write(musicFeat1[i+1]+",");
					bw.write(" "+musicFeat2[i+1]+",");
					bw.write(" "+musicFeat3[i+1]+","); 
					bw.write(" "+"yes");
					bw.newLine();
					testFileNames[k] = musicFileNames[i+1];
					k++;
				}
			}
			
			for(;j<20;j++)
			{
				if(speechFeat1[j+1]!= null)
				{
					bw.write(speechFeat1[j+1]+",");
					bw.write(" "+speechFeat2[j+1]+",");
					bw.write(" "+speechFeat3[j+1]+","); 
					bw.write(" "+"no");
					bw.newLine();
					testFileNames[k] = speechFileNames[j+1];
					k++;
				}
			}
			
			/*for(int p=0;p<20;p++)
			System.out.println(testFileNames[p]);
			*/
			bw.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Return the file names of test data as in the same order written in weka.txt
		return testFileNames;
	}
}