package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/*
    Name: Rafik Hachana
    Group: BS19-04
    E-mail: r.hachana@innopolis.university
*/

/*
    CF submission:
    http://codeforces.com/group/3ZU2JJw8vQ/contest/272963/submission/74504989
 */

public class Main {
    static class FastReader {  //Fast reader class to read input faster
        BufferedReader br; StringTokenizer st;
        private FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        String next() {
            while (st == null || !st.hasMoreElements())
                try { st = new StringTokenizer(br.readLine()); } catch (IOException e) { e.printStackTrace();}
            return st.nextToken();
        }
        int nextInt() { return Integer.parseInt(next()); }
    }



    public static void main(String[] args) {
        FastReader in  = new FastReader();
        int n = in.nextInt();
        Line[] t = new Line[n];
        for(int i=0;i<n;i++)
        {
            int a = in.nextInt();
            int b = in.nextInt();
            int c = in.nextInt();
            int d = in.nextInt();
            t[i] = new Line(a,b,c,d);
        }
        LineIntersect.sweepLine(t);
        //LineIntersect.naive(t);
    }


}

class Point  //holds a 2 dimensional point
{
    int x,y;
    Point(int a,int b) { x = a; y = b; }
    public boolean equals(Point a)
    {
        return this.x==a.x && this.y == a.y;
    }
}
class Line implements Comparable<Line>  //holds a line segment
{
    Point p1,p2;
    boolean vertical;
    private boolean rev;
    Line(int a,int b,int c,int d)
    {
        vertical = (a==c);
        if(a>c || (a==c && b>d)) //we need to have p1 as the point with a smaller x (or smaller y in case of tie) for sorting purposes.
        {
            p1 = new Point(c,d); p2 = new Point(a,b);
            rev = true; return;
        }
        p1 = new Point(a,b); p2 = new Point(c,d);
        rev = false;
    }
    public String toString()
    {
        if(rev) return p2.x + " " + p2.y + " " + p1.x  + " " + p1.y ;
        return p1.x + " " + p1.y + " " + p2.x  + " " + p2.y ;
    }
    private double m(){ return (double)(p2.y - p1.y)/(p2.x-p1.x);}  //slope
    private double b(){ return p1.y - m()*p1.x; }  //y-intercept
    private double getY(double x){ return m()*x + b();}  //y-value for a specific x
    @Override
    public int compareTo(Line a)  //compares using the y-coordinate of the intersection at the sweep line.
    {
        int x = LineIntersect.current_x;
        double diff = this.getY(x) - a.getY(x);
        return diff>0?1:(diff<0?-1:0);
    }
    public boolean equals(Line a)
    {
        return (this.p1.equals(a.p1)) && (this.p2.equals(a.p2));
    }
}

class LineIntersect
{
    /*
        IMPORTANCE OF THE 2 LINE SEGMENTS INTERSECTION PROBLEM:

        Finding the intersection of 2 line segments is one of the fundamental problems of computational geometry, hence its importance.
        When of its applications is  finding the intersections of more complex shapes that are made from line segments (like rectangles).
        It can be applied to solve geometrical problems where the solution is the intersection of two lines (an example is finding the circle
        defined by 3 non-aligned points X,Y and Z: the center of the circle is the intersection of the perpendicular bisectors of XY and YZ).
        And also, the problem of finding the intersection of 2 line segments is important because of the number of real world problems that can
        be reduced to the intersection of 2 line segments problem.

     */
    //the method used to check for intersection is the one in the CLRS textbook
    private static boolean intersect(Line a,Line b) //returns true if a and b intersect
    {
        Point p1 = a.p1; Point p2 = a.p2; Point p3 = b.p1; Point p4 = b.p2;
        long d1 = dir(p1,p2,p3);
        long d2 = dir(p1,p2,p4);
        long d3 = dir(p3,p4,p1);
        long d4 = dir(p3,p4,p2);
        if(diffSign(d1,d2) && diffSign(d3,d4)) return true;
        if(d1==0 && inRange(p1,p2,p3)) return true;  //intersection at endpoints
        if(d2==0 && inRange(p1,p2,p4)) return true;
        if(d3==0 && inRange(p3,p4,p1)) return true;
        if(d4==0 && inRange(p3,p4,p2)) return true;
        return false;
    }

