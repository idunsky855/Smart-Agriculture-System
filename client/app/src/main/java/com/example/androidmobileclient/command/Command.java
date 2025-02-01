package com.example.androidmobileclient.command;

import com.example.androidmobileclient.plant.Plant;
import com.example.androidmobileclient.user.UserId;

import java.util.Date;
import java.util.Map;

public class Command {
    private CommandId commandId;
    private String command;
    private TargetObject targetObject;
    private Date invocationTimestamp;
    private InvokedBy invokedBy;
    private Map<String, Object> commandAttributes;

    public Command() {
    }

    public String getCommand() {
        return command;
    }

    public Command setCommand(String command) {
        this.command = command;
        return this;
    }

    public TargetObject getTargetObject() {
        return targetObject;
    }

    public Command setTargetObject(TargetObject targetObject) {
        this.targetObject = targetObject;
        return this;
    }

    public InvokedBy getInvokedBy() {
        return invokedBy;
    }

    public Command setInvokedBy(InvokedBy invokedBy) {
        this.invokedBy = invokedBy;
        return this;
    }


    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    public Command setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
        return this;
    }

    public Command setInvokedBy(String systemID, String userEmail) {
        this.invokedBy = new InvokedBy(new UserId(systemID,userEmail));
        return this;
    }

    public Command setTargetObject(String systemID, String id) {
        this.targetObject = new TargetObject(systemID,id);
        return this;
    }

    public class CommandId{
        public String systemID;
        public String id;
    }

    public class InvokedBy{
        public UserId userId;

        public InvokedBy(UserId userId) {
            this.userId = userId;
        }
    }

    public class ObjectId{
        public String systemID;
        public String id;

    }


    public class TargetObject{
        public ObjectId objectId;

        public TargetObject(ObjectId objectId) {
            this.objectId = objectId;
        }

        public TargetObject(String systemID, String id) {
            this.objectId = new ObjectId();
            this.objectId.systemID = systemID;
            this.objectId.id = id;
        }
    }


}
