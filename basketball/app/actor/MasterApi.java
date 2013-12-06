package actor;

import java.util.List;

public interface MasterApi {
	public static final Object Start = "Start";
	
	public static class GameIds {
		public final List<Long> games;
		
		public GameIds(List<Long> games) {
			this.games = games;
		}			
	}
}