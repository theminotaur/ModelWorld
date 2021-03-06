
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Spin extends Thread{

    public static float interval, baseWarmup;
    public static int minPlayers;
    public static boolean modded = false;

	public Model[] players = new Model[Model.nInGame];
	public int nPlayers = 0;
	public float warmup = baseWarmup; //Seconds
	public boolean runb = true, gameStarted = false;
	public int[] countries = new int[Model.nInGame];
	public int[] playersc = new int[Model.nInGame];
	public Game game;

	public Spin(Model model){
		addPlayer(model);
		for(int i = 0; i != players.length; i++){
			countries[i] = -1;
			playersc [i] = -1;
		}
	}

	public void addPlayer(Model model){
		players[nPlayers++] = model;
		sendPlayer(nPlayers - 1, 0, null, null);
	}

	public void removePlayer(){
	    System.out.println("Using default remove for "+nPlayers);
		players[nPlayers--] = null;
		System.out.println("Player removed, there are now "+nPlayers+" players.");
	}

	public void removePlayer(int num){
		if(num == nPlayers - 1){removePlayer();return;}
		System.out.println("removing : "+num+" out of "+nPlayers);
		for(int i = num; i != nPlayers; i++){
            System.out.println("i : "+i+" + num : "+num+" + nPlayers : "+nPlayers);
			players[i] = i==5?null:players[i+1];
			if(players[i] != null){
                players[i].playern--;
			}
		}
		nPlayers--;
	}

	public void chooseCountry(int player, int num){
		if(countries[num] != -1 && countries[num] != player){players[player].sendReject(num);return;}
		if(playersc[player] != -1){countries[num] = -1;}
		countries[num] = player;
		playersc[player] = num;
		System.out.println("Set country for player "+player+" to "+num);
	}

	public void sendPlayer(int player, int code, int[] ints, String[] strings){
		String string = "";
		switch(code){
		case 0:
			for(int i = 0; i != players.length; i++){
				if(players[i] == null || playersc[i] == -1){continue;}
				string += playersc[i] + ",";
			}
			if(string != ""){
				string = string.substring(0,string.length()-1);
			}
			players[player].updateWarmup(warmup, nPlayers, string);
			break;
		}
	}

	public void sendAll(int code, int[] ints, String[] strings){
		//System.out.println("Sending with code "+code);
		String string = "";
		switch(code){
		case 2:
			for(int i = 0; i != players.length; i++){
				if(players[i] == null || playersc[i] == -1){continue;}
				string += playersc[i] + ",";
			}
			if(string != ""){
                string = string.substring(0,string.length()-1);
			}
			break;
        case 4:
            string = code+":"+strings[0];
            break;
        case 5:
        case 10:
        case 11:
        case 13:
        case 14:
            string += code+":";
            for(int i = 0; i != ints.length; i++){
                string += ints[i]+(i==ints.length-1?"":";");
            }
            break;
		}
		for(int i = 0; i != nPlayers; i++){
			switch(code){
			case 2:
				players[i].updateWarmup(warmup, nPlayers, string);
            break;
            case 4:
            case 5:
            case 10:
            case 11:
            case 13:
            case 14:
                players[i].send(string);
                break;
            case 12:
                players[i].send("12:;");
                break;
			}
		}
	}

	public void run(){
		System.out.println("Spin started");
		try{
            sendAll(4, null, new String[]{IOHandle.slurp(new FileInputStream(IOHandle.COUNTRY_SETTINGS)).replaceAll("[\\t\\n\\x0B\\f\\r]","")});
		} catch(FileNotFoundException e){
            System.out.println("oh no");
		}
		sendAll(5, new int[]{minPlayers}, null);
		while(true){
			warmup -= interval / 1000;
			if(!(warmup > 0)){
                if(minPlayers <= nPlayers){
                    warmup = (float)0.0;
                    sendAll(2, null, null);
                    break;
                } else {
                    warmup = baseWarmup;
                }
			}
			sendAll(2, null, null);
			try{
				Thread.sleep((long)interval);
			} catch(InterruptedException e){}
		}
		gameStarted = true;
		game = new Game(countries, this);
        game.run();
	}

	public void end(){
        if(game != null){
            game.run = false;
            game = null;
        }
	}

}
