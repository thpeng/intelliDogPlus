package intelliDOG.ai.learning.ann;

import intelliDOG.ai.learning.ann.net.Constants;
import intelliDOG.ai.learning.ann.net.Layer;
import intelliDOG.ai.learning.ann.net.NeuralNet;
import intelliDOG.ai.learning.ann.net.TDTeacher;
import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetException;
import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetFileNotValidException;
import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetNotValidException;
import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetTeacherNotValidException;
import intelliDOG.ai.learning.rl.State;

import java.io.File;
import java.util.Properties;

public class NetAnn implements Ann {
	
	private NeuralNet nn;
	private TDTeacher teacher;
	private int inputNeurons = 87;
	private int hiddenNeurons = 44;
	private int outputNeurons = 1;
	private double alpha = 0.1;
	private double gamma = 0.9;
	private double lambda = 0.8;
	private double momentum = 0.0;
	private double alphaDecrease = 0.0;
	
	
	
	public NetAnn(){
		initNeuralNet();
	}
	
	public NetAnn(int inputNeurons, int hiddenNeurons, int outputNeurons, double alpha, double momentum, double gamma, double lambda, double alphaDecrease){
		this.inputNeurons = inputNeurons;
		this.hiddenNeurons = hiddenNeurons;
		this.outputNeurons = outputNeurons;
		this.alpha = alpha;
		this.momentum = momentum;
		this.gamma = gamma;
		this.lambda = lambda;
		this.alphaDecrease = alphaDecrease;
		initNeuralNet();
	}
	
