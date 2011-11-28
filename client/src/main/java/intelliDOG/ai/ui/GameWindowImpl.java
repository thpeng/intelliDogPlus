package intelliDOG.ai.ui;

import intelliDOG.ai.framework.GameWindow;
import intelliDOG.ai.framework.Players;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A JFrame extension that shows the current game situation
 *
 */
public class GameWindowImpl extends JFrame implements GameWindow{

	private static final String resPath = "/";
	private static final String resBg = "board.png";
	private static final String resHole = "hole.png";
	private static final String resPiece_P1 = "sphere_red.png";
	private static final String resPiece_P2 = "sphere_green.png";
	private static final String resPiece_P3 = "sphere_blue.png";
	private static final String resPiece_P4 = "sphere_yellow.png";
	private static final Color resCol_P1 = Color.red;
	private static final Color resCol_P2 = Color.green;
	private static final Color resCol_P3 = Color.blue;
	private static final Color resCol_P4 = Color.yellow;
	
	private static final String resAdd = "Blue_Glass_Arrow.png";
	private static final String resRem = "Blue_Glass_Arrow_backwards.png";
	
	private static final long serialVersionUID = 4370316588905654795L;
	
	private ImagePanel boardPanel;
	private JLabel[] dummyFields;
	private ImageButton[] neutralFields;
	private ImageButton[] specialFieldsP1;
	private ImageButton[] specialFieldsP2;
	private ImageButton[] specialFieldsP3;
	private ImageButton[] specialFieldsP4;
	
	private JPanel cardPanel;
	private ImageCheckBox[][] cardPanels;
	private JLabel[] playerLabels;
	private String[] playerNames;
	
	private JPanel southPanel;
	
	private JPanel messagePanel;
	private JTextArea taMessages;
	
	private JPanel optionsPanel;
	private JCheckBox cbDisplayNumbers;
	
	private boolean isForSituationCreation;
	private boolean successful;
	private byte[] boardForInitSit;
	private int[][] cardsForInitSit;
	private List<Integer> usedCardsForInitSit;

	private GlassPanel glass;
	private JPanel glassCCCardsPanel;
	private JPanel gsUsedCardsPanel;
	
	private long timeout = 0;
	
	
	public GameWindowImpl(String[] playerNames){
		this(playerNames, false);
	}
	
