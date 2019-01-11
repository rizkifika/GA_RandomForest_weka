/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.randomforestui;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.core.converters.ConverterUtils.DataSource;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;
/**

/**
 *
 * @author rizkifika
 */
public class RandomForestUI extends javax.swing.JFrame {

    /**
     * Creates new form RandomForestUI
     */
    public static class chromosomeData implements Comparable<chromosomeData> {
    boolean[] chromosome;
    double accuracy;
    
    public chromosomeData(boolean[] data,double acc)
    {
        this.chromosome = data;
        this.accuracy = acc;
    }

    @Override
    public int compareTo(chromosomeData o) {
        return (int) (o.accuracy - this.accuracy);
    }
    
    }
    
    public static boolean[] integer_to_chromosome(int temp1)
    {
        boolean[] bits = new boolean[6];
        int temp=temp1;
        
        for (int i = 5; i >= 0; i--) {
        bits[5-i] = (temp & (1 << i)) != 0;
        }
        
        return bits;
    }
    
    public static Instances chromosome(Instances input, boolean bits[])
    {
    Instances temp=input;
            
    if (temp.classIndex() == -1)
        temp.setClassIndex(temp.numAttributes() - 1);
    
    for(int i=5;i>=0;i--)
    {
        if(bits[i]==false)
        {
            temp.deleteAttributeAt(i);
        }
    }
    return temp;
    }
    
    public static boolean[] crossover1(boolean bits1[],boolean bits2[],int point)
    {
        boolean[] bits = new boolean[6];
        
        int i;
        
        for(i=0;i<6;i++)
        {
            if(i<point){
                bits[i]=bits1[i];
            }
            else
            {
                bits[i]=bits2[i];
            }
        }
        return bits;
    }
    
    public static boolean[] crossover2(boolean bits1[],boolean bits2[],int point)
    {
        boolean[] bits = new boolean[6];
        
        int i;
        
        for(i=0;i<6;i++)
        {
            if(i<point){
                bits[i]=bits2[i];
            }
            else
            {
                bits[i]=bits1[i];
            }
        }
        return bits;
    }
    
    public static boolean[] mutation(boolean input[])
    {
        boolean[] bits = new boolean[6];
        
        int max =2;
        int min= 0;
        int i;
        Random r = new Random();
        int randInt;
        
        for(i=0;i<6;i++)
        {
        randInt = r.nextInt(max-min) + min;
        
        if(randInt==0){
            if(input[i]==false)
            {
                bits[i]=true;
            }
            else
            {
                bits[i]=false;            
            }
        }
        
        else
        {
            bits[i] = input[i];
        
        }
        
        }
        
        return bits;
    }
    
    public static double doRandomForest(Instances training, Instances testing) throws Exception
    {
        double accuracy;
        
                //inisialisasi random forest
        String[] options = new String[1];
        // set tree random forest unpruned tree
        options[0] = "-U";            
        // new instance of tree
        RandomForest tree = new RandomForest();         
        // set the options
        tree.setOptions(options);     
        // build classifier using training data
        tree.buildClassifier(training);   
     
        Evaluation eval = new Evaluation(testing);
        eval.evaluateModel(tree, testing);
        //System.out.println((eval.correct()/56)*100);
        
        accuracy = (eval.correct()/56)*100;
        
        return accuracy;
    }
    
