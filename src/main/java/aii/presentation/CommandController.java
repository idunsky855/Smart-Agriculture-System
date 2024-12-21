package aii.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aii.logic.CommandBoundary;
import aii.logic.CommandId;
import aii.logic.TargetObject;
import aii.logic.ObjectId;
import aii.logic.InvokedBy;
import aii.logic.UserId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;

@RestController
public class CommandController {

    private String applicationName;

    // Thread-safe in-memory map of CommandBoundary objects
    private final Map<String, CommandBoundary> commandsDB = Collections.synchronizedMap(new HashMap<>());

    private static final AtomicLong commandIdCounter = new AtomicLong(1); // Initialize with 1

    // Setter for the applicationName
    @Value("${spring.application.name:defaultAppName}")
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
		System.err.println("********" + this.applicationName);
    }

    // Getter:
    public String getApplicationName() {
        return this.applicationName;
    }

    // GET endpoint for fetching all commands as a HashMap
    @GetMapping("/aii/admin/commands")
    public CommandBoundary[] getAllCommands() {
        synchronized (commandsDB) {
            // Return array of the values in the map (CommandBoundary objects):
            return this.commandsDB.values().toArray(new CommandBoundary[0]);
        }
    }

    // POST endpoint for creating a new command
    @PostMapping("/aii/commands")
    @ResponseStatus(HttpStatus.CREATED)
    public CommandBoundary[] addNewCommand(@RequestBody CommandBoundary newCommand) {
        // Input validations:
        if (newCommand == null) {
            throw new RuntimeException("ERROR - Command is null");
        }

        if(newCommand.getCommand() == null) {
            throw new RuntimeException("ERROR - Command is null");
        }

        if (newCommand.getTargetObject() == null) {
            throw new RuntimeException("ERROR - TargetObject is null");
        }

        if (newCommand.getTargetObject().getObjectId() == null) {
            throw new RuntimeException("ERROR - ObjectId is null");
        }

        if (newCommand.getInvokedBy() == null) {
            throw new RuntimeException("ERROR - InvokedBy is null");
        }

        if (newCommand.getInvokedBy().getUserId() == null) {
            throw new RuntimeException("ERROR - UserId is null");
        }

        if (newCommand.getCommandId().getId() == null) {
            throw new RuntimeException("ERROR - CommandId - Id is null");
        }

        if (newCommand.getCommandId().getSystemID() == null) {
            throw new RuntimeException("ERROR - CommandId - SystemID is null");
        }

        if (newCommand.getTargetObject().getObjectId().getId() == null) {
            throw new RuntimeException("ERROR - ObjectId - Id is null");
        }

        if (newCommand.getTargetObject().getObjectId().getSystemID() == null) {
            throw new RuntimeException("ERROR - ObjectId - SystemID is null");
        }

        if (newCommand.getInvokedBy().getUserId().getEmail() == null) {
            throw new RuntimeException("ERROR - UserId - Email is null");
        }

        if (newCommand.getInvokedBy().getUserId().getSystemID() == null) {
            throw new RuntimeException("ERROR - UserId - SystemID is null");
        }

        // Passed validations:
        // Generate a unique commandId
        CommandId generatedCommandId = new CommandId();

        // Read the systemID from the properties file in src/resources/application.properties:
        generatedCommandId.setSystemID(getApplicationName());
        generatedCommandId.setId(String.valueOf(commandIdCounter.getAndIncrement()));

        // Set the generated commandId in the new command
        newCommand.setCommandId(generatedCommandId);

        // Temp TargetObject object to set the ObjectId
        TargetObject generatedTargetObject = new TargetObject();

        // Temp ObjectId object to set the systemID and id
        ObjectId generatedObjectId = new ObjectId();

        // Set the systemID and id in the ObjectId object
        generatedObjectId.setSystemID(newCommand.getTargetObject().getObjectId().getSystemID());
        generatedObjectId.setId(newCommand.getTargetObject().getObjectId().getId());

        // Set the ObjectId in the TargetObject
        generatedTargetObject.setObjectId(generatedObjectId);

        // Set the TargetObject in the new command
        newCommand.setTargetObject(generatedTargetObject);

        // Temp InvokedBy object to set the UserId
        InvokedBy generatedInvokedBy = new InvokedBy();

        // Temp UserId object to set the systemID (copy email from the input command)
        UserId generatedUserId = new UserId();

        // Set the systemID
        generatedUserId.setSystemID(getApplicationName());

        // Copy the email from the input command
        generatedUserId.setEmail(newCommand.getInvokedBy().getUserId().getEmail());

        // Set the UserId in the InvokedBy object
        generatedInvokedBy.setUserId(generatedUserId);

        // Set the InvokedBy in the new command
        newCommand.setInvokedBy(generatedInvokedBy);

        // Set the timestamp for this time no matter what the input is:
        newCommand.setInvocationTimestamp(new Date());

        // Use the commandId as the key in the map
        String commandIdKey = generatedCommandId.getSystemID() + "@@" + generatedCommandId.getId();
        commandsDB.put(commandIdKey, newCommand);

        System.out.println("DEBUG - New command added: " + newCommand.toString());

        // Return an array of the new command:
        return new CommandBoundary[] {newCommand};

    }

    // DELETE all commands
    @DeleteMapping("/aii/admin/commands")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllCommands() {
        synchronized (commandsDB) {
            this.commandsDB.clear();
        }
        System.out.println("WARN - All commands have been deleted");
    }
}
