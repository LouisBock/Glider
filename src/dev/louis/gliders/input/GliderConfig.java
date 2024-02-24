package dev.louis.gliders.input;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class GliderConfig {
	private static LinkedList<String> gameConfigInput = new LinkedList<String>();
    private static HashMap<String, String> gameConfigHashMap = new HashMap<String, String>();
    
    public static int width, height, gliderAmount, groupAmount;
    public static boolean randomPos;
    public static float lookRange, speed, radius, randomDirFactor, neighborAtraction, lookDist;
    private static int[][] colors;
    public static int[] clearColor;
    
    /**
     * this method reads the gameConfig from the file of the given path 
     * @param path this is needed because by running this method in different projects like core or test the path differs
     */
    public static void create(String path) {
    	read(path);
    	write();
    }

	private static void read(String path) {
		//clear old input
    	gameConfigInput.clear();
    	gameConfigHashMap.clear();
    	
        try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line = in.readLine();
			while(line != null) {
				gameConfigInput.add(line);
				line = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        //creating key-value-pairs
        for(String line : gameConfigInput) {
        	String[] configEntry = line.split(":");
        	gameConfigHashMap.put(configEntry[0], configEntry[1]);
        }
	}
    
	private static void write() {
		width = Integer.parseInt(gameConfigHashMap.get("width"));
		height = Integer.parseInt(gameConfigHashMap.get("height"));
		gliderAmount = Integer.parseInt(gameConfigHashMap.get("gliderAmount"));
		groupAmount = Integer.parseInt(gameConfigHashMap.get("groupAmount"));
		
		randomPos = Boolean.parseBoolean(gameConfigHashMap.get("randomPos"));
		
		lookRange = Float.parseFloat(gameConfigHashMap.get("lookRange"));
		speed = Float.parseFloat(gameConfigHashMap.get("speed"));
		radius = Float.parseFloat(gameConfigHashMap.get("radius"));
		randomDirFactor = Float.parseFloat(gameConfigHashMap.get("randomDirFactor"));
		neighborAtraction = Float.parseFloat(gameConfigHashMap.get("neighborAtraction"));
		lookDist = Float.parseFloat(gameConfigHashMap.get("lookDist"));
		
		writeColors();
	}

	private static void writeColors() {
		String[] colorStrings = gameConfigHashMap.get("colors").split("\\|");
		colors = new int[colorStrings.length][3];
		for(int i = 0; i < colors.length; i++) {
			String[] values = colorStrings[i].split(",");
			for(int j = 0; j < 3; j++) {
				colors[i][j] = Integer.parseInt(values[j]);
			}
		}
		
		String[] values = gameConfigHashMap.get("clearColor").split(",");
		clearColor = new int[4];
		for(int i = 0; i < 4; i++) {
			clearColor[i] = Integer.parseInt(values[i]);
		}
	}
	
	public static int[] getGroupColor(int group) {
		if(colors == null || colors.length < 1) {
			return new int[] {255, 255, 255};
		} else if(group < 0 || group >= colors.length) {
			return colors[0].clone();
		} else {
			return colors[group].clone();
		}
	}
}

