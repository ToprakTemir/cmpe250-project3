public class Playlist {
    public int id;
    public MinHeap songsInBlend_heartache; // all the heartache songs that made it to the epic blend from this playlist
    public MaxHeap songsInQueue_heartache; // all the heartache songs that did not make it to the epic blend from this playlist
    public MinHeap songsInBlend_roadtrip;
    public MaxHeap songsInQueue_roadtrip;
    public MinHeap songsInBlend_blissful;
    public MaxHeap songsInQueue_blissful;
    public int numOfSongsInBlend_heartache;
    public int numOfSongsInBlend_roadtrip;
    public int numOfSongsInBlend_blissful;
    public Playlist(int id) {
        this.id = id;
        songsInBlend_heartache = new MinHeap("H");
        songsInQueue_heartache = new MaxHeap("H");
        songsInBlend_roadtrip = new MinHeap("R");
        songsInQueue_roadtrip = new MaxHeap("R");
        songsInBlend_blissful = new MinHeap("B");
        songsInQueue_blissful = new MaxHeap("B");
    }

    public boolean isPlaylistLimitFull(String category) {
        return switch (category) {
            case "H" -> numOfSongsInBlend_heartache >= Project3.blendMaxSongPerPlaylistLimit;
            case "R" -> numOfSongsInBlend_roadtrip >= Project3.blendMaxSongPerPlaylistLimit;
            case "B" -> numOfSongsInBlend_blissful >= Project3.blendMaxSongPerPlaylistLimit;
            default -> false;
        };
    }

    /**
     * simple, initial addToQueue that is called before the operations and creating the first blends,
     * so we don't need to check everything that we check in the other addToQueue method
     */
    public void addToQueue(Song[] elements, String category) {
        for (Song element : elements) {
            if (element == null) continue;
            element.playlist = this;
        }
        switch(category) {
            case "H" -> songsInQueue_heartache.insert(elements);
            case "R" -> songsInQueue_roadtrip.insert(elements);
            case "B" -> songsInQueue_blissful.insert(elements);
        }
    }

    public void addToQueue(Song element, String category) {
        switch(category) {
            case "H" -> {
                if (songsInQueue_heartache.isEmpty()) {
                    songsInQueue_heartache.insert(element);
                    Project3.maxElementsOfEachPlaylistInQueue_H.insert(element);
                    return;
                }
                // if the new element will be changing the max, update the Main
                if (element.compareTo(songsInQueue_heartache.getMax(), category) > 0) {
                    Song oldMax = songsInQueue_heartache.getMax();
                    songsInQueue_heartache.insert(element);
                    Project3.maxElementsOfEachPlaylistInQueue_H.remove(oldMax);
                    Project3.maxElementsOfEachPlaylistInQueue_H.insert(element);
                }
                else {
                    songsInQueue_heartache.insert(element);
                }
            }
            case "R" -> {
                if (songsInQueue_roadtrip.isEmpty()) {
                    songsInQueue_roadtrip.insert(element);
                    Project3.maxElementsOfEachPlaylistInQueue_R.insert(element);
                    return;
                }
                if (element.compareTo(songsInQueue_roadtrip.getMax(), category) > 0) {
                    Song oldMax = songsInQueue_roadtrip.getMax();
                    songsInQueue_roadtrip.insert(element);
                    Project3.maxElementsOfEachPlaylistInQueue_R.remove(oldMax);
                    Project3.maxElementsOfEachPlaylistInQueue_R.insert(element);
                }
                else {
                    songsInQueue_roadtrip.insert(element);
                }
            }
            case "B" -> {
                if (songsInQueue_blissful.isEmpty()) {
                    songsInQueue_blissful.insert(element);
                    Project3.maxElementsOfEachPlaylistInQueue_B.insert(element);
                    return;
                }
                if (element.compareTo(songsInQueue_blissful.getMax(), category) > 0) {
                    Song oldMax = songsInQueue_blissful.getMax();
                    songsInQueue_blissful.insert(element);
                    Project3.maxElementsOfEachPlaylistInQueue_B.remove(oldMax);
                    Project3.maxElementsOfEachPlaylistInQueue_B.insert(element);
                }
                else {
                    songsInQueue_blissful.insert(element);
                }
            }
        }
    }
    public void removeFromQueue(Song element, String category) {
        switch (category) {
            case "H" -> {
                if (element.equals(songsInQueue_heartache.getMax())) {
                    Song oldMax = songsInQueue_heartache.popMax();
                    Project3.maxElementsOfEachPlaylistInQueue_H.remove(oldMax);
                    if (!songsInQueue_heartache.isEmpty())
                        Project3.maxElementsOfEachPlaylistInQueue_H.insert(songsInQueue_heartache.getMax());
                }
                else
                    songsInQueue_heartache.remove(element);
            }
            case "R" -> {
                if (element.equals(songsInQueue_roadtrip.getMax())) {
                    Song oldMax = songsInQueue_roadtrip.popMax();
                    Project3.maxElementsOfEachPlaylistInQueue_R.remove(oldMax);
                    if (!songsInQueue_roadtrip.isEmpty())
                        Project3.maxElementsOfEachPlaylistInQueue_R.insert(songsInQueue_roadtrip.getMax());
                }
                else
                    songsInQueue_roadtrip.remove(element);
            }
            case "B" -> {
                if (element.equals(songsInQueue_blissful.getMax())) {
                    Song oldMax = songsInQueue_blissful.popMax();
                    Project3.maxElementsOfEachPlaylistInQueue_B.remove(oldMax);
                    if (!songsInQueue_blissful.isEmpty())
                        Project3.maxElementsOfEachPlaylistInQueue_B.insert(songsInQueue_blissful.getMax());
                }
                else
                    songsInQueue_blissful.remove(element);
            }
        }
    }

    public void addToBlend(Song element, String category) {
        switch (category) {
            case "H" -> {
                Song oldMinInBlend = songsInBlend_heartache.getMin();
                Song oldMaxInQueue = songsInQueue_heartache.getMax();

                // organising the playlist-side heaps

                songsInQueue_heartache.remove(element);
                songsInBlend_heartache.insert(element);
                numOfSongsInBlend_heartache++;

                // organising the main-side heaps

                // check if the max element (in queue) of this playlist is changed, if it did, replace the max on the main
                if (oldMaxInQueue != null) {
                    if (!oldMaxInQueue.equals(songsInQueue_heartache.getMax())) {
                        Project3.maxElementsOfEachPlaylistInQueue_H.remove(oldMaxInQueue);
                        Project3.maxElementsOfEachPlaylistInQueue_H.insert(songsInQueue_heartache.getMax());
                    }
                }

                // check if the min element (in blend) of this playlist is changed, if it did, replace the min on the main
                if (oldMinInBlend == null) {
                    Project3.minElementsOfEachPlaylistInBlend_H.insert(songsInBlend_heartache.getMin());
                }
//                else if (!oldMinInBlend.equals(songsInBlend_heartache.getMin())) {
//                    Main.minElementsOfEachPlaylistInBlend_H.remove(oldMinInBlend);
//                    Main.minElementsOfEachPlaylistInBlend_H.insert(songsInBlend_heartache.getMin());
//                }
                else {
                    Project3.minElementsOfEachPlaylistInBlend_H.remove(oldMinInBlend);
                    Project3.minElementsOfEachPlaylistInBlend_H.insert(songsInBlend_heartache.getMin());
                }
            }
            case "R" -> {
                Song oldMinInBlend = songsInBlend_roadtrip.getMin();
                Song oldMaxInQueue = songsInQueue_roadtrip.getMax();
                songsInQueue_roadtrip.remove(element);
                songsInBlend_roadtrip.insert(element);
                numOfSongsInBlend_roadtrip++;
                if (oldMaxInQueue != null) {
                    if (!oldMaxInQueue.equals(songsInQueue_roadtrip.getMax())) {
                        Project3.maxElementsOfEachPlaylistInQueue_R.remove(oldMaxInQueue);
                        Project3.maxElementsOfEachPlaylistInQueue_R.insert(songsInQueue_roadtrip.getMax());
                    }
                }
                if (oldMinInBlend == null) {
                    Project3.minElementsOfEachPlaylistInBlend_R.insert(songsInBlend_roadtrip.getMin());
                }
//                else if (!oldMinInBlend.equals(songsInBlend_roadtrip.getMin())) {
//                    Main.minElementsOfEachPlaylistInBlend_R.remove(oldMinInBlend);
//                    Main.minElementsOfEachPlaylistInBlend_R.insert(songsInBlend_roadtrip.getMin());
//                }
                else {
                    Project3.minElementsOfEachPlaylistInBlend_R.remove(oldMinInBlend);
                    Project3.minElementsOfEachPlaylistInBlend_R.insert(songsInBlend_roadtrip.getMin());
                }
            }
            case "B" -> {
                Song oldMinInBlend = songsInBlend_blissful.getMin();
                Song oldMaxInQueue = songsInQueue_blissful.getMax();
                songsInQueue_blissful.remove(element);
                songsInBlend_blissful.insert(element);
                numOfSongsInBlend_blissful++;
                if (oldMaxInQueue != null) {
                    if (!oldMaxInQueue.equals(songsInQueue_blissful.getMax())) {
                        Project3.maxElementsOfEachPlaylistInQueue_B.remove(oldMaxInQueue);
                        Project3.maxElementsOfEachPlaylistInQueue_B.insert(songsInQueue_blissful.getMax());
                    }
                }
                if (oldMinInBlend == null) {
                    Project3.minElementsOfEachPlaylistInBlend_B.insert(songsInBlend_blissful.getMin());
                }
//                else if (!oldMinInBlend.equals(songsInBlend_blissful.getMin())) {
//                    Main.minElementsOfEachPlaylistInBlend_B.remove(oldMinInBlend);
//                    Main.minElementsOfEachPlaylistInBlend_B.insert(songsInBlend_blissful.getMin());
//                }
                else {
                    Project3.minElementsOfEachPlaylistInBlend_B.remove(oldMinInBlend);
                    Project3.minElementsOfEachPlaylistInBlend_B.insert(songsInBlend_blissful.getMin());
                }
            }
        }
    }
    public void removeFromBlend(Song element, String category) {
        switch (category) {
            case "H" -> {
                Song oldMinInBlend = songsInBlend_heartache.getMin();
                Song oldMaxInQueue = songsInQueue_heartache.getMax();

                // organising the playlist-side heaps

                songsInBlend_heartache.remove(element);
                songsInQueue_heartache.insert(element);
                numOfSongsInBlend_heartache--;

                // organising the main-side heaps

                // check if the max element of the queue is changed, if it did, update the main
                if (oldMaxInQueue != null) {
                    if (!oldMaxInQueue.equals(songsInQueue_heartache.getMax())) {
                        Project3.maxElementsOfEachPlaylistInQueue_H.remove(oldMaxInQueue);
                        Project3.maxElementsOfEachPlaylistInQueue_H.insert(songsInQueue_heartache.getMax());
                    }
                }
                else {
                    Project3.maxElementsOfEachPlaylistInQueue_H.insert(songsInQueue_heartache.getMax());
                }
                // check if the min element of the blend is changed, if it did, update the main
                if (!oldMinInBlend.equals(songsInBlend_heartache.getMin())) {
                    Project3.minElementsOfEachPlaylistInBlend_H.remove(oldMinInBlend);
                    Project3.minElementsOfEachPlaylistInBlend_H.insert(songsInBlend_heartache.getMin());
                }
            }
            case "R" -> {
                Song oldMinInBlend = songsInBlend_roadtrip.getMin();
                Song oldMaxInQueue = songsInQueue_roadtrip.getMax();
                songsInBlend_roadtrip.remove(element);
                songsInQueue_roadtrip.insert(element);
                numOfSongsInBlend_roadtrip--;
                if (oldMaxInQueue != null) {
                    if (!oldMaxInQueue.equals(songsInQueue_roadtrip.getMax())) {
                        Project3.maxElementsOfEachPlaylistInQueue_R.remove(oldMaxInQueue);
                        Project3.maxElementsOfEachPlaylistInQueue_R.insert(songsInQueue_roadtrip.getMax());
                    }
                }
                else {
                    Project3.maxElementsOfEachPlaylistInQueue_R.insert(songsInQueue_roadtrip.getMax());
                }
                if (!oldMinInBlend.equals(songsInBlend_roadtrip.getMin())) {
                    Project3.minElementsOfEachPlaylistInBlend_R.remove(oldMinInBlend);
                    Project3.minElementsOfEachPlaylistInBlend_R.insert(songsInBlend_roadtrip.getMin());
                }
            }
            case "B" -> {
                Song oldMinInBlend = songsInBlend_blissful.getMin();
                Song oldMaxInQueue = songsInQueue_blissful.getMax();
                songsInBlend_blissful.remove(element);
                songsInQueue_blissful.insert(element);
                numOfSongsInBlend_blissful--;
                if (oldMaxInQueue != null) {
                    if (!oldMaxInQueue.equals(songsInQueue_blissful.getMax())) {
                        Project3.maxElementsOfEachPlaylistInQueue_B.remove(oldMaxInQueue);
                        Project3.maxElementsOfEachPlaylistInQueue_B.insert(songsInQueue_blissful.getMax());
                    }
                }
                else {
                    Project3.maxElementsOfEachPlaylistInQueue_B.insert(songsInQueue_blissful.getMax());
                }
                if (!oldMinInBlend.equals(songsInBlend_blissful.getMin())) {
                    Project3.minElementsOfEachPlaylistInBlend_B.remove(oldMinInBlend);
                    Project3.minElementsOfEachPlaylistInBlend_B.insert(songsInBlend_blissful.getMin());
                }
            }
        }
    }


    public Song getMaxFromQueue(String category) {
        return switch (category) {
            case "H" -> songsInQueue_heartache.getMax();
            case "R" -> songsInQueue_roadtrip.getMax();
            case "B" -> songsInQueue_blissful.getMax();
            default -> null;
        };
    }
    public Song getMinFromBlend(String category) {
        return switch(category) {
            case "H" -> songsInBlend_heartache.getMin();
            case "R" -> songsInBlend_roadtrip.getMin();
            case "B" -> songsInBlend_blissful.getMin();
            default -> null;
        };
    }

    public Song popMaxFromQueue(String category) {
        return switch (category) {
            case "H" -> songsInQueue_heartache.popMax();
            case "R" -> songsInQueue_roadtrip.popMax();
            case "B" -> songsInQueue_blissful.popMax();
            default -> null;
        };
    }

    public Song popMinFromBlend(String category) {
        return switch(category) {
            case "H" -> songsInBlend_heartache.popMin();
            case "R" -> songsInBlend_roadtrip.popMin();
            case "B" -> songsInBlend_blissful.popMin();
            default -> null;
        };
    }
}
