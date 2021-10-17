import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.lang.model.util.ElementScanner6;

public class CS4375A1 {
    public static void main(String[] args) throws FileNotFoundException {
        learnTree();
        
    }
    public static int highestValue(ArrayList<Integer> tempList) {
        int maxValue = 0;
        for(int i = 0; i < tempList.size(); i++) {
            if(maxValue < tempList.get(i))
            maxValue = tempList.get(i);
        }
        return maxValue;
    }
    public static boolean learnTree() throws FileNotFoundException
    {  
        Scanner scan = new Scanner(System.in);
        System.out.print("Name of training data file: ");
        String trainFileName = scan.nextLine();
        System.out.print("Name of test data file: ");
        String testFileName = scan.nextLine();
        Scanner file = new Scanner(new File(trainFileName));
        String line = file.nextLine();
        int count = 0;                                       //count of classes
        ArrayList<String> names = new ArrayList();           //arrayList of class names
        Scanner reader = new Scanner(line);
        //populates arrayList names with class names and counts total number of classes
        while(reader.hasNext()){
            names.add(reader.next());
            count++;
        }
        ArrayList<Integer>[] list = new ArrayList[count];   //array of arrayLists, where each array is a different class,
        for (int i = 0; i < count; i++) {                   //containing the different values of their respective class
            list[i] = new ArrayList<Integer>();
        }
        //populates the values into each array
        while(file.hasNext()){
            line = file.nextLine();
            Scanner chopper = new Scanner(line);
            for (int i = 0; i < count; i++) {
                list[i].add(chopper.nextInt());
            }
        }
        
        Integer[] maxValues = new Integer[count];           //keeps record each classes' highest class value
        for (int i = 0; i < count; i++) {
            maxValues[i] = highestValue(list[i]);
            //System.out.println(list[0]);                  //to print out class values of certain class
        }
        
        //creates arrays of test data
        Scanner tfile = new Scanner(new File(testFileName));
        String tline = tfile.nextLine();
        ArrayList<String> tnames = new ArrayList<String>();
        Scanner treader = new Scanner(tline);
        while(reader.hasNext()){
            tnames.add(reader.next());
        }
        ArrayList<Integer>[] tlist = new ArrayList[count];   //array of arrayLists, where each array is a different class,
        for (int i = 0; i < count; i++) {                   //containing the different values of their respective class
            tlist[i] = new ArrayList<Integer>();
        }
        //populates the values into each array
        while(tfile.hasNext()){
            tline = tfile.nextLine();
            Scanner tchopper = new Scanner(tline);
            for (int i = 0; i < count; i++) {
                tlist[i].add(tchopper.nextInt());
            }
        }
        
        //CREAT ROOT NODE
        
        Node rootNode = new Node("");
        rootNode = populateNode(rootNode, list[(count-1)], names.get((count-1)));
        double rootEntropy = calculateEntropy(rootNode.getZero(), rootNode.getOne(), rootNode.getTwo());
        
        Node tempNode = new Node("");
        ArrayList<Integer> indexInUse = new ArrayList<Integer>();
        ArrayList<Integer> columnsUsed = new ArrayList<Integer>();
        for(int i = 0; i < list[0].size(); i++) {
            indexInUse.add(i);
        }
        //creating decision tree
        tempNode = populateTree(tempNode, list, names, count-1,indexInUse, count, rootEntropy, columnsUsed);
        
        //printing decision tree
        printTree(tempNode.leftPointer, 0, 0);
        System.out.println();
        printTree(tempNode.middlePointer, 0, 1);
        System.out.println();
        printTree(tempNode.rightPointer, 0, 2);
        System.out.println();
        
        //checking training data to decision tree
        ArrayList<Integer> rowValues = new ArrayList<Integer>();
        int correct = 0;
        int possibilities = 0;
        Node answerNode = new Node();
        for(int i = 0; i < list[0].size(); i++) {
            //record row values into arrayList
            for(int u = 0; u < list.length-1; u++) {
                rowValues.add(list[u].get(i));
            }
            testTree(tempNode, names, 0, rowValues, answerNode);
            if(answerNode.name == "0" && list[count-1].get(i) == 0)
            correct++;
            else if(answerNode.name == "1" && list[count-1].get(i) == 1)
            correct++;
            else if(answerNode.name == "2" && list[count-1].get(i) == 2)
            correct++;
            for(int u = 0; u < rowValues.size(); u++) {
                rowValues.remove(u);
            }
            possibilities++;
            
        }
        if(possibilities != 0)
        System.out.println("Accuracy on training set (" + possibilities + " instances): " + (((double)correct/possibilities) *100) + "%");
        
        //checking test data to decision tree
        ArrayList<Integer> trowValues = new ArrayList<Integer>();
        int tcorrect = 0;
        int tpossibilities = 0;
        Node tanswerNode = new Node();
        for(int i = 0; i < tlist[0].size(); i++) {
            //record row values into arrayList
            for(int u = 0; u < tlist.length-1; u++) {
                trowValues.add(tlist[u].get(i));
            }
            testTree(tempNode, names, 0, trowValues, tanswerNode);
            if(tanswerNode.name == "0" && tlist[count-1].get(i) == 0)
            tcorrect++;
            else if(tanswerNode.name == "1" && tlist[count-1].get(i) == 1)
            tcorrect++;
            else if(tanswerNode.name == "2" && tlist[count-1].get(i) == 2)
            tcorrect++;
            for(int u = 0; u < trowValues.size(); u++) {
                trowValues.remove(u);
            }
            tpossibilities++;
            
        }
        if(possibilities != 0)
        System.out.println("Accuracy on test set (" + tpossibilities + " instances): " + (((double)tcorrect/tpossibilities) *100) + "%");
        return true;
    }
    public static void testTree(Node node, ArrayList<String> names, int value, ArrayList<Integer> rowValues, Node ans) {
        if(node.leftPointer == null && node.middlePointer == null && node.rightPointer == null) {
            if(node.num0 > 0)
            ans.name = "0";
            else if(node.num1 > 0)
            ans.name = "1";
            else if(node.num2 > 0) 
            ans.name = "2"; 
        }
        else {
            int index = names.indexOf(node.name);
            if(index == -1) {
                index = names.indexOf(node.leftPointer.name);
            }
            if(node.leftPointer != null && rowValues.get(index) == 0) {
                testTree(node.leftPointer, names, 0, rowValues, ans);
            }
            if(node.middlePointer != null && rowValues.get(index) == 1) {
                testTree(node.middlePointer, names, 1, rowValues, ans);
            }
            if(node.rightPointer != null && rowValues.get(index) == 2) {
                testTree(node.rightPointer, names, 2, rowValues, ans);
            }
        }
        
        
    }
    // public static void testDecisionTree(ArrayList<Integer>[] list, ArrayList<String> names, Node node, int count) {
        //     int possibilities = 0;
        //     int correct = 0;
        //     int currentIndex = 0;
        //     int value = 3;
        //     ArrayList<Integer> rowValues = new ArrayList<Integer>();
        //     for(int i = 0; i < list[0].size(); i++) {
            //         //record row values into arrayList
            //         System.out.print("Working on row " + i + ": ");
            //         for(int u = 0; u < list.length-1; u++) {
                //             rowValues.add(list[u].get(i));
                //             System.out.print(list[u].get(i));
                //         }  
                //         if(node.leftPointer != null && rowValues.get(currentIndex) == 0) {
                    //             currentIndex = names.indexOf(node.leftPointer.name);
                    //             //if(rowValues.get(currentIndex) == 0)
                    //                 value = traverseTree(node.leftPointer, rowValues, currentIndex, names);
                    //         }
                    //         if(node.middlePointer != null&& rowValues.get(currentIndex) == 1) {
                        //             currentIndex = names.indexOf(node.middlePointer.name);
                        //             //if(rowValues.get(currentIndex) == 1)
                        //                 value = traverseTree(node.middlePointer, rowValues, currentIndex, names);
                        //         }
                        //         if(node.rightPointer != null && rowValues.get(currentIndex) == 2) {
                            //             currentIndex = names.indexOf(node.rightPointer.name);
                            //             //if(rowValues.get(currentIndex) == 2)
                            //                 value = traverseTree(node.rightPointer, rowValues, currentIndex, names);
                            //         }
                            //         for(int u = 0; u < rowValues.size(); u++) {
                                //             rowValues.remove(u);
                                //         }
                                //         System.out.print("  Value = " + value + " Outcome = " + list[count-1].get(i));
                                //         if(value == list[count-1].get(i)) {
                                    //             possibilities++;
                                    //             correct++;
                                    //         }
                                    //         else
                                    //             possibilities++;
                                    
