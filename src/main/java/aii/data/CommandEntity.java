package aii.data;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import aii.logic.CommandId;
import aii.logic.InvokedBy;
import aii.logic.TargetObject;
import aii.logic.converters.InvokedByConverter;
import aii.logic.converters.MapToStringConverter;
import aii.logic.converters.TargetObjectConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "COMMANDS")
public class CommandEntity {

    @Id
    private String commandId;

    @Column(name = "command_value")
    private String command;

    @Convert(converter = TargetObjectConverter.class)
    private TargetObject targetObject;

    @Convert(converter = InvokedByConverter.class)
    private InvokedBy invokedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date invocationTimestamp;

    @Lob
    @Convert(converter = MapToStringConverter.class)
    private Map<String, Object> commandAttributes;

    public CommandEntity() {
        this.commandAttributes = new HashMap<>();
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public void setCommandId(CommandId commandId){
        this.commandId = commandId.getSystemID() + "@@" + commandId.getId();
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

    public InvokedBy getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(InvokedBy invokedBy) {
        this.invokedBy = invokedBy;
    }

    public Date getInvocationTimestamp() {
        return invocationTimestamp;
    }

    public void setInvocationTimestamp(Date invocationTimestamp) {
        this.invocationTimestamp = invocationTimestamp;
    }

    public Map<String, Object> getCommandAttributes() {
        return commandAttributes;
    }

    public void setCommandAttributes(Map<String, Object> commandAttributes) {
        this.commandAttributes = commandAttributes;
    }

    @Override
    public String toString() {
        return "CommandEntity [commandId=" + commandId + ", command=" + command + ", targetObject=" + targetObject
                + ", invokedBy=" + invokedBy + ", invocationTimestamp=" + invocationTimestamp + ", commandAttributes="
                + commandAttributes + "]";
    }
}
