package actor;

import java.util.List;

import models.Game;
import models.Game.ProcessingType;
import models.Player;
import models.RosterPlayer;

import org.joda.time.LocalDate;

import util.DateTimeUtil;
import util.Utilities;
import actor.ActorApi.ActiveRoster;
import actor.ActorApi.ModelException;
import actor.ActorApi.RepeatGame;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.LoadRoster;
import actor.ActorApi.RetrieveRoster;
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
		else if(message instanceof LoadRoster) {
			controller = getSender();
			if (gameId != null && gameId.equals(((LoadRoster)message).gameId) &&
					rosterTeamKey != null && rosterTeamKey.equals(((LoadRoster)message).team))  {
				getContext().stop(getSelf());
				ModelException me = new ModelException("Multiple loops of UpdateRoster");
				listener.tell(me, getSelf());
			}
			else {
				gameId = ((LoadRoster)message).gameId;
				rosterDate = ((LoadRoster)message).date;
				rosterTeamKey = ((LoadRoster)message).team;
				rosterXmlStats.tell(new RetrieveRoster(rosterDate, rosterTeamKey), getSelf());
			}
		}
		else if(message instanceof ActiveRoster) {
			ActiveRoster activeRoster = (ActiveRoster) message;
			List<RosterPlayer> xmlStatsRosterPlayers = activeRoster.rosterPlayers;
			LocalDate fromDate = DateTimeUtil.createDateFromStringDate(rosterDate);
			LocalDate toDate = DateTimeUtil.getDateMaxSeason(DateTimeUtil.createDateFromStringDate(rosterDate));
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
					latestRosterPlayer = RosterPlayer.findLatestByPlayerNameBirthDateSeason(rosterDate, xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), DateTimeUtil.getFindDateShort(xmlStatsPlayer.getBirthDate()), processingType);
					if (latestRosterPlayer == null) {
						//player is not on any roster for current season
						player = Player.findByNameBirthDate(xmlStatsPlayer.getLastName(), xmlStatsPlayer.getFirstName(), DateTimeUtil.getFindDateShort(xmlStatsPlayer.getBirthDate()), processingType);
						if (player == null) {
							//player does not exist
							Player.create(xmlStatsPlayer, processingType);							
							xmlStatsRosterPlayer.setFromDate(fromDate);
							xmlStatsRosterPlayer.setToDate(toDate);
							RosterPlayer.create(xmlStatsRosterPlayer, processingType);
							
							output = new StringBuffer();
							output.append(Utilities.padString("  Player does not exist -", 40));
							output.append(" name = " + Utilities.padString(xmlStatsRosterPlayer.getPlayer().getFirstName() + " " + xmlStatsRosterPlayer.getPlayer().getLastName(), 35));
							output.append(" dob = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getPlayer().getBirthDate()));
							output.append(" fromDate = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getFromDate()));
							output.append(" toDate = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getToDate()));
							System.out.println(output.toString());
						}
						else {
							//player does exist
							xmlStatsRosterPlayer.setPlayer(player);
							xmlStatsRosterPlayer.setFromDate(DateTimeUtil.createDateFromStringDate(rosterDate));
							xmlStatsRosterPlayer.setToDate(DateTimeUtil.getDateMaxSeason(DateTimeUtil.createDateFromStringDate(rosterDate)));
							RosterPlayer.create(xmlStatsRosterPlayer, processingType);
							
							output = new StringBuffer();
							output.append(Utilities.padString("  Player does exist, not on any roster -", 40));
							output.append(" name = " + Utilities.padString(xmlStatsRosterPlayer.getPlayer().getFirstName() + " " + xmlStatsRosterPlayer.getPlayer().getLastName(), 35));
							output.append(" dob = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getPlayer().getBirthDate()));
							output.append(" fromDate = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getFromDate()));
							output.append(" toDate = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getToDate()));
							System.out.println(output.toString());
						}
					}
					else {
						//player is on another roster for current season
						latestRosterPlayer.setToDate(DateTimeUtil.getDateMinusOneDay(DateTimeUtil.createDateFromStringDate(rosterDate)));
						RosterPlayer.update(latestRosterPlayer, processingType);
						
						output = new StringBuffer();
						output.append(Utilities.padString("  Player on another team -  " + latestRosterPlayer.getTeam().getAbbr() + " - term -", 40));
						output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
						output.append(" dob = " + DateTimeUtil.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
						output.append(" fromDate = " + DateTimeUtil.getFindDateShort(latestRosterPlayer.getFromDate()));
						output.append(" toDate = " + DateTimeUtil.getFindDateShort(DateTimeUtil.getDateMinusOneDay(DateTimeUtil.createDateFromStringDate(rosterDate))));
						System.out.println(output.toString());
						
						xmlStatsRosterPlayer.setPlayer(latestRosterPlayer.getPlayer());
						xmlStatsRosterPlayer.setFromDate(DateTimeUtil.createDateFromStringDate(rosterDate));
						xmlStatsRosterPlayer.setToDate(DateTimeUtil.getDateMaxSeason(DateTimeUtil.createDateFromStringDate(rosterDate)));
						RosterPlayer.create(xmlStatsRosterPlayer, processingType);
						
						output = new StringBuffer();
						output.append(Utilities.padString("  Player on another team  - " + xmlStatsRosterPlayer.getTeam().getAbbr() + " - add - ", 40));
						output.append(" name = " + Utilities.padString(xmlStatsRosterPlayer.getPlayer().getFirstName() + " " + xmlStatsRosterPlayer.getPlayer().getLastName(), 35));
						output.append(" dob = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getPlayer().getBirthDate()));
						output.append(" fromDate = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getFromDate()));
						output.append(" toDate = " + DateTimeUtil.getFindDateShort(xmlStatsRosterPlayer.getToDate()));
						System.out.println(output.toString());
					}
				}
				else {
					//player is on current team roster
//					output = new StringBuffer();
//					output.append(Utilities.padString("  Player is on current team roster -", 40));
//					output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
//					output.append(" dob = " + DateTime.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
//					output.append(" fromDate = " + DateTime.getFindDateShort(latestRosterPlayer.getFromDate()));
//					output.append(" toDate = " + DateTime.getFindDateShort(latestRosterPlayer.getToDate()));
//					System.out.println(output.toString());
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
				for (int j = 0; j < xmlStatsRosterPlayers.size(); j++) {
					xmlStatsRosterPlayer = xmlStatsRosterPlayers.get(j);
					xmlStatsPlayer = xmlStatsRosterPlayer.getPlayer();
					if (player.equals(xmlStatsPlayer)) {
						//player is on current team roster
//						output = new StringBuffer();
//						output.append(Utilities.padString("  Player is on current team roster -", 40));
//						output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
//						output.append(" dob = " + DateTime.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
//						output.append(" fromDate = " + DateTime.getFindDateShort(latestRosterPlayer.getFromDate()));
//						output.append(" toDate = " + DateTime.getFindDateShort(latestRosterPlayer.getToDate()));
//						System.out.println(output.toString());
						foundPlayerOnRoster = true;
						break;
					}
				}
				if (!foundPlayerOnRoster) {
					//player is not on current team roster
					latestRosterPlayer.setToDate(DateTimeUtil.getDateMinusOneDay(DateTimeUtil.createDateFromStringDate(rosterDate)));
					RosterPlayer.update(latestRosterPlayer, processingType);
					
					output = new StringBuffer();
					output.append(Utilities.padString("  Player is not on current team roster -", 40));
					output.append(" name = " + Utilities.padString(latestRosterPlayer.getPlayer().getFirstName() + " " + latestRosterPlayer.getPlayer().getLastName(), 35));
					output.append(" dob = " + DateTimeUtil.getFindDateShort(latestRosterPlayer.getPlayer().getBirthDate()));
					output.append(" fromDate = " + DateTimeUtil.getFindDateShort(latestRosterPlayer.getFromDate()));
					output.append(" toDate = " + DateTimeUtil.getFindDateShort(latestRosterPlayer.getToDate()));
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