	public GameWindowImpl(String[] playerNames, boolean isForSituationCreation){
		this.playerNames = playerNames;
		System.out.println(resPath+resBg);
		boardPanel = new ImagePanel(resPath + resBg);
		boardPanel.setLayout(new GridLayout(21,21));
		boardPanel.setPreferredSize(new Dimension(600, 600));
		initiateBoardPanel();
		
		cardPanel = new JPanel(new GridLayout(8,1));
		cardPanel.setPreferredSize(new Dimension(300, 600));
		initiateCardPanel();
		
		messagePanel = new JPanel(new BorderLayout());
		messagePanel.setPreferredSize(new Dimension(600, 100));
		taMessages = new JTextArea();
		taMessages.setEditable(false);
		JScrollPane spMessages = new JScrollPane(taMessages);
		messagePanel.add(spMessages, BorderLayout.CENTER);
		
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
		optionsPanel.setPreferredSize(new Dimension(300, 100));
		optionsPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 0, Color.DARK_GRAY));
		initiateOptionsPanel(isForSituationCreation);
		
		southPanel = new JPanel(new BorderLayout());
		southPanel.add(messagePanel, BorderLayout.CENTER);
		southPanel.add(optionsPanel, BorderLayout.EAST);
		
		//add panels to main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(boardPanel, BorderLayout.CENTER);
		mainPanel.add(cardPanel, BorderLayout.EAST);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		
		this.add(mainPanel);
		
		this.isForSituationCreation = isForSituationCreation;
		if(isForSituationCreation){
			addSituationCreationActionListeners();
			initOverlay();
			usedCardsForInitSit = new ArrayList<Integer>();
		}
		
		this.addWindowListener(new GameWindowListener());
		this.setLocation(new Point(100, 200));
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	private void initiateBoardPanel(){
		dummyFields = new JLabel[362];
		for(int i = 0; i < dummyFields.length; i++){
			dummyFields[i] = new JLabel();
			dummyFields[i].setBackground(Color.gray);
		}
		neutralFields = new ImageButton[64];
		for(int i = 0; i < neutralFields.length; i++){
			neutralFields[i] = new ImageButton(resPath + resHole);
			neutralFields[i].setOpaque(true);
			neutralFields[i].setForeground(Color.white);
			neutralFields[i].setText(" " + i);
			neutralFields[i].setFieldNumber(i);
		}
		specialFieldsP1 = new ImageButton[5];
		for(int i = 0; i < specialFieldsP1.length; i++){
			specialFieldsP1[i] = new ImageButton(resPath + resHole, resCol_P1);
			specialFieldsP1[i].setOpaque(true);
			specialFieldsP1[i].setOwner(Players.P1);
			if(i != 0){ 
				specialFieldsP1[i].setText(" " + (59 + i + 1 * 4));
				specialFieldsP1[i].setFieldNumber(59 + i + 1 * 4);
			}
			else{
				specialFieldsP1[i].setText(" " + 0);
				specialFieldsP1[i].setFieldNumber(0);
			}
		}
		specialFieldsP2 = new ImageButton[5];
		for(int i = 0; i < specialFieldsP2.length; i++){
			specialFieldsP2[i] = new ImageButton(resPath + resHole, resCol_P2);
			specialFieldsP2[i].setOpaque(true);
			specialFieldsP2[i].setOwner(Players.P2);
			if(i != 0){ 
				specialFieldsP2[i].setText(" " + (59 + i + 2 * 4));
				specialFieldsP2[i].setFieldNumber(59 + i + 2 * 4);
			}
			else{ 
				specialFieldsP2[i].setText(" " + 16);
				specialFieldsP2[i].setFieldNumber(16);
			}
		}
		specialFieldsP3= new ImageButton[5];
		for(int i = 0; i < specialFieldsP3.length; i++){
			specialFieldsP3[i] = new ImageButton(resPath + resHole, resCol_P3);
			specialFieldsP3[i].setOpaque(true);
			specialFieldsP3[i].setOwner(Players.P3);
			if(i != 0){ 
				specialFieldsP3[i].setText(" " + (59 + i + 3 * 4));
				specialFieldsP3[i].setFieldNumber(59 + i + 3 * 4);
			}
			else{
				specialFieldsP3[i].setText(" " + 32);
				specialFieldsP3[i].setFieldNumber(32);
			}
		}
		specialFieldsP4 = new ImageButton[5];
		for(int i = 0; i < specialFieldsP4.length; i++){
			specialFieldsP4[i] = new ImageButton(resPath + resHole, resCol_P4);
			specialFieldsP4[i].setOpaque(true);
			specialFieldsP4[i].setOwner(Players.P4);
			if(i != 0){ 
				specialFieldsP4[i].setText(" " + (59 + i + 4 * 4));
				specialFieldsP4[i].setFieldNumber(59 + i + 4 * 4);
			}
			else{ 
				specialFieldsP4[i].setText(" " + 48);
				specialFieldsP4[i].setFieldNumber(48);
			}
		}
		
		//row 1:
		for(int i = 0; i < 8; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(specialFieldsP2[0]);
		for(int i = 15; i >= 12; i--){
			boardPanel.add(neutralFields[i]);
		}
		for(int i = 8; i < 16; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 2:
		for(int i = 16; i < 24; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[17]);
		boardPanel.add(specialFieldsP2[1]);
		boardPanel.add(dummyFields[24]);
		boardPanel.add(dummyFields[25]);
		boardPanel.add(neutralFields[11]);
		for(int i = 26; i < 34; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 3:
		for(int i = 34; i < 42; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[18]);
		boardPanel.add(dummyFields[42]);
		boardPanel.add(specialFieldsP2[2]);
		boardPanel.add(dummyFields[43]);
		boardPanel.add(neutralFields[10]);
		for(int i = 44; i < 52; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 4:
		for(int i = 52; i < 60; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[19]);
		boardPanel.add(dummyFields[60]);
		boardPanel.add(specialFieldsP2[3]);
		boardPanel.add(dummyFields[61]);
		boardPanel.add(neutralFields[9]);
		for(int i = 62; i < 70; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 5:
		for(int i = 70; i < 78; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[20]);
		boardPanel.add(dummyFields[78]);
		boardPanel.add(specialFieldsP2[4]);
		boardPanel.add(dummyFields[79]);
		boardPanel.add(neutralFields[8]);
		for(int i = 80; i < 88; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 6:
		for(int i = 88; i < 95; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[21]);
		for(int i = 95; i < 100; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[7]);
		for(int i = 100; i < 107; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 7:
		for(int i = 107; i < 113; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[22]);
		for(int i = 113; i < 120; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[6]);
		for(int i = 120; i < 126; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 8:
		for(int i = 126; i < 131; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[23]);
		for(int i = 131; i < 140; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[5]);
		for(int i = 140; i < 145; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 9:
		for(int i = 28; i >= 24; i--){
			boardPanel.add(neutralFields[i]);
		}
		for(int i = 145; i < 156; i++){
			boardPanel.add(dummyFields[i]);
		}
		for(int i = 4; i > 0; i--){
			boardPanel.add(neutralFields[i]);
		}
		boardPanel.add(specialFieldsP1[0]);
		//row 10:
		boardPanel.add(neutralFields[29]);
		for(int i = 156; i < 174; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(specialFieldsP1[1]);
		boardPanel.add(neutralFields[63]);
		//row 11:
		boardPanel.add(neutralFields[30]);
		boardPanel.add(dummyFields[174]);
		boardPanel.add(specialFieldsP3[2]);
		boardPanel.add(specialFieldsP3[3]);
		boardPanel.add(specialFieldsP3[4]);
		for(int i = 175; i < 186; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(specialFieldsP1[4]);
		boardPanel.add(specialFieldsP1[3]);
		boardPanel.add(specialFieldsP1[2]);
		boardPanel.add(dummyFields[186]);
		boardPanel.add(neutralFields[62]);
		//row 12:
		boardPanel.add(neutralFields[31]);
		boardPanel.add(specialFieldsP3[1]);
		for(int i = 187; i < 205; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[61]);
		//row 13:
		boardPanel.add(specialFieldsP3[0]);
		for(int i = 33; i < 37; i++){
			boardPanel.add(neutralFields[i]);
		}
		for(int i = 205; i < 216; i++){
			boardPanel.add(dummyFields[i]);
		}
		for(int i = 56; i < 61; i++){
			boardPanel.add(neutralFields[i]);
		}
		//row 14:
		for(int i = 216; i < 221; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[37]);
		for(int i = 221; i < 230; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[55]);
		for(int i = 230; i < 235; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 15:
		for(int i = 235; i < 241; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[38]);
		for(int i = 241; i < 248; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[54]);
		for(int i = 248; i < 254; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 16:
		for(int i = 254; i < 261; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[39]);
		for(int i = 261; i < 266; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[53]);
		for(int i = 266; i < 273; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 17:
		for(int i = 273; i < 281; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[40]);
		boardPanel.add(dummyFields[281]);
		boardPanel.add(specialFieldsP4[4]);
		boardPanel.add(dummyFields[282]);
		boardPanel.add(neutralFields[52]);
		for(int i = 283; i < 291; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 18:
		for(int i = 291; i < 299; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[41]);
		boardPanel.add(dummyFields[299]);
		boardPanel.add(specialFieldsP4[3]);
		boardPanel.add(dummyFields[300]);
		boardPanel.add(neutralFields[51]);
		for(int i = 301; i < 309; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 19:
		for(int i = 309; i < 317; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[42]);
		boardPanel.add(dummyFields[317]);
		boardPanel.add(specialFieldsP4[2]);
		boardPanel.add(dummyFields[318]);
		boardPanel.add(neutralFields[50]);
		for(int i = 319; i < 327; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 20:
		for(int i = 327; i < 335; i++){
			boardPanel.add(dummyFields[i]);
		}
		boardPanel.add(neutralFields[43]);
		boardPanel.add(dummyFields[335]);
		boardPanel.add(dummyFields[336]);
		boardPanel.add(specialFieldsP4[1]);
		boardPanel.add(neutralFields[49]);
		for(int i = 337; i < 345; i++){
			boardPanel.add(dummyFields[i]);
		}
		//row 21:
		for(int i = 345; i < 353; i++){
			boardPanel.add(dummyFields[i]);
		}
		for(int i = 44; i <= 47; i++){
			boardPanel.add(neutralFields[i]);
		}
		boardPanel.add(specialFieldsP4[0]);
		for(int i = 353; i < 361; i++){
			boardPanel.add(dummyFields[i]);
		}
	}
	
	private void initiateCardPanel(){
		playerLabels = new JLabel[4];
		cardPanels = new ImageCheckBox[4][6];
		//Validierung der Spielernamen!
		boolean namesOk = playerNames != null && playerNames.length == 4;
		if(namesOk){
			for(int i = 0; i < playerNames.length; i++){
				if(namesOk && playerNames[i] != null && !playerNames[i].equals("") && !playerNames[i].equals(" ")){
					for(int j = i + 1; j < playerNames.length; j++){
						if(playerNames[i].equals(playerNames[j])){
							namesOk = false;
							break;
						}
					}
				}
				if(!namesOk){ break; }
			}
		}
		if(!namesOk){
			playerNames = new String[] {"Player 1", "Player 2", "Player 3", "Player 4"};
		}
		
		for(int i = 0; i < cardPanels.length; i++){
			JLabel actualPlayerLabel = new JLabel(playerNames[i] + ": ");
			switch(i + 1){
				case 1:
					actualPlayerLabel.setForeground(resCol_P1);
					break;
				case 2:
					actualPlayerLabel.setForeground(resCol_P2);
					break;
				case 3:
					actualPlayerLabel.setForeground(resCol_P3);
					break;
				case 4:
					actualPlayerLabel.setForeground(resCol_P4);
					break;
			}
			cardPanel.add(playerLabels[i] = actualPlayerLabel);
			JPanel playerCards = new JPanel(new GridLayout(1,6));
			for(int j = cardPanels[0].length - 1; j >= 0; j--){
				cardPanels[i][j] = new ImageCheckBox();
				playerCards.add(cardPanels[i][j]);
			}
			cardPanel.add(playerCards);
		}
		cardPanel.setBackground(Color.DARK_GRAY);
	}
	
	private void initiateOptionsPanel(boolean isForSituationCreation){
		cbDisplayNumbers = new JCheckBox("Display numbers");
		cbDisplayNumbers.setSelected(true);
		cbDisplayNumbers.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(cbDisplayNumbers.isSelected()){
					for(int i = 0; i < neutralFields.length; i++){
						neutralFields[i].setText(" " + i);
					}
					for(int i = 0; i < specialFieldsP1.length; i++){
						if(i != 0){ specialFieldsP1[i].setText(" " + (59 + i + 1 * 4)); }
						else{ specialFieldsP1[i].setText(" " + 0 + (specialFieldsP1[i].getText().contains("P") ? " P" : "")); }
					}
					for(int i = 0; i < specialFieldsP2.length; i++){
						if(i != 0){ specialFieldsP2[i].setText(" " + (59 + i + 2 * 4)); }
						else{ specialFieldsP2[i].setText(" " + 16 + (specialFieldsP2[i].getText().contains("P") ? " P" : "")); }
					}
					for(int i = 0; i < specialFieldsP3.length; i++){
						if(i != 0){ specialFieldsP3[i].setText(" " + (59 + i + 3 * 4)); }
						else{ specialFieldsP3[i].setText(" " + 32 + (specialFieldsP3[i].getText().contains("P") ? " P" : "")); }
					}
					for(int i = 0; i < specialFieldsP4.length; i++){
						if(i != 0){ specialFieldsP4[i].setText(" " + (59 + i + 4 * 4)); }
						else{ specialFieldsP4[i].setText(" " + 48 + (specialFieldsP4[i].getText().contains("P") ? " P" : "")); }
					}
				}else{
					for(int i = 0; i < neutralFields.length; i++){
						neutralFields[i].setText("");
					}
					for(int i = 0; i < specialFieldsP1.length; i++){
						if(i == 0 && specialFieldsP1[i].getText().contains("P")){
							specialFieldsP1[i].setText(" P");
						}else{
							specialFieldsP1[i].setText("");
						}
					}
					for(int i = 0; i < specialFieldsP2.length; i++){
						if(i == 0 && specialFieldsP2[i].getText().contains("P")){
							specialFieldsP2[i].setText(" P");
						}else{
							specialFieldsP2[i].setText("");
						}
					}
					for(int i = 0; i < specialFieldsP3.length; i++){
						if(i == 0 && specialFieldsP3[i].getText().contains("P")){
							specialFieldsP3[i].setText(" P");
						}else{
							specialFieldsP3[i].setText("");
						}
					}
					for(int i = 0; i < specialFieldsP4.length; i++){
						if(i == 0 && specialFieldsP4[i].getText().contains("P")){
							specialFieldsP4[i].setText(" P");
						}else{
							specialFieldsP4[i].setText("");
						}
					}
				}
				boardPanel.repaint();
			}});
		cbDisplayNumbers.setAlignmentX(Component.CENTER_ALIGNMENT);
		optionsPanel.add(cbDisplayNumbers);
		
		if(isForSituationCreation){
			JButton bSetCards = new JButton("Set cards");
			bSetCards.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					glass.setVisible(true);
				}});
			bSetCards.setAlignmentX(Component.CENTER_ALIGNMENT);
			optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			optionsPanel.add(bSetCards);
			
			JButton bokButton = new JButton("Ok");
			bokButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(validateSituation()){
						successful = true;
						getThis().dispose();
					}
				}});
			bokButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			optionsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			optionsPanel.add(bokButton);
		}
	}
	
	private void addSituationCreationActionListeners() {
		ActionListener spal = new SetPieceActionListener();
		MouseListener shfpml = new SetHomeFieldPieceMouseListener();
		for(ImageButton current : neutralFields){
			current.addActionListener(spal);
			current.setEnabled(true);
		}
		for(int i = 0; i < specialFieldsP1.length; i++){
			if(i == 0){
				specialFieldsP1[i].addMouseListener(shfpml);
			}else{
				specialFieldsP1[i].addActionListener(spal);
			}
			specialFieldsP1[i].setEnabled(true);
		}
		for(int i = 0; i < specialFieldsP2.length; i++){
			if(i == 0){
				specialFieldsP2[i].addMouseListener(shfpml);
			}else{
				specialFieldsP2[i].addActionListener(spal);
			}
			specialFieldsP2[i].setEnabled(true);
		}
		for(int i = 0; i < specialFieldsP3.length; i++){
			if(i == 0){
				specialFieldsP3[i].addMouseListener(shfpml);
			}else{
				specialFieldsP3[i].addActionListener(spal);
			}
			specialFieldsP3[i].setEnabled(true);
		}
		for(int i = 0; i < specialFieldsP4.length; i++){
			if(i == 0){
				specialFieldsP4[i].addMouseListener(shfpml);
			}else{
				specialFieldsP4[i].addActionListener(spal);
			}
			specialFieldsP4[i].setEnabled(true);
		}
	}
	
	private void initOverlay(){
		glass = new GlassPanel();
	    this.setGlassPane(glass);
	    glass.setLayout(new BorderLayout());
	    
	    //start emptyPanel
	    JPanel emptyPanel = new JPanel(new BorderLayout());
	    emptyPanel.setPreferredSize(cardPanel.getPreferredSize());
	    emptyPanel.setOpaque(false);
	    
	    //start gButtonPanel
	    JPanel gButtonPanel = new JPanel();
	    gButtonPanel.setPreferredSize(optionsPanel.getPreferredSize());
	    gButtonPanel.setBorder(optionsPanel.getBorder());
	    gButtonPanel.addMouseListener(new MouseAdapter(){});
	    
	    gButtonPanel.setLayout(new BoxLayout(gButtonPanel, BoxLayout.PAGE_AXIS));
	    
	    JButton gokButton = new JButton("Ok");
	    gokButton.setAlignmentX(Component.CENTER_ALIGNMENT);
	    gokButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				glass.setVisible(false);
			}});
	    
	    gButtonPanel.add(Box.createVerticalGlue());
	    gButtonPanel.add(gokButton);
	    gButtonPanel.add(Box.createVerticalGlue());
	    
	    emptyPanel.add(gButtonPanel, BorderLayout.SOUTH);
	    //end gButtonPanel
	    
	    glass.add(emptyPanel, BorderLayout.EAST);
	    //end emptyPanel
	    
	    //start gpCenter
	    GlassPanel gpCenter = new GlassPanel();
	    gpCenter.setAlpha(0.6f);
	    gpCenter.setLayout(new BorderLayout());
	    
	    //start glassCCCardsPanel
	    glassCCCardsPanel = new JPanel(new GridLayout(7, 8));
	    glassCCCardsPanel.setOpaque(false);
	    for(int i = 0; i < 53; i++){
	    	if(i == 52){
	    		ImageCheckBox icb = new ImageCheckBox(100);
	    		icb.setNrOfCardsToDistribute(8);
	    		glassCCCardsPanel.add(icb);
	    	}else{
	    		glassCCCardsPanel.add(new ImageCheckBox(i + 1));
	    	}
	    }
	    gpCenter.add(glassCCCardsPanel, BorderLayout.CENTER);
	    //end glassCCCardsPanel
	    
	    
	    //start glassCCButtons
	    JPanel glassCCButtons = new JPanel(new GridLayout(8,1));
	    glassCCButtons.setPreferredSize(new Dimension(cardPanel.getPreferredSize().width / 3, cardPanel.getPreferredSize().height));
	    glassCCButtons.setOpaque(false);
	    ActionListener arcal = new AddRemCardActionListener();
	    
	    for(int i = 0; i < 4; i++){
		    JPanel pbPanel = new JPanel();
		    pbPanel.setLayout(new BoxLayout(pbPanel, BoxLayout.LINE_AXIS));
		    pbPanel.setOpaque(false);
		    ImageButton ibAdd = new ImageButton(resPath + resAdd);
		    ImageButton ibRem = new ImageButton(resPath + resRem);
		    ibAdd.setPreferredSize(new Dimension(60, 37));
		    ibAdd.setMaximumSize(new Dimension(60, 37));
		    ibAdd.setMinimumSize(new Dimension(30, 16));
		    ibRem.setPreferredSize(new Dimension(60, 37));
		    ibRem.setMaximumSize(new Dimension(60, 37));
		    ibRem.setMinimumSize(new Dimension(30, 16));
		    
		    ibAdd.setActionCommand("Add_P" + (i + 1));
		    ibRem.setActionCommand("Rem_P" + (i + 1));
		    ibAdd.setEnabled(true);
		    ibRem.setEnabled(true);
		    
		    ibAdd.addActionListener(arcal);
		    ibRem.addActionListener(arcal);
		    
		    pbPanel.add(Box.createHorizontalGlue());
		    pbPanel.add(ibRem);
		    pbPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		    pbPanel.add(ibAdd);
		    
		    JPanel temp = new JPanel();
		    temp.setOpaque(false);
		    glassCCButtons.add(temp);
		    glassCCButtons.add(pbPanel);
	    }
	    
	    gpCenter.add(glassCCButtons, BorderLayout.EAST);
	    //end glassCCButtons
	    
	    
	    //start glassSouthPanel
	    JPanel glassSouthPanel = new JPanel(new BorderLayout());
	    glassSouthPanel.setPreferredSize(new Dimension(gpCenter.getPreferredSize().width / 3, optionsPanel.getPreferredSize().height));
	    glassSouthPanel.setOpaque(false);
	    
	    //start gsButtonPanel
	    JPanel gsButtonPanel = new JPanel();
	    gsButtonPanel.setLayout(new BoxLayout(gsButtonPanel, BoxLayout.PAGE_AXIS));
	    gsButtonPanel.setOpaque(false);
	    
	    ImageButton ibAdd = new ImageButton(resPath + resAdd);
	    ImageButton ibRem = new ImageButton(resPath + resRem);
	    ibAdd.setPreferredSize(new Dimension(60, 37));
	    ibAdd.setMaximumSize(new Dimension(60, 37));
	    ibAdd.setMinimumSize(new Dimension(30, 16));
	    ibRem.setPreferredSize(new Dimension(60, 37));
	    ibRem.setMaximumSize(new Dimension(60, 37));
	    ibRem.setMinimumSize(new Dimension(30, 16));
	    
	    ibAdd.setActionCommand("Add_Used");
	    ibRem.setActionCommand("Rem_Used");
	    ibAdd.setEnabled(true);
	    ibRem.setEnabled(true);
	    
	    ibAdd.addActionListener(arcal);
	    ibRem.addActionListener(arcal);
	    
	    gsButtonPanel.add(Box.createVerticalGlue());
	    gsButtonPanel.add(ibRem);
	    gsButtonPanel.add(Box.createRigidArea(new Dimension(0, 5)));
	    gsButtonPanel.add(ibAdd);
	    gsButtonPanel.add(Box.createVerticalGlue());
	    
	    glassSouthPanel.add(gsButtonPanel, BorderLayout.WEST);
	    //end gsButtonPanel
	    
	    //start gsUsedCardsPanel
	    gsUsedCardsPanel = new JPanel(new GridLayout(4, 28, 0, 3));
	    gsUsedCardsPanel.setOpaque(false);
	    for(int i = 0; i < 112; i++){
	    	ImageCheckBox icb = new ImageCheckBox();
	    	icb.setOpaque(false);
	    	gsUsedCardsPanel.add(icb);
	    }
	    
	    glassSouthPanel.add(gsUsedCardsPanel, BorderLayout.CENTER);
	    //end gsUsedCardsPanel
	    
	    gpCenter.add(glassSouthPanel, BorderLayout.SOUTH);
	    //end glassSouthPanel
	    
	    glass.add(gpCenter, BorderLayout.CENTER);
	    //end gpCenter
	    
	    glass.setVisible(false);
	}
	
	private GameWindowImpl getThis(){
		return this;
	}
	
	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#update(byte[], int[][], byte, boolean)
	 */
	public void update(byte[] board, int[][] cards, byte playerOnTurn, boolean lastBeforeCardsDistribution) {
		updateBoard(board);
		updateCards(cards);
		updatePlayerLabels(playerOnTurn);
		this.repaint();
		
		if(this.timeout != 0 && !lastBeforeCardsDistribution){
			try {
				Thread.sleep(this.timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#updateOnNewRound(int[][])
	 */
	public void updateOnNewRound(int[][] cards){
		updateCards(cards);
		cardPanel.repaint();
		if(this.timeout != 0){
			try {
				Thread.sleep(this.timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updatePlayerLabels(byte playerOnTurn) {
		for(byte i = 0; i < playerLabels.length; i++){
			playerLabels[i].setText(playerNames[i] + (playerOnTurn - 1 == i ? ": *" :": "));
		}
	}

	private void updateBoard(byte[] board){
		assert board.length == 80;
		
		for(int i = 0; i < board.length; i++){
			//heaven fields
			if(i >= 64){
				int pos = i - 59;
				if(i < 68){ //p1
					pos -= 1 * 4;
					specialFieldsP1[pos].setPiece(board[i] == Players.ANY_SAVE ? Players.P1 : Players.EMPTY);
				}else if(i < 72){ //p2
					pos -= 2 * 4;
					specialFieldsP2[pos].setPiece(board[i] == Players.ANY_SAVE ? Players.P2 : Players.EMPTY);
				}else if(i < 76){ //p3
					pos -= 3 * 4;
					specialFieldsP3[pos].setPiece(board[i] == Players.ANY_SAVE ? Players.P3 : Players.EMPTY);
				}else{ //p4
					pos -= 4 * 4;
					specialFieldsP4[pos].setPiece(board[i] == Players.ANY_SAVE ? Players.P4 : Players.EMPTY);
				}
				continue;
			}
			//home fields
			if(i == 0 || i == 16 || i == 32 || i == 48){
				if(i == 0){
					specialFieldsP1[0].setPiece(board[i] == Players.ANY_SAVE ? Players.P1 : board[i]);
				}
				if(i == 16){
					specialFieldsP2[0].setPiece(board[i] == Players.ANY_SAVE ? Players.P2 : board[i]);
				}
				if(i == 32){
					specialFieldsP3[0].setPiece(board[i] == Players.ANY_SAVE ? Players.P3 : board[i]);
				}
				if(i == 48){
					specialFieldsP4[0].setPiece(board[i] == Players.ANY_SAVE ? Players.P4 : board[i]);
				}
				continue;
			}
			//normal fields
			neutralFields[i].setPiece(board[i]);
		}
	}
	
	private void updateCards(int[][] cards) {
		assert cards.length == 4 && cards[0].length == 6;
		for(int i = 0; i < cards.length; i++){
			for(int j = 0; j < cards[0].length; j++){
				setCardForLabel(cards[i][j], cardPanels[i][j]);
			}
		}
	}
	
	private void setCardForLabel(int card, ImageCheckBox cardCheckBox) {
		if(card == -1 || card == 0){
			cardCheckBox.resetImage();
			return;
		}
		cardCheckBox.setCard(card);
	}
	
	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#addMessage(java.lang.String)
	 */
	public void addMessage(String text){
		taMessages.setText(text + "\n" + taMessages.getText());
	}
	
	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#clearMessages()
	 */
	public void clearMessages(){
		taMessages.setText("");
	}

	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#setTimeout(long)
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#wasSuccessful()
	 */
	public boolean wasSuccessful() {
		return successful;
	}
	
	/* (non-Javadoc)
	 * @see intelliDOG.ai.ui.GameWindow#getPlayerNames()
	 */
	public String[] getPlayerNames() {
		return playerNames;
	}
	
	/**
	 * 
	 * @return the board that was created for the initial situation
	 */
	protected byte[] getBoard(){
		if(boardForInitSit == null){ boardForInitSit = new byte[80]; }
		for(Component c : boardPanel.getComponents()){
			if(c instanceof ImageButton){
				ImageButton ib = (ImageButton)c;
				if(ib.getFieldNumber() < 64){
					boardForInitSit[ib.getFieldNumber()] = ib.getText().endsWith("P") ? Players.ANY_SAVE : ib.getPiece();
				}else if(ib.getPiece() != Players.EMPTY){
					boardForInitSit[ib.getFieldNumber()] = Players.ANY_SAVE;
				}
			}
		}
		return boardForInitSit;
	}
	
	/**
	 * 
	 * @return the cards that where distributed for an initial situation
	 */
	protected int[][] getCards(){
		if(cardsForInitSit == null){ cardsForInitSit = new int[4][6]; }
		for(int i = 0; i < cardPanels.length; i++){
			for(int j = 0; j < cardPanels[i].length; j++){
				ImageCheckBox icb = cardPanels[i][j];
				cardsForInitSit[i][j] = icb.getCard() == 0 ? (-1) : icb.getCard(); 
			}
		}
		return cardsForInitSit;
	}
	
	/**
	 * 
	 * @return a list of used cards for an initial situation
	 */
	protected List<Integer> getUsedCards(){
		return usedCardsForInitSit;
	}
	
	private boolean validateSituation(){
		return validateBoard() && validateCards();
	}
	
	private boolean validateBoard(){
		getBoard();
		if(countPiecesInGameForPlayer(Players.P1) > 4){
			JOptionPane.showMessageDialog(optionsPanel, "Too many pieces on the board for Player 1", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(countPiecesInGameForPlayer(Players.P2) > 4){
			JOptionPane.showMessageDialog(optionsPanel, "Too many pieces on the board for Player 2", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(countPiecesInGameForPlayer(Players.P3) > 4){
			JOptionPane.showMessageDialog(optionsPanel, "Too many pieces on the board for Player 3", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		if(countPiecesInGameForPlayer(Players.P4) > 4){
			JOptionPane.showMessageDialog(optionsPanel, "Too many pieces on the board for Player 4", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		 return true;
	}
	
	private int countPiecesInGameForPlayer(byte player){
		int pieceCount = 0;
		switch(player){
			case Players.P1:
				if(boardForInitSit[0] == Players.ANY_SAVE){ pieceCount += 1; }
				for(int i = 64; i < 68; i++){
					if(boardForInitSit[i] == Players.ANY_SAVE){ pieceCount += 1; }
				}
				break;
			case Players.P2:
				if(boardForInitSit[16] == Players.ANY_SAVE){ pieceCount += 1; }
				for(int i = 68; i < 72; i++){
					if(boardForInitSit[i] == Players.ANY_SAVE){ pieceCount += 1; }
				}
				break;
			case Players.P3:
				if(boardForInitSit[32] == Players.ANY_SAVE){ pieceCount += 1; }
				for(int i = 72; i < 76; i++){
					if(boardForInitSit[i] == Players.ANY_SAVE){ pieceCount += 1; }
				}
				break;
			case Players.P4:
				if(boardForInitSit[48] == Players.ANY_SAVE){ pieceCount += 1; }
				for(int i = 76; i < 80; i++){
					if(boardForInitSit[i] == Players.ANY_SAVE){ pieceCount += 1; }
				}
				break;
		}
		
		//count pieces
		for(int i = 0; i < 64; i++){
			if(boardForInitSit[i] == player){
				pieceCount += 1;
			}
		}
		return pieceCount;
	}
	
	private boolean validateCards(){
		getCards();
		int[] count = new int[cardsForInitSit.length];
		
		for(int i = 0; i < cardsForInitSit.length; i++){
			for(int j = 0; j < cardsForInitSit[i].length; j++){
				if(cardsForInitSit[i][j] != -1){ count[i]++; }
			}
		}
		int maxCount = 0;
		for(int i = 1; i < count.length; i++){
			for(int j = 2; j < count.length; j++){
				if(count[i - 1] < maxCount){
					JOptionPane.showMessageDialog(optionsPanel, "Cards are not distributed in a valid way!", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				maxCount = count[i-1];
				if(count[i - 1] > count[j]){ 
					JOptionPane.showMessageDialog(optionsPanel, "Cards are not distributed in a valid way!", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
				if(count[i - 1] < count[j] && count[j] - count[i - 1] > 1){
					JOptionPane.showMessageDialog(optionsPanel, "Cards are not distributed in a valid way!", "Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}
		}
		return true;
	}
	
	private class ImagePanel extends JPanel {

		private static final long serialVersionUID = -3729174135407802661L;
		private Image img;
		private int card;
		  
		public ImagePanel(){}
		
		public ImagePanel(String img) {
			setImage(new ImageIcon(getClass().getResource(img)).getImage());
		}
		
		public ImagePanel(Image img) {
		  setImage(img);
		}
		
		public void paintComponent(Graphics g) {
			Rectangle r = g.getClipBounds();
			if(img != null){
				g.drawImage(img, 0, 0, r.width, r.height, null);
			}else if(card != 0){
				Image cardImage = CardMapper.getInstance().getCardImage(card, r.width, r.height); 
				g.drawImage(cardImage, 0, 0, r.width, r.height, null);
			}
		}
		  
		public void resetImage(){
			this.img = null;
			this.card = 0;
		}
		  
		public void setCard(int card){
			this.card = card;
		}
		  
		public void setImage(String img){
			setImage(new ImageIcon(getClass().getResource(img)).getImage());
		}
		  
		public void setImage(Image img){
			this.img = img;
			Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}
	}
	
	private class ImageButton extends JButton {
		private static final long serialVersionUID = 2449605186150695347L;
		private Image img;
		private Color borderColor;
		private byte piece = Players.EMPTY;
		private byte owner = Players.EMPTY;
		private int fieldNumber;
		
		public ImageButton(String img){
			init(new ImageIcon(getClass().getResource(img)).getImage(), null);
		}
		
		public ImageButton(String img, Color borderColor){
			init(new ImageIcon(getClass().getResource(img)).getImage(), borderColor);
		} 
		
		public ImageButton(Image img){
			this(img, null);
		}
		
		public ImageButton(Image img, Color borderColor){
			init(img, borderColor);
		}
		
		private void init(Image img, Color borderColor){
			super.setBorder(null);
			super.setRolloverEnabled(false);
			super.setEnabled(false);
			super.setFocusable(false);
			this.img = img;
			this.borderColor = borderColor;
			Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
			setSize(size);
			setLayout(null);
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Rectangle r = g.getClipBounds();
			g2.drawImage(img, 4, 4, r.width - 4, r.height - 4, null);
			if(borderColor != null){
				Ellipse2D.Double circle = new Ellipse2D.Double(4, 4, r.width - 6, r.height - 6);
				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(2.5f)); // 2.5-pixel wide pen
				Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f);
				g2.setComposite(alpha);
				g2.draw(circle);
			}
			if(piece != Players.EMPTY && piece != Players.ANY_SAVE){
				String pimg = "";
				switch(piece){
					case Players.P1:
						pimg = resPath + resPiece_P1;
						break;
					case Players.P2:
						pimg = resPath + resPiece_P2;
						break;
					case Players.P3:
						pimg = resPath + resPiece_P3;
						break;
					case Players.P4:
						pimg = resPath + resPiece_P4;
						break;
				}
				Image pieceImg = new ImageIcon(getClass().getResource(pimg)).getImage();
				g2.drawImage(pieceImg, 5, 5, r.width - 5, r.width - 5, null);
			}
			if(super.getText() != null && super.getText() != ""){
				g2.setColor(Color.white);
				String t = super.getText();
				g2.drawString(t, r.width / 2 - t.length() * 3 , r.height / 2 + 4);
			}
		  }
		
		protected void setPiece(byte player){
			this.piece = player;
		}
		
		protected byte getPiece(){
			return this.piece;
		}
		
		protected void setOwner(byte player){
			this.owner = player;
		}
		
		protected byte getOwner(){
			return this.owner;
		}

		/**
		 * @return the fieldNumber
		 */
		public int getFieldNumber() {
			return fieldNumber;
		}

		/**
		 * @param fieldNumber the fieldNumber to set
		 */
		public void setFieldNumber(int fieldNumber) {
			this.fieldNumber = fieldNumber;
		}
	}
	
	private class SetPieceActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageButton srcButton = (ImageButton)e.getSource();
			if(srcButton.getOwner() != Players.EMPTY){
				srcButton.setPiece(srcButton.getPiece() == Players.EMPTY ? srcButton.getOwner() : Players.EMPTY);
			}else{
				srcButton.setPiece((byte)((srcButton.getPiece() + 1) % 5));
			}
			srcButton.getParent().repaint();
		}
	}
	
	private class AddRemCardActionListener implements ActionListener{
		private int nrOfCardsP1;
		private int nrOfCardsP2;
		private int nrOfCardsP3;
		private int nrOfCardsP4;
		
		private int nrOfUsedCards;
		
		//TODO: add structure to count current cards of a type, if type is already distributed twice (joker -> eight times) then unselect & disable this card's checkbox
		//TODO: if such a card is removed from a player reenable the checkbox.
		
		@Override
		public void actionPerformed(ActionEvent e) {
			ImageCheckBox icb;
			if(e.getActionCommand().startsWith("Add")){
				if(e.getActionCommand().endsWith("P1")){
					for(Component c : glassCCCardsPanel.getComponents()){
						icb = (ImageCheckBox)c;
						if(icb.isSelected()){
							if(nrOfCardsP1 > 5){
								JOptionPane.showMessageDialog(glass, "There are too many cards for player 1!", "Error", JOptionPane.ERROR_MESSAGE);
								break;
							}
							setCardForLabel(icb.getCard(), cardPanels[0][nrOfCardsP1++]);
							icb.useCard();
						}
					}
				}else if(e.getActionCommand().endsWith("P2")){
					for(Component c : glassCCCardsPanel.getComponents()){
						icb = (ImageCheckBox)c;
						if(icb.isSelected()){
							if(nrOfCardsP2 > 5){
								JOptionPane.showMessageDialog(glass, "There are too many cards for player 2!", "Error", JOptionPane.ERROR_MESSAGE);
								break;
							}
							setCardForLabel(icb.getCard(), cardPanels[1][nrOfCardsP2++]);
							icb.useCard();
						}
					}
				}else if(e.getActionCommand().endsWith("P3")){
					for(Component c : glassCCCardsPanel.getComponents()){
						icb = (ImageCheckBox)c;
						if(icb.isSelected()){
							if(nrOfCardsP3 > 5){
								JOptionPane.showMessageDialog(glass, "There are too many cards for player 3!", "Error", JOptionPane.ERROR_MESSAGE);
								break;
							}
							setCardForLabel(icb.getCard(), cardPanels[2][nrOfCardsP3++]);
							icb.useCard();
						}
					}
				}else if(e.getActionCommand().endsWith("P4")){
					for(Component c : glassCCCardsPanel.getComponents()){
						icb = (ImageCheckBox)c;
						if(icb.isSelected()){
							if(nrOfCardsP4 > 5){
								JOptionPane.showMessageDialog(glass, "There are too many cards for player 4!", "Error", JOptionPane.ERROR_MESSAGE);
								break;
							}
							setCardForLabel(icb.getCard(), cardPanels[3][nrOfCardsP4++]);
							icb.useCard();
						}
					}
				}else if(e.getActionCommand().endsWith("Used")){
					for(Component c : glassCCCardsPanel.getComponents()){
						icb = (ImageCheckBox)c;
						if(icb.isSelected()){
							Component[] ucIcbs = (gsUsedCardsPanel.getComponents());
							setCardForLabel(icb.getCard(), (ImageCheckBox)ucIcbs[nrOfUsedCards++]);
							usedCardsForInitSit.add(new Integer(icb.getCard()));
							icb.useCard();
						}
					}
				}
			}else if(e.getActionCommand().startsWith("Rem")){
				if(e.getActionCommand().endsWith("P1")){
					for(int i = cardPanels[0].length - 1; i >= 0; i--){
						ImageCheckBox currIcb = cardPanels[0][i];
						if(currIcb.isSelected()){
							reAddCard(currIcb.getCard());
							if(nrOfCardsP1 > 0){
								int lastCard = 0;
								for(int j = cardPanels[0].length - 1; j >= i; j--){
									if(lastCard == 0){
										if(cardPanels[0][j].getCard() != 0){
											lastCard = cardPanels[0][j].getCard();
											cardPanels[0][j].resetImage();
										}else{
											continue;
										}
									}else{
										int temp = lastCard;
										lastCard = cardPanels[0][j].getCard();
										cardPanels[0][j].setCard(temp);
									}
								}
								nrOfCardsP1 -= 1;
							}
							currIcb.setSelected(false);
						}
					}
				}else if(e.getActionCommand().endsWith("P2")){
					for(int i = cardPanels[1].length - 1; i >= 0; i--){
						ImageCheckBox currIcb = cardPanels[1][i];
						if(currIcb.isSelected()){
							reAddCard(currIcb.getCard());
							if(nrOfCardsP2 > 0){
								int lastCard = 0;
								for(int j = cardPanels[1].length - 1; j >= i; j--){
									if(lastCard == 0){
										if(cardPanels[1][j].getCard() != 0){
											lastCard = cardPanels[1][j].getCard();
											cardPanels[1][j].resetImage();
										}else{
											continue;
										}
									}else{
										int temp = lastCard;
										lastCard = cardPanels[1][j].getCard();
										cardPanels[1][j].setCard(temp);
									}
								}
								nrOfCardsP2 -= 1;
							}
							currIcb.setSelected(false);
						}
					}
				}else if(e.getActionCommand().endsWith("P3")){
					for(int i = cardPanels[2].length - 1; i >= 0; i--){
						ImageCheckBox currIcb = cardPanels[2][i];
						if(currIcb.isSelected()){
							reAddCard(currIcb.getCard());
							if(nrOfCardsP3 > 0){
								int lastCard = 0;
								for(int j = cardPanels[2].length - 1; j >= i; j--){
									if(lastCard == 0){
										if(cardPanels[2][j].getCard() != 0){
											lastCard = cardPanels[2][j].getCard();
											cardPanels[2][j].resetImage();
										}else{
											continue;
										}
									}else{
										int temp = lastCard;
										lastCard = cardPanels[2][j].getCard();
										cardPanels[2][j].setCard(temp);
									}
								}
								nrOfCardsP3 -= 1;
							}
							currIcb.setSelected(false);
						}
					}
				}else if(e.getActionCommand().endsWith("P4")){
					for(int i = cardPanels[3].length - 1; i >= 0; i--){
						ImageCheckBox currIcb = cardPanels[3][i];
						if(currIcb.isSelected()){
							reAddCard(currIcb.getCard());
							if(nrOfCardsP4 > 0){
								int lastCard = 0;
								for(int j = cardPanels[3].length - 1; j >= i; j--){
									if(lastCard == 0){
										if(cardPanels[3][j].getCard() != 0){
											lastCard = cardPanels[3][j].getCard();
											cardPanels[3][j].resetImage();
										}else{
											continue;
										}
									}else{
										int temp = lastCard;
										lastCard = cardPanels[3][j].getCard();
										cardPanels[3][j].setCard(temp);
									}
								}
								nrOfCardsP4 -= 1;
							}
							currIcb.setSelected(false);
						}
					}
				}else if(e.getActionCommand().endsWith("Used")){
					Component[] ucIcbs;
					for(int i = nrOfUsedCards; i >= 0; i--){
						ucIcbs = (gsUsedCardsPanel.getComponents());
						ImageCheckBox currIcb = (ImageCheckBox)ucIcbs[i];
						if(currIcb.isSelected()){
							usedCardsForInitSit.remove(new Integer(currIcb.getCard()));
							reAddCard(currIcb.getCard());
							if(nrOfUsedCards > 0){
								int lastCard = 0;
								for(int j = nrOfUsedCards - 1; j >= i; j--){
									ImageCheckBox actualIcb = (ImageCheckBox)ucIcbs[j];
									if(lastCard == 0){
										if(actualIcb.getCard() != 0){
											lastCard = actualIcb.getCard();
											actualIcb.resetImage();
										}else{
											continue;
										}
									}else{
										int temp = lastCard;
										lastCard = actualIcb.getCard();
										actualIcb.setCard(temp);
									}
								}
								nrOfUsedCards -= 1;
							}
							currIcb.setSelected(false);
						}
					}
				}
			}
			cardPanel.repaint();
			gsUsedCardsPanel.repaint();
		}
		
		private void reAddCard(int card){
			for(Component c : glassCCCardsPanel.getComponents()){
				ImageCheckBox icb = (ImageCheckBox)c;
				if(icb.getCard() == card){
					icb.reAddCard();
				}
			}
		}
	}
	
	private class SetHomeFieldPieceMouseListener implements MouseListener{
		@Override
		public void mouseClicked(MouseEvent e) {
			ImageButton srcButton = (ImageButton)e.getSource();
			if(e.getButton() == MouseEvent.BUTTON3){
				if(srcButton.getOwner() == srcButton.getPiece()){
					String oldText = srcButton.getText();
					if(oldText.contains("P")){
						srcButton.setText(oldText.substring(0, oldText.indexOf("P") - 1));
					}else{
						srcButton.setText(oldText + " P");
					}
				}
			}else{
				if(!srcButton.getText().contains("P")){
					srcButton.setPiece((byte)((srcButton.getPiece() + 1) % 5));
				}
			}
			srcButton.getParent().repaint();
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}
	
	private class GlassPanel extends JComponent{
		private static final long serialVersionUID = 7973571796893538781L;

		private float alphaDegree = 0.0f;
		
		@Override
		public void paintComponent(Graphics g){
			Color oldColor = g.getColor();
			Graphics2D g2 = (Graphics2D)g;
			Composite oldComposite = g2.getComposite();
			//Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.75f);
			Composite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaDegree);
			g2.setComposite(alpha);
			Rectangle r = g2.getClipBounds();
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, (int)r.getWidth(), (int)r.getHeight());
			g.setColor(oldColor);
			g2.setComposite(oldComposite);
			super.paintChildren(g);
		}
		
		protected void setAlpha(float f){
			this.alphaDegree = f;
			if(f != 0.0f){
				this.addMouseListener(new MouseAdapter(){});
			}
		}
		
	}
	
	private class ImageCheckBox extends JCheckBox{
		//TODO: ensure that cards of bots cannot be selected, when not in situationcreation mode.
		//TODO: do not darken not enabled checkboxes
		private static final long serialVersionUID = -3038830460369813185L;
		
		private int card;
		private int nrOfCardsToDistribute = 2; //only used for situation creation and for the cards to choose from
		
		public ImageCheckBox() {}
		
		public ImageCheckBox(int card) {
		  this.card = card;
		}
		
		public void paintComponent(Graphics g) {
			Rectangle r = g.getClipBounds();
			if(card != 0){
				Image cardImage = CardMapper.getInstance().getCardImage(card, r.width, r.height);
				if(this.isSelected()){
					BufferedImage srcImage = createBufferedImage(cardImage);
					BufferedImage dstImage = null;
					RescaleOp op = new RescaleOp(1.4f, 0.0f, null);
					dstImage = op.filter(srcImage, null);
					g.drawImage(dstImage,  0, 0, r.width, r.height, null);
				}else{
					BufferedImage srcImage = createBufferedImage(cardImage);
					BufferedImage dstImage = null;
					RescaleOp op = new RescaleOp(isForSituationCreation ? this.isEnabled() ? 0.8f : 0.5f : this.isEnabled() ? 0.9f : 1.0f, 0.0f, null);
					dstImage = op.filter(srcImage, null);
					g.drawImage(dstImage,  0, 0, r.width, r.height, null);
				}
			}
		}
		
		private BufferedImage createBufferedImage(Image image)
		{
		   if(image instanceof BufferedImage) {
		      return (BufferedImage)image;
		   }
		  BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null),
		    BufferedImage.TYPE_INT_ARGB); // ARGB to support transparency if in original image
		  Graphics2D g = bi.createGraphics();
		  g.drawImage(image, 0, 0, null);
		  g.dispose(); // supposedly recommended for cleanup...
		 
		  return bi;
		}
		
		/**
		 * call this when this card type is distributed so that the available ones are decreased.
		 * The control will disable itself if no more cards of this types are available.
		 */
		public void useCard(){
			nrOfCardsToDistribute -= 1;
			if(nrOfCardsToDistribute == 0){
				this.setSelected(false);
				this.setEnabled(false);
			}
		}
		
		/**
		 * this method is the counterpart to the useCard method. it will increase the available cards of this type.
		 * The control will enable itself when it was disabled before.
		 */
		public void reAddCard(){
			if(nrOfCardsToDistribute == 0){
				this.setSelected(false);
				this.setEnabled(true);
			}
			nrOfCardsToDistribute += 1;
		}
		
		public void setNrOfCardsToDistribute(int count){
			this.nrOfCardsToDistribute = count;
		}
		
		/**
		 * @return the card
		 */
		public int getCard() {
			return card;
		}

		/**
		 * @param card the card to set
		 */
		public void setCard(int card) {
			this.card = card;
		}
		
		public void resetImage() {
			this.card = 0;
		}

	}
	
	private class GameWindowListener implements WindowListener{
		@Override
		public void windowActivated(WindowEvent e) {}
		@Override
		public void windowClosed(WindowEvent e) {}
		@Override
		public void windowClosing(WindowEvent e) {}
		@Override
		public void windowDeactivated(WindowEvent e) {}
		@Override
		public void windowDeiconified(WindowEvent e) {}
		@Override
		public void windowIconified(WindowEvent e) {}
		@Override
		public void windowOpened(WindowEvent e) {}
		
	}
	
	
}
