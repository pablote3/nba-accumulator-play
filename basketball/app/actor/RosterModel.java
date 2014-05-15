package actor;

import java.util.Date;
import java.util.List;

import models.Game;
import models.Game.ProcessingType;
import models.Player;
import models.RosterPlayer;
import util.DateTime;
import util.Utilities;
import actor.ActorApi.ActiveRosterPlayers;
import actor.ActorApi.RepeatGame;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.UpdateRoster;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class RosterModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef rosterXmlStats;
	private ActorRef controller;
	private Long gameId;
	private String rosterDate;
	private String rosterTeamKey;
	private ProcessingType processingType;
	
	public RosterModel(ActorRef listener) {
		this.listener = listener;
		rosterXmlStats = getContext().actorOf(Props.create(RosterXmlStats.class, listener), "rosterXmlStats");
	}

	public void onReceive(Object message) {
		if (message instanceof ServiceProps) {
			processingType = Game.ProcessingType.valueOf(((ServiceProps) message).processType);
			rosterXmlStats.tell(message, getSender());
		}
		else if(message instanceof UpdateRoster) {
			controller = getSender();
			gameId = ((UpdateRoster)message).gameId;
			rosterDate = ((UpdateRoster)message).date;
			rosterTeamKey = ((UpdateRoster)message).team;
		
			rosterXmlStats.tell(message, getSelf());
		}
		else if(message instanceof ActiveRosterPlayers) {
			ActiveRosterPlayers activeRosterPlayers = (ActiveRosterPlayers) message;
			List<RosterPlayer> rosterPlayers = activeRosterPlayers.rosterPlayers;
			Date fromDate = DateTime.createDateFromStringDate(rosterDate);
			Date toDate = DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate));

			//activate new roster players
			System.out.println("Activate Players for " + rosterTeamKey + " on " + rosterDate);
			RosterPlayer rosterPlayer;
			Player player;
			for (int i = 0; i < rosterPlayers.size(); i++) {
				RosterPlayer activeRosterPlayer = rosterPlayers.get(i);
				Player activePlayer = activeRosterPlayer.getPlayer();
				rosterPlayer = RosterPlayer.findByDatePlayerNameTeam(rosterDate, activePlayer.getLastName(), activePlayer.getFirstName(), rosterTeamKey, processingType);
				if (rosterPlayer == null) {
					//player is not on current team roster
					rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason(rosterDate, activePlayer.getLastName(), activePlayer.getFirstName(), DateTime.getFindDateShort(activePlayer.getBirthDate()), processingType);
					if (rosterPlayer == null) {
						//player is not on any roster for current season
						player = Player.findByNameBirthDate(activePlayer.getLastName(), activePlayer.getFirstName(), DateTime.getFindDateShort(activePlayer.getBirthDate()), processingType);
						if (player == null) {
							//player does not exist
							Player.create(activePlayer, processingType);							
							activeRosterPlayer.setFromDate(fromDate);
							activeRosterPlayer.setToDate(toDate);
							RosterPlayer.create(activeRosterPlayer, processingType);
							
							StringBuffer output = new StringBuffer();
							output.append(Utilities.padString("  Player does not exist -", 36));
							output.append(" name = " + Utilities.padString(activePlayer.getFirstName() + " " + activePlayer.getLastName(), 35));
							output.append(" dob = " + DateTime.getFindDateShort(activePlayer.getBirthDate()));
							output.append(" fromDate = " + rosterDate);
							output.append(" toDate = " + DateTime.getFindDateShort(toDate));
							System.out.println(output.toString());
						}
						else {
							//player does exist
							activeRosterPlayer.setPlayer(player);
							activeRosterPlayer.setFromDate(DateTime.createDateFromStringDate(rosterDate));
							activeRosterPlayer.setToDate(DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate)));
							RosterPlayer.create(activeRosterPlayer, processingType);
							
							StringBuffer output = new StringBuffer();
							output.append(Utilities.padString("  Player does exist -", 36));
							output.append(" name = " + Utilities.padString(activePlayer.getFirstName() + " " + activePlayer.getLastName(), 35));
							output.append(" dob = " + DateTime.getFindDateShort(activePlayer.getBirthDate()));
							output.append(" fromDate = " + rosterDate);
							output.append(" toDate = " + DateTime.getFindDateShort(toDate));
							System.out.println(output.toString());
						}
					}
					else {
						//player is on another roster for current season
						//terminate current roster and add new roster entry
					}
				}
				else {
					//player is on current team roster
					StringBuffer output = new StringBuffer();
					output.append(Utilities.padString("  Player is on current team roster -", 36));
					output.append(" name = " + Utilities.padString(activePlayer.getFirstName() + " " + activePlayer.getLastName(), 35));
					output.append(" dob = " + DateTime.getFindDateShort(activePlayer.getBirthDate()));
					output.append(" fromDate = " + DateTime.getFindDateShort(rosterPlayer.getFromDate()));
					output.append(" toDate = " + DateTime.getFindDateShort(rosterPlayer.getToDate()));
					System.out.println(output.toString());
				}
			}
			
			//terminate inactive roster players
			List<RosterPlayer> latestRoster = RosterPlayer.findByDateTeam(rosterDate, rosterTeamKey);
			System.out.println(latestRoster);

			controller.tell(new RepeatGame(gameId), getSelf());
		}
		else {
			unhandled(message);
		}
	}
}