    private static long dir(Point a,Point b,Point c)  //computes the determinant of ab and ac
    {
        return ((long)b.x - a.x)*(c.y-a.y) - ((long)c.x-a.x)*(b.y-a.y);
    }

    private static boolean inRange(Point a,Point b,Point m)  //returns true if m is in the rectangle defined by a and b
    {
        return Math.min(a.x,b.x)<=m.x && Math.max(a.x,b.x)>=m.x && Math.min(a.y,b.y)<=m.y && Math.max(a.y,b.y)>=m.y;
    }

    private static boolean diffSign(long a,long b)  //returns true if a and b are of different signs
    {
        return (a>0 && b<0) || (a<0 && b>0);
    }

    static void naive(Line[] t) //the naive algorithm given in the task statement
    {
        int n = t.length;
        for(int i=0;i<n;i++)  //just check every pair for intersection
        {
            for(int j=i+1;j<n;j++)
            {
                if(intersect(t[i],t[j]))
                {
                    printIntersection(t[i],t[j]); return;
                }
            }
        }
        System.out.println("NO INTERSECTIONS");
    }

    /*



        SUMMARY OF THE SWEEP LINE ALGORITHM:

        We can visualize the algorithm as a vertical line sweeping through the xy-plane in the positive direction.
        At each x-coordinate, we can compare line segments based on the y-coordinate of their intersection with the sweep line at the current x.
        Assuming no three segments intersect at the same point, each pair of intersecting segments intersect the sweep line in points that are directly next to each other in the sweep line close (when the sweep line is right before the segment intersection).
        This helps, because now we only check the intersection for segments that are next to each other (using the intersection with the sweep line).
        Furthermore we only need to put the sweep at the interesting x-coordinates: those that have endpoints of line segments. The result is the following sweep line algorithm:

        We consider segment endpoints as events that move the sweep line. We sort them based on their x-coordinate, and then we traverse them one by one.
        During the traversal, we keep a binary search tree that is initially empty.
        And at each x-coordinate, it will contain the segments intersecting the sweep line ordered by the y-coordinate of the respective intersection.
        At each endpoint, we insert the segment if it is a left endpoint, and we check its intersection with its new neighbors in the BST (predecessor and successor).
        If it is a right endpoint, we delete the segment and check the intersection of its pred and succ in the BST (that are now neighbors).




        FOR THIS SPECIFIC IMPLEMENTATION:

        Since we only need to find at most one PAIR of intersecting segments, the case of three segments intersecting at the same point is supported.
        But if that intersection is found, only 2 of the segments will be printed.

        Vertical segments are supported as a corner case.
        The comparison for normal segments uses the slope of the line which we can't use with vertical lines.
        Therefore vertical segments are considered as one event. And they can either:
            - intersect with non-vertical lines: we create 2 dummy horizontal lines that go through the endpoints of the vertical segments and check their pred and succ to look for intersections with the vertical segment.
            - intersect with other vertical segments: this happens only with consecutive vertical segments in the list of events.
                so we keep the last seen vertical segment and check for intersection with it when we find the next vertical segment.
     */
    static int current_x;

