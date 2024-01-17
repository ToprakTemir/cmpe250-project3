import java.io.*;
import java.util.Scanner;

public class Project3 {
    public static int blendMaxSongPerPlaylistLimit;
    public static int categoryLimit_H;
    public static int categoryLimit_R;
    public static int categoryLimit_B;
    public static int numOfSongsInBlend_heartache = 0;
    public static int numOfSongsInBlend_roadtrip = 0;
    public static int numOfSongsInBlend_blissful = 0;
    public static MinHeap minElementsOfEachPlaylistInBlend_H;
    public static MinHeap minElementsOfEachPlaylistInBlend_R;
    public static MinHeap minElementsOfEachPlaylistInBlend_B;
    public static MaxHeap maxElementsOfEachPlaylistInQueue_H;
    public static MaxHeap maxElementsOfEachPlaylistInQueue_R;
    public static MaxHeap maxElementsOfEachPlaylistInQueue_B;
    public static PrintWriter printWriter;
    public static int numOfAllSongs;
    public static Playlist[] playlists;
    public static void main(String[] args) throws IOException {

        double initialTime = System.currentTimeMillis();

        String mainInputFileName = "C:\\JavaProjects\\Project3\\test-cases\\inputs\\ten_playlists_large.txt";
        String songsInputFileName = "C:\\JavaProjects\\Project3\\test-cases\\songs.txt";
        String outputFileName = "output.txt";

//        String songsInputFileName = args[0];
//        String mainInputFileName = args[1];
//        String outputFileName = args[2];

        File inputFile = new File(mainInputFileName);
        File songsInputFile = new File(songsInputFileName);
        Scanner input = new Scanner(inputFile);
        Scanner songsInput = new Scanner(songsInputFile);
        FileWriter fileWriter = new FileWriter(outputFileName, false);
        printWriter = new PrintWriter(fileWriter);


        // reading songs from song database

        numOfAllSongs = songsInput.nextInt();
        songsInput.nextLine();
        Song[] allSongs = new Song[numOfAllSongs+1]; // song with id X is stored at songs[X] (because of the ordered property of the input)
        for (int i=1; i<numOfAllSongs+1; i++) {
            String tempCurLine = songsInput.nextLine();
            if (tempCurLine.isEmpty()) continue;
            String[] curLine = tempCurLine.split(" ");
            allSongs[i] = new Song(Integer.parseInt(curLine[0]), curLine[1], Integer.parseInt(curLine[2]), Integer.parseInt(curLine[3]), Integer.parseInt(curLine[4]), Integer.parseInt(curLine[5]));
        }

        String[] categories = {"H", "R", "B"};


        // the minHeaps below hold the minimum elements of each playlist that made it to the epic blend.
        // maxHeaps are the reverse, they hold the maximum elements of each playlist that didn't make it to the epic blend.

        minElementsOfEachPlaylistInBlend_H = new MinHeap("H");
        minElementsOfEachPlaylistInBlend_R = new MinHeap("R");
        minElementsOfEachPlaylistInBlend_B = new MinHeap("B");

        maxElementsOfEachPlaylistInQueue_H = new MaxHeap("H");
        maxElementsOfEachPlaylistInQueue_R = new MaxHeap("R");
        maxElementsOfEachPlaylistInQueue_B = new MaxHeap("B");

        // reading first line

        blendMaxSongPerPlaylistLimit = input.nextInt();
        categoryLimit_H = input.nextInt();
        categoryLimit_R = input.nextInt();
        categoryLimit_B = input.nextInt();


        // reading initial state of playlists

        int numOfPlaylists = Integer.parseInt(input.next());
        playlists = new Playlist[numOfPlaylists+1]; // playlist with id X is stored at playlists[X] (because of the ordered property of the input)
        input.nextLine();

        for (int i=1; i<numOfPlaylists+1; i++) {

            // first line
            String tmp = input.nextLine();
            String[] playlistLine = tmp.split(" ");
            Playlist newPlaylist = new Playlist(Integer.parseInt(playlistLine[0]));
            int numOfSongs = Integer.parseInt(playlistLine[1]);

            if (numOfSongs == 0) {
                playlists[i] = newPlaylist;
                input.nextLine();
                continue;
            }

            // second line
            String[] idsOfSongsToBeAdded = input.nextLine().split(" ");
            Song[] songsToBeAdded = new Song[numOfSongs];
            for (int j=0; j < idsOfSongsToBeAdded.length; j++) {
                Song newSong = allSongs[Integer.parseInt(idsOfSongsToBeAdded[j])];
                songsToBeAdded[j] = newSong;
            }
            newPlaylist.addToQueue(songsToBeAdded, "H");
            newPlaylist.addToQueue(songsToBeAdded, "R");
            newPlaylist.addToQueue(songsToBeAdded, "B");

            playlists[i] = newPlaylist;
        }

        // setting up the initial maxHeap

        // I first store the elements I will add into an array and then add them to make use of the buildHeap operation, which makes the operations faster
//        Song[] maxSongsToBeAdded_H = new Song[numOfPlaylists];
//        Song[] maxSongsToBeAdded_R = new Song[numOfPlaylists];
//        Song[] maxSongsToBeAdded_B = new Song[numOfPlaylists];
//        int idx = 0;
        for (Playlist playlist : playlists) {
            if (playlist == null) continue;
            if (!playlist.songsInQueue_heartache.isEmpty()) {
                maxElementsOfEachPlaylistInQueue_H.insert(playlist.getMaxFromQueue("H"));
            }
            if (!playlist.songsInQueue_roadtrip.isEmpty()) {
                maxElementsOfEachPlaylistInQueue_R.insert(playlist.getMaxFromQueue("R"));
            }
            if (!playlist.songsInQueue_blissful.isEmpty()) {
                maxElementsOfEachPlaylistInQueue_B.insert(playlist.getMaxFromQueue("B"));
            }
//            idx++;
        }
//        maxElementsOfEachPlaylistInQueue_H.insert(maxSongsToBeAdded_H);
//        maxElementsOfEachPlaylistInQueue_R.insert(maxSongsToBeAdded_R);
//        maxElementsOfEachPlaylistInQueue_B.insert(maxSongsToBeAdded_B);


        // building the initial blend

        Song[] tempArrayForLimitFilledPlaylists_H = new Song[numOfPlaylists+1];
        Song[] tempArrayForLimitFilledPlaylists_R = new Song[numOfPlaylists+1];
        Song[] tempArrayForLimitFilledPlaylists_B = new Song[numOfPlaylists+1];

        boolean run_H = true;
        boolean run_R = true;
        boolean run_B = true;

        while (run_H || run_R || run_B) {
            if (run_H)
                if (isBlendFull("H") || maxElementsOfEachPlaylistInQueue_H.isEmpty()) run_H = false;
            if (run_R)
                if (isBlendFull("R") || maxElementsOfEachPlaylistInQueue_R.isEmpty()) run_R = false;
            if (run_B)
                if (isBlendFull("B") || maxElementsOfEachPlaylistInQueue_B.isEmpty()) run_B = false;

            Song maxInQueue_H = maxElementsOfEachPlaylistInQueue_H.getMax();
            Song maxInQueue_R = maxElementsOfEachPlaylistInQueue_R.getMax();
            Song maxInQueue_B = maxElementsOfEachPlaylistInQueue_B.getMax();

            if (maxInQueue_H != null && !isBlendFull("H")) {
                if (maxInQueue_H.playlist.isPlaylistLimitFull("H")) {
                    maxElementsOfEachPlaylistInQueue_H.remove(maxInQueue_H);
                    tempArrayForLimitFilledPlaylists_H[maxInQueue_H.playlist.id] = maxInQueue_H;
                } else {
                    maxInQueue_H.playlist.addToBlend(maxInQueue_H, "H");
                    Project3.numOfSongsInBlend_heartache++;
                }
            }
            if (maxInQueue_R != null && !isBlendFull("R")) {
                if (maxInQueue_R.playlist.isPlaylistLimitFull("R")) {
                    maxElementsOfEachPlaylistInQueue_R.remove(maxInQueue_R);
                    tempArrayForLimitFilledPlaylists_R[maxInQueue_R.playlist.id] = maxInQueue_R;
                } else {
                    maxInQueue_R.playlist.addToBlend(maxInQueue_R, "R");
                    Project3.numOfSongsInBlend_roadtrip++;
                }
            }
            if (maxInQueue_B != null && !isBlendFull("B")) {
                if (maxInQueue_B.playlist.isPlaylistLimitFull("B")) {
                    maxElementsOfEachPlaylistInQueue_B.remove(maxInQueue_B);
                    tempArrayForLimitFilledPlaylists_B[maxInQueue_B.playlist.id] = maxInQueue_B;
                } else {
                    maxInQueue_B.playlist.addToBlend(maxInQueue_B, "B");
                    Project3.numOfSongsInBlend_blissful++;
                }
            }
        }
        maxElementsOfEachPlaylistInQueue_H.insert(tempArrayForLimitFilledPlaylists_H);
        maxElementsOfEachPlaylistInQueue_R.insert(tempArrayForLimitFilledPlaylists_R);
        maxElementsOfEachPlaylistInQueue_B.insert(tempArrayForLimitFilledPlaylists_B);


        // handling the operations

        int numOfOperations = Integer.parseInt(input.nextLine());
        for (int i=0; i<numOfOperations; i++) {
            String[] curLine = input.nextLine().split(" ");
            switch (curLine[0]) {
                case "ADD" -> {
                    Song songToAdd = allSongs[Integer.parseInt(curLine[1])];
                    Playlist playlist = playlists[Integer.parseInt(curLine[2])];
                    songToAdd.playlist = playlist;

                    handleOperations("ADD", songToAdd, playlist, categories);
                }
                case "REM" -> {
                    Song songToRemove = allSongs[Integer.parseInt(curLine[1])];
                    Playlist playlist = playlists[Integer.parseInt(curLine[2])];

                    handleOperations("REM", songToRemove, playlist, categories);
                }
                case "ASK" -> {
                    boolean[] containsSong = new boolean[numOfAllSongs];
                    MaxHeap sortHeap = new MaxHeap("ASK");

                    Playlist curPlaylist;
                    for (int m = 1; m < playlists.length; m++) {
                        curPlaylist = playlists[m];
                        for (int q = 1; q <= curPlaylist.songsInBlend_heartache.currentSize; q++) {
                            Song song1 = curPlaylist.songsInBlend_heartache.list[q];
                            if (song1 == null) continue;
                            if (!containsSong[song1.id]) {
                                if (sortHeap.currentSize == sortHeap.list.length - 2)
                                    sortHeap.setCapacity(2 * sortHeap.currentSize + 1);
                                sortHeap.list[++sortHeap.currentSize] = song1;
                            }

                            containsSong[song1.id] = true;
                        }
                        for (int q = 1; q <= curPlaylist.songsInBlend_roadtrip.currentSize; q++) {
                            Song song2 = curPlaylist.songsInBlend_roadtrip.list[q];
                            if (song2 == null) continue;
                            if (!containsSong[song2.id]) {
                                if (sortHeap.currentSize == sortHeap.list.length - 2)
                                    sortHeap.setCapacity(2 * sortHeap.currentSize + 1);
                                sortHeap.list[++sortHeap.currentSize] = song2;
                            }

                            containsSong[song2.id] = true;
                        }
                        for (int q = 1; q <= curPlaylist.songsInBlend_blissful.currentSize; q++) {
                            Song song3 = curPlaylist.songsInBlend_blissful.list[q];
                            if (song3 == null) continue;
                            if (!containsSong[song3.id]) {
                                if (sortHeap.currentSize == sortHeap.list.length - 2)
                                    sortHeap.setCapacity(2 * sortHeap.currentSize + 1);
                                sortHeap.list[++sortHeap.currentSize] = song3;
                            }

                            containsSong[song3.id] = true;
                        }
                    }

                    sortHeap.buildHeap();
                    while (!sortHeap.isEmpty()) {
                        printWriter.print(sortHeap.popMax().id + " ");
                    }
                    printWriter.println();
                }
            }
        }
        printWriter.close();

        double endingTime = System.currentTimeMillis();
        double totalTime = (endingTime-initialTime) / 1000;
        System.out.println(totalTime);
    }
    public static void handleOperations(String operation, Song song, Playlist playlist, String[] categories) {
        int[] addedSongs = new int[3];
        int[] removedSongs = new int[3];
        switch (operation) {
            case "ADD" -> {
                for (String category : categories) {
                    int[] temp = handleAddSong(category, song, playlist);
                    switch (category) {
                        case "H" -> {addedSongs[0] = temp[0]; removedSongs[0] = temp[1];}
                        case "R" -> {addedSongs[1] = temp[0]; removedSongs[1] = temp[1];}
                        case "B" -> {addedSongs[2] = temp[0]; removedSongs[2] = temp[1];}
                    }
                }
                printWriter.println(addedSongs[0] + " " + addedSongs[1] + " " + addedSongs[2]);
                printWriter.println(removedSongs[0] + " " + removedSongs[1] + " " + removedSongs[2]);
            }
            case "REM" -> {
                for (String category : categories) {
                    int[] temp = handleRemoveSong(category, song, playlist);
                    switch (category) {
                        case "H" -> {addedSongs[0] = temp[0]; removedSongs[0] = temp[1];}
                        case "R" -> {addedSongs[1] = temp[0]; removedSongs[1] = temp[1];}
                        case "B" -> {addedSongs[2] = temp[0]; removedSongs[2] = temp[1];}
                    }
                }
                printWriter.println(addedSongs[0] + " " + addedSongs[1] + " " + addedSongs[2]);
                printWriter.println(removedSongs[0] + " " + removedSongs[1] + " " + removedSongs[2]);
            }
        }
    }
    public static int[] handleAddSong(String category, Song songToAdd, Playlist playlist) {
        int addedToBlend = 0;
        int removedFromBlend = 0;

        if (!isBlendFull(category)) {
            if (!playlist.isPlaylistLimitFull(category)) {
                playlist.addToBlend(songToAdd, category);
                addedToBlend = songToAdd.id;
                incrementBlendSongCount(category);
            }
            else { // we'll try to remove the min element from playlist and add this instead
                if (songToAdd.compareTo(playlist.getMinFromBlend(category), category) > 0) { // we can remove min and add after
                    removedFromBlend = playlist.getMinFromBlend(category).id;
                    playlist.removeFromBlend(playlist.getMinFromBlend(category), category);
                    playlist.addToBlend(songToAdd, category);
                    addedToBlend = songToAdd.id;
                }
                else { // we can't add to blend, add to queue
                    playlist.addToQueue(songToAdd, category);
                }
            }
        }
        else {
            if (!playlist.isPlaylistLimitFull(category)) { // try to remove the min of the whole blend and add the new one instead
                if (songToAdd.compareTo(getMinFromBlend(category), category) > 0) { // remove the minOfBlend and add
                    Song oldMinOfBlend = getMinFromBlend(category);
                    removedFromBlend = oldMinOfBlend.id;
                    oldMinOfBlend.playlist.removeFromBlend(oldMinOfBlend, category);
                    playlist.addToBlend(songToAdd, category);
                    addedToBlend = songToAdd.id;
                }
                else { // we can't add to blend, add to the queue
                    playlist.addToQueue(songToAdd, category);
                }
            }
            else { // both blend and the category limit is full, try to remove the min of the playlist and add the new one
                if (songToAdd.compareTo(playlist.getMinFromBlend(category), category) > 0) { // we can remove min and add after
                    removedFromBlend = playlist.getMinFromBlend(category).id;
                    playlist.removeFromBlend(playlist.getMinFromBlend(category), category);
                    playlist.addToBlend(songToAdd, category);
                    addedToBlend = songToAdd.id;
                }
                else { // we can't add to blend, add to queue
                    playlist.addToQueue(songToAdd, category);
                }
            }
        }
        return new int[]{addedToBlend, removedFromBlend};
    }
    public static int[] handleRemoveSong(String category, Song songToRemove, Playlist playlist) {
        int addedToBlend = 0;
        int removedFromBlend = 0;

        Song minFromPlaylist = playlist.getMinFromBlend(category);
        boolean songIsFromBlend = false;
        if (minFromPlaylist == null)
            songIsFromBlend = false;
        else if (songToRemove.compareTo(playlist.getMinFromBlend(category), category) > 0 || songToRemove.equals(playlist.getMinFromBlend(category)))
            songIsFromBlend = true;

        if (songIsFromBlend) { // songToRemove is from blend
            removedFromBlend = songToRemove.id;
            switch (category) {
                case "H" -> {

                    boolean wasBlendFull = isBlendFull(category);

                    // removal
                    if (!songToRemove.equals(playlist.songsInBlend_heartache.getMin())) {
                        playlist.songsInBlend_heartache.remove(songToRemove);
                        playlist.numOfSongsInBlend_heartache--;
                        Project3.numOfSongsInBlend_heartache--;
                    }
                    else {
                        minElementsOfEachPlaylistInBlend_H.remove(songToRemove);
                        playlist.songsInBlend_heartache.remove(songToRemove);
                        playlist.numOfSongsInBlend_heartache--;
                        Project3.numOfSongsInBlend_heartache--;
                    }

                    // adding new appropriate song
                    if (!wasBlendFull) { // we can only replace the removed from the same playlist
                        if (!playlist.songsInQueue_heartache.isEmpty()) {
                            addedToBlend = playlist.songsInQueue_heartache.getMax().id;
                            playlist.addToBlend(playlist.songsInQueue_heartache.getMax(), category);
                            Project3.numOfSongsInBlend_heartache++;
                        }
                    }
                    else { // blend is full, we will try to replace with the max of all queues
                        Song maxSongWithoutAFilledPlaylist = maxElementsOfEachPlaylistInQueue_H.getNextMax();
                        if (maxSongWithoutAFilledPlaylist != null){
                            maxSongWithoutAFilledPlaylist.playlist.addToBlend(maxSongWithoutAFilledPlaylist, category);
                            addedToBlend = maxSongWithoutAFilledPlaylist.id;
                            Project3.numOfSongsInBlend_heartache++;
                        }
                    }

                }
                case "R" -> {
                    boolean wasBlendFull = isBlendFull(category);
                    if (!songToRemove.equals(playlist.songsInBlend_roadtrip.getMin())) {
                        playlist.songsInBlend_roadtrip.remove(songToRemove);
                        playlist.numOfSongsInBlend_roadtrip--;
                        Project3.numOfSongsInBlend_roadtrip--;
                    }
                    else {
                        minElementsOfEachPlaylistInBlend_R.remove(songToRemove);
                        playlist.songsInBlend_roadtrip.remove(songToRemove);
                        playlist.numOfSongsInBlend_roadtrip--;
                        Project3.numOfSongsInBlend_roadtrip--;
                    }
                    if (!wasBlendFull) { // we can only replace the removed from the same playlist
                        if (!playlist.songsInQueue_roadtrip.isEmpty()) {
                            addedToBlend = playlist.songsInQueue_roadtrip.getMax().id;
                            playlist.addToBlend(playlist.songsInQueue_roadtrip.getMax(), category);
                            Project3.numOfSongsInBlend_roadtrip++;
                        }
                    }
                    else { // blend is full, we will try to replace with the max of all queues
                        Song maxSongWithoutAFilledPlaylist = maxElementsOfEachPlaylistInQueue_R.getNextMax();
                        if (maxSongWithoutAFilledPlaylist != null){
                            maxSongWithoutAFilledPlaylist.playlist.addToBlend(maxSongWithoutAFilledPlaylist, category);
                            addedToBlend = maxSongWithoutAFilledPlaylist.id;
                            Project3.numOfSongsInBlend_roadtrip++;
                        }
                    }
                }
                case "B" -> {
                    boolean wasBlendFull = isBlendFull(category);
                    if (!songToRemove.equals(playlist.songsInBlend_blissful.getMin())) {
                        playlist.songsInBlend_blissful.remove(songToRemove);
                        playlist.numOfSongsInBlend_blissful--;
                        Project3.numOfSongsInBlend_blissful--;
                    }
                    else {
                        minElementsOfEachPlaylistInBlend_B.remove(songToRemove);
                        playlist.songsInBlend_blissful.remove(songToRemove);
                        playlist.numOfSongsInBlend_blissful--;
                        Project3.numOfSongsInBlend_blissful--;
                    }
                    if (!wasBlendFull) { // we can only replace the removed from the same playlist
                        if (!playlist.songsInQueue_blissful.isEmpty()) {
                            addedToBlend = playlist.songsInQueue_blissful.getMax().id;
                            playlist.addToBlend(playlist.songsInQueue_blissful.getMax(), category);
                            Project3.numOfSongsInBlend_blissful++;
                        }
                    }
                    else { // blend is full, we will try to replace with the max of all queues
                        Song maxSongWithoutAFilledPlaylist = maxElementsOfEachPlaylistInQueue_B.getNextMax();
                        if (maxSongWithoutAFilledPlaylist != null){
                            maxSongWithoutAFilledPlaylist.playlist.addToBlend(maxSongWithoutAFilledPlaylist, category);
                            addedToBlend = maxSongWithoutAFilledPlaylist.id;
                            Project3.numOfSongsInBlend_blissful++;
                        }
                    }
                }
            }
        }

        else { // songToRemove is not from blend
            playlist.removeFromQueue(songToRemove, category);
        }

        return new int[]{addedToBlend, removedFromBlend};
    }
    public static void incrementBlendSongCount(String category) {
        switch (category) {
            case "H" -> numOfSongsInBlend_heartache++;
            case "R" -> numOfSongsInBlend_roadtrip++;
            case "B" -> numOfSongsInBlend_blissful++;
        }
    }
    public static Song getMinFromBlend(String category){
        return switch (category) {
            case "H" -> minElementsOfEachPlaylistInBlend_H.getMin();
            case "R" -> minElementsOfEachPlaylistInBlend_R.getMin();
            case "B" -> minElementsOfEachPlaylistInBlend_B.getMin();
            default -> null;
        };
    }
    public static boolean isBlendFull(String category) {
        return switch (category) {
            case "H" -> numOfSongsInBlend_heartache >= categoryLimit_H;
            case "R" -> numOfSongsInBlend_roadtrip >= categoryLimit_R;
            case "B" -> numOfSongsInBlend_blissful >= categoryLimit_B;
            default -> false;
        };
    }

}