    public void RandomForest() throws Exception{
        
        String lala = "Processing Data\n";
        jTextArea1.setText(lala);
        
        DataSource source = new DataSource("/home/rizkifika/NetBeansProjects/randomforest/src/randomforest/train.arff");
        Instances training = source.getDataSet();
        // set kelas pada data training
        if (training.classIndex() == -1)
            training.setClassIndex(training.numAttributes() - 1);
        
        // set data testing
        DataSource testing = new DataSource("/home/rizkifika/NetBeansProjects/randomforest/src/randomforest/test.arff");
        Instances test = testing.getDataSet();
        // set kelas pada data testing
        if (test.classIndex() == -1)
            test.setClassIndex(test.numAttributes() - 1);
        
        //set parameter untuk random integer 1 sd < 64
        int max = 64;
        int min = 1;
        int i;
        Random r = new Random();
        int randInt;
        int input;
        
        // inisialisasi instances untuk testing dan training serta bit cromosom
        boolean[] inputbits = new boolean[6];
        Instances inputTrain;
        Instances inputTest;
        
        double acc = 0;
        
        //inisialisasi data kromosom dengan nilai 0
        chromosomeData[] data = new chromosomeData[200];
        
        for(i=0;i<200;i++)
        {
            input = 0;
            chromosomeData tempData = new chromosomeData(inputbits,acc);
            data[i] = tempData;
        }
        
        //set inisial kromosom dengan random kromosom
        System.out.println("------Initialize initial chromosome------");
        for(i=0;i<20;i++)
        {
        //get random integer
        randInt = r.nextInt(max-min) + min;
        input = randInt;
        //convert random integer ke format kromosom
        inputbits = integer_to_chromosome(input);
        //menyesuaikan data training dengan kromosom
        inputTrain = chromosome(source.getDataSet(),inputbits);
        //menesuaikan data testing dengan kromosom
        inputTest = chromosome(testing.getDataSet(),inputbits);
        //random forest
        acc = doRandomForest(inputTrain,inputTest);
        //masukkan kromoom dan hasilnya dalam class
        chromosomeData tempData = new chromosomeData(inputbits,acc);
        data[i] = tempData;
        
        System.out.println("kromosom = " + Arrays.toString(inputbits)+" accuracy = "+acc);
        
        }
        Arrays.sort(data);
        System.out.println("------sorted------");
        
        System.out.println("------do Genetic Algorithm with random forest------");
        //inisialisasi kromosom 1 dan 2
        boolean[] chromosome1 = new boolean[6];
        boolean[] chromosome2 = new boolean[6];
        //set batas max rank exclusive kromosom
        int maxchromosome = 11;
        //set batas min rank inclusive kromosom
        int minchromosome = 1;
        Random rchromosome = new Random();
        int index;
        int point;
        
        int init=19;
        
        for(i=0;i<30;i++)
        {
        System.out.println("-----GA ke-"+i+" -----");
        //get random integer
        randInt = rchromosome.nextInt(maxchromosome-minchromosome) + minchromosome;
        index = randInt;
        //set kromosom pertama
        chromosome1 = data[index].chromosome;
        //get random integer
        randInt = rchromosome.nextInt(maxchromosome-minchromosome) + minchromosome;
        index = randInt;
        //set kromosom kedua
        chromosome2 = data[index].chromosome;
        //set point untuk crossover
        randInt = rchromosome.nextInt(6-2) + 2;
        point = randInt;
        //do crossover kromosom 1
        chromosome1 = crossover1(chromosome1,chromosome2,point);
        //do crossover kromosom 2
        chromosome2 = crossover2(chromosome1,chromosome2,point);
        //do mutation
        chromosome1 = mutation(chromosome1);
        chromosome2 = mutation(chromosome2);        
        
        //evaluate kromosom 1
        inputTrain = chromosome(source.getDataSet(),chromosome1);
        //menesuaikan data testing dengan kromosom
        inputTest = chromosome(testing.getDataSet(),chromosome1);
        //random forest
        acc = doRandomForest(inputTrain,inputTest);
        //masukkan kromoom dan hasilnya dalam class
        chromosomeData tempData = new chromosomeData(chromosome1,acc);
        init=init+1;
        data[init] = tempData;
        System.out.println("kromosom1 = " + Arrays.toString(chromosome1)+" accuracy = "+acc);        
        //evaluate kromosom 2
        inputTrain = chromosome(source.getDataSet(),chromosome2);
        //menesuaikan data testing dengan kromosom
        inputTest = chromosome(testing.getDataSet(),chromosome2);
        //random forest
        acc = doRandomForest(inputTrain,inputTest);
        //masukkan kromoom dan hasilnya dalam class
        chromosomeData tempData2 = new chromosomeData(chromosome2,acc);
        init=init+1;
        data[init] = tempData2;
        
        System.out.println("kromosom2 = " + Arrays.toString(chromosome2)+" accuracy = "+acc);
        
        Arrays.sort(data);
        System.out.println("------sorted------");
        
        }
        
        System.out.println("-------10 kromosom terbaik-------");
        String result = "Processing Data\n";
        for(i=0;i<10;i++)
        {
        //true fitur dipakai false fitur tidak dipakai, urutan kromosom sesuai file arff
        System.out.println("kromosom = " + Arrays.toString(data[i].chromosome)+" acc = "+data[i].accuracy);
        result = result + "kromosom = " + Arrays.toString(data[i].chromosome)+" acc = "+data[i].accuracy+"\n";
        }
        jTextArea1.setText(result);
    
    }
    public RandomForestUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        label1 = new java.awt.Label();
        button1 = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        label1.setText("Hasil Kromosom 10 Terbaik");

        button1.setLabel("do RandomForest");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(130, 130, 130))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
            try {
                // TODO add your handling code here:
                RandomForest();
            } catch (Exception ex) {
                Logger.getLogger(RandomForestUI.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_button1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RandomForestUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RandomForestUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RandomForestUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RandomForestUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RandomForestUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private java.awt.Label label1;
    // End of variables declaration//GEN-END:variables
}
