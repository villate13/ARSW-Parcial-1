package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FileThread extends Thread {

    private File file;
    private TransactionReader transactionReader;
    private TransactionAnalyzer transactionAnalyzer;
    private AtomicInteger amountOfFilesProcessed;

    public FileThread(TransactionReader transactionReader, TransactionAnalyzer transactionAnalyzer, File file, AtomicInteger amountOfFilesProcessed) {
        this.file = file;
        this.transactionAnalyzer = transactionAnalyzer;
        this.transactionReader = transactionReader;
        this.amountOfFilesProcessed = amountOfFilesProcessed;
    }

    @Override
    public void run() {
        List<Transaction> transactions = transactionReader.readTransactionsFromFile(file);
        for (Transaction transaction : transactions) {
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
            transactionAnalyzer.addTransaction(transaction);
        }
        amountOfFilesProcessed.incrementAndGet();

    }
}
