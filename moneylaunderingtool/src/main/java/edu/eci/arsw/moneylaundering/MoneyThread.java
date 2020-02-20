package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MoneyThread extends Thread {

    private List<File> files;
    private TransactionReader transactionReader;
    private TransactionAnalyzer transactionAnalyzer;
    private AtomicInteger amountOfFilesProcessed;

    public MoneyThread(TransactionReader transactionReader, TransactionAnalyzer transactionAnalyzer, List<File> files, AtomicInteger amountOfFilesProcessed) {
        this.files = files;
        this.transactionAnalyzer = transactionAnalyzer;
        this.transactionReader = transactionReader;
        this.amountOfFilesProcessed = amountOfFilesProcessed;
    }

    @Override
    public void run() {
        for (File transactionFile : files) {
            synchronized (this) {
                if (MoneyLaundering.onPause()) {
                    try {
                        wait();
                        notifyAll();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for (Transaction transaction : transactions) {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
    }
}
