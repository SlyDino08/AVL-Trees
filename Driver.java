import java.io.IOException;
import java.util.LinkedList;

public class Driver {
	public static void main (String [] args) {
		int [] fieldLengths = new int [] {5, 8, 9};
		
		char [][] fields = new char [3][5];
		char [][] fields2 = new char [3][8];
		char [][] fields3 = new char [3][9];
		
		for (int i = 0; i < fields.length; i++) {
			fields [i] = new char [fieldLengths[i]];
			fields2 [i] = new char [fieldLengths[i]];
			fields3 [i] = new char [fieldLengths[i]];
		}
		
		for (int i = 0; i < fields.length; i++) {
			for (int j = 0; j < fields[i].length; j++) {
				fields[i] [j] = 'Y';
				
			}
		}
		for (int i = 0; i < fields2.length; i++) {
			for (int j = 0; j < fields2[i].length; j++) {
				fields2[i] [j] = 'X';
				
			}
		}
		for (int i = 0; i < fields3.length; i++) {
			for (int j = 0; j < fields3[i].length; j++) {
				fields3[i] [j] = 'G';
				
			}
		}
		try {
			
			AVLTree t = new AVLTree("test_tree1", fieldLengths);
			//t.print();
			t.insert(50, fields);
			
			t.insert(25, fields3);
			
			t.insert(100, fields2);
			
			t.insert(70, fields);
			
			t.insert(75, fields3);
			
			t.insert(200, fields2);
			
			t.insert(201, fields3);
			
			t.insert(202, fields3);
			
			t.insert(203, fields3);
			t.print();
			t.remove(50);
			t.print();
			t.remove(100);
			
			t.insert(80, fields);
			t.insert(200, fields2);
			t.insert(90, fields3);
			//LinkedList<String> l = t.find(80);
			t.print();
			t.close();
			/*
			System.out.println(l.size());
			
			while (!l.isEmpty()) {
				//System.out.print(l.removeFirst());
			}
			t.insert(18, fields3);
			*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
