package com.company;

import java.util.*;


/* Coding Assignment 1
    Rafik Hachana
    Group 4
 */

// functions for task 2,3 are in Main
/*
    Below are the implementations of data structures:
        - Dynamic Array using static arrays
        - Stack using the dynamic array (for task 2)
        - List ADT interface (task)
        - Dynamic Array List using the dynamic array (task 1)
        - Doubly Linked List using nodes of 1 value and 2 pointers (task 1)
        - Phonebook data structure using HashMap and ArrayList (task 3)
 */

/*

    CF submission link for task 2:
    http://codeforces.com/group/3ZU2JJw8vQ/contest/269072/submission/71294944

    CF submission link for task 3:
    http://codeforces.com/group/3ZU2JJw8vQ/contest/269072/submission/71295520

 */

public class Main {

    public static void main(String[] args) {

        //Put in comments the functions you don't want to execute

        //task 1 (driver code to test the implementation of the list ADT)
        test_List();
        //task 2 (Codeforces submission 71268382)
        //balanced_parentheses();
        //task 3 (Codeforces submission 71271663)
        //phonebook();
    }

    static char associated(char c)  //returns the corresponding opening/closing parenthesis in O(1)
    {
        if(c=='{') return '}';
        if(c=='(') return ')';
        if(c=='[') return ']';
        if(c==')') return '(';
        if(c==']') return '[';
        return '{';
    }

    static boolean opening_parentheses(char c)  //returns true if c is an opening parenthesis in O(1)
    {
        return c=='{' || c=='[' || c=='(';
    }

    static boolean closing_parentheses(char c)  //returns true if c is an closing parenthesis in O(1)
    {
        return c=='}' || c==']' || c==')';
    }

    public static void balanced_parentheses()  //function for task 2 ( in O(n*L) (n the number of lines and L the length of the longest line))
    {
        Scanner scr = new Scanner(System.in);
        myStack s = new myStack();
        int l = 0;
        int n = scr.nextInt();
        scr.nextLine();
        int i = 0;
        while(n>0)  //read line by line
        {
            String t = scr.nextLine();
            l++; n--;
            for(i=0;i<t.length();i++)
            {
                if(opening_parentheses(t.charAt(i))) s.push(t.charAt(i));  //push opening p. to stack
                else if(closing_parentheses(t.charAt(i))) //the closing p. should match the top of the stack
                {
                    if(s.isEmpty()) //no parenthesis has been opened
                    {
                        System.out.println("Error in line "+l+", column "+(i+1)+": unexpected closing '"+t.charAt(i)+"'.");
                        return;
                    }
                    if(s.top().compareTo(associated(t.charAt(i)))==0) s.pop(); //the parenthesis match
                    else                                                    //the parenthesis don't match
                    {
                        System.out.println("Error in line "+l+", column "+(i+1)+": expected '"+associated(s.top())+"', but got '"+t.charAt(i)+"'.");
                        return;
                    }
                }
            }
        }
        if(!s.isEmpty()) //some parenthesis was not closed
        {
            System.out.println("Error in line "+l+", column "+(i)+": expected '"+associated(s.top())+"', but got end of input.");
            return;
        }
        System.out.println("Input is properly balanced.");
    }



    static void phonebook()  //function for task 3
            //overall worst case complexity is O(N²) (consider using only one name for all phone numbers and inserting N/2 numbers then printing N/2 times)
    {
        Scanner scr = new Scanner(System.in);
        int n = scr.nextInt();
        scr.nextLine();
        Phonebook p = new Phonebook();
        for(int i=0;i<n;i++)    //read all n queries
        {
            String s = scr.nextLine();

            //then parse the input string and execute the adequate query
            String[] t = s.split(" ",2);
            if(t[0].compareTo("ADD")==0)
            {
                t = t[1].split(",");
                p.add(t[0],t[1]);
            }
            else if(t[0].compareTo("FIND")==0)
            {
                ArrayList<String> m = p.find(t[1]);
                if(m.isEmpty())
                {
                    System.out.println("No contact info found for "+t[1]);
                }
                else
                {
                    m.remove(null);
                    System.out.print("Found "+m.size()+" phone numbers for "+t[1]+":");
                    for(String k:m) System.out.print(" "+k); //printing is in linear time
                    System.out.println();
                }
            }
            else if(t[0].compareTo("DELETE")==0)
            {
                t = t[1].split(",");
                if(t.length==1)
                {
                    p.deleteContact(t[0]);
                }
                else
                {
                    p.deleteNumber(t[0],t[1]);
                }
            }
        }
    }

