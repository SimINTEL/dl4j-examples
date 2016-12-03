package org.deeplearning4j.examples.recurrent.mse;

/**
 * Created by Yukai Ji on 2016/11/20.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the dataset operator for predict the passenger amount in each gate of airplane
 */
public class PassengerDataIterator {
    private static final int VECTOR_SIZE = 6;
    //每批次的训练数据组数
    private int batchNum;

    //每组训练数据长度(DailyData的个数)
    private int exampleLength;

    //数据集
    private List<PeriodData> dataList;

    //存放剩余数据组的index信息
    private List<Integer> dataRecord;

    private double[] maxNum;

    public PassengerDataIterator() {
        this.dataRecord = new ArrayList<>();
    }
}
