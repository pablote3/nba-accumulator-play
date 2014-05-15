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
			List<RosterPlayer> xmlStatsRosterPlayers = activeRosterPlayers.rosterPlayers;
			Date fromDate = DateTime.createDateFromStringDate(rosterDate);
			Date toDate = DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate));

			//activate new roster players
			System.out.println("Activate Players for " + rosterTeamKey + " on " + rosterDate);
			RosterPlayer rosterPlayer;
			Player player;
			for (int i = 0; i < xmlStatsRosterPlayers.size(); i++) {
				RosterPlayer xmlStatsRosterPlayer = xmlStatsRosterPlayers.get(i);
				Player xmlStatsPlayer = xmlStatsRosterPlayer.getPlayer();
				rosterPlayer = RosterPlayer.findByDatePlayerNameTeam(rosterDate, xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), rosterTeamKey, processingType);
				if (rosterPlayer == null) {
					//player is not on current team roster
					rosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason(rosterDate, xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()), processingType);
					if (rosterPlayer == null) {
						//player is not on any roster for current season
						player = Player.findByNameBirthDate(xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()), processingType);
						if (player == null) {
							//player does not exist
							Player.create(xmlStatsPlayer, processingType);							
							xmlStatsRosterPlayer.setFromDate(fromDate);
							xmlStatsRosterPlayer.setToDate(toDate);
							RosterPlayer.create(xmlStatsRosterPlayer, processingType);
							
							StringBuffer output = new StringBuffer();
							output.append(Utilities.padString("  Player does not exist -", 40));
							output.append(" name = " + Utilities.padString(xmlStatsPlayer.getFirstName() + " " + xmlStatsPlayer.getLastName(), 35));
							output.append(" dob = " + DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()));
							output.append(" fromDate = " + rosterDate);
							output.append(" toDate = " + DateTime.getFindDateShort(toDate));
							System.out.println(output.toString());
						}
						else {
							//player does exist
							xmlStatsRosterPlayer.setPlayer(player);
							xmlStatsRosterPlayer.setFromDate(DateTime.createDateFromStringDate(rosterDate));
							xmlStatsRosterPlayer.setToDate(DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate)));
							RosterPlayer.create(xmlStatsRosterPlayer, processingType);
							
							StringBuffer output = new StringBuffer();
							output.append(Utilities.padString("  Player does exist, not on any roster -", 40));
							output.append(" name = " + Utilities.padString(xmlStatsPlayer.getFirstName() + " " + xmlStatsPlayer.getLastName(), 35));
							output.append(" dob = " + DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()));
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
					output.append(Utilities.padString("  Player is on current team roster -", 40));
					output.append(" name = " + Utilities.padString(xmlStatsPlayer.getFirstName() + " " + xmlStatsPlayer.getLastName(), 35));
					output.append(" dob = " + DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()));
					output.append(" fromDate = " + DateTime.getFindDateShort(rosterPlayer.getFromDate()));
					output.append(" toDate = " + DateTime.getFindDateShort(rosterPlayer.getToDate()));
					System.out.println(output.toString());
				}
			}
			
			//terminate inactive roster players
			System.out.println("Terminate Players for " + rosterTeamKey + " on " + rosterDate);
			List<RosterPlayer> latestRoster = RosterPlayer.findByDateTeam(rosterDate, rosterTeamKey, processingType);
			for (int i = 0; i < latestRoster.size(); i++) {
				RosterPlayer activeRosterPlayer = latestRoster.get(i);
				Player activePlayer = activeRosterPlayer.getPlayer();
				for (int j = 0; i < xmlStatsRosterPlayers.size(); j++) {
					RosterPlayer xmlStatsRosterPlayer = xmlStatsRosterPlayers.get(j);
					Player xmlStatsPlayer = xmlStatsRosterPlayer.getPlayer();
					if (activePlayer.equals(xmlStatsPlayer)) {
						//player is on current team roster
						
					}
				}
			}
			controller.tell(new RepeatGame(gameId), getSelf());
		}
		else {
			unhandled(message);
		}
	}
}