                                    //         System.out.println();
                                    //     }
                                    //     System.out.println("This set is " + correct + "/" + possibilities + " correct.");
                                    //     if(possibilities != 0)
                                    //         System.out.println("Accuracy on ... is " + (((double)correct/possibilities) *100) + "%");
                                    // }
                                    // public static int traverseTree( Node node, ArrayList<Integer> rowValues, int currentIndex, ArrayList<String> names) {
                                        //     int newCurrentIndex = 0;
                                        //     if(node.leftPointer != null)
                                        //         newCurrentIndex = names.indexOf(node.leftPointer.name);
                                        //     if(node.leftPointer != null && rowValues.get(newCurrentIndex) == 0) {
                                            //         newCurrentIndex = names.indexOf(node.leftPointer.name);
                                            //         //if(rowValues.get(newCurrentIndex) == 0)
                                            //             return traverseTree(node.leftPointer, rowValues, newCurrentIndex, names);
                                            //     } 
                                            //     if(node.middlePointer != null && rowValues.get(newCurrentIndex) == 1) {
                                                //         newCurrentIndex = names.indexOf(node.middlePointer.name);
                                                //         //if(rowValues.get(newCurrentIndex) == 1)
                                                //             return traverseTree(node.middlePointer, rowValues, newCurrentIndex, names);
                                                //     }
                                                //     if(node.rightPointer != null && rowValues.get(newCurrentIndex) == 2) {
                                                    //         newCurrentIndex = names.indexOf(node.rightPointer.name);
                                                    //         //if(rowValues.get(newCurrentIndex) == 2)
                                                    //             return traverseTree(node.rightPointer, rowValues, newCurrentIndex, names);
                                                    //     }
                                                    //     System.out.print(" num0="+node.num0+" num1=" + node.num1 + " num2="+node.num2);
                                                    //     if(node.num0 >= node.num1 && node.num0 >= node.num2)
                                                    //         return 0;
                                                    //     else if(node.num1 > node.num0 && node.num1 > node.num2)
                                                    //         return 1;
                                                    //     else if(node.num2 > node.num0 && node.num2 > node.num1)
                                                    //         return 2;
                                                    // return 0;
                                                    // }
                                                    public static void printTree(Node node, int level, int value) {
                                                        boolean checker = true;
                                                        for(int i = 0; i < level; i++) {
                                                            System.out.print("| ");
                                                            checker = true;
                                                        }
                                                        System.out.print(node.name + " = " + value + ": ");
                                                        if(node.leftPointer != null) {
                                                            System.out.println();
                                                            printTree(node.leftPointer, (level+1), 0);
                                                            checker = false;
                                                        }
                                                        if(node.middlePointer != null) { 
                                                            System.out.println();
                                                            printTree(node.middlePointer, level+1, 1);
                                                            checker = false;
                                                        }
                                                        if(node.rightPointer != null) {
                                                            System.out.println();
                                                            printTree(node.rightPointer, level+1, 2);
                                                            checker = false;
                                                        }
                                                        if(checker == true) {
                                                            if(node.num0 != 0)
                                                            System.out.print("0");
                                                            else if(node.num1 != 0)
                                                            System.out.print("1");
                                                            else if(node.num2 != 0) 
                                                            System.out.print("2");
                                                            else
                                                            System.out.print("0");
                                                            
                                                        }
                                                        
                                                    }
                                                    public static Node populateTree(Node node, ArrayList<Integer>[] list, ArrayList<String> names, int index, ArrayList<Integer> indexInUse, int count, double rootEntropy, ArrayList<Integer> columnsUsed){
                                                        boolean firstChecker = true;
                                                        boolean secondChecker = true;
                                                        columnsUsed.add(index);
                                                        //checks if all values in Y are same or in X are same
                                                        for(int i = 0; i < indexInUse.size()-1; i++) {
                                                            if(index == count-1)
                                                            firstChecker = false;
                                                            else if(list[count-1].get(indexInUse.get(i)) != list[count-1].get(indexInUse.get(i+1)))
                                                            firstChecker = false;
                                                        }
                                                        if(firstChecker) {
                                                            ArrayList<Integer> tempYList = new ArrayList<Integer>();
                                                            for(int i = 0; i < indexInUse.size(); i++) {
                                                                tempYList.add(list[count-1].get(indexInUse.get(i)));
                                                            }
                                                            node = populateNode(node, tempYList, names.get(index));
                                                            return node;
                                                        }
                                                        if(columnsUsed.size() > count-1) {
                                                            int num0 =0;
                                                            int num1 = 0;
                                                            int num2 = 0;
                                                            for(int i = 0; i < indexInUse.size(); i++) {
                                                                if(list[count-1].get(indexInUse.get(i)) == 0)
                                                                num0++;
                                                                else if(list[count-1].get(indexInUse.get(i)) == 1)
                                                                num1++;
                                                                else if(list[count-1].get(indexInUse.get(i)) == 2)
                                                                num2++;
                                                            }
                                                            if(num0 >= num1 && num0 >= num2)
                                                            node = populateNode(node, names.get(index), num0, 0, 0);
                                                            else if(num1 > num0 && num1 >num2)
                                                            node = populateNode(node, names.get(index), 0, num1, 0);
                                                            else if(num2 > num0 && num2 >num1)
                                                            node = populateNode(node, names.get(index), 0, 0, num2);
                                                            return node;
                                                        }
                                                        double highestIG = 0;
                                                        int indexOfIG = 0;
                                                        for(int i = 0; i < (count-1); i++) {
                                                            if(i != count-1 && columnsUsed.indexOf(i) == -1) {
                                                                Node temp = new Node();
                                                                ArrayList<Integer> tempLeftList = new ArrayList<Integer>();
                                                                ArrayList<Integer> tempMiddleList = new ArrayList<Integer>();
                                                                ArrayList<Integer> tempRightList = new ArrayList<Integer>();
                                                                for(int u = 0; u < indexInUse.size(); u++) {
                                                                    //tempList.add(list[i].get(u));
                                                                    if(list[i].get(indexInUse.get(u)) == 0) 
                                                                    tempLeftList.add(list[count-1].get(indexInUse.get(u)));
                                                                    else if(list[i].get(indexInUse.get(u)) == 1) 
                                                                    tempMiddleList.add(list[count-1].get(indexInUse.get(u)));
                                                                    else if(list[i].get(indexInUse.get(u)) == 2) 
                                                                    tempRightList.add(list[count-1].get(indexInUse.get(u)));
                                                                }
                                                                
                                                                double val = calculateIG(rootEntropy, tempLeftList, tempMiddleList, tempRightList);
                                                                if(val > highestIG) {
                                                                    highestIG = val;
                                                                    indexOfIG = i;
                                                                }
                                                            }
                                                        }
                                                        //for left child
                                                        ArrayList<Integer> leftValues = new ArrayList<Integer>();
                                                        ArrayList<Integer> indexLeftValues = new ArrayList<Integer>();
                                                        ArrayList<Integer> newLColumnsUsed = new ArrayList<Integer>();
                                                        for(int i = 0; i < indexInUse.size(); i++) {
                                                            if(list[indexOfIG].get(indexInUse.get(i)) == 0) {
                                                                leftValues.add(list[count-1].get(indexInUse.get(i)));
                                                                indexLeftValues.add(indexInUse.get(i));
                                                            }
                                                        }
                                                        for(int i = 0; i < columnsUsed.size(); i++) {
                                                            newLColumnsUsed.add(columnsUsed.get(i));
                                                        }
                                                        Node leftNode = new Node();
                                                        leftNode = populateNode(leftNode, leftValues, names.get(indexOfIG));
                                                        node.leftPointer = leftNode;
                                                        node.leftPointer = populateTree(node.leftPointer,list, names, indexOfIG, indexLeftValues, count, rootEntropy, newLColumnsUsed);
                                                        
                                                        //middle child
                                                        ArrayList<Integer> middleValues = new ArrayList<Integer>();
                                                        ArrayList<Integer> indexMiddleValues = new ArrayList<Integer>();
                                                        ArrayList<Integer> newMColumnsUsed = new ArrayList<Integer>();
                                                        for(int i = 0; i < indexInUse.size(); i++) {
                                                            if(list[indexOfIG].get(indexInUse.get(i)) == 1) {
                                                                middleValues.add(list[count-1].get(indexInUse.get(i)));
                                                                indexMiddleValues.add(indexInUse.get(i));
                                                            }
                                                        }
                                                        for(int i = 0; i < columnsUsed.size(); i++) {
                                                            newMColumnsUsed.add(columnsUsed.get(i));
                                                        }
                                                        Node middleNode = new Node();
                                                        middleNode = populateNode(middleNode, middleValues, names.get(indexOfIG));
                                                        node.middlePointer = middleNode;
                                                        node.middlePointer = populateTree(node.middlePointer, list, names, indexOfIG, indexMiddleValues, count, rootEntropy, newMColumnsUsed);
                                                        
                                                        //right child
                                                        ArrayList<Integer> rightValues = new ArrayList<Integer>();
                                                        ArrayList<Integer> indexRightValues = new ArrayList<Integer>();
                                                        ArrayList<Integer> newRColumnsUsed = new ArrayList<Integer>();
                                                        for(int i = 0; i < indexInUse.size(); i++) {
                                                            if(list[indexOfIG].get(indexInUse.get(i)) == 2) {
                                                                rightValues.add(list[count-1].get(indexInUse.get(i)));
                                                                indexRightValues.add(indexInUse.get(i));
                                                            }
                                                        }
                                                        for(int i = 0; i < columnsUsed.size(); i++) {
                                                            newRColumnsUsed.add(columnsUsed.get(i));
                                                        }
                                                        Node rightNode = new Node();
                                                        rightNode = populateNode(rightNode, rightValues, names.get(indexOfIG));
                                                        node.rightPointer = rightNode;
                                                        node.rightPointer = populateTree(node.rightPointer, list, names, indexOfIG, indexRightValues, count, rootEntropy, newRColumnsUsed);
                                                        
                                                        return node;
                                                    }
                                                    
