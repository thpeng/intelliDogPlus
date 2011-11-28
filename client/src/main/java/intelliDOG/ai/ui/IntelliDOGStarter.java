package intelliDOG.ai.ui;

import intelliDOG.ai.utils.IntelliOnlyArena;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * A JFrame extension which is used to start games and see some statistics
 *
 */
public class IntelliDOGStarter extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7333794369978190684L;
	
	private JCheckBox cbSituation;
	private JCheckBox cbGUI;
	private JCheckBox cbEnableProb;
	private JCheckBox cbChart;
	private JSpinner spTimeout;
	private JSpinner spGames;
	
	private JComboBox[] cobPlayers;
	private JComboBox[] cobEvaluators;
	private JTextField[] tfNamePlayers;
	private String[] botTypes;
	private String[] evalTypes;
	private String[] playerNames;

	private JButton bStart;
	private JButton bStatistics;
	private JButton bExit;
	
	private JLabel lGameNr;
	private JLabel lAvgWinPercent;
	private JLabel lAvgWinHeight;
	private JLabel lAvgTime;
	private JLabel lAvgNrOfMoves;
	private JLabel lAvgTimePerMove;
	
	private JTable tblStatistics;
	
	private GameWindowImpl gwInitSituation;
	
	private byte[] boardForInitSit;
	private int[][] cardsForInitSit;
	private List<Integer> usedCardsForInitSit;
	
	private JComboBox cobPlayersChart; 
	
	private StatisticsChart sc;

	private RLParametersWindow rlpw;
	
	private IntelliDOGStarter(){
		//This call will preload the card's images in a background thread
		CardMapper.getInstance();
		botTypes = new String[] {"SimpleBot" ,"SimpleBotV2", "AlphaBeta_V1", "AlphaBeta_V2", "ComplexBot", "N_StepBot", "RandomBot", 
				 "Star1_Bot","Star2_Bot","Star2Bot_V1", "TD_lambda_Bot", "ThreeStepBot", "TwoStepBot", "CheatingBot","MC_HeuRsubj100","MC_HeuRobj100","MC_RdmRSubj100","MC_RdmRobj100"};
		evalTypes = new String[] {"Standard", "SE", "SE_V2", "SE_V3", "SE_V4", "SE_V5", "File - SE_V5 prop"};
		
		playerNames = new String[] {"Player 1", "Player 2", "Player 3", "Player 4"};
		
		JPanel northPanel = new JPanel(new GridLayout(1,2));
		initNorthPanel(northPanel);
		
		JPanel southPanel = new JPanel();
		initSouthPanel(southPanel);
		
		JPanel eastPanel = new JPanel();
		initEastPanel(eastPanel);
		
		JPanel settingsPanel = new JPanel(new BorderLayout());
		settingsPanel.add(northPanel, BorderLayout.NORTH);
		settingsPanel.add(southPanel, BorderLayout.SOUTH);
		
		JPanel westPanel = new JPanel(new BorderLayout());
		westPanel.setPreferredSize(new Dimension(250, 400));
		JPanel statisticsPanel = new JPanel();
		initStatisticsPanel(statisticsPanel);
		westPanel.add(statisticsPanel, BorderLayout.NORTH);
		initTblStatistics();
		JScrollPane scrollPane = new JScrollPane(tblStatistics);
		tblStatistics.setFillsViewportHeight(true);
		westPanel.add(scrollPane, BorderLayout.CENTER);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(settingsPanel, BorderLayout.CENTER);
		mainPanel.add(eastPanel, BorderLayout.EAST);
		mainPanel.add(westPanel, BorderLayout.WEST);
		
		this.add(mainPanel);
		
		this.setLocation(new Point(200, 200));
		this.pack();
		this.setTitle("intelliDOG");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	private void initTblStatistics() {
		Object[] colNames = new Object[]{"Nr.", "Win", "Res", "Time (s)", "# Moves"};
		DefaultTableModel dtm = new DefaultTableModel(colNames, 0){
			private static final long serialVersionUID = 4748692690199151862L;
			@Override
			public boolean isCellEditable(int rowIndex, int vColIndex) {
				return false;
			}
		};
		tblStatistics = new JTable(dtm);
	}

	private void initStatisticsPanel(JPanel statisticsPanel) {
		statisticsPanel.setLayout(new GridLayout(6,2));
		statisticsPanel.setBackground(Color.PINK);
		statisticsPanel.setPreferredSize(new Dimension(250, 200));
		
		lGameNr = new JLabel();
		lAvgWinPercent = new JLabel();
		lAvgWinHeight = new JLabel();
		lAvgTime = new JLabel();
		lAvgNrOfMoves = new JLabel();
		lAvgTimePerMove = new JLabel();
		
		statisticsPanel.add(new JLabel("Game #: "));
		statisticsPanel.add(lGameNr);
		statisticsPanel.add(new JLabel("Win % (T1/T2): "));
		statisticsPanel.add(lAvgWinPercent);
		statisticsPanel.add(new JLabel("Avg. win height: "));
		statisticsPanel.add(lAvgWinHeight);
		statisticsPanel.add(new JLabel("Avg. time: "));
		statisticsPanel.add(lAvgTime);
		statisticsPanel.add(new JLabel("Avg. # of moves: "));
		statisticsPanel.add(lAvgNrOfMoves);
		statisticsPanel.add(new JLabel("Avg. time/move: "));
		statisticsPanel.add(lAvgTimePerMove);
		
		
	}

	private void initNorthPanel(JPanel northPanel){
		//northPanel.setBackground(Color.BLUE);
		northPanel.setPreferredSize(new Dimension(700, 200));
		
		JPanel northWestPanel = new JPanel(new GridLayout(4,1));
		northWestPanel.setBorder(BorderFactory.createTitledBorder("Framework"));
		cbSituation = new JCheckBox("Run with selected situation");
		cbSituation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!cbSituation.isSelected()){ return; } //If the checkbox was checked (and will now be unchecked) don't open the GameWindow
				if(gwInitSituation == null){
					gwInitSituation = new GameWindowImpl(getBotNames(), true);
				}else{
					gwInitSituation.setVisible(true);
				}
			}});
		
		northWestPanel.add(cbSituation); 
		
		JPanel northWestChart = new JPanel(new GridLayout(1,2));
		
		cobPlayersChart = new JComboBox();
		cobPlayersChart = new JComboBox(playerNames); 
		cobPlayersChart.setVisible(false); 
		northWestChart.add(cobPlayersChart); 
		
		// FIXME
		JButton fakeButton = new JButton("test");
		fakeButton.setVisible(false); 
		northWestChart.add(fakeButton); 
		
		cbEnableProb = new JCheckBox("Enable probability calculation"); 
		//cbEnableProb.setVisible(true); 
		cbEnableProb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				if(!cbEnableProb.isSelected()) {
					cbChart.setSelected(false); 
					cbChart.setVisible(false); 
					cobPlayersChart.setVisible(false); 
				} else
					cbChart.setVisible(true); 
			}});
		
		cbChart = new JCheckBox("Display card probabilities");
		cbChart.setVisible(false); 
		
		cbChart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!cbChart.isSelected()){ 
					cobPlayersChart.setVisible(false); 
				} else
					cobPlayersChart.setVisible(true); 
			}});
		
		northWestPanel.add(cbEnableProb); 
		northWestPanel.add(cbChart); 
		northWestPanel.add(northWestChart); 
		
		JPanel northEastPanel = new JPanel(new GridLayout(4,2));
		northEastPanel.setBorder(BorderFactory.createTitledBorder("Options"));
		cbGUI = new JCheckBox("GUI");
		cbGUI.setEnabled(true);  
		spTimeout = new JSpinner(new SpinnerNumberModel(500, 0, 10000, 100));
		spGames = new JSpinner(new SpinnerNumberModel(1, 1, 10000000, 1));
		northEastPanel.add(new JLabel());
		northEastPanel.add(cbGUI);
		northEastPanel.add(new JLabel("Timeout: "));
		northEastPanel.add(spTimeout);
		northEastPanel.add(new JLabel("Nr. of Games: "));
		northEastPanel.add(spGames);
		
		northPanel.add(northWestPanel);
		northPanel.add(northEastPanel);
	}
	
	private void initSouthPanel(JPanel southPanel){
		//southPanel.setBackground(Color.RED);
		southPanel.setLayout(new GridLayout(4,1));
		southPanel.setPreferredSize(new Dimension(700, 200));
		southPanel.setBorder(BorderFactory.createTitledBorder("Players: "));
		cobPlayers = new JComboBox[4];
		cobEvaluators = new JComboBox[4];
		tfNamePlayers = new JTextField[4];
		
		for(int i = 0; i < 4; i++){
			JPanel pPi = new JPanel(new GridLayout(1,7));
			cobPlayers[i] = new JComboBox(botTypes);
			cobEvaluators[i] = new JComboBox(evalTypes);
			tfNamePlayers[i] = new JTextField("Bot" + (i + 1), 12);
			pPi.add(new JLabel("Player " + (i + 1) + ": "));
			JPanel temp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
			temp.add(new JLabel("Type: "));
			pPi.add(temp);
			pPi.add(cobPlayers[i]);
			temp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
			temp.add(new JLabel("Evaluator: "));
			pPi.add(temp);
			pPi.add(cobEvaluators[i]);
			temp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
			temp.add(new JLabel("Name: "));
			pPi.add(temp);
			pPi.add(tfNamePlayers[i]);
			southPanel.add(pPi);
		}
	}
	
	private void initEastPanel(JPanel eastPanel){
		//eastPanel.setBackground(Color.GREEN);
		eastPanel.setPreferredSize(new Dimension(120, 400));
		eastPanel.setLayout(new GridLayout(20, 1));
		
		bStatistics = new JButton("Statistics");
		//bStatistics.setEnabled(false);
		bStart = new JButton("Start");
		bExit = new JButton("Exit");
		addListeners();
		
		for(int i = 0; i < 14; i++){
			eastPanel.add(new JLabel());
		}
		eastPanel.add(bStatistics);
		eastPanel.add(new JLabel());
		eastPanel.add(bStart);
		eastPanel.add(new JLabel());
		eastPanel.add(bExit);
		eastPanel.add(new JLabel());
	}
	
	private void addListeners(){
		bStart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				resetStats();
				String[] botClassNames = new String[4];
				String[] botNames = new String[4];
				boolean forTD = false;
				for(int i = 0; i < 4; i++){
					botClassNames[i] = "intelliDOG.ai.bots." + cobPlayers[i].getSelectedItem().toString();
					botNames [i] = tfNamePlayers[i].getText();
					if(botClassNames[i].contains("TD_lambda_Bot")){
						forTD = true;
					}
				}
				if(cbSituation.isSelected()){
					if(gwInitSituation.wasSuccessful()) {
						boardForInitSit = gwInitSituation.getBoard();
						cardsForInitSit = gwInitSituation.getCards();
						usedCardsForInitSit = gwInitSituation.getUsedCards();
					}else{ cbSituation.setSelected(false); }
				}
				if(forTD){
					rlpw = new RLParametersWindow();
					
			    	rlpw.addWindowListener(new WindowListener(){
						@Override
						public void windowActivated(WindowEvent e) {
							// TODO Auto-generated method stub
						}
						@Override
						public void windowClosed(WindowEvent e) {
							//start the game
							IntelliOnlyArena ioa = new IntelliOnlyArena(getThis());
							ioa.start();
						}
						@Override
						public void windowClosing(WindowEvent e) {
							// TODO Auto-generated method stub
						}
						@Override
						public void windowDeactivated(WindowEvent e) {
							// TODO Auto-generated method stub
						}
						@Override
						public void windowDeiconified(WindowEvent e) {
							// TODO Auto-generated method stub
						}
						@Override
						public void windowIconified(WindowEvent e) {
							// TODO Auto-generated method stub
						}
						@Override
						public void windowOpened(WindowEvent e) {
							// TODO Auto-generated method stub
						}
			    	});
				}else{
					//start the game
					IntelliOnlyArena ioa = new IntelliOnlyArena(getThis());
					ioa.start();
				}
			}});
		
		bExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}});
		
		bStatistics.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/*Statistics.printTimeTable();
				Statistics.clearTime();*/
				if(sc == null){
					sc = new StatisticsChart();
				}else{
					sc.setVisible(true);
				}
			}});
	}
	
	/**
	 * checks if the <class>JCheckBox</class> cbGUI is selected
	 * @return true if cbGUI is selected, false otherwise
	 */
	public boolean isGUISelected(){
		return cbGUI.isSelected();
	}

	/**
	 * checks if the <class>JCheckBox</class> cbSituation is selected
	 * @return true if cbSituation is selected, false otherwise
	 */
	public boolean isSituation(){
		return cbSituation.isSelected();
	}
	
	/**
	 * checks if the <class>JCheckBox</class> cbChart is selected
	 * @return true if cbChart is selected, false otherwise
	 */
	public boolean isChartSelected(){
		return cbChart.isSelected();
	}
	 
	/**
	 * checks if the <class>JCheckBox</class> cbEnableProb is selected
	 * @return true if cbEnableProb is selected, false otherwise
	 */
	public boolean isProbSelected(){
		return cbEnableProb.isSelected();
	}
	
	/**
	 * return the player whose probability is being observed 
	 */
	public byte probFromPointOfView(){
		return (byte)(cobPlayersChart.getSelectedIndex()+1);
	}
	
	
	/**
	 * gets the timeout from the <class>JSpinner</class> spTimeout
	 * @return the value of spTimeout
	 */
	public int getTimeout(){
		return (Integer)spTimeout.getValue();
	}
	
	/**
	 * gets the nr of games form the <class>JSpinner</class> spGames
	 * @return the value of spGames
	 */
	public int getNrOfGames(){
		return (Integer)spGames.getValue();
	}
	
	/**
	 * gets the types of the bots (selected in the <class>JComboBox</class>es)
	 * @return the selected items (with an the prefix for the right package)
	 */
	public String[] getBotClassNames(){
		String[] botClassNames = new String[4];
		for(int i = 0; i < 4; i++){
			botClassNames[i] = "intelliDOG.ai.bots." + cobPlayers[i].getSelectedItem().toString();
		}
		return botClassNames;
	}
	
	/**
	 * gets the names of the bots (specified in the <class>JTextField</class>s)
	 * @return the bot names
	 */
	public String[] getBotNames(){
		String[] botNames = new String[4];
		for(int i = 0; i < 4; i++){
			botNames[i] = tfNamePlayers[i].getText();
		}
		return botNames;
	}
	
	/**
	 * gets the types of the evaluators (selected in the <class>JComboBox</class>es)
	 * @return the selected items (with some name replacements and a prefix
	 * for the right package)
	 */
	public String[] getEvaluators(){
		String[] evaluators = new String[4];
		for(int i = 0; i < 4; i++){
			if(cobEvaluators[i].getSelectedItem().toString().equals("Standard")){
				evaluators[i] = "Standard";
			}else{
				evaluators[i] = "intelliDOG.ai.evaluators." + cobEvaluators[i].getSelectedItem().toString().replace("_", "").replace("SE", "SimpleEvaluator");
			}
		}
		return evaluators;
	}
	
	private IntelliDOGStarter getThis(){
		return this;
	}
	
	/**
	 * method to update the game statistics
	 * @param gameNr the number of the current game
	 * @param winningTeam the team that has won
	 * @param pieceCount1 the pieces in heaven of team one
	 * @param pieceCount2 the pieces in heaven of team two
	 * @param time the duration the game took
	 * @param moves the number of moves that where made in this game
	 */
	public void addGameStat(int gameNr, int winningTeam, int pieceCount1, int pieceCount2, double time, int moves){
		//Object[] colNames = new Object[]{"Nr.", "Win", "Res", "Time", "# Moves"};
		DefaultTableModel dtm = (DefaultTableModel)tblStatistics.getModel();
		dtm.addRow(new Object[] {gameNr, winningTeam, pieceCount1 + "/" + pieceCount2, Math.round(time * 100.) / 100., moves});
		
		lGameNr.setText("" + gameNr);
		double winPerc = 0;
		double resultAvg1 = 0;
		double resultAvg2 = 0;
		double timeAvg = 0;
		int moveAvg = 0;
		double timePMoveAvg = 0;
		
		for(int i = 0; i < dtm.getRowCount(); i++){
			winPerc += (Integer)dtm.getValueAt(i, 1) == 1 ? 1 : 0;
			resultAvg1 += Integer.parseInt(((String)dtm.getValueAt(i, 2)).substring(0, 1));
			resultAvg2 += Integer.parseInt(((String)dtm.getValueAt(i, 2)).substring(2, 3));
			double t = (Double)dtm.getValueAt(i, 3);
			int m = (Integer)dtm.getValueAt(i, 4);
			timeAvg += t;
			moveAvg += m;
			timePMoveAvg += t / m;
		}
		
		winPerc = Math.round((winPerc / gameNr * 100.0) * 100.) / 100.;
		resultAvg1 = Math.round((resultAvg1 / gameNr) * 100.) / 100.;
		resultAvg2 = Math.round((resultAvg2 / gameNr) * 100.) / 100.;
		timeAvg = Math.round((timeAvg / gameNr) * 100.) / 100.;
		moveAvg = moveAvg / gameNr;
		timePMoveAvg = Math.round((timePMoveAvg / gameNr) * 10000.) / 10.;
		
		lAvgWinPercent.setText("" + winPerc + "/" + (100 - winPerc) + "%");
		lAvgWinHeight.setText(resultAvg1 + "/" + resultAvg2);
		lAvgTime.setText("" + timeAvg);
		lAvgNrOfMoves.setText("" + moveAvg);
		lAvgTimePerMove.setText("" + timePMoveAvg + " ms");
		
		if(sc != null && sc.isVisible()){
			sc.addData(gameNr, winningTeam, pieceCount1, pieceCount2, Math.round(time * 100.) / 100., moves, timeAvg, moveAvg);
		}
	}
	
	/**
	 * this method will reset all statistics
	 */
	private void resetStats(){
		DefaultTableModel dtm = (DefaultTableModel)tblStatistics.getModel();
		int rowCount = dtm.getRowCount() - 1;
		for(int i = rowCount; i >= 0; i--){
			dtm.removeRow(i);
		}
		lGameNr.setText("");
		lAvgWinPercent.setText("");
		lAvgWinHeight.setText("");
		lAvgTime.setText("");
		lAvgNrOfMoves.setText("");
		lAvgTimePerMove.setText("");
		
		if(sc != null && sc.isVisible()){
			sc.resetData();
		}
	}
	
	/**
	 * @return the boardForInitSit
	 */
	public byte[] getBoardForInitSit() {
		return boardForInitSit;
	}

	/**
	 * @return the cardsForInitSit
	 */
	public int[][] getCardsForInitSit() {
		return cardsForInitSit;
	}
	
	/**
	 * 
	 * @return the usedCardsForInitSit
	 */
	public List<Integer> getUsedCardsForInitSit() {
		return usedCardsForInitSit;
	}
	
	public void showPropertiesErrorDialog(){
		int result = JOptionPane.showConfirmDialog(this, 
				"Your property file seems to be incomplete or in a wrong format!\nYou can now choose the file(s) to load again!", 
				"Error with Properties", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.OK_OPTION){
			IntelliOnlyArena ioa = new IntelliOnlyArena(getThis());
			ioa.start();
		}
	}
	
	public File showFileDialog(String title){
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showDialog(this, title);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			return fc.getSelectedFile();
		}else{
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new IntelliDOGStarter();
	}
	
	/**
	 * @return the rlpw
	 */
	public RLParametersWindow getRlpw() {
		return rlpw;
	}
	
	public class RLParametersWindow extends JFrame{
		
		private JSpinner alphaTxt = new JSpinner(new SpinnerNumberModel(0.1, 0.001, 1.0, 0.01));
		private JSpinner alphaDecTxt = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 0.2, 0.01));
		private JSpinner alphaDecCycTxt = new JSpinner(new SpinnerNumberModel(1000, 0, 100000, 500));
		private JSpinner betaTxt = new JSpinner(new SpinnerNumberModel(-0.1, -0.1, 3.0, 0.1));
		private JSpinner betaIncTxt = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.1));
		private JSpinner betaIncCycTxt = new JSpinner(new SpinnerNumberModel(1000, 0, 100000, 500));
		private JSpinner gammaTxt = new JSpinner(new SpinnerNumberModel(0.9, 0.0, 1.0, 0.01));
		private JSpinner lambdaTxt = new JSpinner(new SpinnerNumberModel(0.8, 0.0, 1.0, 0.01));
		private JSpinner momentumTxt = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.01));
		
		private JSpinner NN_InputTxt = new JSpinner(new SpinnerNumberModel(87, 87, 87, 0));
		private JSpinner NN_HiddenTxt = new JSpinner(new SpinnerNumberModel(44, 0, 87, 1));
		private JSpinner NN_OutputTxt = new JSpinner(new SpinnerNumberModel(1, 1, 1, 0));
		
		private JSpinner rewardWonTxt = new JSpinner(new SpinnerNumberModel(1, 0, 10, 1));
		private JSpinner rewardLostTxt = new JSpinner(new SpinnerNumberModel(0, -10, 0, 1));
		private JSpinner rewardOwnPieceInHeavenTxt = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		private JSpinner rewardPartnerPieceInHeavenTxt = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
		private JSpinner rewardOpponentPieceInHeavenTxt = new JSpinner(new SpinnerNumberModel(0, -10, 0, 1));
		
		private JSpinner saveCycle = new JSpinner(new SpinnerNumberModel(50000, 0, 1000000, 10000));
		
		private JCheckBox cbNormalized;
		private JCheckBox cbLearning;
		
		private File f;
		
		protected RLParametersWindow(){
			
			this.setLayout(new BorderLayout());
			
			//parameters panel
			JPanel northPanel = new JPanel(new GridLayout(9, 4, 5, 1));
			JLabel tempLabel = new JLabel("alpha");
			tempLabel.setToolTipText("Learning rate");
			northPanel.add(tempLabel);
			northPanel.add(alphaTxt);
			
			tempLabel = new JLabel("NN Input");
			tempLabel.setToolTipText("Number of Neurons of the input layer [by now only 87 is possible]");
			northPanel.add(tempLabel);
			northPanel.add(NN_InputTxt);
			
			tempLabel = new JLabel("alphaDecrease");
			tempLabel.setToolTipText("alpha will be multiplied by (1 - this value) every alphaDecCycle episodes.");
			northPanel.add(tempLabel);
			northPanel.add(alphaDecTxt);
			
			tempLabel = new JLabel("NN Hidden");
			tempLabel.setToolTipText("Number of Neurons of the hidden layer [0 - 87] (if 0, no hidden layer will be used)");
			northPanel.add(tempLabel);
			northPanel.add(NN_HiddenTxt);
			
			tempLabel = new JLabel("alphaDecCycle");
			tempLabel.setToolTipText("After how many episodes shall alpha be decreased?");
			northPanel.add(tempLabel);
			northPanel.add(alphaDecCycTxt);
			
			tempLabel = new JLabel("NN Output");
			tempLabel.setToolTipText("Number of Neurons of the output layer [by now only 1 is possible]");
			northPanel.add(tempLabel);
			northPanel.add(NN_OutputTxt);
			
			tempLabel = new JLabel("beta");
			tempLabel.setToolTipText("the exploration value tells how much weight shall be laid on exploration vs exploitation\n" +
					" -> 0.0 = pure exploration with uniform distribution / 3.0 = almost no exploratiion / -0.1 = no exploration at all");
			northPanel.add(tempLabel);
			northPanel.add(betaTxt);
			
			tempLabel = new JLabel("Reward Win");
			tempLabel.setToolTipText("The reward for a win [0 to 10]");
			northPanel.add(tempLabel);
			northPanel.add(rewardWonTxt);
			
			tempLabel = new JLabel("betaIncrease");
			tempLabel.setToolTipText("the value that is added to beta after betaIncCycle episodes.");
			northPanel.add(tempLabel);
			northPanel.add(betaIncTxt);
			
			tempLabel = new JLabel("Reward Loss");
			tempLabel.setToolTipText("The reward for a lost game [(-10) to 0]");
			northPanel.add(tempLabel);
			northPanel.add(rewardLostTxt);
			
			tempLabel = new JLabel("betaIncCycle");
			tempLabel.setToolTipText("after how many episodes shall beta be increased?");
			northPanel.add(tempLabel);
			northPanel.add(betaIncCycTxt);
			
			tempLabel = new JLabel("Reward Own Heaven");
			tempLabel.setToolTipText("The reward per own piece in heaven [0 to 10]");
			northPanel.add(tempLabel);
			northPanel.add(rewardOwnPieceInHeavenTxt);
			
			tempLabel = new JLabel("gamma");
			tempLabel.setToolTipText("The discount factor");
			northPanel.add(tempLabel);
			northPanel.add(gammaTxt);
			
			tempLabel = new JLabel("Reward Partner Heaven");
			tempLabel.setToolTipText("The reward per partner's piece in heaven [0 to 10]");
			northPanel.add(tempLabel);
			northPanel.add(rewardPartnerPieceInHeavenTxt);
			
			tempLabel = new JLabel("lambda");
			tempLabel.setToolTipText("Trace decay parameter");
			northPanel.add(tempLabel);
			northPanel.add(lambdaTxt);
			
			tempLabel = new JLabel("Reward Opponent Heaven");
			tempLabel.setToolTipText("The reward per opponent's piece in heaven [(-10) to 0]");
			northPanel.add(tempLabel);
			northPanel.add(rewardOpponentPieceInHeavenTxt);
			
			tempLabel = new JLabel("momentum");
			tempLabel.setToolTipText("momentum");
			northPanel.add(tempLabel);
			northPanel.add(momentumTxt);
			
			tempLabel = new JLabel("Learning save cycle");
			tempLabel.setToolTipText("After how many episodes shall the intermediate result of learning be saved?");
			northPanel.add(tempLabel);
			northPanel.add(saveCycle);
			
			this.add(northPanel, BorderLayout.NORTH);
			
			//buttons panel
			JPanel southPanel = new JPanel();
			
			cbLearning = new JCheckBox("Learning");
			cbLearning.setSelected(true);
			southPanel.add(cbLearning);
			
			JButton okButton = new JButton("Ok");
			okButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					disposeMe();
				}});
			southPanel.add(okButton);
			
			JButton loadButton = new JButton("Load");
			loadButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					setFile();
				}});
			southPanel.add(loadButton);
			
			cbNormalized = new JCheckBox("Simplified input");
			cbNormalized.setSelected(true);
			southPanel.add(cbNormalized);
			
			this.add(southPanel, BorderLayout.SOUTH);
			
			this.setLocation(new Point(200, 200));
			this.pack();
			this.setTitle("TD-lambda parameters");
			this.setVisible(true);			
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		}
		
		private void disposeMe(){
			this.dispose();
		}
		
		private void setFile(){
			this.f = showFileDialog("Load rl properties");
			this.setVisible(true);
		}
		
		public File getFile(){
			return this.f;
		}
		
		public boolean isNormalizedChecked(){
			return this.cbNormalized.isSelected();
		}
		
		public boolean isLearningChecked(){
			return this.cbLearning.isSelected();
		}

		/**
		 * @return the alphaTxt
		 */
		public double getAlpha() {
			return (Double)alphaTxt.getValue();
		}
		/**
		 * @return the alphaDecTxt
		 */
		public double getAlphaDec() {
			return (Double)alphaDecTxt.getValue();
		}
		/**
		 * @return the alphaDecCycTxt
		 */
		public int getAlphaDecCyc() {
			return (Integer)alphaDecCycTxt.getValue();
		}
		/**
		 * @return the betaTxt
		 */
		public double getBeta() {
			return (Double)betaTxt.getValue();
		}
		/**
		 * @return the betaIncTxt
		 */
		public double getBetaInc() {
			return (Double)betaIncTxt.getValue();
		}
		/**
		 * @return the betaIncCycTxt
		 */
		public int getBetaIncCyc() {
			return (Integer)betaIncCycTxt.getValue();
		}
		/**
		 * @return the gammaTxt
		 */
		public double getGamma() {
			return (Double)gammaTxt.getValue();
		}
		/**
		 * @return the lambdaTxt
		 */
		public double getLambda() {
			return (Double)lambdaTxt.getValue();
		}
		/**
		 * @return the momentumTxt
		 */
		public double getMomentum() {
			return (Double)momentumTxt.getValue();
		}
		/**
		 * @return the nN_InputTxt
		 */
		public int getNN_Input() {
			return (Integer)NN_InputTxt.getValue();
		}
		/**
		 * @return the nN_HiddenTxt
		 */
		public int getNN_Hidden() {
			return (Integer)NN_HiddenTxt.getValue();
		}
		/**
		 * @return the nN_OutputTxt
		 */
		public int getNN_Output() {
			return (Integer)NN_OutputTxt.getValue();
		}
		/**
		 * @return the rewardWonTxt
		 */
		public int getRewardWon() {
			return (Integer)rewardWonTxt.getValue();
		}
		/**
		 * @return the rewardLostTxt
		 */
		public int getRewardLost() {
			return (Integer)rewardLostTxt.getValue();
		}
		/**
		 * @return the rewardOwnPieceInHeavenTxt
		 */
		public int getRewardOwnPieceInHeaven() {
			return (Integer)rewardOwnPieceInHeavenTxt.getValue();
		}
		/**
		 * @return the rewardPartnerPieceInHeavenTxt
		 */
		public int getRewardPartnerPieceInHeaven() {
			return (Integer)rewardPartnerPieceInHeavenTxt.getValue();
		}
		/**
		 * @return the rewardOponentPieceInHeavenTxt
		 */
		public int getRewardOpponentPieceInHeaven() {
			return (Integer)rewardOpponentPieceInHeavenTxt.getValue();
		}
		/**
		 * 
		 * @return the saveCycle
		 */
		public int getSaveCycle(){
			return (Integer)saveCycle.getValue();
		}
		
	}

}
