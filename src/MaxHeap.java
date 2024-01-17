import java.util.HashMap;

public class MaxHeap {
    private static final int defaultCapacity = 30;
    public int currentSize;
    public Song[] list;
    public String compareMode; // determines if the songs will be sorted using heartache score, roadtrip score or blissful score

    // TODO make sure all the methods use this hashtable correctly then implement removeFromMiddle
    public HashMap<Integer, Integer> indexesOfElements; // takes the id of a song, returns the index of the song


    public MaxHeap(String compareMode) {
        list = new Song[defaultCapacity];
        currentSize = 0;
        this.compareMode = compareMode;
        indexesOfElements = new HashMap<>();
    }

//    public MaxHeap(String compareMode, Song[] elements) {
//        indexesOfElements = new HashMap<>();
//        this.compareMode = compareMode;
//        currentSize = elements.length;
//        list = new Song[(currentSize + 2) * 11 / 10];
//
//        int i = 1;
//        for (Song element : elements) {
//            list[i] = element;
//            indexesOfElements.put(list[i].id, i);
//            i++;
//        }
//        buildHeap();
//    }

    public Song parent(int idx) {
        if (idx == 1) return null;
        return list[idx / 2];
    }

    private void percolateDown(int hole) {

        if (hole == 0) return;
        if (isEmpty()) return;

        Song tmp = list[hole];
        int child;

        while (hole * 2 <= currentSize) {

            child = hole * 2;
            if (child != currentSize)
                if (list[child + 1].compareTo(list[child], compareMode) > 0)
                    child++;

            if (list[child].compareTo(tmp, compareMode) > 0) {
                list[hole] = list[child];
                indexesOfElements.put(list[hole].id, hole);
            } else {
                break;
            }
            hole = child;
        }

        list[hole] = tmp;
        indexesOfElements.put(list[hole].id, hole);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after percDown");
    }

    /**
     * replaces the song at the index "hole" with the given element, then percolates the element up
     * @param element element that will be inserted
     * @param hole    index of the place that element will be placed
     */
    private void percolateUp(Song element, int hole) {

        if (hole == 1) {
//            list[hole] = element;
            return;
        }
//        for (list[0] = element; list[hole/2].compareTo(list[hole], compareMode) < 0; hole /= 2) {
//            list[hole] = list[hole/2];
//            indexesOfElements.replace(list[hole].id, hole);
//        }
        while (hole > 1){
            if (list[hole/2].compareTo(element, compareMode) > 0) break;
            list[hole] = list[hole/2];
            indexesOfElements.put(list[hole].id, hole);
            hole /= 2;
        }
        list[hole] = element;
        indexesOfElements.put(list[hole].id, hole);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after percUp maxHeap");
    }

    public void insert(Song element) {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before insert maxHeap");
        if (element == null) return;
        if (currentSize == list.length - 1)
            setCapacity(2 * currentSize + 1);

        int hole = ++currentSize;
        list[hole] = element;
        indexesOfElements.put(list[hole].id, hole);
        percolateUp(element, hole);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after insert maxHeap");
    }

    public void insert(Song[] elements) {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before insert2 maxHeap");
        if (currentSize + elements.length >= list.length)
            setCapacity((currentSize + elements.length) * 2 + 1);

        for (Song element : elements) {
            if (element == null) continue;
            list[++currentSize] = element;
            indexesOfElements.put(list[currentSize].id, currentSize);
        }

        buildHeap();
//        if (isHeapPropertyViolated(1))
//            System.out.println("after insert2 maxHeap");
    }

    public Song getMax() {
        if (currentSize == 0) return null;
        return list[1];
    }

    public Song popMax() {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before popMax maxHeap");
        Song maxElement = getMax();
        indexesOfElements.remove(maxElement.id);
        if (currentSize != 1) {
            list[1] = list[currentSize];
            indexesOfElements.put(list[1].id, 1);
        }
        currentSize--;
        percolateDown(1);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after popMax maxHeap");
        return maxElement;
    }

    /**
     * removes the first instance of the input, works in O(n)
     * @param element the element to be removed
     */
    public void remove(Song element) {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before remove maxHeap");
        if (isEmpty()) return;
        if (element.equals(getMax())) {
            popMax();
            return;
        }
        Integer index = indexesOfElements.get(element.id);
        if (index == null) return;
        indexesOfElements.remove(element.id);
        list[index] = list[currentSize--];
        indexesOfElements.put(list[index].id, index);
        if (list[index].compareTo(parent(index), compareMode) > 0)
            percolateUp(list[index], index);
        else {
            percolateDown(index);
        }
//        if (isHeapPropertyViolated(1))
//            System.out.println("after remove in maxHeap");
    }

