package intelliDOG.ai.ui;

import intelliDOG.ai.framework.Cards;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;


public class CardMapper {
	
	
private static CardMapper instance;
	
	private HashMap<Integer, String> cards;
	private static final String resCardsPath = "/cards/";
	private HashMap<Integer, Image> images;

	private CardMapper(){
		cards = new HashMap<Integer, String>(53);
		cards.put(Cards.CLUBS_ACE, "1_club.png");
		cards.put(Cards.CLUBS_EIGHT, "8_club.png");
		cards.put(Cards.CLUBS_FIVE, "5_club.png");
		cards.put(Cards.CLUBS_FOUR, "4_club.png");
		cards.put(Cards.CLUBS_JACK, "jack_club.png");
		cards.put(Cards.CLUBS_KING, "king_club.png");
		cards.put(Cards.CLUBS_NINE, "9_club.png");
		cards.put(Cards.CLUBS_QUEEN, "queen_club.png");
		cards.put(Cards.CLUBS_SEVEN, "7_club.png");
		cards.put(Cards.CLUBS_SIX, "6_club.png");
		cards.put(Cards.CLUBS_TEN, "10_club.png");
		cards.put(Cards.CLUBS_THREE, "3_club.png");
		cards.put(Cards.CLUBS_TWO, "2_club.png");
		
		cards.put(Cards.DIAMONDS_ACE, "1_diamond.png");
		cards.put(Cards.DIAMONDS_EIGHT, "8_diamond.png");
		cards.put(Cards.DIAMONDS_FIVE, "5_diamond.png");
		cards.put(Cards.DIAMONDS_FOUR, "4_diamond.png");
		cards.put(Cards.DIAMONDS_JACK, "jack_diamond.png");
		cards.put(Cards.DIAMONDS_KING, "king_diamond.png");
		cards.put(Cards.DIAMONDS_NINE, "9_diamond.png");
		cards.put(Cards.DIAMONDS_QUEEN, "queen_diamond.png");
		cards.put(Cards.DIAMONDS_SEVEN, "7_diamond.png");
		cards.put(Cards.DIAMONDS_SIX, "6_diamond.png");
		cards.put(Cards.DIAMONDS_TEN, "10_diamond.png");
		cards.put(Cards.DIAMONDS_THREE, "3_diamond.png");
		cards.put(Cards.DIAMONDS_TWO, "2_diamond.png");
		
		cards.put(Cards.HEARTS_ACE, "1_heart.png");
		cards.put(Cards.HEARTS_EIGHT, "8_heart.png");
		cards.put(Cards.HEARTS_FIVE, "5_heart.png");
		cards.put(Cards.HEARTS_FOUR, "4_heart.png");
		cards.put(Cards.HEARTS_JACK, "jack_heart.png");
		cards.put(Cards.HEARTS_KING, "king_heart.png");
		cards.put(Cards.HEARTS_NINE, "9_heart.png");
		cards.put(Cards.HEARTS_QUEEN, "queen_heart.png");
		cards.put(Cards.HEARTS_SEVEN, "7_heart.png");
		cards.put(Cards.HEARTS_SIX, "6_heart.png");
		cards.put(Cards.HEARTS_TEN, "10_heart.png");
		cards.put(Cards.HEARTS_THREE, "3_heart.png");
		cards.put(Cards.HEARTS_TWO, "2_heart.png");
		
		cards.put(Cards.SPADES_ACE, "1_spade.png");
		cards.put(Cards.SPADES_EIGHT, "8_spade.png");
		cards.put(Cards.SPADES_FIVE, "5_spade.png");
		cards.put(Cards.SPADES_FOUR, "4_spade.png");
		cards.put(Cards.SPADES_JACK, "jack_spade.png");
		cards.put(Cards.SPADES_KING, "king_spade.png");
		cards.put(Cards.SPADES_NINE, "9_spade.png");
		cards.put(Cards.SPADES_QUEEN, "queen_spade.png");
		cards.put(Cards.SPADES_SEVEN, "7_spade.png");
		cards.put(Cards.SPADES_SIX, "6_spade.png");
		cards.put(Cards.SPADES_TEN, "10_spade.png");
		cards.put(Cards.SPADES_THREE, "3_spade.png");
		cards.put(Cards.SPADES_TWO, "2_spade.png");
		
		cards.put(Cards.JOKER, "black_joker.png");
		
		images = new HashMap<Integer, Image>(53);
		loadimages.execute();
	}
	
	/**
	 * get the only instance of the Rules class
	 * This is the thread-safe but yet performant implementation of a singleton.
	 * @return return an instance of the Rules class
	 */
	public synchronized static CardMapper getInstance() {
        if (instance == null) {
        	synchronized (CardMapper.class) {
				if(instance == null){
					instance = new CardMapper();
				}
			}
        }
        return instance;
    }
	
	public String getCardRes(int card){
		return cards.get(card);
	}
	
	public Image getCardImage(int card, int width, int height){
		return getScaledImage(images.get(card), width, height);
	}
	
	
	/**
     * SwingWorker class that loads the images a background thread and calls publish
     * when a new one is ready to be displayed.
     *
     * We use Void as the first SwingWroker param as we do not need to return
     * anything from doInBackground().
     */
    private SwingWorker<Void, Void> loadimages = new SwingWorker<Void, Void>() {
        
        /**
         * Creates full size versions of the target image files.
         */
        @Override
        protected Void doInBackground() throws Exception {
        	for(int card : cards.keySet()){
                ImageIcon icon;
                icon = new ImageIcon(getClass().getResource(resCardsPath + cards.get(card)));
                
                images.put(card, icon.getImage());
            }
            // unfortunately we must return something, and only null is valid to
            // return when the return type is void.
            return null;
        }
    };
    
    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

}
