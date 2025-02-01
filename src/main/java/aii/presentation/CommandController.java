package aii.presentation;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import aii.logic.CommandBoundary;
import aii.logic.EnhancedCommandService;



@RestController
public class CommandController {

    private EnhancedCommandService commands;

    public CommandController(EnhancedCommandService commands) {
        this.commands = commands;
    }

    // GET endpoint for fetching all commands as a HashMap
    @GetMapping(
        path = {"/aii/admin/commands"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public CommandBoundary[] getAllCommands(
        @RequestParam(name = "userSystemID", required = true) String userSystemID,
        @RequestParam(name = "userEmail", required = true) String userEmail,
        @RequestParam(name = "page", required = false, defaultValue = "0") int page,
        @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        CommandBoundary[] rv = this.commands.getAllCommands(userSystemID, userEmail, page, size).toArray(new CommandBoundary[0]);
        return rv;
    }

    // POST endpoint for creating a new command
    @PostMapping(
        path = {"/aii/commands"},
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public Object[] addNewCommand(@RequestBody CommandBoundary newCommand) {

        // Pass the command to the service layer for processing
        List<Object> result = this.commands.invokeCommand(newCommand);

        // Convert the list to an array
        return result.toArray(new Object[0]);
    }

    // DELETE all commands
    @DeleteMapping("/aii/admin/commands")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllCommands(
        @RequestParam(name = "userSystemID", required=true) String userSystemID,
        @RequestParam(name = "userEmail", required=true) String userEmail) {
        this.commands.deleteAllCommands(userSystemID, userEmail);
    }
}
