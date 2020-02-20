package edu.eci.arsw.exams.moneylaunderingapi.service;

import edu.eci.arsw.exams.moneylaunderingapi.model.MoneyLauderingNotFoundException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;

import java.util.List;

public interface MoneyLaunderingService {
    void updateAccountStatus(SuspectAccount suspectAccount) throws MoneyLauderingNotFoundException;
    SuspectAccount getAccountStatus(String accountId) throws MoneyLauderingNotFoundException;
    List<SuspectAccount> getSuspectAccounts();
    void addSuspectAccount(SuspectAccount suspectAccount) throws MoneyLauderingNotFoundException;
    void addSuspectAmount(SuspectAccount suspectAccount) throws MoneyLauderingNotFoundException;
    
}