    public static void sweepLine(Line[] tmp)  //implementation of sweep line
    {
        ArrayList<Event> t = new ArrayList<>();
        for(Line l:tmp)  //create the list of events
        {
            if(l.vertical)  //vertical lines are only one event
            {
                t.add(new Event(l,false));
                continue;
            }
            t.add(new Event(l,false));
            t.add(new Event(l,true));
        }
        t = (new Sort<Event>()).mergeSort(t);  //sort the event
        AVLTree<Line> s = new AVLTree<>();  //bst data structure to maintain segments
        Line previousVertical = null;  //the last seen vertical segment
        for(Event i:t)
        {
            Line curr = i.l;
            current_x = i.right?curr.p2.x:curr.p1.x;   //the current x-coordinate of the sweep line
            if(curr.vertical)
            {
                if(previousVertical!=null && intersect(curr, previousVertical))
                {
                    printIntersection(curr,previousVertical); return;
                }
                previousVertical = curr;

                //next we use dummy line segments to find the segments in the BST that may cross the vertical segment.

                Line bottom = new Line(current_x-1,curr.p2.y,current_x+1,curr.p2.y);  //horizontal segment passing through the bottom endpoint
                Line aboveBottom = s.successor(bottom);
                if(aboveBottom!=null && intersect(curr,aboveBottom))
                {
                    printIntersection(curr,aboveBottom); return;
                }
                Line top = new Line(current_x-1,curr.p2.y,current_x+1,curr.p2.y); //horizontal segment passing through the top endpoint
                Line belowTop = s.predecessor(top);
                if(belowTop!=null && intersect(curr, belowTop))
                {
                    printIntersection(curr,belowTop); return;
                }
                continue;
            }
            if(!i.right)  //if it is a left endpoint
            {
                s.insert(curr);

                Line above = s.successor(curr);
                if(above!=null && intersect(curr,above))
                {
                    printIntersection(curr,above); return;
                }

                Line below = s.predecessor(curr);
                if(below!=null && intersect(curr,below))
                {
                    printIntersection(curr,below); return;
                }
            }
            else  //if it is a right endpoint
            {
                Line above = s.successor(curr);  Line below = s.predecessor(curr);
                if(above!=null && below!=null && intersect(below,above) && !above.equals(below))
                {
                    printIntersection(below,above); return;
                }

                s.delete(curr);
            }
        }
        System.out.println("NO INTERSECTIONS");
    }

    private static class Event implements Comparable<Event> {  //object for the sorted list of points in the sweep line algorithm
        boolean right;
        Point p;
        Line l;
        Event(Line a,boolean b)
        {
            right= b;
            l = a;
            p = b?l.p2:l.p1;
        }
        @Override
        public int compareTo(Event a) {  //the comparison used for events: small x first, if there is a tie we take the left endpoints first, if tie again take the smaller y
            if(this.p.x<a.p.x) return -1;
            if(this.p.x>a.p.x) return 1;
            if(this.right && !a.right) return 1;
            if(!this.right && a.right) return -1;
            if(this.p.y<a.p.y) return -1;
            if(this.p.y>a.p.y) return 1;
            return 0;
        }

    }
    private static void printIntersection(Line a,Line b)  //prints an intersection when it is found
    {
        System.out.println("INTERSECTION");
        System.out.println(a); System.out.println(b);
    }
}

class Sort<T extends Comparable>
{
    /*
        This implementation of merge sort:
        - runs in O(N.logN)
        - is out-of-place (since it copies the elements to new arrays)
        - is stable (when merging, if 2 elements are equal the one on the left is taken first,
        so it preserves the original order of equal elements).
     */

    public ArrayList<T> mergeSort(ArrayList<T> t)
    {
        int s = t.size();
        if(s<=1) return t;  //base case of empty array or array with 1 element
        int med = s/2;
        ArrayList<T> right = new ArrayList<>();
        ArrayList<T> left = new ArrayList<>();
        for(int i=0;i<s;i++)                //splits the array in the middle
        {
            if(i<med) left.add(t.get(i));
            else right.add(t.get(i));
        }
        right = mergeSort(right);  //recursively sorts the 2 new arrays
        left = mergeSort(left);
        return merge(left,right);  //merge them and return the result
    }

    private ArrayList<T> merge(ArrayList<T> left,ArrayList<T> right)
    {
        int i = 0, j = 0;
        ArrayList<T> ret = new ArrayList<>();
        while(i<left.size() && j<right.size())  //takes the least element of at indices i and j in the left and right respectively until one of them is empty
        {
            if(left.get(i).compareTo(right.get(j))<=0)
            {
                ret.add(left.get(i)); i++;
            }
            else
            {
                ret.add(right.get(j)); j++;
            }
        }
        while(i<left.size())  //adds what is left of left
        {
            ret.add(left.get(i)); i++;
        }
        while(j<right.size())  //adds what is left of right
        {
            ret.add(right.get(j)); j++;
        }
        return ret;
    }
}

class AVLTree<T extends Comparable>
{

