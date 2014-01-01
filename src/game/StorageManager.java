package game;


import java.util.prefs.Preferences;

/**
 * Created by Andrew on 1/01/14.
 */
public class StorageManager {
    public static final Preferences prefs = Preferences.userRoot().node("/settings");

    public static int getBestTime() {
        return prefs.getInt("bestTime", Integer.MAX_VALUE);
    }

    public static void setBestTime(int bestTime) {
        prefs.putInt("bestTime", bestTime);
    }

    public static int getLowestMoves() {
        return prefs.getInt("lowestMoves", Integer.MAX_VALUE);
    }

    public static void setLowestMoves(int lowestMoves) {
        prefs.putInt("lowestMoves", lowestMoves);
    }

    public static double getRatio() {
        int losses = getLosses();

        return losses == 0 ? 0 : ((double) getWins()) / ((double) losses);
    }

    public static int getLosses() {
        return (int) prefs.getInt("losses", 0);
    }

    public static int getWins() {
        return (int) prefs.getInt("wins", 0);
    }

    public static void win(int elapsedTime, int numMoves) {
        prefs.putInt("wins", prefs.getInt("wins", 0) + 1);
        if (getLowestMoves() > numMoves) {
            setLowestMoves(numMoves);
        }
        if (getBestTime() > elapsedTime) {
            setBestTime(elapsedTime);
        }
    }

    public static void loss() {
        prefs.putInt("losses", prefs.getInt("losses", 0) + 1);
    }
}