    static void test_List() //to test the list implementations
    {
        //Put in comment the implementation that you don't want to use

        DynamicArrayList<Integer> l = new DynamicArrayList<>();
        //DoublyLinkedList<Integer> l = new DoublyLinkedList<>();
        try
        {
            l.addFirst(5);
            l.addFirst(3);
            l.addLast(10);
            l.addLast(70);
            l.addLast(98);
            l.addLast(78);
            l.add(2,654);
            l.set(2,70);
            l.delete(2);
            l.deleteFirst();
            l.deleteLast();
            l.delete((Integer)70);
            System.out.println(l.size);
            for(int i=0;i<l.size;i++) System.out.print(l.get(i) + " ");
        }
        catch(IndexOutOfBoundsException e)
        {
            System.out.print(e);
        }
    }

}

//Dynamic Array implementation

class DynamicArray<T extends Comparable>
{
    Object[] t;
    int size;  //size of the dynamic array
    private int actual_size;  //size of the internal static array
    public DynamicArray(int s)  //initialization with initial size
    {
        t= new Object[s];
        size=actual_size= s;
    }
    public DynamicArray()  //initialization without initial size
    {
        t= new Object[8];  //an arbitrary initial size to avoid expensive array doubling for small arrays
        size= 0;
        actual_size = 8;
    }
    void arrayDoubling() //creates a new internal array with double the size and copies the values to it in O(size) (Linear)
    {
        Object[] tmp = new Object[actual_size*2];
        actual_size*=2;
        for(int i=0;i<size;i++) tmp[i] = t[i];
        t = tmp;
    }
    void add(T e)  //adds element to the end in O(1) amortized complexity (can be in O(size) in case of array doubling)
    {
        if(size==actual_size) arrayDoubling();  //doubles the array if it is no longer big enough
        t[size++] = e;
    }
    T get(int i) throws ArrayIndexOutOfBoundsException //returns value at index i in O(1)
    {
        if(i>=size) throw new ArrayIndexOutOfBoundsException("Index out of bound.");
        return (T)t[i];
    }
    void removeLast() throws ArrayIndexOutOfBoundsException  //removes the last element in the array in O(1)
    {
        if(size==0) throw new ArrayIndexOutOfBoundsException("Removing from an empty array.");
        t[size-1] = null;  //set the reference to null so that the garbage collector will delete the object
        size--;
    }

    void set(int i,T e) throws ArrayIndexOutOfBoundsException  //sets element at index i to e in O(1)
    {
        if(i>=size) throw new ArrayIndexOutOfBoundsException("Index out of bound.");
        t[i] = e;
    }
}




// PART 1: Implementing list ADT
interface ListADT<T>   //interface to be implemented in different ways
{
    boolean isEmpty();
    void add(int i,T e);
    void addFirst(T e);
    void addLast(T e);
    void delete(T e);
    void delete(int i);
    void deleteFirst();
    void deleteLast();
    T get(int i);
    void set(int i,T e);
}

//Dynamic Array List
//the generic type should extend Comparable in order to use the function compareTo (needed in the delete by value function)

class DynamicArrayList<T extends Comparable> implements ListADT<T>
{
    DynamicArray<T> t;  //internal dynamic array
    int size = 0; //size of the list
    public DynamicArrayList()  //constructor
    {
        t = new DynamicArray<T>();
        size = 0;
    }

    @Override
    public boolean isEmpty() {  //returns true if the list is empty in O(1)
        return size==0;
    }

    @Override
    public void addLast(T e) {   //add e to the back of the list in O(1)
        t.add(e);
        size++;
    }

    @Override
    public void add(int i, T e) {   //inserts e in position i in O(size) (linear)
        t.add(null);
        for(int j=size-1;j>=i;j--) t.set(j+1,t.get(j));  //needs to offset the elements to the right
        t.set(i,e);
        size++;
    }

    @Override
    public void addFirst(T e) {  //adds elements to the front of the list in O(size) (linear)
        add(0,e);
    }

    @Override
    public void delete(int i) throws IndexOutOfBoundsException {  //deletes the element at pos i in O(size) (linear)
        if(i>=size) throw new IndexOutOfBoundsException("Index too large.");
        for(int j=i;j<size-1;j++) t.set(j,t.get(j+1)); //needs to offset the elements back to the left
        size--;
        t.removeLast();
    }

