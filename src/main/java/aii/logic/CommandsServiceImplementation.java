package aii.logic;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aii.dal.CommandsCrud;
import aii.data.CommandEntity;
import aii.data.UserRole;
import aii.logic.exceptions.InvalidCommandException;
import aii.logic.exceptions.InvalidInputException;
import aii.logic.exceptions.UserUnauthorizedException;
import aii.logic.utilities.EmailValidator;




@Service
public class CommandsServiceImplementation implements EnhancedCommandService {
    private CommandsCrud commands;
    private String springApplicationName;
    private EmailValidator emailValidator;
    private EnhancedUsersService users;
    private EnhancedObjectsService objects;



    public CommandsServiceImplementation(CommandsCrud commands, EnhancedUsersService users, EnhancedObjectsService objects) {
        this.commands = commands;
        this.users = users;
        this.objects = objects;
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

        // Validate user's permission:
        UserRole role = this.users.getUserRole(newCommand.getInvokedBy().getUserId().getSystemID(),
                newCommand.getInvokedBy().getUserId().getEmail());

        if (role != UserRole.END_USER) {
            throw new InvalidCommandException("ERROR - User does not have permission to invoke commands");
        }

        ObjectId obj = newCommand.getTargetObject().getObjectId();
        UserId ui = newCommand.getInvokedBy().getUserId();

        // Check for object existence and active is true (this method will throw if the conditions are not met.)
        Optional<ObjectBoundary> op = this.objects.getSpecificObject(ui.getSystemID(), ui.getEmail(), obj.getSystemID(), obj.getId());

        // Passed all validations, create and save the command entity:
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
    @Deprecated
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail) {
        throw new UnsupportedOperationException("ERROR - Deprecated method");
        /*
        // Validate admin credentials
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("ERROR - Admin credentials are required");
        }

        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("ERROR - Invalid email format");
        }

        UserRole role = this.users.getUserRole(adminSystemID, adminEmail);

        if (role != UserRole.ADMIN) {
            throw new UserUnauthorizedException("ERROR - User does not have permission to view all commands");
        }

        // Fetch all command entities from the DB
        List<CommandEntity> commandEntities = this.commands.findAll();

        // Convert CommandEntity to CommandBoundary for response
        List<CommandBoundary> commandBoundaries = new ArrayList<>();
        for (CommandEntity entity : commandEntities) {
            commandBoundaries.add(new CommandBoundary(entity));
        }

        return commandBoundaries;
        */
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail, int page, int size) {
        // Validate admin credentials
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("ERROR - Admin credentials are required");
        }

        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("ERROR - Invalid email format");
        }

        UserRole role = this.users.getUserRole(adminSystemID, adminEmail);

        if (role != UserRole.ADMIN) {
            throw new UserUnauthorizedException("ERROR - User does not have permission to view all commands");
        }

        return this.commands
            .findAll(PageRequest.of(page, size, Direction.DESC, "invocationTimestamp", "commandId"))
            .stream()
            .skip(page * size)
            .limit(size)
            .map(CommandBoundary::new)
            .toList();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllCommands(String adminSystemID, String adminEmail) {
        if (adminSystemID == null || adminEmail == null) {
            throw new InvalidInputException("[ERROR] - Admin credentials are required");
        }

        if (!emailValidator.isEmailValid(adminEmail)) {
            throw new InvalidInputException("[ERROR] - Invalid email format");
        }

        UserRole role = this.users.getUserRole(adminSystemID, adminEmail);

        if (role != UserRole.ADMIN) {
            throw new UserUnauthorizedException("[ERROR] - User does not have permission to delete all commands");
        }

        this.commands.deleteAll();
        System.out.println("[WARN] - All commands deleted by admin: " + adminSystemID + " / " + adminEmail);
    }

}