    public void setCapacity(int newSize) {
        Song[] newList = new Song[newSize];
        for (int i=1; i<list.length; i++) {
            newList[i] = list[i];
        }
        list = newList;
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    /**
     * makes a heap with sorting a random list into a heap-sorted list
     */
    public void buildHeap() {
        for (int i = currentSize / 2; i > 0; i--)
            percolateDown(i);
    }

    public Song getNextMax() {
        return getNextMax(1);
    }
    private Song getNextMax(int index) {

        if (isEmpty()) return null;
        if (index > currentSize) return null;

        switch (compareMode) {
            case "H" -> {
                if (list[index].playlist.numOfSongsInBlend_heartache < Project3.blendMaxSongPerPlaylistLimit)
                    return list[index];
            }
            case "R" -> {
                if (list[index].playlist.numOfSongsInBlend_roadtrip < Project3.blendMaxSongPerPlaylistLimit)
                    return list[index];
            }
            case "B" -> {
                if (list[index].playlist.numOfSongsInBlend_blissful < Project3.blendMaxSongPerPlaylistLimit)
                    return list[index];
            }
        }

        // if you can't return current song, return the max of the children
        Song leftMax = getNextMax(index * 2);
        Song rightMax = getNextMax(index * 2 + 1);
        if (leftMax != null && rightMax != null) {
            boolean isLeftLarger = leftMax.compareTo(rightMax, compareMode) > 0;
            if (isLeftLarger) return leftMax;
            else return rightMax;
        }
        else if (leftMax == null && rightMax != null)
            return rightMax;
        else return leftMax;

    }
    private boolean isHeapPropertyViolated(int i) {
        boolean violated = false;
        if (i >= currentSize / 2)
            return false;
        if (list[i].compareTo(list[2*i], compareMode) < 0 || list[i].compareTo(list[2*i+1], compareMode) < 0)
            violated = true;
        if (isHeapPropertyViolated(2*i) || isHeapPropertyViolated(2*i + 1))
            violated = true;
        return violated;
    }
}
















//public class MaxHeap {
//    private MinHeap internalMinHeap;
//    public String compareMode;
//    public MaxHeap(String compareMode) {
//        internalMinHeap = new MinHeap(compareMode);
//        this.compareMode = compareMode;
//    }
//    public MaxHeap(String compareMode, Song[] elements) {
//        for (Song element : elements)
//            flipSortingScore(element);
//        internalMinHeap = new MinHeap(compareMode, elements);
//    }
//
//    private void flipSortingScore(Song element) {
//        switch (compareMode) {
//            case "H" -> element.score_H *= -1;
//            case "R" -> element.score_R *= -1;
//            case "B" -> element.score_B *= -1;
//        }
//    }
//    public void insert(Song element) {
//        flipSortingScore(element);
//        internalMinHeap.insert(element);
//    }
//    public void insert(Song[] elements) {
//        for (Song element : elements) {
//            if (element == null) continue;
//            flipSortingScore(element);
//        }
//        internalMinHeap.insert(elements);
//    }
//    public Song getMax() {
//        Song max = internalMinHeap.getMin();
//        if (max == null) return null;
//        flipSortingScore(max);
//        return max;
//    }
//    public Song popMax() {
//        Song max = internalMinHeap.popMin();
//        flipSortingScore(max);
//        return max;
//    }
//    public void remove(Song element) {
//        internalMinHeap.remove(element);
//    }
//    public boolean isEmpty() {
//         return internalMinHeap.isEmpty();
//    }
//
//    /**
//     * @return the max element that is not stuck on the playlist addition limit
//     */
//    public Song getNextMax() {
//        return getNextMax(1);
//    }
//    public Song getNextMax(int index) {
//
//        // return max song (the song at the index) if the song's playlist's limit isn't exceeded
//        switch (compareMode) {
//            case "H" -> {
//                if (internalMinHeap.list[index].playlist.numOfSongsInBlend_heartache < Main.categoryLimit_H)
//                    return internalMinHeap.list[index];
//            }
//            case "R" -> {
//                if (internalMinHeap.list[index].playlist.numOfSongsInBlend_roadtrip < Main.categoryLimit_R)
//                    return internalMinHeap.list[index];
//            }
//            case "B" -> {
//                if (internalMinHeap.list[index].playlist.numOfSongsInBlend_blissful < Main.categoryLimit_B)
//                    return internalMinHeap.list[index];
//            }
//        }
//
//        // if you can't return current song, return the max of the children
//        Song leftMax = getNextMax(index*2);
//        Song rightMax = getNextMax(index*2 + 1);
//        boolean isLeftLarger = leftMax.compareTo(rightMax, compareMode) > 0;
//
//        if (isLeftLarger)
//            return leftMax;
//        else
//            return rightMax;
//
//    }
//}
