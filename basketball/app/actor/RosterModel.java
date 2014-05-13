package actor;

import java.util.List;

import models.Game;
import models.Game.ProcessingType;
import models.Player;
import models.RosterPlayer;
import util.DateTime;
import actor.ActorApi.ActiveRosterPlayers;
import actor.ActorApi.ServiceProps;
import actor.ActorApi.UpdateRoster;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class RosterModel extends UntypedActor {
	private ActorRef listener;
	private final ActorRef rosterXmlStats;
	private ActorRef controller;
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
			rosterDate = ((UpdateRoster) message).date;
			rosterTeamKey = ((UpdateRoster) message).team;
		
			rosterXmlStats.tell(message, getSelf());
		}
		else if(message instanceof ActiveRosterPlayers) {
			ActiveRosterPlayers activeRosterPlayers = (ActiveRosterPlayers) message;
			List<RosterPlayer> rosterPlayers = activeRosterPlayers.rosterPlayers;

			//activate new roster players
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
							activeRosterPlayer.setFromDate(DateTime.createDateFromStringDate(rosterDate));
							activeRosterPlayer.setToDate(DateTime.getDateMaxSeason(DateTime.createDateFromStringDate(rosterDate)));
							RosterPlayer.create(activeRosterPlayer, processingType);
						}
						else {
							//player does exist
							//add new roster entry
							
						}
					}
					else {
						//player is on another roster for current season
						//terminate current roster and add new roster entry
					}
				}
				else {
					//player is on current team roster
					break;

				}
			}
			
			//terminate inactive roster players
			List<RosterPlayer> latestRoster = RosterPlayer.findByDateTeam(rosterDate, rosterTeamKey);
			
			controller.tell(message, getSelf());
		}
		else {
			unhandled(message);
		}
	}
}