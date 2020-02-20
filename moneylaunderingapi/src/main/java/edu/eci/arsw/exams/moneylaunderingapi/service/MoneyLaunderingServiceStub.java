package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.MoneyLauderingNotFoundException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;


@Service
public class MoneyLaunderingServiceStub implements MoneyLaunderingService {
    List<SuspectAccount> suspectAccounts = new CopyOnWriteArrayList<>();

    public MoneyLaunderingServiceStub(){
        suspectAccounts.add(new SuspectAccount("14",1));
        suspectAccounts.add(new SuspectAccount("16",4));
        suspectAccounts.add(new SuspectAccount("34",7));
        suspectAccounts.add(new SuspectAccount("1313",10));
    }
    @Override
    public void updateAccountStatus(SuspectAccount suspectAccount) throws MoneyLauderingNotFoundException {
        SuspectAccount target=null;
        for(SuspectAccount suspect:suspectAccounts){
            if(suspect.getAccountId().equals(suspectAccount.getAccountId())){
                target = suspect;
            }
        }
        if(target==null){
            throw new MoneyLauderingNotFoundException("Error, >> Usuario No existente");
        }
        target.setAmount(suspectAccount.getAmount());
    }

    @Override
    public SuspectAccount getAccountStatus(String accountId) throws MoneyLauderingNotFoundException {
        SuspectAccount suspect=null;
        for(SuspectAccount suspectAccount:suspectAccounts){
            if(suspectAccount.getAccountId().equals(accountId)){
                suspect = suspectAccount;
            }
        }
        if(suspect==null){
            throw new MoneyLauderingNotFoundException("Error, >> Usuario No existente");
        }
        return suspect;
    }

    @Override
    public List<SuspectAccount> getSuspectAccounts() {
        return suspectAccounts;
    }

    @Override
    public void addSuspectAccount(SuspectAccount suspectAccount) throws MoneyLauderingNotFoundException {
        boolean found = false;
        for(SuspectAccount suspect:suspectAccounts){
            if(suspect.getAccountId().equals(suspectAccount.getAccountId())){
                found=true;
            }
        }
        if(found){
            throw new MoneyLauderingNotFoundException("Error, >> Usuario ya Exixtente");
        }
        suspectAccounts.add(suspectAccount);
    }

    @Override
    public void addSuspectAmount(SuspectAccount suspectAccount) throws MoneyLauderingNotFoundException {
        SuspectAccount suspect=null;
        for(SuspectAccount sujeto:suspectAccounts){
            if(sujeto.getAccountId().equals(suspectAccount.getAccountId())){
                suspect = sujeto;
            }
        }
        if(suspect==null){
            throw new MoneyLauderingNotFoundException("Error, >> Usuaro no existente");
        }
        suspect.AddAmount(suspectAccount.getAmount());
    }
}
