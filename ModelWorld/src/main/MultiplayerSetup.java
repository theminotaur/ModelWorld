package main;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import multiplayer.Game;
import multiplayer.Init;
import multiplayer.Client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.awt.Font;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

@SuppressWarnings("unused")
public class MultiplayerSetup {
	
	public static int[] serverSettings = new int[]{1};
	public static boolean modded = false;
	public static MultiplayerSetup mthis;
	public static Game game;
	public static UnicodeFont FONT, FONT2, FONT3;
	public static boolean[] locked = new boolean[6];
	public static float warmupTime;
	public static int playersInGame = 1;
	public static int WIDTH = 1000, HEIGHT = 1000;
	public static int RWIDTH, RHEIGHT;
	public static int twidth, theight;
	
	public Texture black, purple;
	public Texture[] images;
	
	public int chosen = -1;
	public String[] list = {"North America", "South America", "Europe", "Africa", "Asia", "Oceania"};
	public int[][] text, text2;
	public String[] texts, texts2;
	public boolean[] colours, colours2;

	public boolean run = true, except = false;
	public int mousex, mousey, translate_x, translate_y;
	
	@SuppressWarnings("unchecked")
	public void init_LWJGL(int[] settings){

		RWIDTH = Display.getDesktopDisplayMode().getWidth() - 10;
		RHEIGHT = Display.getDesktopDisplayMode().getHeight() - 50;
		
		DisplayMode[] modes = null;
		try {
			modes = Display.getAvailableDisplayModes();
		} catch (LWJGLException e1) {
			e1.printStackTrace();
		}
		 
		for (int i=0;i<modes.length;i++) {
		    DisplayMode current = modes[i];
		    System.out.println(current.getWidth() + "x" + current.getHeight() + "x" + current.getBitsPerPixel() + " " + current.getFrequency() + "Hz");
		}
		
		try {
			Display.setInitialBackground(255, 255, 255);
			Display.setDisplayMode(new DisplayMode(RWIDTH, RHEIGHT));
			Display.setResizable(true);
			Display.setLocation(0, 0);
			Display.setFullscreen(true);
			Display.setTitle("Captain Wiggles");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH, HEIGHT, 0, -1, 10);
		glMatrixMode(GL_MODELVIEW);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
		Font awtFont = new Font("Times New Roman", Font.BOLD, 60);
		FONT = new UnicodeFont(awtFont);
		FONT.addAsciiGlyphs();
		FONT.addGlyphs(400, 600);
		FONT.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		try {
		    FONT.loadGlyphs();
		} 
		catch (SlickException e){
		    System.out.println("something went wrong here!");
		    e.printStackTrace();
		    Display.destroy();
		}
        glEnable(GL_TEXTURE_2D);
        
        texts = new String[]{"Play", "Settings"};
        text = new int[texts.length][];
		
        for(int i = 0; i != text.length; i++){
        	text[i] = new int[]{(int) (WIDTH / 2.3 - FONT.getWidth(texts[i]) / 2), HEIGHT / (text.length + 1) * (i + 1) - FONT.getHeight(texts[i])};
        }
	}
	
	@SuppressWarnings("unchecked")
	public void init_fonts(){
		Font font = new Font("Comic Sans MS", Font.BOLD, 30);
	    FONT3 = new UnicodeFont(font);
	    FONT3.addAsciiGlyphs();
	    FONT3.addGlyphs(400, 600);
	    FONT3.getEffects().add(new ColorEffect(java.awt.Color.white));
	    try {
	        FONT3.loadGlyphs();
	    } catch (SlickException e) {
		    System.out.println("something went wrong here!");
		    e.printStackTrace();
		    Display.destroy();
	    }
	}
	
