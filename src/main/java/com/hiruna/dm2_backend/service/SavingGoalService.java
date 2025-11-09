package com.hiruna.dm2_backend.service;

import org.springframework.stereotype.Service;

import com.hiruna.dm2_backend.data.model.SavingGoal;
import com.hiruna.dm2_backend.data.repo.SavingGoalRepo;

@Service
public class SavingGoalService {
    private SavingGoalRepo savingGoalRepo;
    private GenericEntityService genericEntityService;
    private String syncUrl;

    public SavingGoalService(SavingGoalRepo savingGoalRepo, GenericEntityService genericEntityService){
        this.savingGoalRepo=savingGoalRepo;
        this.genericEntityService=genericEntityService;
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
    
}
