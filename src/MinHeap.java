import java.util.HashMap;

public class MinHeap {
    private static final int defaultCapacity = 30;
    public int currentSize;
    public Song[] list;
    public String compareMode; // determines if the songs will be sorted using heartache score, roadtrip score or blissful score
    public HashMap<Integer, Integer> indexesOfElements; // takes the id of a song, returns the index of the song


    public MinHeap(String compareMode) {
        list = new Song[defaultCapacity];
        currentSize = 0;
        this.compareMode = compareMode;
        indexesOfElements = new HashMap<>();
    }
//    public MinHeap(String compareMode, Song[] elements) {
//        indexesOfElements = new HashMap<>();
//        this.compareMode = compareMode;
//        currentSize = elements.length;
//        list = new Song[(currentSize + 2) * 11/10];
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
        return list[idx/2];
    }

    private void percolateDown(int hole) {

        if (hole == 0) return;
        if (isEmpty()) return;

        Song tmp = list[hole];
        int child;

        while (hole * 2 <= currentSize) {

            // select which child (right/left)
            child = hole * 2;
            if (child != currentSize)
                if (list[child+1].compareTo(list[child], compareMode) < 0)
                    child++;

            // swap
            if (list[child].compareTo(tmp, compareMode) < 0) {
                list[hole] = list[child];
                indexesOfElements.replace(list[hole].id, hole);
            }
            else
                break;

            hole = child;
        }

        list[hole] = tmp;
        indexesOfElements.put(list[hole].id, hole);
//        if (isHeapPropertyViolated(1)) {
//            System.out.println("after percDown");
//        }
    }

    /**
     * replaces the song at the index "hole" with the given element, then percolates the element up
     * @param element element that will be inserted
     * @param hole index of the place that element will be placed
     */
    private void percolateUp(Song element, int hole) {
        if (hole == 1) {
            return;
        }
//        for (list[0] = element ; list[hole/2].compareTo(list[hole], compareMode) > 0 ; hole /= 2) {
//            list[hole] = list[hole/2];
//            indexesOfElements.replace(list[hole].id, hole);
//        }
        while (hole > 1){
            if (list[hole/2].compareTo(element, compareMode) < 0) break;
            list[hole] = list[hole/2];
            indexesOfElements.replace(list[hole].id, hole);
            hole /= 2;
        }
        list[hole] = element;
        indexesOfElements.replace(list[hole].id, hole);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after percUp");
    }

    public void insert(Song element) {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before insert");
        if (element == null) return;
        if (currentSize == list.length - 1)
            setCapacity(2 * currentSize + 1);

        int hole = ++currentSize;
        list[hole] = element;
        indexesOfElements.put(list[hole].id, hole);
        percolateUp(element, hole);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after insert");
    }

    public void insert(Song[] elements) {
        if (currentSize + elements.length >= list.length)
            setCapacity((currentSize + elements.length)*2 + 1);

        for (Song element : elements) {
            if (element == null) continue;
            list[++currentSize] = element;
            indexesOfElements.put(list[currentSize].id, currentSize);
        }

        buildHeap();
    }

    public Song getMin() {
        if (currentSize == 0) return null;
        return list[1];
    }

    public Song popMin() {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before popMin");
        Song minElement = getMin();
        indexesOfElements.remove(minElement.id);
        if (currentSize != 1) {
            list[1] = list[currentSize];
            indexesOfElements.put(list[1].id, 1);
        }
        currentSize--;
        percolateDown(1);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after popMin");
        return minElement;
    }

    /**
     * removes the first instance of the input, works in O(n)
     * @param element the element to be removed
     */
    public void remove(Song element) {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before remove");

        if (isEmpty()) return;
        if (element.equals(getMin())) {
            popMin();
            return;
        }
        Integer index = indexesOfElements.get(element.id);
        if (index == null) return;
        indexesOfElements.remove(element.id);
        list[index] = list[currentSize--];
        indexesOfElements.put(list[index].id, index);
        if (list[index].compareTo(parent(index), compareMode) < 0)
            percolateUp(list[index], index);
        else {
            percolateDown(index);
        }

//        if (isHeapPropertyViolated(1))
//            System.out.println("after remove in minHeap");
    }

    private void setCapacity(int newSize) {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before setCap");
        Song[] newList = new Song[newSize];
        for (int i=1; i<list.length; i++) {
            newList[i] = list[i];
        }
        list = newList;
//        if (isHeapPropertyViolated(1))
//            System.out.println("after setCap");
    }

    public boolean isEmpty() {
        return currentSize == 0;
    }

    /**
     * makes a heap with sorting a random list into a heap-sorted list
     */
    public void buildHeap() {
//        if (isHeapPropertyViolated(1))
//            System.out.println("before buildHeap");
        for (int i=currentSize/2; i>0; i--)
            percolateDown(i);
//        if (isHeapPropertyViolated(1))
//            System.out.println("after buildHeap");
    }

    private boolean isHeapPropertyViolated(int i) {
        if (i >= currentSize / 2) {
            return false;  // Base case: i is a leaf node
        }

        int leftChild = 2 * i;
        int rightChild = 2 * i + 1;
        boolean violated = false;

        // Check if the heap property is violated for the current node
        if (list[i].compareTo(list[leftChild], compareMode) > 0 ||
                (rightChild <= currentSize && list[i].compareTo(list[rightChild], compareMode) > 0)) {
            violated = true;
        }

        // Recursively check for violations in the left and right subtrees
        if (isHeapPropertyViolated(leftChild) || isHeapPropertyViolated(rightChild)) {
            violated = true;
        }

        return violated;
    }
}
