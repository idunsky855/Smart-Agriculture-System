package aii.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aii.dal.CommandsCrud;
import aii.data.CommandEntity;
import aii.logic.exceptions.InvalidCommandException;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.utilities.EmailValidator;


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
    @Transactional(readOnly = false)
    public List<Object> invokeCommand(CommandBoundary newCommand) {
        // Input validations:
        if (newCommand == null) {
            throw new InvalidCommandException("ERROR - Command is null");
        }

        if (newCommand.getCommand() == null) {
            throw new InvalidCommandException("ERROR - Command is null");
        }

        if (newCommand.getCommand().isEmpty()) {
            throw new InvalidCommandException("ERROR - Command is empty");
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

        if (newCommand.getCommandId().getSystemID().isEmpty()) {
            throw new InvalidCommandException("ERROR - CommandId - SystemID is empty");
        }

        if (newCommand.getTargetObject().getObjectId().getId() == null) {
            throw new InvalidCommandException("ERROR - ObjectId - Id is null");
        }

        if (newCommand.getTargetObject().getObjectId().getSystemID() == null) {
            throw new InvalidCommandException("ERROR - ObjectId - SystemID is null");
        }

        if (newCommand.getTargetObject().getObjectId().getSystemID().isEmpty()) {
            throw new InvalidCommandException("ERROR - ObjectId - SystemID is empty");
        }

        if (newCommand.getInvokedBy().getUserId().getEmail() == null) {
            throw new InvalidCommandException("ERROR - UserId - Email is null");
        }

        if (!emailValidator.isEmailValid(newCommand.getInvokedBy().getUserId().getEmail())) {
            throw new InvalidCommandException("ERROR - UserId - Invalid email format");
        }

        if (newCommand.getInvokedBy().getUserId().getSystemID() == null) {
            throw new InvalidCommandException("ERROR - UserId - SystemID is null");
        }

        if (newCommand.getInvokedBy().getUserId().getSystemID().isEmpty()) {
            throw new InvalidCommandException("ERROR - UserId - SystemID is empty");
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
    @Transactional(readOnly = true)
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail) {
        // Validate admin credentials
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("ERROR - Admin credentials are required");
        }

        // TODO: Validate admin credentials

        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("ERROR - Invalid email format");
        }


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
    @Transactional(readOnly = false)
    public void deleteAllCommands(String adminSystemID, String adminEmail) {
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("[ERROR] - Admin credentials are required");
        }

        // TODO: Validate admin credentials

        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("[ERROR] - Invalid email format");
        }


        this.commands.deleteAll();
        System.out.println("[WARN] - All commands deleted by admin: " + adminSystemID + " / " + adminEmail);
    }

}
