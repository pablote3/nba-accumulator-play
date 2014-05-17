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
			StringBuffer output;

			//activate new roster players
			System.out.println("Activate Players for " + rosterTeamKey + " on " + rosterDate);
			RosterPlayer latestRosterPlayer;
			Player player;
			for (int i = 0; i < xmlStatsRosterPlayers.size(); i++) {
				RosterPlayer xmlStatsRosterPlayer = xmlStatsRosterPlayers.get(i);
				Player xmlStatsPlayer = xmlStatsRosterPlayer.getPlayer();
				latestRosterPlayer = RosterPlayer.findByDatePlayerNameTeam(rosterDate, xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), rosterTeamKey, processingType);
				if (latestRosterPlayer == null) {
					//player is not on current team roster
					latestRosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason(rosterDate, xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()), processingType);
					if (latestRosterPlayer == null) {
						//player is not on any roster for current season
						player = Player.findByNameBirthDate(xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), DateTime.getFindDateShort(xmlStatsPlayer.getBirthDate()), processingType);
						if (player == null) {
							//player does not exist
							Player.create(xmlStatsPlayer, processingType);							
							xmlStatsRosterPlayer.setFromDate(fromDate);
							xmlStatsRosterPlayer.setToDate(toDate);
							RosterPlayer.create(xmlStatsRosterPlayer, processingType);
							
							output = new StringBuffer();
							output.append(Utilities.padString("  Player does not exist -", 40));
							output.append(" name = " + Utilities.padString(xmlStatsRosterPlayer.getPlayer().getFirstName() + " " + xmlStatsRosterPlayer.getPlayer().getLastName(), 35));
							output.append(" dob = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getPlayer().getBirthDate()));
							output.append(" fromDate = " + xmlStatsRosterPlayer.getFromDate());
							output.append(" toDate = " + xmlStatsRosterPlayer.getToDate());
							System.out.println(output.toString());
						}
						else {
							//player does exist
							xmlStatsRosterPlayer.setPlayer(player);
							xmlStatsRosterPlayer.setFromDate(DateTime.createDateFromStringDate(rosterDate));
							xmlStatsRosterPlayer.setToDate(DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate)));
							RosterPlayer.create(xmlStatsRosterPlayer, processingType);
							
							output = new StringBuffer();
							output.append(Utilities.padString("  Player does exist, not on any roster -", 40));
							output.append(" name = " + Utilities.padString(xmlStatsRosterPlayer.getPlayer().getFirstName() + " " + xmlStatsRosterPlayer.getPlayer().getLastName(), 35));
							output.append(" dob = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getPlayer().getBirthDate()));
							output.append(" fromDate = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getFromDate()));
							output.append(" toDate = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getToDate()));
							System.out.println(output.toString());
						}
					}
					else {
						//player is on another roster for current season
						latestRosterPlayer.setToDate(DateTime.createDateFromStringDate(rosterDate));
						RosterPlayer.update(latestRosterPlayer, processingType);
						
						output = new StringBuffer();
						output.append(Utilities.padString("  Player on another team roster - term -", 40));
						output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
						output.append(" dob = " + DateTime.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
						output.append(" fromDate = " + DateTime.getFindDateShort(latestRosterPlayer.getFromDate()));
						output.append(" toDate = " + DateTime.getFindDateShort(latestRosterPlayer.getToDate()));
						System.out.println(output.toString());
						
						xmlStatsRosterPlayer.setPlayer(latestRosterPlayer.getPlayer());
						xmlStatsRosterPlayer.setFromDate(DateTime.createDateFromStringDate(rosterDate));
						xmlStatsRosterPlayer.setToDate(DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate)));
						RosterPlayer.create(xmlStatsRosterPlayer, processingType);
						
						output = new StringBuffer();
						output.append(Utilities.padString("  Player on another team roster - add -", 40));
						output.append(" name = " + Utilities.padString(xmlStatsRosterPlayer.getPlayer().getFirstName() + " " + xmlStatsRosterPlayer.getPlayer().getLastName(), 35));
						output.append(" dob = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getPlayer().getBirthDate()));
						output.append(" fromDate = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getFromDate()));
						output.append(" toDate = " + DateTime.getFindDateShort(xmlStatsRosterPlayer.getToDate()));
						System.out.println(output.toString());
					}
				}
				else {
					//player is on current team roster
					output = new StringBuffer();
					output.append(Utilities.padString("  Player is on current team roster -", 40));
					output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
					output.append(" dob = " + DateTime.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
					output.append(" fromDate = " + DateTime.getFindDateShort(latestRosterPlayer.getFromDate()));
					output.append(" toDate = " + DateTime.getFindDateShort(latestRosterPlayer.getToDate()));
					System.out.println(output.toString());
				}
			}
			
			//terminate inactive roster players
			System.out.println("Terminate Players for " + rosterTeamKey + " on " + rosterDate);
			List<RosterPlayer> latestRoster = RosterPlayer.findByDateTeam(rosterDate, rosterTeamKey, processingType);
			boolean foundPlayerOnRoster;
			for (int i = 0; i < latestRoster.size(); i++) {
				latestRosterPlayer = latestRoster.get(i);
				player = latestRosterPlayer.getPlayer();
				foundPlayerOnRoster = false;
				RosterPlayer xmlStatsRosterPlayer = null;
				Player xmlStatsPlayer = null;
				for (int j = 0; i < xmlStatsRosterPlayers.size(); j++) {
					xmlStatsRosterPlayer = xmlStatsRosterPlayers.get(j);
					xmlStatsPlayer = xmlStatsRosterPlayer.getPlayer();
					if (player.equals(xmlStatsPlayer)) {
						//player is on current team roster
						output = new StringBuffer();
						output.append(Utilities.padString("  Player is on current team roster -", 40));
						output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
						output.append(" dob = " + DateTime.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
						output.append(" fromDate = " + DateTime.getFindDateShort(latestRosterPlayer.getFromDate()));
						output.append(" toDate = " + DateTime.getFindDateShort(latestRosterPlayer.getToDate()));
						System.out.println(output.toString());
						foundPlayerOnRoster = true;
						break;
					}
				}
				if (!foundPlayerOnRoster) {
					//player is not on current team roster
					latestRosterPlayer.setToDate(DateTime.createDateFromStringDate(rosterDate));
					RosterPlayer.update(latestRosterPlayer, processingType);
					
					output = new StringBuffer();
					output.append(Utilities.padString("  Player is not on current team roster -", 40));
					output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
					output.append(" dob = " + DateTime.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
					output.append(" fromDate = " + DateTime.getFindDateShort(latestRosterPlayer.getFromDate()));
					output.append(" toDate = " + DateTime.getFindDateShort(latestRosterPlayer.getToDate()));
					System.out.println(output.toString());
				}
			}
			controller.tell(new RepeatGame(gameId), getSelf());
		}
		else {
			unhandled(message);
		}
	}
}