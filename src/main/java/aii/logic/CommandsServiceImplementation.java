package aii.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import aii.dal.CommandsCrud;
import aii.data.CommandEntity;
import aii.logic.exceptions.InvalidCommandException;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.utilities.EmailValidator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CommandsServiceImplementation implements CommandsService {
    private CommandsCrud commands;
    private String springApplicationName;
    private EmailValidator emailValidator;



    public CommandsServiceImplementation(CommandsCrud commands){
        this.commands = commands;
        emailValidator = new EmailValidator();
    }

    @Value("${spring.application.name:defaultAppName}")
    public void setSpringApplicationName(String springApplicationName) {
        this.springApplicationName = springApplicationName;
        System.err.println("[DEBUG] - CommandsService " + this.springApplicationName);
    }

    @Override
    @Transactional
    public List<Object> invokeCommand(CommandBoundary newCommand) {
        // Input validations:
        if (newCommand == null) {
            throw new InvalidCommandException("ERROR - Command is null");
        }

        if(newCommand.getCommand() == null) {
            throw new InvalidCommandException("ERROR - Command is null");
        }

        if (newCommand.getTargetObject() == null) {
            throw new InvalidCommandException("ERROR - TargetObject is null");
        }

        if (newCommand.getTargetObject().getObjectId() == null) {
            throw new InvalidCommandException("ERROR - ObjectId is null");
        }

        if (newCommand.getInvokedBy() == null) {
            throw new InvalidCommandException("ERROR - InvokedBy is null");
        }

        if (newCommand.getInvokedBy().getUserId() == null) {
            throw new InvalidCommandException("ERROR - UserId is null");
        }

        if (newCommand.getCommandId().getId() == null) {
            throw new InvalidCommandException("ERROR - CommandId - Id is null");
        }

        if (newCommand.getCommandId().getSystemID() == null) {
            throw new InvalidCommandException("ERROR - CommandId - SystemID is null");
        }

        if (newCommand.getTargetObject().getObjectId().getId() == null) {
            throw new InvalidCommandException("ERROR - ObjectId - Id is null");
        }

        if (newCommand.getTargetObject().getObjectId().getSystemID() == null) {
            throw new InvalidCommandException("ERROR - ObjectId - SystemID is null");
        }

        if (newCommand.getInvokedBy().getUserId().getEmail() == null) {
            throw new InvalidCommandException("ERROR - UserId - Email is null");
        }

        if (newCommand.getInvokedBy().getUserId().getSystemID() == null) {
            throw new InvalidCommandException("ERROR - UserId - SystemID is null");
        }

        CommandEntity commandEntity = new CommandEntity();

        CommandId commandId = new CommandId(this.springApplicationName, UUID.randomUUID().toString());
        commandEntity.setCommandId(commandId);

        commandEntity.setCommand(newCommand.getCommand());
        commandEntity.setTargetObject(newCommand.getTargetObject());
        commandEntity.setInvocationTimestamp(new Date());
        commandEntity.setCommandAttributes(newCommand.getCommandAttributes());
        commandEntity.setInvokedBy(newCommand.getInvokedBy());


        this.commands.save(commandEntity);

        CommandBoundary response = new CommandBoundary(commandEntity);

        return List.of(response);

    }


    @Override
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail) {
        // Validate admin credentials
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("ERROR - Admin credentials are required");
        }

        // TODO: Validate admin credentials
        // TODO: Validate real email
        /*
        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("ERROR - Invalid email format");
        }
        */

        // Fetch all command entities from the DB
        List<CommandEntity> commandEntities = this.commands.findAll();

        // Convert CommandEntity to CommandBoundary for response
        List<CommandBoundary> commandBoundaries = new ArrayList<>();
        for (CommandEntity entity : commandEntities) {
            commandBoundaries.add(new CommandBoundary(entity));
        }

        return commandBoundaries;
    }

    @Override
    public void deleteAllCommands(String adminSystemID, String adminEmail) {
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("[ERROR] - Admin credentials are required");
        }

        // TODO: Validate admin credentials
        // TODO: Validate real email:
        /*
        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("[ERROR] - Invalid email format");
        }
        */

        this.commands.deleteAll();
        System.out.println("[WARN] - All commands deleted by admin: " + adminSystemID + " / " + adminEmail);
    }

}
