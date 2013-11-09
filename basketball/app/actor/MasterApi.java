package actor;

import java.util.List;

import models.partial.GameKey;

public interface MasterApi {
	public static final Object Start = "Start";
	
	public static class GameKeys {
		public final List<GameKey> games;
		
		public GameKeys(List<GameKey> games) {
			this.games = games;
		}			
	}
}