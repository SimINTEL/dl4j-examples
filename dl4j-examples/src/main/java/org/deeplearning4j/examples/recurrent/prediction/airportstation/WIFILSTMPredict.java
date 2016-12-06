package org.deeplearning4j.examples.recurrent.prediction.airportstation;

import org.deeplearning4j.examples.recurrent.prediction.stocks.LSTMPredict;
import org.deeplearning4j.examples.recurrent.prediction.stocks.StockDataIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * Created by Yukai Ji on 2016/12/2.
 */
public class WIFILSTMPredict {
    private static final int IN_NUM = 5;
    private static final int OUT_NUM = 1;
    private static final int Epochs = 100;

    private static final int lstmLayer1Size = 50;
    private static final int lstmLayer2Size = 100;

    public static MultiLayerNetwork getNetModel(int nIn, int nOut){
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1)
            .learningRate(0.1)
            .rmsDecay(0.5)
            .seed(12345)
            .regularization(true)
            .l2(0.001)
            .weightInit(WeightInit.XAVIER)
            .updater(Updater.RMSPROP)
            .list()
            .layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(lstmLayer1Size)
                .activation("tanh").build())
            .layer(1, new GravesLSTM.Builder().nIn(lstmLayer1Size).nOut(lstmLayer2Size)
                .activation("tanh").build())
            .layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MSE).activation("identity")
                .nIn(lstmLayer2Size).nOut(nOut).build())
            .pretrain(false).backprop(true)
            .build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));

        return net;
    }

    public static void train(MultiLayerNetwork net,WIFIDataIterator iterator){
        //迭代训练
        for(int i=0;i<Epochs;i++) {
            DataSet dataSet = null;
            while (iterator.hasNext()) {
                dataSet = iterator.next();
                net.fit(dataSet);
            }
            iterator.reset();
            System.out.println();
            System.out.println("=================>完成第"+i+"次完整训练");
            INDArray initArray = getInitArray(iterator);

            System.out.println("预测结果：");

            for(int j=0;j<10;j++) {
                INDArray output = net.rnnTimeStep(initArray);
                StringBuilder builder = new StringBuilder();
                /*builder.append(output.getDouble(0)*iterator.getMaxArr()[0]
//                    +","
//                    + output.getDouble(1)*iterator.getMaxArr()[1] + ","
//                    + output.getDouble(2)*iterator.getMaxArr()[2] + ","
//                    + output.getDouble(3)*iterator.getMaxArr()[3] + ","
//                    + output.getDouble(4)*iterator.getMaxArr()[4]
                );*/

                builder.append(output.getDouble(0)*iterator.getMaxArr()[0]);
                builder.append("\n");
                System.out.print(builder.toString());
            }
            System.out.println();
            net.rnnClearPreviousState();
        }
    }

    private static INDArray getInitArray(WIFIDataIterator iter){
        double[] maxNums = iter.getMaxArr();
        INDArray initArray = Nd4j.zeros(1, 5, 1);
        initArray.putScalar(new int[]{0,0,0}, 43/maxNums[0]);
        initArray.putScalar(new int[]{0,1,0}, 5520/maxNums[1]);
        initArray.putScalar(new int[]{0,2,0}, 74/maxNums[2]);
        initArray.putScalar(new int[]{0,3,0}, 0/maxNums[3]);
        initArray.putScalar(new int[]{0,4,0}, 2/maxNums[4]);
        return initArray;
    }

    public static void main(String[] args) {
        String inputFile = WIFILSTMPredict.class.getClassLoader().getResource("airport/wifiMinute/gates/WIFIGateE1-1A-1E1-1-01.csv").getPath();
        int batchSize = 1;
        int exampleLength = 20;
        //初始化深度神经网络
        WIFIDataIterator iterator = new WIFIDataIterator();
        iterator.loadData(inputFile,batchSize,exampleLength);

        MultiLayerNetwork net = getNetModel(IN_NUM,OUT_NUM);
        train(net, iterator);
    }

}