    @Override
    public void deleteFirst() throws IndexOutOfBoundsException {  //deletes first element in in O(size) (linear)
        if(isEmpty()) throw new IndexOutOfBoundsException("Deleting from an empty list.");
        delete(0);
    }

    @Override
    public void deleteLast() throws IndexOutOfBoundsException {  //deletes the last elements in in O(1) (constant)
        if(isEmpty()) throw new IndexOutOfBoundsException("Deleting from an empty list");
        t.removeLast();
        size--;
    }

    @Override
    public void set(int i, T e) throws IndexOutOfBoundsException {  //sets the elements at pos i to e in O(1) (constant)
        if(i>=size) throw new IndexOutOfBoundsException("Index too large.");
        t.set(i,e);
    }

    @Override
    public void delete(T e) {  //deletes all occurrences of e in the list in O(size) (linear)
        DynamicArray<Integer> tmp = new DynamicArray<>(); //tmp will contain the indices of all the elements to be deleted
        for(int i=0;i<size;i++) //we check all the elements of the list
        {
            if(t.get(i).compareTo(e)==0)  //we need compareTo function here (that's why T extends Comparable)
            {
                tmp.add(i);
            }
        }
        for(int i=0;i<tmp.size;i++)  //iterate over all elements to be deleted
        {
            this.delete((int)tmp.get(i)-i);  //each time we delete an element we offset the following elements
            // so we subtract i from the position to be deleted
            // since i is the number of elements that are already deleted
        }
    }

    @Override
    public T get(int i) throws IndexOutOfBoundsException {  //returns the values of the elements at pos i in O(1)
        if(i>=size) throw new IndexOutOfBoundsException("Index too large.");
        return t.get(i);
    }
}

// Doubly Linked List (no sentinel)
//nodes for the list
class node<T extends Comparable>
{
    T value; //value stored in the node
    node<T> prev, next;  //forward and backward pointers
    node(T value)
    {
        this.value = value;
        prev= next  = null;
    }
}

class DoublyLinkedList<T extends Comparable> implements ListADT<T>
{
    int size;  //maintains the size
    node<T> head,tail;  //point to the head and tail nodes of the list respectively

    DoublyLinkedList()  //initialize with size 0 and null pointers (no nodes)
    {
        size = 0;
        head = tail = null;
    }

    @Override
    public boolean isEmpty() {  //returns true if the list is empty in O(1)
        return size == 0;
    }

    @Override
    public void addFirst(T e) {  //adds element to the front of the list in O(1)
        if(size==0)  //specific case for the first insertion
        {
            head = new node<T>(e);
            tail  = head;
            head.next = tail;
            tail.prev = head;
            size++;
            return;
        }
        node<T> tmp = new node<T>(e);  //create new node
        head.prev = tmp;   //redirect pointers
        tmp.next = head;
        head = tmp;  //the node is the new head
        size++;
    }

    @Override
    public void addLast(T e) {  //adds element to the back of the list in O(1)
        if(size==0)  //specific case for the first insertion
        {
            addFirst(e);
            return;
        }
        node<T> tmp = new node<T>(e);  //create new node
        tail.next = tmp;  //redirect pointers
        tmp.prev = tail;
        tail = tmp;  //the node is the new tail
        size++;
    }

    @Override
    public void add(int i, T e) {  //inserts value at pos i in O(size) (linear)
        if(i==size)   //insertion at the back  (constant time for this case)
        {
            addLast(e);
            return;
        }
        if(i==0)  //insertion at the front  (constant time for this case)
        {
            addFirst(e);
            return;
        }
        node<T> target = head;
        for(int j=0;j<i;j++) target = target.next;  //follow pointer until we arrive at the desired position
        node<T> p = target.prev;
        node<T> newnode = new node<T>(e); //new node
        p.next = newnode;  //redirect pointers to insert the new node in between
        newnode.prev = p;
        target.prev = newnode;
        newnode.next = target;
        size++;
    }

    @Override
    public void deleteFirst() throws IndexOutOfBoundsException {  //delete the first element in O(1)
        if(isEmpty()) throw new IndexOutOfBoundsException("Deleting from an empty list.");
        head = head.next;  //the head is now the 2nd elem
        head.prev = null;  //remove pointers to the previous head
        size--;
    }

    @Override
    public void deleteLast() throws IndexOutOfBoundsException {  //deletes the last elem in O(1)
        if(isEmpty()) throw new IndexOutOfBoundsException("Deleting from an empty list.");
        tail = tail.prev; //the tail is now the elem before the last
        tail.next = null;  //remove pointers to the previous tail
        size--;
    }

