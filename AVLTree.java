/* 
 * Name: Bauer, Jonah
 * CS340 AVL Tree
 * 
 * */


import java.io.*;
import java.util.*;
public class AVLTree {
/*
Implements a ALV tree of ints (the keys) and fixed length character strings fields
stored in a random access file.
Duplicates keys are not allowed. There will be at least 1 character string field
*/
private RandomAccessFile f;
private long root; //the address of the root node in the file
private long free; //the address in the file of the first node in the free list
 private int numFields; //the number of fixed length character fields
 private int fieldLengths[]; //the length of each field

 private class Node {
 private int key;
 private char fields[][];
 private long left;
 private long right;
 private int height; //negative if taller on left positive if taller on right if (h > 1 || h < -1)
 
 private Node(long l, int d, long r, char fld[][]) {
 //constructor for a new node
	 fields = new char[numFields][];
	 for(int i = 0; i < numFields; i++) {
		 fields[i] = new char[fieldLengths[i]];
	 }
	 fields = fld;
	 right = r;
	 left = l;
	 key = d;
	 height = 0;
 }
 private Node(long addr) throws IOException{
 //constructor for a node that exists and is stored in the file
	 f.seek(addr);
	 key = f.readInt();
	 fields= new char[numFields][];
	 for(int i = 0; i < numFields; i++) {
		fields[i] = new char[fieldLengths[i]]; 
	 }
	 for(int i = 0; i < numFields; i++) {
		 for(int j = 0; j < fieldLengths[i]; j++) {
			 fields[i][j] = f.readChar();
		 }
	 }
	 left = f.readLong();
	 right  = f.readLong();
	 height = f.readInt();
	
 }
 private void writeNode(long addr) throws IOException {
 //writes the node to the file at location addr
	 //System.out.println(addr);
	 f.seek(addr);
	 f.writeInt(key);
	 for(int i = 0; i < numFields; i++) {
		 for(int j = 0; j < fieldLengths[i]; j++) {
			 f.writeChar(fields[i][j]);
		 }
	 }
	 f.writeLong(left);
	 f.writeLong(right);
	 f.writeInt(height);
 }
 }

 public AVLTree(String fname, int fldLengths[]) throws IOException {
 //creates a new empty AVL tree stored in the file fname
 //the number of character string fields is fieldLengths.length
 //fieldLengths contains the length of each field
	 f = new RandomAccessFile(fname, "rw");
	 //f.setLength(0);
	 fieldLengths = fldLengths;
	 numFields = fldLengths.length;
	 free = (20 + (4*numFields));
	 f.writeLong(0); 
	 f.writeLong(free);
	 f.writeInt(fldLengths.length); 
	 for(int i = 0; i < fldLengths.length; i++) {
		 f.writeInt(fldLengths[i]);
	 }
	 f.seek(free);
	 f.writeLong(0);
	 
	 
 }

 public AVLTree(String fname) throws IOException {
 //reuse an existing tree store in the file fname
	 f = new RandomAccessFile(fname, "rw");	 
	 //address of root
	 f.seek(0);
	 root = f.readLong();
	 free = f.readLong();
	 numFields = f.readInt();
	 fieldLengths = new int [numFields];
	 for(int i = 0; i < numFields;i++) {
		 fieldLengths[i] = f.readInt();
	 } 
 }