                                                    //*****Change to make better way to record maxValue *****/
                                                    public static Node populateNode(Node temp, ArrayList<Integer> classValues, String name) {
                                                        Node node = new Node();
                                                        node = temp;
                                                        node.name = name;
                                                        for(int i = 0; i < classValues.size();i++) {
                                                            if(classValues.get(i) == 0)
                                                            node.num0++;
                                                            else if(classValues.get(i) == 1)
                                                            node.num1++;
                                                            else if(classValues.get(i) == 2)
                                                            node.num2++;
                                                            
                                                        }
                                                        return node;
                                                    }
                                                    public static Node populateNode(Node temp, String name, int numZero, int numOne, int numTwo) {
                                                        Node node = temp;
                                                        node.name = name;
                                                        node.num0 = numZero;
                                                        node.num1 = numOne;
                                                        node.num2 = numTwo;
                                                        return node;
                                                    }
                                                    public static double calculateEntropy(int num0, int num1, int num2) {
                                                        double ans = 0;
                                                        if (num0+num1+num2 == 0) {
                                                            return 0;
                                                        }
                                                        int sum = num0 + num1 + num2;
                                                        ans += (-1 * ((double)num0)/sum) *log2((double)num0/(double)sum);
                                                        ans += (-1 * ((double)num1)/sum) *log2((double)num1/ (double)sum);
                                                        ans += (-1 * ((double)num2)/sum) *log2((double)num2/ (double)sum);
                                                        return ans;
                                                    }
                                                    
