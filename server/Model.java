
import java.util.ArrayList;
import java.io.PrintWriter;

public class Model extends Thread implements GameI{

	public static final int nInGame = 6;
	public static int updateInterval;

	public static int games = 0, clients = 0, id;
	public int playern;
	public static ArrayList<Spin> spins = new ArrayList<Spin>();
	private Spin spin;
	private PrintWriter out;
	public boolean runB = true, hi = false;
	public ArrayList<String> send = new ArrayList<String>();

	public Model(PrintWriter out, int id){
		this.out = out;
		this.id = id;
		this.playern = clients % nInGame;
		clients++;
	}

	public void run(){
		System.out.println("Model started");
		if(spins.size() * nInGame < clients){
			System.out.println("new Spin started " + spins.size() +" "+ nInGame + " " + clients);
			spins.add(new Spin(this));
			spins.get(spins.size()-1).start();
		} else {
			spins.get(spins.size()-1).addPlayer(this);
		}
		spin = spins.get(spins.size()-1);
		System.out.println("RUN STARTED ");
		while(runB){
			//System.out.println("STILL RUNNNNIG " + hi);
			/*if(!send.isEmpty()){
				String string = send.remove(0);
				out.println(string);
				//System.out.println("SENT STRING "+string);
			}*/
			try{
				Thread.sleep(4000);
			}catch(InterruptedException e){}
		}
		spin.removePlayer(playern);
		clients--;
		if(spin.nPlayers == 0){
			spins.remove(spins.indexOf(spin));
			spin.end();
			spin = null;
			System.out.println("RUN ENDED, Removing Spin");
		} else {
            System.out.println("RUN ENDED : " + spin.nPlayers);
        }
	}

	public void updateWarmup(double time, int players, String countries){
		String string;
		string = "2:"+time+";"+players+";"+countries;
		out.println(string);
	}

	public void sendReject(int country){
        String string;
        string = "3:"+country;
        System.out.println(id + "rejecting country choice!");
        out.println(string);
        spin.sendAll(2, null, null);
	}

	public void send(String string){
        out.println(string);
	}

	public void receive(String string){
	    int subpro, x, y, id, country, country2, reserve;
		send.add(string);
		hi = true;
		String[] strings = string.split(":");
		int protocol = Integer.parseInt(strings[0]);
		strings = strings[1].split(";");
		switch(protocol){
		case 0:
			System.out.println(playern +" choosing country");
			spin.chooseCountry(playern, Integer.parseInt(strings[0]));
			break;
		case 1:
			this.end();
			break;
        case 10:
            subpro = Integer.parseInt(strings[0]);
            switch(subpro){
            case 0:
                x = Integer.parseInt(strings[1]);
                y = Integer.parseInt(strings[2]);
                id = Integer.parseInt(strings[3]);
                country = Integer.parseInt(strings[4]);
                if(strings.length == 6){
                    reserve = 2;
                } else {
                    reserve = 0;
                }
                synchronized(spin.game.countries[country].add){
                    spin.game.countries[country].add.add(new int[]{reserve, x, y, country, id});
                }
                break;
            case 1:
                // This should never be sent. The server is fully in charge of destroying soldiers.
                break;
            case 2:
                x = Integer.parseInt(strings[1]);
                y = Integer.parseInt(strings[2]);
                id = Integer.parseInt(strings[3]);
                synchronized(spin.game.countries[0].add){
                    spin.game.countries[0].add.add(new int[]{1, x, y, id});
                }
                break;
            }
            break;
        case 11:
            subpro = Integer.parseInt(strings[0]);
            x = Integer.parseInt(strings[2]);
            y = Integer.parseInt(strings[3]);
            country = Integer.parseInt(strings[4]);
            if(Integer.parseInt(strings[1]) == 1){
                switch(subpro){
                case 0:
                    spin.game.countries[country].AImineAdd(x, y);
                    break;
                case 1:
                    spin.game.countries[country].AIopiumAdd(x, y);
                    break;
                case 2:
                    break;
                }
            } else {
                System.out.println("Nothing has been programmed to happen here");
            }
            break;
        case 14:
            subpro = Integer.parseInt(strings[0]);
            country = Integer.parseInt(strings[1]);
            country2 = Integer.parseInt(strings[2]);
            spin.game.setWar(country, country2, subpro==1);
            break;
		}
		System.out.println("received string "+string);
	}

	public void end(){
		runB = false;
		//spin.removePlayer(playern); THIS IS ALREADY DONE AT THE END OF THE LOOP
	}

	public static void main(String[] args){
	    updateInterval = Integer.parseInt(Server.msettings.get("update_interval"));
	    Spin.interval = Integer.parseInt(Server.msettings.get("warmup_interval"));
	    Spin.baseWarmup = Integer.parseInt(Server.msettings.get("warmup_start"));
	    Spin.minPlayers = Integer.parseInt(Server.msettings.get("min_players"));
		System.out.println("Model game started");
	}

}
