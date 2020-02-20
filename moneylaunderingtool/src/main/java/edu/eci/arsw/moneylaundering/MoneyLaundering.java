package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private static TransactionAnalyzer transactionAnalyzer;
    private static TransactionReader transactionReader;
    private static int amountOfFilesTotal;
    private static AtomicInteger amountOfFilesProcessed;
    private static int numThreads;
    private static int numFiles;
    private static boolean pause;
    private static Thread[] threads;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
        pause = false; //Paralelice
        numThreads = 5; //Cambiar numero de Hilos, si es requerido
        numFiles=3; //BONO archivos peados (en este caso son 3)
        threads = new Thread[numThreads+numFiles]; // BONO modificado por bono
        
    }

    public void processTransactionData(List<File> transactionFiles){
        for (File transactionFile : transactionFiles) {
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for (Transaction transaction : transactions) {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }
        
        // Se pasa este codigo a la clase hilos
        /*amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        for(File transactionFile : transactionFiles)
        {            
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                transactionAnalyzer.addTransaction(transaction);
            }
            amountOfFilesProcessed.incrementAndGet();
        }*/
    }

    /**
     * 
     * @return 
     */
    public List<String> getOffendingAccounts(){
        return transactionAnalyzer.listOffendingAccounts();
    }

    /**
     * 
     * @return 
     */
    private static List<File> getTransactionFileList(){
      
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
        
        
    }
    
    
    /**
     * 
     * @param moneyLaundering 
     */
    public static void processAnalizer(MoneyLaundering moneyLaundering) {
        String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
        List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
        String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2) -> s1 + "\n" + s2);
        message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
        System.out.println(message);
    }
    
    /**
     * 
     * @return 
     */
    public static boolean onPause() {
        return pause;
    }


    /**
     * 
     * @param args 
     */
    public static void main(String[] args){
        System.out.println(getBanner());//banner
        
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        int rangeThreads = (amountOfFilesTotal-numFiles) / numThreads;
        int init = numFiles; //Se sabe que hay 3 archivos pesados
        int range;
        for (int i = 0; i < numThreads+numFiles; i++) {
            // Se seleccionan primero los 3 archivos pesados. ya que tienen diferente nombre a los demas
            // dataFeb-19-2020_0 / dataFeb-19-2020_1 / dataFeb-19-2020_2
            System.out.println("Transaccion: "+transactionFiles.get(i).getName());
            System.out.println("amount: "+rangeThreads);
            if(i<numFiles){
                //BOMO, se crea otro espacio de hilo solo para estos 3 archivos pesados
                threads[i] = new FileThread(transactionReader, transactionAnalyzer, transactionFiles.get(i), amountOfFilesProcessed);
            }
            else{
                //Punto 1, se hace el analisis con los archivos livianos
                range = rangeThreads;
                if (i == numThreads - 1) {
                    range += ((amountOfFilesTotal-numFiles) % numThreads);
                }
                int nfinal = init + range;
                List<File> files = transactionFiles.subList(init, nfinal); //Archivos a analizar
                threads[i] = new MoneyThread(transactionReader, transactionAnalyzer, files, amountOfFilesProcessed);
                init += rangeThreads;
            }
            threads[i].start(); //Iniciamos los hilos
        }
        System.out.println(getHelp()); // Ayuda
        //Mientras el proceso, para  Paralelizar el proceso
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String enter = scanner.nextLine();
            if (enter.isEmpty()) {
                pause = !pause;
                if (onPause()) {
                    System.out.println("Ha activado el modo pausa");
                    processAnalizer(moneyLaundering);
                } else {
                    System.out.println("Ha desactivado el modo pausa, Esta ejecutando el programa");
                    System.out.println("Al finalizar el programa, presione enter para ver el proceso");
                }
            }
        }
        /*
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData());
        processingThread.start();
        while(true)
        {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
            {
                System.exit(0);
            }
            
            ///>>>> Aca se debe retirar este codigo para poder ejecutarlo en vaios hilos 
            ///>>>> Nueva processAnalzer
            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);
            ///<<<<<
        }*/
    }

    private static String getBanner(){
        String banner = "\n";
        try {
            banner = String.join("\n", Files.readAllLines(Paths.get("src/main/resources/banner.ascii")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return banner;
    }

    private static String getHelp(){
        String help = "Type 'exit' to exit the program. Press 'Enter' to get a status update\n";
        return help;
    }
}
