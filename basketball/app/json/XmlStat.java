package json;

import models.BoxScore;
import models.BoxScorePlayer;
import models.Game;
import models.Official;
import models.Team;

public class XmlStat {
	public Team away_team;
	public Team home_team;
	public Game event_information;
	public int[] away_period_scores;
	public int[] home_period_scores;
	public BoxScore away_totals;
	public BoxScore home_totals;
	public BoxScorePlayer[] away_stats;
	public BoxScorePlayer[] home_stats;
	public Official[] officials;
}
