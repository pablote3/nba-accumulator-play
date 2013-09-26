package models.partial;

import models.entity.BoxScore;
import models.entity.Game;
import models.entity.Official;
import models.entity.Team;

public class XmlStats {
	public Team away_team;
	public Team home_team;
	public Game event_information;
	public int[] away_period_scores;
	public int[] home_period_scores;
	public BoxScore away_totals;
	public BoxScore home_totals;
	public Official[] officials;
}
