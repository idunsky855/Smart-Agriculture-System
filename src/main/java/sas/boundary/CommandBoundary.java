package sas.boundary;

import java.util.Map;
import java.util.HashMap;

public class CommandBoundary {

    private CommandId commandId;
    private String command;
    private TargetObject targetObject;
    private String invocationTimestamp;
    private InvokedBy invokedBy;
    private Map<String, Object> commandAttributes;

    public CommandBoundary() {
        this.commandAttributes = new HashMap<>();
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

    public String getInvocationTimestamp() {
        return invocationTimestamp;
    }

    public void setInvocationTimestamp(String invocationTimestamp) {
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
                + invocationTimestamp + '\''
                + invokedBy
                + commandAttributes +
                '}';
    }
}
