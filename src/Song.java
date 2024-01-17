public class Song {
    public final int id;
    public final String name;
    public final int playCount;
    public int score_H;
    public int score_R;
    public int score_B;
    public Playlist playlist; // references to the playlist that this song belongs to

    public Song(int id, String name, int playCount, int score_H, int score_R, int score_B) {
        this.id = id;
        this.name = name;
        this.playCount = playCount;
        this.score_B = score_B;
        this.score_H = score_H;
        this.score_R = score_R;
    }

    public int compareTo(Song other, String scoreType) {
        int ret=0;
        switch (scoreType) {
            case "H" -> ret = this.score_H - other.score_H;
            case "R" -> ret = this.score_R - other.score_R;
            case "B" -> ret = this.score_B - other.score_B;
            case "ASK" -> ret = this.playCount - other.playCount;
        }
        if (ret == 0) { // compare names
            ret = other.name.compareTo(this.name);
        }
        return ret;
    }

}