                                                    public static double calculateIG(double rootEnt, ArrayList<Integer> leftList, ArrayList<Integer> middleList, ArrayList<Integer> rightList) {
                                                        int lcount0 = 0;
                                                        int lcount1 = 0;
                                                        int lcount2 = 0;
                                                        int mcount0 = 0;
                                                        int mcount1 = 0;
                                                        int mcount2 = 0;
                                                        int rcount0 = 0;
                                                        int rcount1 = 0;
                                                        int rcount2 = 0;
                                                        for(int i = 0; i < leftList.size(); i++) {
                                                            if(leftList.get(i) ==0)
                                                            lcount0++;
                                                            else if(leftList.get(i) == 1)
                                                            lcount1++;
                                                            else if(leftList.get(i) == 2)
                                                            lcount2++;
                                                        }
                                                        for(int i = 0; i < middleList.size(); i++) {
                                                            if(middleList.get(i) ==0)
                                                            mcount0++;
                                                            else if(middleList.get(i) == 1)
                                                            mcount1++;
                                                            else if(middleList.get(i) == 2)
                                                            mcount2++;
                                                        }
                                                        for(int i = 0; i < rightList.size(); i++) {
                                                            if(rightList.get(i) ==0)
                                                            rcount0++;
                                                            else if(rightList.get(i) == 1)
                                                            rcount1++;
                                                            else if(rightList.get(i) == 2)
                                                            rcount2++;
                                                        }
                                                        
                                                        double igValue = rootEnt;
                                                        int lsum = lcount0 + lcount1 + lcount2;
                                                        int msum = mcount0 + mcount1 + mcount2;
                                                        int rsum = rcount0 + rcount1 + rcount2;
                                                        int tsum = lsum + msum + rsum;
                                                        igValue -= ((double)lsum/tsum) * calculateEntropy(lcount0, lcount1, lcount2);
                                                        igValue -= ((double)msum/tsum) * calculateEntropy(mcount0, mcount1, mcount2);
                                                        igValue -= ((double)rsum/tsum) * calculateEntropy(rcount0, rcount1, rcount2);
                                                        
                                                        return igValue;
                                                    }
                                                    public static double log2(double n) {
                                                        if(n == 0) 
                                                        return 0;
                                                        return Math.log(n) / Math.log(2);
                                                    }
                                                    static class Node {
                                                        String name;
                                                        Node leftPointer;
                                                        Node middlePointer;
                                                        Node rightPointer;
                                                        int num0;
                                                        int num1;
                                                        int num2;
                                                        boolean hasMiddle;
                                                        int choices;
                                                        public Node() {
                                                            name = "EMPTY";
                                                            leftPointer = null;
                                                            middlePointer = null;
                                                            rightPointer = null;
                                                            num0 = 0;
                                                            num1 = 0;
                                                            num2 = 0;
                                                            hasMiddle = false;
                                                            choices = 0;
                                                        }
                                                        public Node(String temp) {
                                                            name = temp;
                                                        }
                                                        public Node(String temp, int zeros) {
                                                            name = temp;
                                                            num0 = zeros;
                                                        }
                                                        public Node(String temp, int zeros, int ones) {
                                                            name = temp;
                                                            num0 = zeros;
                                                            num1 = ones;
                                                            hasMiddle = false;
                                                            choices = 2;
                                                        }
                                                        public Node(String temp, int zeros, int ones, int twos) {
                                                            name = temp;
                                                            num0 = zeros;
                                                            num1 = ones;
                                                            num2 = twos;
                                                            hasMiddle = true;
                                                        }
                                                        public void setZero(int zero) {
                                                            num0 = zero;
                                                        }
                                                        public void setOne(int one) {
                                                            num1 = one;
                                                        }
                                                        public void setTwo(int two) {
                                                            num2 = two;
                                                        }
                                                        public int getZero() {
                                                            return num0;
                                                        }
                                                        public int getOne() {
                                                            return num1;
                                                        }
                                                        public int getTwo() {
                                                            return num2;
                                                        }
                                                        public void setLeft(Node leftNode) {
                                                            leftPointer = leftNode;
                                                        }
                                                        public void setMiddle(Node middleNode) {
                                                            middlePointer = middleNode;
                                                        }
                                                        public void setRight(Node rightNode) {
                                                            rightPointer = rightNode;
                                                        }
                                                        public int sum() {
                                                            return num0+num1+num2;
                                                        }
                                                    }
                                                }
                                                
                                                