    @Override
    public T get(int i) throws IndexOutOfBoundsException {   //returns the value at pos i in O(size) (linear)
        if(i>=size) throw new IndexOutOfBoundsException("Index too large.");
        node<T> tmp = head;
        for(int j=0;j<i;j++) tmp = tmp.next;  //follow pointers to the desired position
        return tmp.value;  //return the value of the node
    }

    @Override
    public void set(int i, T e) throws IndexOutOfBoundsException {  //sets the value at pos i to e in O(size) (linear)
        if(i>=size) throw new IndexOutOfBoundsException("Index too large.");
        node<T> tmp = head;
        for(int j=0;j<i;j++) tmp = tmp.next;  //follow pointers to the desired position
        tmp.value = e;  //set the value of the node
    }

    @Override
    public void delete(int i) throws IndexOutOfBoundsException {  //deletes the elem at pos i in O(size) (linear)
        if(i>=size) throw new IndexOutOfBoundsException("Index too large.");
        if(i==0)  //deletion at the head  (constant time for this case)
        {
            deleteFirst();
            return;
        }
        if(i==size-1)  //deletion at the back  (constant time for this case)
        {
            deleteLast();
            return;
        }
        node<T> tmp = head;
        for(int j=0;j<i;j++) tmp = tmp.next;  //follow pointers to the desired pos
        node<T> p = tmp.prev;
        node<T> n = tmp.next;
        p.next = n;
        n.prev = p;  //redirect pointers of the neighboring nodes to each other
        size--;
        //since no node points at the node to be deleted, the garbage collector will delete it
    }

    @Override
    public void delete(T e) {  //deletes all occurrences of e in the list in O(size) (linear time)
        node<T> tmp = head;
        while(tmp!=null)  //iterates over all nodes
        {
            if(tmp.value.compareTo(e)==0)  //if the value is to be deleted
            {
                //specific cases
                if(tmp.next==null)
                {
                    deleteLast();
                }
                else if(tmp.prev==null)
                {
                    deleteFirst();
                }
                else
                {
                    node<T> p = tmp.prev;
                    node<T> n = tmp.next;
                    p.next = n;  //redirect pointers
                    n.prev = p;
                    size--;
                }
            }
            tmp = tmp.next;  //move to the next node
        }
    }
}

// PART 2: Balanced parenthesis
// Stack implementation

//no generics since we only need a stack of characters for task 2

class myStack
{
    DynamicArray<Character> m;  //internal array
    int size;  //size of the stack
    public myStack()
    {
        m = new DynamicArray<Character>();
        size = 0;
    }
    boolean isEmpty()
    {
        return size == 0;
    }
    Character top() throws EmptyStackException  //returns the top of the stack in O(1)
    {
        if(isEmpty()) throw new EmptyStackException();
        return m.get(size-1);
    }
    void push(Character c)  //pushes c to the stack in O(1)
    {
        m.add(c);
        size++;
    }
    void pop() throws EmptyStackException  //pops the top of the stack in O(1)
    {
        if(isEmpty()) throw new EmptyStackException();
        m.removeLast();
        size--;
    }
}


// PART 3: Phonebook
//Data structure to store the phonebook

class Phonebook
{
    HashMap<String,ArrayList<String> > m;
    //the HashMap will store the names and map each name to an ArrayList of phone numbers

    //the cost of insertion and lookup in the HashMap is O(1)

    public Phonebook()
    {
        this.m = new HashMap<>();
    }

    void add(String name, String num)  //adds num to the contact with the specified name in O(N) (worst case when there is one contact with N numbers)
    {
        if(m.containsKey(name))  //if the contact name exists
        {
            for(String k:m.get(name)) if(k.compareTo(num)==0) return;  //check if the phone number is already there
            m.get(name).add(num);
        }
        else  //otherwise : create a new contact with the num
        {
            ArrayList<String> tmp = new ArrayList<>();
            tmp.add(num);
            m.put(name,tmp);
        }
    }

    void deleteContact(String name)  //delete the whole contact in O(1)
    {
        m.remove(name);
    }

    void deleteNumber(String name,String num)  //deletes num from the contact with the specified name in O(N)
    {
        if(m.containsKey(name))
        {
            m.get(name).remove(num);  //remove is linear for ArrayList
        }
    }

    ArrayList<String> find(String name)  //returns a ref to the list of numbers of the specified contact in O(1)
    {
        if(m.containsKey(name)) return m.get(name);
        return new ArrayList<>();  //empty list if the contact doesn't exist
    }
}