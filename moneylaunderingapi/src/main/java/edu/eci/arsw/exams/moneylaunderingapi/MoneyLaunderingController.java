package edu.eci.arsw.exams.moneylaunderingapi;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import edu.eci.arsw.exams.moneylaunderingapi.model.MoneyLauderingNotFoundException;
import edu.eci.arsw.exams.moneylaunderingapi.model.SuspectAccount;
import edu.eci.arsw.exams.moneylaunderingapi.service.MoneyLaunderingService;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
@RequestMapping(value = "/fraud-bank-accounts")
public class MoneyLaunderingController
{
    MoneyLaunderingService mlService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> offendingAccounts() {
        return new ResponseEntity<>(mlService.getSuspectAccounts(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET , path = "/{accountId}")
    public ResponseEntity<?> accountStatus(@PathVariable String accountId) throws MoneyLauderingNotFoundException{
        try{
            Map<String, SuspectAccount> suspect = new HashMap();
            List<SuspectAccount> susList = new ArrayList<>();
            susList.addAll((Collection<? extends SuspectAccount>) mlService.getAccountStatus(accountId));
            
            for (SuspectAccount x : susList) {
                suspect.put(x.accountId, x);
            }
            

            String data = new Gson().toJson(suspect);
            return new ResponseEntity<>(data, HttpStatus.ACCEPTED);
        } catch (MoneyLauderingNotFoundException ex) {
            Logger.getLogger(MoneyLaunderingAPIApplication.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("ERROR, ", HttpStatus.NOT_FOUND);
        }
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addAccountId(@RequestBody String bp) throws MoneyLauderingNotFoundException {
        //Formato json {"bp5":{"author":"Isaza","points":[{"x":180,"y":181},{"x":190,"y":190}],"name":"bp5"}}
        try {
            //System.out.println("data"+bp);
            Type jsonToList = new TypeToken<Map<String, SuspectAccount>>() {}.getType();
            
            Map<String, SuspectAccount> data = new Gson().fromJson(bp, jsonToList);

            Object[] keys = data.keySet().toArray();
            
            //System.out.println("llave"+keys[0].toString());

            mlService.addSuspectAmount(data.get(keys[0]));

            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (MoneyLauderingNotFoundException ex) {
            Logger.getLogger(MoneyLaunderingAPIApplication.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("ERROR. >> No se ha podido añadir ", HttpStatus.FORBIDDEN);
        }
    }
    
    @RequestMapping(method = RequestMethod.PUT, path = "{accountId}")
    public ResponseEntity<?> addAmountByAccountId(@PathVariable("accountId") String accountId,
            @PathVariable("amount") String amount) throws MoneyLauderingNotFoundException {
        //Formato json {"suspect":{"1":"2"}}
        try {
            //System.out.println("data"+bp);
            SuspectAccount selectSus = mlService.getAccountStatus(accountId);

            Type jsonToList = new TypeToken<Map<String, int[]>>() {
            }.getType();
            Map<String, int[]> data = new Gson().fromJson(amount, jsonToList);

            Object[] keys = data.keySet().toArray();
            
            //System.out.println("llave"+keys[0].toString());

            selectSus.setAmount(Arrays.asList(data.get(keys[0])));

            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (MoneyLauderingNotFoundException ex) {
            Logger.getLogger(MoneyLaunderingAPIApplication.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("ERROR. >> No se ha podido añadir", HttpStatus.FORBIDDEN);
        }
    }
    
      
}
