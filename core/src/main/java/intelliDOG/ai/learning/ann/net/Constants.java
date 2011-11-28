package intelliDOG.ai.learning.ann.net;

/**
 * Interface mit Konstanten, die in mehreren Klassen fuer Spiellogik und -ablauf benoetigt werden.
 * 
 * Farbe: Farben, welche einen Spieler oder den Inhalt eines Spielfeldes repraesentieren.
 * Spielzug: Art eines Spielzuges
 * Feldwert: Spezialwerte fuer die Move Klasse beim setzen oder entfernen von Spielsteinen.
 * 
 * @author Ralf Mauerhofer, Andreas Iseli
 * @date 07.04.2008
 */
public final class Constants {
    
    /**
     * Layertypen
     */
    public static final int INPUTLAYER = 199;
    public static final int HIDDENLAYER = 200;
    public static final int OUTPUTLAYER = 201;
    
    /**
     * Aktivierungsfunktionstypen
     */
    public static final int LINEARFUNCTION = 221;
    public static final int SIGMOIDFUNCTION = 222;
    
    /**
     * 
     */
    public static final int BACKPROP_NORMAL = 300;
    public static final int BACKPROP_TD_LAMBDA = 301;
    
    /**
     * Inputtypen für das Neurale Netz; müssen ALLE mit NNInputType_ beginnen
     */
    //Des aktuellen Spielbrettes
    public static final int NNInputType_POS00ACTUALBOARD = 500;
    public static final int NNInputType_POS01ACTUALBOARD = 501;
    public static final int NNInputType_POS02ACTUALBOARD = 502;
    public static final int NNInputType_POS03ACTUALBOARD = 503;
    public static final int NNInputType_POS04ACTUALBOARD = 504;
    public static final int NNInputType_POS05ACTUALBOARD = 505;
    public static final int NNInputType_POS06ACTUALBOARD = 506;
    public static final int NNInputType_POS07ACTUALBOARD = 507;
    public static final int NNInputType_POS08ACTUALBOARD = 508;
    public static final int NNInputType_POS09ACTUALBOARD = 509;
    public static final int NNInputType_POS10ACTUALBOARD = 510;
    public static final int NNInputType_POS11ACTUALBOARD = 511;
    public static final int NNInputType_POS12ACTUALBOARD = 512;
    public static final int NNInputType_POS13ACTUALBOARD = 513;
    public static final int NNInputType_POS14ACTUALBOARD = 514;
    public static final int NNInputType_POS15ACTUALBOARD = 515;
    public static final int NNInputType_POS16ACTUALBOARD = 516;
    public static final int NNInputType_POS17ACTUALBOARD = 517;
    public static final int NNInputType_POS18ACTUALBOARD = 518;
    public static final int NNInputType_POS19ACTUALBOARD = 519;
    public static final int NNInputType_POS20ACTUALBOARD = 520;
    public static final int NNInputType_POS21ACTUALBOARD = 521;
    public static final int NNInputType_POS22ACTUALBOARD = 522;
    public static final int NNInputType_POS23ACTUALBOARD = 523;
    public static final int NNInputType_POS24ACTUALBOARD = 524;
    public static final int NNInputType_POS25ACTUALBOARD = 525;
    public static final int NNInputType_POS26ACTUALBOARD = 526;
    public static final int NNInputType_POS27ACTUALBOARD = 527;
    public static final int NNInputType_POS28ACTUALBOARD = 528;
    public static final int NNInputType_POS29ACTUALBOARD = 529;
    public static final int NNInputType_POS30ACTUALBOARD = 530;
    public static final int NNInputType_POS31ACTUALBOARD = 531;
    public static final int NNInputType_POS32ACTUALBOARD = 532;
    public static final int NNInputType_POS33ACTUALBOARD = 533;
    public static final int NNInputType_POS34ACTUALBOARD = 534;
    public static final int NNInputType_POS35ACTUALBOARD = 535;
    public static final int NNInputType_POS36ACTUALBOARD = 536;
    public static final int NNInputType_POS37ACTUALBOARD = 537;
    public static final int NNInputType_POS38ACTUALBOARD = 538;
    public static final int NNInputType_POS39ACTUALBOARD = 539;
    public static final int NNInputType_POS40ACTUALBOARD = 540;
    public static final int NNInputType_POS41ACTUALBOARD = 541;
    public static final int NNInputType_POS42ACTUALBOARD = 542;
    public static final int NNInputType_POS43ACTUALBOARD = 543;
    public static final int NNInputType_POS44ACTUALBOARD = 544;
    public static final int NNInputType_POS45ACTUALBOARD = 545;
    public static final int NNInputType_POS46ACTUALBOARD = 546;
    public static final int NNInputType_POS47ACTUALBOARD = 547;
    public static final int NNInputType_POS48ACTUALBOARD = 548;
    public static final int NNInputType_POS49ACTUALBOARD = 549;
    public static final int NNInputType_POS50ACTUALBOARD = 550;
    public static final int NNInputType_POS51ACTUALBOARD = 551;
    public static final int NNInputType_POS52ACTUALBOARD = 552;
    public static final int NNInputType_POS53ACTUALBOARD = 553;
    public static final int NNInputType_POS54ACTUALBOARD = 554;
    public static final int NNInputType_POS55ACTUALBOARD = 555;
    public static final int NNInputType_POS56ACTUALBOARD = 556;
    public static final int NNInputType_POS57ACTUALBOARD = 557;
    public static final int NNInputType_POS58ACTUALBOARD = 558;
    public static final int NNInputType_POS59ACTUALBOARD = 559;
    public static final int NNInputType_POS60ACTUALBOARD = 560;
    public static final int NNInputType_POS61ACTUALBOARD = 561;
    public static final int NNInputType_POS62ACTUALBOARD = 562;
    public static final int NNInputType_POS63ACTUALBOARD = 563;
    public static final int NNInputType_POS64ACTUALBOARD = 564;
    public static final int NNInputType_POS65ACTUALBOARD = 565;
    public static final int NNInputType_POS66ACTUALBOARD = 566;
    public static final int NNInputType_POS67ACTUALBOARD = 567;
    public static final int NNInputType_POS68ACTUALBOARD = 568;
    public static final int NNInputType_POS69ACTUALBOARD = 569;
    public static final int NNInputType_POS70ACTUALBOARD = 570;
    public static final int NNInputType_POS71ACTUALBOARD = 571;
    public static final int NNInputType_POS72ACTUALBOARD = 572;
    public static final int NNInputType_POS73ACTUALBOARD = 573;
    public static final int NNInputType_POS74ACTUALBOARD = 574;
    public static final int NNInputType_POS75ACTUALBOARD = 575;
    public static final int NNInputType_POS76ACTUALBOARD = 576;
    public static final int NNInputType_POS77ACTUALBOARD = 577;
    public static final int NNInputType_POS78ACTUALBOARD = 578;
    public static final int NNInputType_POS79ACTUALBOARD = 579;
    //Cards
    public static final int NNInputType_Card01OWN = 601;
    public static final int NNInputType_Card02OWN = 602;
    public static final int NNInputType_Card03OWN = 603;
    public static final int NNInputType_Card04OWN = 604;
    public static final int NNInputType_Card05OWN = 605;
    public static final int NNInputType_Card06OWN = 606;
    //Player
    public static final int NNInputType_PlayerToValidate = 610;
    

}