	public void init_game(int[] settings){
		texts2 = new String[]{"Back"};
		text2 = new int[][]{{0, HEIGHT - Main.FONT.getHeight("Back")}};
        colours2 = new boolean[text2.length];
		
		theight = HEIGHT / 10;
		twidth = theight;
		//images = new Texture[15];
		
		try {
			black = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/black.png")));
			purple = TextureLoader.getTexture("PNG", new FileInputStream(new File("res/purple.png")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        setupStrings(list);
		
	}
	
	public void setupStrings(String[] newa){
		
		String[] tempTexts = newa.clone();
		
		//System.out.println("Setting up srings - "+tempTexts.length);
		
		for(int i = 0; i != tempTexts.length; i++){
			tempTexts[i] += (locked[i]&&i!=chosen? "(Locked)":"");
		}
		
		if(chosen == -1){
			chosen = 5-Main.unlocked;
		}
		int[][] tempText = new int[tempTexts.length][];
        //text = new int[tempTexts.length][];
        colours = new boolean[tempTexts.length];
		
		for(int i = 0; i != tempText.length; i++){
        	tempText[i] = new int[]{(int) (WIDTH / 2.3 - Main.FONT.getWidth(tempTexts[i]) / 2), HEIGHT / (tempTexts.length + 1) * (i + 1) - Main.FONT.getHeight(tempTexts[i])};
        }
		
		texts = tempTexts;
		text = tempText;
	}
	
	public void init(boolean initi){
		warmupTime = -1;
		init_fonts();
		int[] settings = IOHandle.getSettings();
		if(initi){
			init_LWJGL(settings);
		} else {
			RWIDTH = Main.RWIDTH;
			RHEIGHT = Main.RHEIGHT;
		}
		init_game(settings);
		
		if(except){
			System.out.println("Server not up!");
			String[] newTexts = Main.mthis.texts;
			newTexts[1] = "Server not up";
			Main.mthis.setupStrings(newTexts);
			run = false;
			except = true;
			return;
		}
		
		HashMap<String, String> msettings = IOHandle.getMultiSettings();
		
		try {
			Client.main(msettings);
		} catch (IOException e) {
			System.out.println("Server not up!");
			String[] newTexts = Main.mthis.texts;
			newTexts[1] = "Server not up";
			Main.mthis.setupStrings(newTexts);
			run = false;
			except = true;
		} catch (Exception e) {}
		
		game = new Game();
		Init.init_game();
		
	}
	
	public void run(boolean initi) {
		
		mthis = this;
		
		FONT = Main.FONT;
		FONT2 = Main.FONT2;
		
		WIDTH = Main.WIDTH;
		HEIGHT = Main.HEIGHT;
		
		init(initi);
		
		while (run) {
			
			glClear(GL_COLOR_BUFFER_BIT);
			
			glPushMatrix();
			
			glTranslatef(translate_x, translate_y, 0);
			
			if (Display.wasResized()) {
	            RWIDTH = Display.getWidth();
	            RHEIGHT = Display.getHeight();

	            GL11.glViewport(0, 0, RWIDTH, RHEIGHT);
	            GL11.glLoadIdentity();
			}
			
			// Retrieve the "true" coordinates of the mouse.
			mousex = (int) (Mouse.getX() * ((float)(WIDTH) / RWIDTH) - translate_x);
			mousey = (int) (HEIGHT - Mouse.getY() * ((float)(HEIGHT) / RHEIGHT) - 1 - translate_y);
			Draw.renderthistex(new Rectangle(0,0,WIDTH,HEIGHT), purple);
			
			logic();
			if(!run){
				break;
			}
			render();
			
			if(warmupTime == 0.0){
				game.setChosen(chosen);
				run = game.run();
			}
			
			update();
			
			if(Display.isCloseRequested()){
				run = false;
				Main.mthis.end();
			}

			glPopMatrix();
			Display.update();
			Display.sync(60);
		}
		
		Main.RWIDTH = RWIDTH;
		Main.RHEIGHT = RHEIGHT;
		
		end();
		
	}
	
	public void logic(){
		
		Mouse.poll();
		while(Mouse.next()){
			if(Mouse.getEventButtonState()){
				int eventKey = Mouse.getEventButton();
				switch(eventKey){
				case 0:
					for(int i = 0; i != text.length; i++){
						if(mousex >= text[i][0] && mousex <= text[i][0] + Main.FONT.getWidth(texts[i]) && mousey >= text[i][1] && mousey <= text[i][1] + Main.FONT.getHeight(texts[i])){
							if(locked[i]){break;}
							chosen = i;
							Client.send(0, ""+chosen);
						}
					}
					for(int i = 0; i != text2.length; i++){
						if(mousex >= text2[i][0] && mousex <= text2[i][0] + Main.FONT.getWidth(texts2[i]) && mousey >= text2[i][1] && mousey <= text2[i][1] + Main.FONT.getHeight(texts2[i])){
							switch(i){
							case 0:
								run = false;
								break;
							}
						}
					}
					break;
				default:
					//System.out.println(eventKey);
					break;
				}
			}
		}
		
		Keyboard.poll();
		while(Keyboard.next()){
			if(Keyboard.getEventKeyState()){
				int eventKey = Keyboard.getEventKey();
				switch(eventKey){
				case KEY_ESCAPE:
					run = false;
					break;
				default:
					break;
				}
			}
		}

		for(int i = 0; i != text.length; i++){
			if(mousex >= text[i][0] && mousex <= text[i][0] + FONT.getWidth(texts[i]) && mousey >= text[i][1] && mousey <= text[i][1] + FONT.getHeight(texts[i])){
				colours[i] = true;
			} else {
				colours[i] = false;
			}
		}
		

		for(int i = 0; i != text2.length; i++){
			if(mousex >= text2[i][0] && mousex <= text2[i][0] + FONT.getWidth(texts2[i]) && mousey >= text2[i][1] && mousey <= text2[i][1] + FONT.getHeight(texts2[i])){
				colours2[i] = true;
			} else {
				colours2[i] = false;
			}
		}
	}
	
	public void render(){
		
		for(int i = 0; i != text.length; i++){
			if(colours[i]){
				FONT2.drawString(text[i][0], text[i][1], texts[i]+(i==chosen?" - ":""));
				continue;
			}
			FONT.drawString(text[i][0], text[i][1], texts[i]+(i==chosen?" - ":""));
		}
		
		for(int i = 0; i != text2.length; i++){
			if(colours2[i]){
				FONT2.drawString(text2[i][0], text2[i][1], texts2[i]);
				continue;
			}
			FONT.drawString(text2[i][0], text2[i][1], texts2[i]);
		}
		
		FONT3.drawString(0, 0, "Time left : "+String.format("%.2f", warmupTime));
		String string = "Players: "+playersInGame;
		int psh = 0;
		FONT3.drawString(WIDTH-FONT3.getWidth(string), psh, string);
		psh += FONT3.getHeight(string);
		string = "Minium Players: "+serverSettings[0];
		FONT3.drawString(WIDTH-FONT3.getWidth(string), psh, string);
		psh += FONT3.getHeight(string);
		string = "Modded: "+(modded?"Yes":"No");
		FONT3.drawString(WIDTH-FONT3.getWidth(string), psh, string);
		//Main.FONT.drawString(0, HEIGHT - Main.FONT.getHeight("Back"), "Back");
	}
	
	public void update(){
		
	}
	
	public void end(){
		
		if(!except){
			Client.closeConnection();
		}
		
	}
	
}