    /*
        NOTES ABOUT THIS AVL TREE
        - it supports duplicates, for the sake of use in the sweep line algorithm with duplicate line segments,
        however, the deletion of of all occurrences of a value at once is not supported because it is not needed in the sweep line algorithm

        - all operations that use recursion have 2 methods, one private that has the recursion and takes tree nodes in the parameters,
        and one public with no nodes in the parameters that calls the private method.
        This was done to keep the interface clean.
     */

    private class Node  //object that implements a tree node
    {
        T key;  //the value of the node
        int occ;
        Node right,left;   //ref to right and left children
        private int height;  //the height of the subtree rooted at this node
        Node(T v)
        {
            key = v;
            right = left = null;
            height = 1;
            occ = 1;
        }
        int balance() { return getHeight(right)-getHeight(left); }  //difference in height between the left and right child

        void updateHeight(){ height = 1 + Math.max(getHeight(left),getHeight(right)); } //updates the height of the subtree (used after modifications to the tree)

    }
    private int getHeight(Node a)  //returns the height of a tree (or 0 if it's null)
    {
        return a==null?0:a.height;
    }
    private Node root;  //reference to the root of the tree
    private int size;  //holds the current size of the tree


    public AVLTree()  //tree constructor: constructs an empty tree
    {
        root = null;
        size = 0;
    }

    public int size(){return size;}  //returns the current size of the tree

    /*
        INSERTION in O(logN) since it traverses the tree from root to leaves
        therefore the time complexity = height of tree = logN (because the tree is balanced)

        inserts a new node with value "val" in the tree and keeps the BST invariant and balances the tree if needed.
        this insert method allows duplicate insertion.
     */
    public void insert(T val)
    {
        root = insert(root,val);  //calls the private recursive method insert
        size++;  //new node inserted = size increases
    }
    private Node insert(Node curr,T val)
    {
        if(curr==null) return new Node(val);  //insert the node
        if(curr.key.compareTo(val)<0)  curr.right = insert(curr.right,val);  //recursion using the BST invariant
        else curr.left =  insert(curr.left,val);

        curr.updateHeight();
        int b = curr.balance();

        if(b>1) //right heavy
        {
            if(curr.right.key.compareTo(val)>=0)  curr.right = rightRotate(curr.right); //right left case
            return leftRotate(curr);
        }
        if(b<-1)  //left heavy
        {
            if(curr.left.key.compareTo(val)<0)  curr.left = leftRotate(curr.left); //left right case
            return rightRotate(curr);
        }
        return curr;
    }

    /*
        DELETION in O(logN) since it traverses the tree from root to leaves
        therefore the time complexity = height of tree = logN (because the tree is balanced)

        Deletes ONLY one occurrence of val in the tree while keeping the BST invariant and balancing the tree
        Returns true if the element exists in the tree and one occurrence of it was deleted. returns false otherwise.
     */

    public boolean delete(T val)
    {
        if(!find(val)) return false;  //stops because val is not in the tree
        root = delete(root,val); //calls the private recursive method
        size--;  //decrease size
        return true;  //deletion done
    }
    private Node delete(Node curr, T val)
    {
        //if(curr==null) return null;
        if(curr.key.equals(val))
        {
            if(curr.left==null) curr =  curr.right; //covers leaf case and 1 right child case
            else if(curr.right==null) curr =  curr.left;  //1 left child case
            else  //2 children case
            {
                T min = minValue(curr.right);  //copy the successor in the right subtree to the node
                curr.key = min;
                curr.right = delete(curr.right,min); //delete the successor instead (which is a leaf)
            }
        }
        else if(curr.key.compareTo(val)<0) curr.right = delete(curr.right,val);  //recursion with the BST invariant
        else curr.left= delete(curr.left,val);

        if(curr==null) return null;  //if no subtree

        curr.updateHeight();  //update height
        int b = curr.balance();  //calculate balance


        //balance the tree depending on the case
        if(b>1)  //right heavy
        {
            if(curr.right.balance()<0) curr.right = rightRotate(curr.right);  //right left case
            return leftRotate(curr);
        }
        if(b<-1)  //left heavy
        {
            if(curr.left.balance()>0) curr.left = leftRotate(curr.left);  //left right case
            return rightRotate(curr);
        }
        return curr;  //reach hear if the subtree is still balanced
    }