 public void insert(int k, char fields[][]) throws IOException {
 //PRE: the number and lengths of the fields matches the expected number and lengths
//insert k and the fields into the tree
//if k is in the tree do nothing	
	root = insert(k,fields,root);
 }

private long insert(int k, char[][] fields, long r) throws IOException{
	if(r == ((long)0)) {
		Node n = new Node(0,k,0,fields);
		long f = updateFreeInsert();
		n.writeNode(f);
		return f;
	}
	Node cur = new Node(r);
	int compare =((Integer) k).compareTo(cur.key);
	if(compare<0) {
		 cur.left = insert(k,fields,cur.left);
		 cur.writeNode(r);
		 if((getHeight(cur.left) - getHeight(cur.right)) == 2) {
			 if(((Integer)k).compareTo(new Node(cur.left).key) < 0) {
				 
				 r = singleLeftRotation(r);
			}
			 else {
				 r = doubleLeftRotation(r);
			 }
		 }
		 cur = new Node(r);
	}
	else if(compare>0) {
		cur.right = insert(k,fields,cur.right);
		cur.writeNode(r);
		if(getHeight(cur.right) - getHeight(cur.left) == 2) {
			if(((Integer)k).compareTo(new Node(cur.right).key) > 0) {
				//System.out.println("SRR before: " + r);
				r = singleRightRotation(r);
				//System.out.println("SRR after: " + r);
			 }
			else {
				r = doubleRightRotation(r);		
			 }
		}
		 cur = new Node(r);
	}
	cur.height = Math.max(getHeight(cur.right),getHeight(cur.left)) + 1;
	cur.writeNode(r);
	return r;
}
private long doubleLeftRotation(long s) throws IOException{
	//System.out.println("double left rotation");
	Node sNode = new Node(s);
	sNode.left = singleRightRotation(sNode.left);
	sNode.writeNode(s);
	return singleLeftRotation(s);
}
private long singleLeftRotation(long s) throws IOException{
	//System.out.println("single left rotation");
	Node oldRoot = new Node(s);
	long l = oldRoot.left;
	Node newRoot = new Node(l);
	oldRoot.left = newRoot.right;
	newRoot.right = s;
	oldRoot.height = Math.max(getHeight(oldRoot.left),
				getHeight(oldRoot.right))+1;
	newRoot.height = Math.max(getHeight(newRoot.left),
				getHeight(s) ) + 1;
	newRoot.writeNode(l);
	oldRoot.writeNode(s);
	return l;
}
private long doubleRightRotation(long s) throws IOException {
	//System.out.println("double right rotation");
	Node oldRoot = new Node(s);
	oldRoot.right = singleLeftRotation(oldRoot.right);
	oldRoot.writeNode(s);
	return singleRightRotation(s);
}
private long singleRightRotation(long s) throws IOException {
	//System.out.println("single right rotation");
	Node oldRoot = new Node(s);
	long r = oldRoot.right;
	Node newRoot = new Node(r);
	oldRoot.right = newRoot.left;
	newRoot.left = s;
	oldRoot.height = Math.max(getHeight(((oldRoot.left))),
				(getHeight((oldRoot.right))))+1;
	newRoot.height = Math.max((getHeight(newRoot.right)),
				getHeight(s) ) + 1;
	newRoot.writeNode(r);
	oldRoot.writeNode(s);
	return r;
	
}
public void print() throws IOException {
 //Print the contents of the nodes in the tree is ascending order of the key
	print(root);
 }
private void print(long s) throws IOException{
	if(s == 0) {
		return;
	}
	Node r = new Node(s);
	if(r.left != 0) {
		print(r.left);
	}
	System.out.print(r.key + ": ");
	for(int i = 0; i < numFields; i++) {	
		for(int j = 0; j < fieldLengths[i]; j++) {
			System.out.print(r.fields[i][j]);
		}
		System.out.print(" ");
	}
	System.out.println();
	if(r.right != 0) {
		print(r.right);
	}
}
public LinkedList<String> find(int k) throws IOException {
//if k is in the tree return a linked list of the fields associated with k
//otherwise return null
 //The strings in ths list must NOT include the padding (i.e the null chars)
	LinkedList<String> result = new LinkedList<String>();
	long r = root;
	while(r > 0) {
		Node cur = new Node(r);
		if(cur.key == k) {
			String res = "";
			for(int i = 0; i < numFields;i++) {
				for(int j = 0; j < fieldLengths[i];j++) {
					res += cur.fields[i][j];
				}
				result.add(res);
				res = "";
			}
			return result;
		}
		else if(cur.key < k) {
			if(cur.right > 0) {
				r = cur.right;
			}
			else {return null;}
		}
		else if(cur.key > k){
			if(cur.left > 0) {
				r = cur.left;
			}
			else {return null;}
		}
	}
	return null;
	
 }
public void remove(int k) throws IOException {
//if k is in the tree removed the node with key k from the tree
//otherwise do nothing
	root = remove(k,root);
}
private long remove(int k, long r) throws IOException {
	if(r == (long)0) {
		System.out.println("not in tree");
		return r;
	}
		
	Node cur = new Node(r);
	int compare = ((Integer)k).compareTo(cur.key);
	if(compare < 0) {
		cur.left = remove(k,cur.left);
		cur.writeNode(r);
	}
	else if(compare > 0) {
		cur.right = remove(k,cur.right);
		cur.writeNode(r);
	}
	else if (cur.left != 0 && cur.right != 0) {
		long minAddr = findMin(cur.right);
		Node min = new Node(minAddr);
		//updateFreeRemove(minAddr);
		cur.key = min.key;
		cur.fields = min.fields;
		cur.right = remove(cur.key,cur.right);
		cur.writeNode(r);
		}
	else if (cur.left != 0) {
		//updateFreeRemove(r);
		r = cur.left;
		cur = new Node(r);
		cur.writeNode(r);
	}
	else if (cur.right != 0){
		//updateFreeRemove(r);
		r = cur.right;
		cur = new Node(r);
		cur.writeNode(r);
	}
	else {
		//updateFreeRemove(r);
		return 0;
	}
	if(r != 0) {
		r = rebalance(r);
		cur = new Node(r);
		cur.height = Math.max(getHeight(cur.right), getHeight(cur.left)) + 1;
	}
	cur.writeNode(r);
	return r;
}
private long findMin(long r) throws IOException {
	if(r == (long)0) {
		return r;
	}
	long c = r;
	boolean control = true;
	Node cur = new Node(r);
	while(control) {
		if (cur.left == 0 || c == 0) {control = false;}
		else {
			c = cur.left;
			cur = new Node(cur.left);
			
		}
	}
	return c;
}
private int getHeight(long r) throws IOException{
	if(r == (long)0) {
		return -1;
	}
	Node n = new Node(r);
	return n.height;
}

private long rebalance (long n) throws IOException {
	Node cur = new Node(n);
	long l = cur.left;
	long r = cur.right;
	if(getHeight(l) - getHeight(r) == 2) {
		if(cur.left > 0) {
			Node left = new Node(cur.left);
			r = left.right;
			l = left.left;
		}
		if(getHeight(l) > getHeight(r)) {
			n = singleLeftRotation(n);
		}
		else {
			n = doubleLeftRotation(n);
		}
	}
	if(getHeight(r) - getHeight(l) == 2) {
		if(cur.right > 0) {
			Node right = new Node(cur.right);
			r = right.right;
			l = right.left;
		}
		if(getHeight(r) > getHeight(l)) {
			n = singleRightRotation(n);
		}
		else {
			n = doubleRightRotation(n);
		}
	}
	//cur.writeNode(n);
	return n;
}
public void close() throws IOException {

 //update root and free in the file (if necessary)
 //close the random access file
	f.seek(0);
	f.writeLong(root);
	f.writeLong(free);
	f.writeInt(numFields);
	for(int i = 0;i < fieldLengths.length;i++) {
		f.writeInt(fieldLengths[i]);
	}

	f.close();
 }
private long updateFreeInsert() throws IOException {
	long used = free;
	if(used == (long)0) {
		free = 0;
		return used = f.length();
	}
	f.seek(free);
	free = f.readLong();
	f.seek(8);
	f.writeLong(free);
	
	return used;
	
}
private void updateFreeRemove(long addr) throws IOException {
	
}

}