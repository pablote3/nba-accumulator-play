package json.xmlStats;

import models.BoxScore;
import models.Game;
import models.Official;
import models.Team;

public class NBABoxScore {
	public Team away_team;
	public Team home_team;
	public Game event_information;
	public int[] away_period_scores;
	public int[] home_period_scores;
	public BoxScore away_totals;
	public BoxScore home_totals;
	public BoxScorePlayerDTO[] away_stats;
	public BoxScorePlayerDTO[] home_stats;
	public Official[] officials;
}