    /*
        FIND OR SEARCH FUNCTION in O(logN) since it traverses the tree from root to leaves
        therefore the time complexity = height of tree = logN (because the tree is balanced)

        Returns true if the value "val" is already in the tree (at least one occurrence), and false otherwise.
     */
    public boolean find(T val)
    {
        return find(root,val);  //calls the private recursive method
    }
    private boolean find(Node curr,T val)
    {
        if(curr==null) return false;  //reached a leaf and the value wasn't found
        if(curr.key.equals(val)) return true;  //val found
        if(curr.key.compareTo(val)<0) return find(curr.right,val);  //recursion using the BST invariant
        return find(curr.left,val);
    }

    /*
        SUCCESSOR function runs in O(logN) since it traverses the tree from root to leaves
        therefore the time complexity = height of tree = logN (because the tree is balanced)

        returns the value of the inorder successor of val in the tree (even if val doesn't exist in the tree).
        returns null if there is no successor
     */

    public T successor(T val)
    {
        return successor(root,val); //calls the private recursive method
    }
    private T successor(Node curr,T val)
    {
        if(curr==null) return null; //if no succ exists
        if(curr.key.equals(val)) return minValue(curr.right);  //if val is in the tree returns the min in its left subtree
        if(curr.key.compareTo(val)<0) return successor(curr.right,val);  //recursion using the BST invariant
        T s = successor(curr.left,val);
        if(s==null) return curr.key;
        return curr.key.compareTo(s)<0?curr.key:s; //the key in the current node is a potential successor
    }

    /*
        PREDESSOR function runs in O(logN) since it traverses the tree from root to leaves
        therefore the time complexity = height of tree = logN (because the tree is balanced)

        returns the value of the inorder predecessor of val in the tree (even if val doesn't exist in the tree).
        returns null if there is no predecessor
     */

    public T predecessor(T val)
    {
        return predecessor(root,val); //calls the private recursive method
    }

    private T predecessor(Node curr,T val)
    {
        if(curr==null) return null; //if no pred exists
        if(curr.key.equals(val)) return maxValue(curr.left);  //if val exists in the tree take the max in its right subtree
        if(curr.key.compareTo(val)>0) return predecessor(curr.left,val);  //recursion following the BST invariant
        T p = predecessor(curr.right,val);
        if(p==null) return curr.key;
        return curr.key.compareTo(p)>0?curr.key:p;  //the key of the current node is a potential pred
    }

    /*
        INORDER TRAVERSAL function: runs in O(N) since it visits every node of the N nodes in the tree.
        it prints all the keys stored in the tree sorted from least to greatest
     */
    public void inOrder()
    {
        inOrder(root);  //calls the private recursive method
    }
    private void inOrder(Node curr)
    {
        if(curr==null) return; //base case: child of a leaf is null
        inOrder(curr.left);  //prints the smaller key first
        System.out.println(curr.key); //prints the current key
        inOrder(curr.right);  //prints the larger keys after
    }


    //private methods used internally

    /*
        The maxValue/minValue functions returns the greatest/least key in the subtree rooted at the node curr

        they run in O(logN) since they traverse the height of the subtree

        maxValue keeps going to the right child
        minValue keeps going to the left child
     */
    private T maxValue(Node curr)
    {
        if(curr==null) return null;
        while(curr.right!=null) curr = curr.right;
        return curr.key;
    }
    private T minValue(Node curr)
    {
        if(curr==null) return null;
        while(curr.left!=null) curr = curr.left;
        return curr.key;
    }

    /*
        AVL ROTATION functions
        these functions perform rotations on subtrees to keep the tree balanced.
        Both of them run in O(1) since they only change 2 object references each time
     */
    private Node leftRotate(Node z)
    {
        Node y = z.right;
        Node subTree = y.left;
        y.left = z;  //perform rotation
        z.right = subTree;
        z.updateHeight();  y.updateHeight();  //update height IN THIS ORDER
        return y;  //return the new root of the subtree
    }
    private Node rightRotate(Node z)
    {
        Node y = z.left;
        Node subTree = y.right;
        y.right = z;  //perform rotation
        z.left = subTree;
        z.updateHeight();  y.updateHeight();  //update height IN THIS ORDER
        return y; //return the new root of the subtree
    }
}
