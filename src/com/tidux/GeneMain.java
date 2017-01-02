/**
 * Project: xmlSignTest
 * 
 * File Created at 2016年12月29日
 * $Id$
 * 
 * Copyright 2008 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package com.tidux;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * TODO Comment of GeneMain
 * 
 * @author Administrator
 */
public class GeneMain extends JFrame {

    public static int   unitNum       = 8;
    public static int   maxGeneration = 100000;
    public static int[] st            = { 1, 2, 3, 4, 5, 6, 7 };

    /**
     * @param args
     */
    public static void main(String[] args) {
        //1,设置一个群集起始群集
        //2,确定适应函数, 目标搜索解
        //3,计算适应概率
        //4,选择
        //5,交叉
        //6,变异

        GeneMain geneMain = new GeneMain();
        System.out.println("===============================");

    }

    private JPanel getJPanel() {
        JPanel jpanel = new JPanel();
        jpanel.getGraphics().draw3DRect(22, 11, 222, 111, true);
        return jpanel;
    }

    private static String toBinaryString(int v) {
        return Integer.toBinaryString(64 + v).substring(1);
    }

    public GeneMain() {

        setDefaultCloseOperation(3);
        setSize(500, 500);
        setVisible(true);
        getContentPane().add(getJPanel());

        for (int currentGeneration = 0; currentGeneration < maxGeneration; currentGeneration++) {
            List<Unit> units = new ArrayList<Unit>();
            int sumFitVal = 0;
            for (int i = 0; i < unitNum; i++) {
                Unit unit = new Unit();
                sumFitVal += unit.getFitVal();
                units.add(unit);
            }

            for (Unit unit : units) {
                unit.evaluationFitRate(sumFitVal);
            }

            display(units, currentGeneration, sumFitVal);

            List<Unit> nextGenUnits = new ArrayList<Unit>();
            //选择
            nextGenUnits = choose(units);

            //交叉
            nextGenUnits = cross(units);

            //变异
            //nextGenUnits = genovariation(units);

            units = nextGenUnits;
        }

    }

    private List<Unit> cross(List<Unit> units) {
        List<Unit> crossUnits = new ArrayList<Unit>();
        UnitPair unitPair = new UnitPair();
        while (unitPair.pair(units)) {
            unitPair.cross();
            crossUnits.add(unitPair.getLeft());
            crossUnits.add(unitPair.getRight());
        }

        return crossUnits;
    }

    class UnitPair {
        private Unit left;
        private Unit right;

        public boolean pair(List<Unit> units) {

            if (units == null || units.size() == 0 || units.size() % 2 == 1) {
                return false;
            }

            Random random = new Random();
            left = units.get(random.nextInt(units.size()));
            units.remove(left);

            right = units.get(random.nextInt(units.size()));
            units.remove(right);

            return true;
        }

        public void cross() {
            Random random = new Random();
            int crossPoint = random.nextInt(left.getGeneStr().length());

            //swap
            //10|0111   1|00011  1|10110
            //01|0011   0|10110  0|00011

            String leftGeneStrA = left.getGeneStr().substring(0, crossPoint);
            String leftGeneStrB = left.getGeneStr().substring(crossPoint);

            String rightGeneStrA = right.getGeneStr().substring(0, crossPoint);
            String rightGeneStrB = right.getGeneStr().substring(crossPoint);

            String newLeftGeneStr = leftGeneStrA + rightGeneStrB;
            String newRightGeneStr = rightGeneStrA + leftGeneStrB;

            left.rebuild(newLeftGeneStr);
            right.rebuild(newRightGeneStr);
        }

        public Unit getLeft() {
            return left;
        }

        public void setLeft(Unit left) {
            this.left = left;
        }

        public Unit getRight() {
            return right;
        }

        public void setRight(Unit right) {
            this.right = right;
        }

    }

    private List<Unit> genovariation(List<Unit> units) {
        List<Unit> genovariationUnits = new ArrayList<Unit>();
        return genovariationUnits;
    }

    private List<Unit> choose(List<Unit> units) {
        List<Unit> choosedUnits = new ArrayList<Unit>();
        for (int i = 0; i < unitNum; i++) {
            double point = new Random().nextDouble();
            double start = 0;
            for (Unit unit : units) {
                if (point >= start && point < start + unit.getFitRate()) {
                    try {
                        choosedUnits.add((Unit) unit.clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                start += unit.getFitRate();
            }
        }
        return choosedUnits;
    }

    private void display(List<Unit> units, int currentGeneration, double sumFitVal) {
        System.out.println(String.format(
                "---------- CURRENT GENERATION : %s ,  SumFitVal: %s ---------- ",
                currentGeneration, sumFitVal));
        for (Unit unit : units) {
            //System.out.println(unit);
        }
        //        System.out.println("------------ ");
        //      System.out.println();
    }

    class Unit implements Cloneable {
        private String uuid    = UUID.randomUUID().toString();
        private int    gene    = 0;
        private String geneStr = "";
        private int    x1      = 0;
        private int    x2      = 0;

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        private int    fitVal  = 0;
        private double fitRate = 0d;

        public Unit() {
            //生成显性性状
            x1 = new Random().nextInt(st.length);
            x2 = new Random().nextInt(st.length);

            //根据性状生成基因编码
            fitVal = evaluationFitVal(x1, x2);
            gene = x1 << 3 | x2;
            geneStr = toBinaryString(gene);

        }

        public void rebuild(String geneStr) {
            //根据基因编码重新生成显性性状
            x1 = Integer.valueOf(geneStr.substring(0, 3), 2);
            x2 = Integer.valueOf(geneStr.substring(3), 2);

            //根据性状生成基因编码
            fitVal = evaluationFitVal(x1, x2);
            gene = x1 << 3 | x2;
            geneStr = toBinaryString(gene);

            fitRate = 0d;
        }

        private int evaluationFitVal(int x1, int x2) {
            return (int) (Math.pow(x1, 2) + Math.pow(x2, 2));
        }

        private void evaluationFitRate(double sumFitVal) {
            fitRate = fitVal / sumFitVal;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getGene() {
            return gene;
        }

        public void setGene(int gene) {
            this.gene = gene;
        }

        public String getGeneStr() {
            return geneStr;
        }

        public void setGeneStr(String geneStr) {
            this.geneStr = geneStr;
        }

        public int getX1() {
            return x1;
        }

        public void setX1(int x1) {
            this.x1 = x1;
        }

        public int getX2() {
            return x2;
        }

        public void setX2(int x2) {
            this.x2 = x2;
        }

        public int getFitVal() {
            return fitVal;
        }

        public void setFitVal(int fitVal) {
            this.fitVal = fitVal;
        }

        public double getFitRate() {
            return fitRate;
        }

        public void setFitRate(double fitRate) {
            this.fitRate = fitRate;
        }

        @Override
        public String toString() {
            return String.format(
                    "UUID:, x1: %s, x2: %s, gene: %s, geneStr: %s, fitVal: %s, fitRate: %s", x1,
                    x2, gene, geneStr, fitVal, fitRate);
        }
    }
}
