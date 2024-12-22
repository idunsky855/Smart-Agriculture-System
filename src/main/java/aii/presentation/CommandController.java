package aii.presentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aii.logic.CommandBoundary;
import aii.logic.CommandId;
import aii.logic.CommandsService;
import aii.logic.TargetObject;
import aii.logic.ObjectId;
import aii.logic.InvokedBy;
import aii.logic.UserId;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

import java.util.Date;

@RestController
public class CommandController {

    private CommandsService commands;
    private String applicationName;

    public CommandController(CommandsService commands) {
        this.commands = commands;
    }

    // GET endpoint for fetching all commands as a HashMap
    @GetMapping(
        path = {"/aii/admin/commands/{adminSystemID}/{adminEmail}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public CommandBoundary[] getAllCommands(
        @PathVariable("adminSystemID") String userSystemID,
        @PathVariable("adminEmail") String userEmail
    ) {
        CommandBoundary[] rv = this.commands.getAllCommands(userSystemID, userEmail).toArray(new CommandBoundary[0]);
        System.out.println("[DEBUG] - All commands: " + rv);
        return rv;
    }

    // POST endpoint for creating a new command
    @PostMapping(
        path = {"/aii/commands"},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommandBoundary[] addNewCommand(@RequestBody CommandBoundary newCommand) {

    // Pass the command to the service layer for processing
    List<Object> result = this.commands.invokeCommand(newCommand);

    // Ensure the returned list contains only CommandBoundary objects
    List<CommandBoundary> commandBoundaries =
        result
            .stream()
            .filter(obj -> obj instanceof CommandBoundary) // Ensure type safety
            .map(obj -> (CommandBoundary) obj)
            .collect(Collectors.toList());

    // Convert the list to an array
    return commandBoundaries.toArray(new CommandBoundary[0]);
}

    // DELETE all commands
    @DeleteMapping("/aii/admin/commands/{adminSystemID}/{adminEmail}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllCommands(
        @PathVariable("adminSystemID") String adminSystemID,
        @PathVariable("adminEmail") String adminEmail
    ) {
        try {
            this.commands.deleteAllCommands(adminSystemID, adminEmail);
            System.out.println("[INFO] - All commands have been deleted.");
        } catch (Exception e) {
            System.out.println("ERROR - Could not delete all commands by " + adminSystemID + " and " + adminEmail + " due to: " + e.getMessage());
        }
    }
}