	private void initNeuralNet(){
		try{
			//create new neural net
			this.nn = new NeuralNet();
			//add new inputLayer;
			this.nn.addLayer(new Layer(this.inputNeurons, Constants.INPUTLAYER, Constants.LINEARFUNCTION));
			//add hidden layer (only if needed -> hiddenNeurons > 0)
			if(this.hiddenNeurons > 0){
				this.nn.addLayer(new Layer(this.hiddenNeurons, Constants.HIDDENLAYER, Constants.SIGMOIDFUNCTION));
			}
			//add output layer
			this.nn.addLayer(new Layer(this.outputNeurons, Constants.OUTPUTLAYER, Constants.SIGMOIDFUNCTION));
			
			//add input types
			this.nn.setInputValueTypes(getInputTypes());
			
			//create a TD teacher
			this.teacher = new TDTeacher(this.alpha, this.momentum, 1, this.inputNeurons, gamma, lambda, this.alphaDecrease);
			//set the teacher for the net
			this.nn.setTeacher(this.teacher);
			
		}catch(NeuralNetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	@Override
	public double getValue(State s) {
		double output = 0.0;
		try {
			output = this.nn.calculateOutput(s.getInputVector())[0];
		} catch (NeuralNetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	@Override
	public void updateWeights(double td, double outpErr) {
		try {
			this.nn.learn(td, outpErr, true);
		} catch (NeuralNetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void decreaseAlpha(){
		this.alpha = this.teacher.decreaseAlpha();
	}
	
	@Override
	public Properties getProperties(){
		Properties p = new Properties();
		p.setProperty("alpha", Double.toString(this.alpha));
		p.setProperty("alphaDecrease", Double.toString(this.alphaDecrease));
		p.setProperty("gamma", Double.toString(this.gamma));
		p.setProperty("lambda", Double.toString(this.lambda));
		p.setProperty("momentum", Double.toString(this.momentum));
		
		return p;
	}
	
	@Override
	public void setProperties(Properties p){
		this.alpha = Double.parseDouble(p.getProperty("alpha"));
		this.alphaDecrease = Double.parseDouble(p.getProperty("alphaDecrease"));
		this.gamma = Double.parseDouble(p.getProperty("gamma"));
		this.lambda = Double.parseDouble(p.getProperty("lambda"));
		this.momentum = Double.parseDouble(p.getProperty("momentum"));
		
		//create a TD teacher
		this.teacher = new TDTeacher(this.alpha, this.momentum, 1, this.inputNeurons, gamma, lambda, this.alphaDecrease);
		//set the teacher for the net
		try {
			this.nn.setTeacher(this.teacher);
		} catch (NeuralNetTeacherNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void load(String fileName) {
		File f = new File(fileName);
		try {
			this.nn = new NeuralNet(f);
		} catch (NeuralNetFileNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void save(String fileName) {
		try {
			this.nn.saveNNToFile(fileName);
		} catch (NeuralNetNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NeuralNetFileNotValidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int[] getInputTypes(){
		int[] input = new int[87];
		
		input[0] = Constants.NNInputType_POS00ACTUALBOARD;
		input[1] = Constants.NNInputType_POS01ACTUALBOARD;
		input[2] = Constants.NNInputType_POS02ACTUALBOARD;
		input[3] = Constants.NNInputType_POS03ACTUALBOARD;
		input[4] = Constants.NNInputType_POS04ACTUALBOARD;
		input[5] = Constants.NNInputType_POS05ACTUALBOARD;
		input[6] = Constants.NNInputType_POS06ACTUALBOARD;
		input[7] = Constants.NNInputType_POS07ACTUALBOARD;
		input[8] = Constants.NNInputType_POS08ACTUALBOARD;
		input[9] = Constants.NNInputType_POS09ACTUALBOARD;
		input[10] = Constants.NNInputType_POS10ACTUALBOARD;
		input[11] = Constants.NNInputType_POS11ACTUALBOARD;
		input[12] = Constants.NNInputType_POS12ACTUALBOARD;
		input[13] = Constants.NNInputType_POS13ACTUALBOARD;
		input[14] = Constants.NNInputType_POS14ACTUALBOARD;
		input[15] = Constants.NNInputType_POS15ACTUALBOARD;
		input[16] = Constants.NNInputType_POS16ACTUALBOARD;
		input[17] = Constants.NNInputType_POS17ACTUALBOARD;
		input[18] = Constants.NNInputType_POS18ACTUALBOARD;
		input[19] = Constants.NNInputType_POS19ACTUALBOARD;
		input[20] = Constants.NNInputType_POS20ACTUALBOARD;
		input[21] = Constants.NNInputType_POS21ACTUALBOARD;
		input[22] = Constants.NNInputType_POS22ACTUALBOARD;
		input[23] = Constants.NNInputType_POS23ACTUALBOARD;
		input[24] = Constants.NNInputType_POS24ACTUALBOARD;
		input[25] = Constants.NNInputType_POS25ACTUALBOARD;
		input[26] = Constants.NNInputType_POS26ACTUALBOARD;
		input[27] = Constants.NNInputType_POS27ACTUALBOARD;
		input[28] = Constants.NNInputType_POS28ACTUALBOARD;
		input[29] = Constants.NNInputType_POS29ACTUALBOARD;
		input[30] = Constants.NNInputType_POS30ACTUALBOARD;
		input[31] = Constants.NNInputType_POS31ACTUALBOARD;
		input[32] = Constants.NNInputType_POS32ACTUALBOARD;
		input[33] = Constants.NNInputType_POS33ACTUALBOARD;
		input[34] = Constants.NNInputType_POS34ACTUALBOARD;
		input[35] = Constants.NNInputType_POS35ACTUALBOARD;
		input[36] = Constants.NNInputType_POS36ACTUALBOARD;
		input[37] = Constants.NNInputType_POS37ACTUALBOARD;
		input[38] = Constants.NNInputType_POS38ACTUALBOARD;
		input[39] = Constants.NNInputType_POS39ACTUALBOARD;
		input[40] = Constants.NNInputType_POS40ACTUALBOARD;
		input[41] = Constants.NNInputType_POS41ACTUALBOARD;
		input[42] = Constants.NNInputType_POS42ACTUALBOARD;
		input[43] = Constants.NNInputType_POS43ACTUALBOARD;
		input[44] = Constants.NNInputType_POS44ACTUALBOARD;
		input[45] = Constants.NNInputType_POS45ACTUALBOARD;
		input[46] = Constants.NNInputType_POS46ACTUALBOARD;
		input[47] = Constants.NNInputType_POS47ACTUALBOARD;
		input[48] = Constants.NNInputType_POS48ACTUALBOARD;
		input[49] = Constants.NNInputType_POS49ACTUALBOARD;
		input[50] = Constants.NNInputType_POS50ACTUALBOARD;
		input[51] = Constants.NNInputType_POS51ACTUALBOARD;
		input[52] = Constants.NNInputType_POS52ACTUALBOARD;
		input[53] = Constants.NNInputType_POS53ACTUALBOARD;
		input[54] = Constants.NNInputType_POS54ACTUALBOARD;
		input[55] = Constants.NNInputType_POS55ACTUALBOARD;
		input[56] = Constants.NNInputType_POS56ACTUALBOARD;
		input[57] = Constants.NNInputType_POS57ACTUALBOARD;
		input[58] = Constants.NNInputType_POS58ACTUALBOARD;
		input[59] = Constants.NNInputType_POS59ACTUALBOARD;
		input[60] = Constants.NNInputType_POS60ACTUALBOARD;
		input[61] = Constants.NNInputType_POS61ACTUALBOARD;
		input[62] = Constants.NNInputType_POS62ACTUALBOARD;
		input[63] = Constants.NNInputType_POS63ACTUALBOARD;
		input[64] = Constants.NNInputType_POS64ACTUALBOARD;
		input[65] = Constants.NNInputType_POS65ACTUALBOARD;
		input[66] = Constants.NNInputType_POS66ACTUALBOARD;
		input[67] = Constants.NNInputType_POS67ACTUALBOARD;
		input[68] = Constants.NNInputType_POS68ACTUALBOARD;
		input[69] = Constants.NNInputType_POS69ACTUALBOARD;
		input[70] = Constants.NNInputType_POS70ACTUALBOARD;
		input[71] = Constants.NNInputType_POS71ACTUALBOARD;
		input[72] = Constants.NNInputType_POS72ACTUALBOARD;
		input[73] = Constants.NNInputType_POS73ACTUALBOARD;
		input[74] = Constants.NNInputType_POS74ACTUALBOARD;
		input[75] = Constants.NNInputType_POS75ACTUALBOARD;
		input[76] = Constants.NNInputType_POS76ACTUALBOARD;
		input[77] = Constants.NNInputType_POS77ACTUALBOARD;
		input[78] = Constants.NNInputType_POS78ACTUALBOARD;
		input[79] = Constants.NNInputType_POS79ACTUALBOARD;
		
		input[80] = Constants.NNInputType_Card01OWN;
		input[81] = Constants.NNInputType_Card02OWN;
		input[82] = Constants.NNInputType_Card03OWN;
		input[83] = Constants.NNInputType_Card04OWN;
		input[84] = Constants.NNInputType_Card05OWN;
		input[85] = Constants.NNInputType_Card06OWN;
		
		input[86] = Constants.NNInputType_PlayerToValidate;
		
		return input;

	}

}
