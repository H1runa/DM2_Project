package com.hiruna.dm2_backend.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hiruna.dm2_backend.data.model.Account;
import com.hiruna.dm2_backend.data.model.SavingGoal;
import com.hiruna.dm2_backend.data.repo.AccountRepo;
import com.hiruna.dm2_backend.data.repo.SavingGoalRepo;

@Service
public class SavingGoalService {
    private SavingGoalRepo savingGoalRepo;
    private GenericEntityService genericEntityService;
    private AccountRepo accountRepo;
    private String syncUrl;

    public SavingGoalService(SavingGoalRepo savingGoalRepo, GenericEntityService genericEntityService, AccountRepo accountRepo){
        this.savingGoalRepo=savingGoalRepo;
        this.genericEntityService=genericEntityService;
        this.accountRepo=accountRepo;
        this.syncUrl="/api/savinggoal";
    }

    //creating
    public SavingGoal createSavingGoal(SavingGoal goal){
        return genericEntityService.insertRecord(savingGoalRepo, goal, syncUrl);
    }

    //update
    public Boolean updateSavingGoal(SavingGoal goal){
        return genericEntityService.updateRecord(savingGoalRepo, goal, syncUrl, (entity, updated)->{
            entity.setGoalName(updated.getGoalName());
            entity.setTargetAmount(updated.getTargetAmount());
            entity.setDeadline(updated.getDeadline());            
        });
    }

    //delete
    public Boolean deleteSavingGoal(Long id){
        return genericEntityService.deleteRecord(savingGoalRepo, id, syncUrl);
    }

    //sync all
    public void syncAll(){
        genericEntityService.syncAll(savingGoalRepo, syncUrl);
    }

    //getSavingGoals
    public List<Map<String,Object>> getSavingGoals(Long userID){
        List<Map<String,Object>> list = new ArrayList<>();

        Optional<List<Account>> opt_acc_list = accountRepo.findByUserID(userID);

        if (opt_acc_list.isPresent()){
            List<Account> acc_list = opt_acc_list.get();

            for (Account acc : acc_list){
                List<SavingGoal> goalList = savingGoalRepo.findByAccID(acc.getAccID());
                Map<String,Object> item = new HashMap<>();

                for (SavingGoal goal : goalList){                    

                    item.put("goalName", goal.getGoalName());
                    item.put("targetAmount", goal.getTargetAmount());
                    item.put("balance", acc.getBalance());

                    try{
                        String nlsDateFormatPattern = "dd-MMM-yy";
                        SimpleDateFormat formatter = new SimpleDateFormat(nlsDateFormatPattern);

                        LocalDate currentDay = LocalDate.now();
                        Date date = formatter.parse(goal.getDeadline());
                        LocalDate deadline = date.toInstant()       // convert Date → Instant
                                            .atZone(ZoneId.systemDefault()) // apply system time zone
                                            .toLocalDate();

                        Long days = ChronoUnit.DAYS.between(currentDay, deadline);
                        item.put("remainingDays", days);
                    } catch(Exception e){
                        //leaving this empty for now   
                    }

                    if (((acc.getBalance()/goal.getTargetAmount())*100) >= 100){
                        item.put("progressStatus", 100);
                    } else {
                        item.put("progressStatus", (acc.getBalance()/goal.getTargetAmount())*100);
                    }

                    if (acc.getBalance()<goal.getTargetAmount()){
                        item.put("status", "Pending");
                    } else {
                        item.put("status", "Achieved");
                    }
                }

                if (!item.isEmpty()){
                    list.add(new HashMap<>(item));
                }                
            }

            return list;

        } else {
            throw new RuntimeException("NOT_FOUND");
        }        

        
    }
    
}
