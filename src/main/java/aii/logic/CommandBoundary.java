package aii.logic;

import java.util.Map;

import aii.data.CommandEntity;

import java.util.HashMap;
import java.util.Date;

public class CommandBoundary {

    private CommandId commandId;
    private String command;
    private TargetObject targetObject;
    private Date invocationTimestamp;
    private InvokedBy invokedBy;
    private Map<String, Object> commandAttributes;

    public CommandBoundary() {
        this.commandAttributes = new HashMap<>();
    }

    public CommandBoundary(CommandEntity entity) {
        this();
        String[] cmdIdStr = entity.getCommandId().split("@@");
        this.commandId = new CommandId(cmdIdStr[0], cmdIdStr[1]);
        this.command = entity.getCommand();
        this.targetObject = entity.getTargetObject();
        this.invocationTimestamp = entity.getInvocationTimestamp();
        this.invokedBy = entity.getInvokedBy();
        this.commandAttributes = entity.getCommandAttributes();
    }

    // Getters and setters
    public CommandId getCommandId() {
        return commandId;
    }

    public void setCommandId(CommandId commandId) {
        this.commandId = commandId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TargetObject getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(TargetObject targetObject) {
        this.targetObject = targetObject;
    }

    public Date getInvocationTimestamp() {
        return invocationTimestamp;
    }

    public void setInvocationTimestamp(Date invocationTimestamp) {
        this.invocationTimestamp = invocationTimestamp;
    }

    public InvokedBy getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(InvokedBy invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    public void setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }

    // toString method
    @Override
    public String toString() {
        return "CommandBoundary: {" + commandId
                + command + '\''
                + targetObject
                + invocationTimestamp.toString() + '\''
                + invokedBy
                + commandAttributes +
                '}';
    }
}
