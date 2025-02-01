package aii.logic;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private Log logger = LogFactory.getLog(CommandsServiceImplementation.class);

    public CommandsServiceImplementation(CommandsCrud commands, EnhancedUsersService users,
            EnhancedObjectsService objects) {
        this.commands = commands;
        this.users = users;
        this.objects = objects;
        emailValidator = new EmailValidator();
    }

    @Value("${spring.application.name:defaultAppName}")
    public void setSpringApplicationName(String springApplicationName) {
        this.logger.trace("setSpringApplicationName(" + springApplicationName + ")");
        this.springApplicationName = springApplicationName;
        this.logger.debug(this.springApplicationName);
    }

    @Override
    @Transactional(readOnly = false)
    public List<Object> invokeCommand(CommandBoundary newCommand) {
        // Input validations:
        if (newCommand == null) {
            this.logger.error("Command is null");
            throw new InvalidCommandException("ERROR - Command is null");
        }

        this.logger.trace("invokeCommand(" + newCommand.toString() + ")");

        if (newCommand.getCommand() == null) {
            this.logger.error("Command is null");
            throw new InvalidCommandException("ERROR - Command is null");
        }

        if (newCommand.getCommand().isEmpty()) {
            this.logger.error("Command is empty");
            throw new InvalidCommandException("ERROR - Command is empty");
        }

        if (newCommand.getTargetObject() == null) {
            this.logger.error("TargetObject is null");
            throw new InvalidCommandException("ERROR - TargetObject is null");
        }

        if (newCommand.getTargetObject().getObjectId() == null) {
            this.logger.error("ObjectId is null");
            throw new InvalidCommandException("ERROR - ObjectId is null");
        }

        if (newCommand.getInvokedBy() == null) {
            this.logger.error("InvokedBy is null");
            throw new InvalidCommandException("ERROR - InvokedBy is null");
        }

        if (newCommand.getInvokedBy().getUserId() == null) {
            this.logger.error("UserId is null");
            throw new InvalidCommandException("ERROR - UserId is null");
        }

        if (newCommand.getTargetObject().getObjectId().getId() == null) {
            this.logger.error("ObjectId - Id is null");
            throw new InvalidCommandException("ERROR - ObjectId - Id is null");
        }

        if (newCommand.getTargetObject().getObjectId().getSystemID() == null) {
            this.logger.error("ObjectId - SystemID is null");
            throw new InvalidCommandException("ERROR - ObjectId - SystemID is null");
        }

        if (newCommand.getTargetObject().getObjectId().getSystemID().isEmpty()) {
            this.logger.error("ObjectId - SystemID is empty");
            throw new InvalidCommandException("ERROR - ObjectId - SystemID is empty");
        }

        if (newCommand.getInvokedBy().getUserId().getEmail() == null) {
            this.logger.error("UserId - Email is null");
            throw new InvalidCommandException("ERROR - UserId - Email is null");
        }

        if (!emailValidator.isEmailValid(newCommand.getInvokedBy().getUserId().getEmail())) {
            this.logger.error("UserId - Invalid email format");
            throw new InvalidCommandException("ERROR - UserId - Invalid email format");
        }

        if (newCommand.getInvokedBy().getUserId().getSystemID() == null) {
            this.logger.error("UserId - SystemID is null");
            throw new InvalidCommandException("ERROR - UserId - SystemID is null");
        }

        if (newCommand.getInvokedBy().getUserId().getSystemID().isEmpty()) {
            this.logger.error("UserId - SystemID is empty");
            throw new InvalidCommandException("ERROR - UserId - SystemID is empty");
        }

        // Validate user's permission:
        UserRole role = this.users.getUserRole(newCommand.getInvokedBy().getUserId().getSystemID(),
                newCommand.getInvokedBy().getUserId().getEmail());

        this.logger.debug("UserRole: " + role);

        if (role != UserRole.END_USER) {
            this.logger.error("User does not have permission to invoke commands");
            throw new InvalidCommandException("ERROR - User does not have permission to invoke commands");
        }

        ObjectId obj = newCommand.getTargetObject().getObjectId();
        UserId ui = newCommand.getInvokedBy().getUserId();

        // Check for object existence and active is true (this method will throw if the
        // conditions are not met.)
        ObjectBoundary op = this.objects
                .getSpecificObject(ui.getSystemID(), ui.getEmail(), obj.getSystemID(), obj.getId())
                .orElseThrow(() -> new InvalidCommandException("ERROR - Object not found"));

        this.logger.debug("Object found: " + op.toString());

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

        int size = 100;
        int page = 0;

        switch (newCommand.getCommand()) {
            case "Get_plants_for_watering":
                return this.objects.getPlantsForWatering(ui.getSystemID(), ui.getEmail(), size, page)
                        .stream()
                        .map(o -> (Object) o)
                        .collect(Collectors.toList());

            case "Water_plants": {
                List<ObjectBoundary> plantsToWater = this.objects.getPlantsForWatering(ui.getSystemID(), ui.getEmail(),
                        size, page);

                UserBoundary userBoundary = new UserBoundary();
                userBoundary.setUserId(ui);
                userBoundary.setRole(UserRole.OPERATOR);
                users.updateUser(ui.getSystemID(), ui.getEmail(), userBoundary); // update user role to
                                                                                 // operator - in order to
                                                                                 // update object
    
                for (Iterator iterator = plantsToWater.iterator(); iterator.hasNext();) {
                    ObjectBoundary objectBoundary = (ObjectBoundary) iterator.next();
                    Map<String, Object> details = objectBoundary.getObjectDetails();
                    Set<String> keys = details.keySet();


                    // If there is an entry with key isRaining with boolean value - check it
                    // If there isn't an isRaining entry - default isRaining value is false
                    
                    Boolean isRaining;
                    if (details.get("isRaining") == null ){
                        isRaining = false;
                        details.put("isRaining",isRaining);
                    } else if (details.get("isRaining") instanceof Boolean){
                        isRaining = (Boolean) details.get("isRaining");
                    } else {
                        logger.error("Water_plants command: object " + objectBoundary.getObjectId() +  ", contains illegal isRaining!");
                        throw new InvalidInputException("Could not water " + objectBoundary.getObjectId().getId() + " right now due to invalid isRaining, try again later!");
                        
                    }
                

                    if (!isRaining) {
                        if (keys.contains("optimalSoilMoistureLevel") &&
                                details.get("optimalSoilMoistureLevel") != null &&
                                details.get("optimalSoilMoistureLevel") instanceof Integer) {
                            details.put("currentSoilMoistureLevel", details.get("optimalSoilMoistureLevel"));


                            objects.update(ui.getSystemID(), ui.getEmail(), objectBoundary.getObjectId().getSystemID(), objectBoundary.getObjectId().getId(),
                                    objectBoundary);
                            
                        }
                    }

                }
                userBoundary.setRole(UserRole.END_USER); // restore end user permissions
                users.updateUser(ui.getSystemID(), ui.getEmail(), userBoundary);

                // now, get all plants that need watering after the watering (should return the
                // plants with isRaining = true)
                plantsToWater = this.objects.getPlantsForWatering(ui.getSystemID(), ui.getEmail(),
                        size, page);
                return plantsToWater.stream()
                        .map(o -> (Object) o)
                        .collect(Collectors.toList());
            }
            default:
                break;
        }

        CommandBoundary response = new CommandBoundary(commandEntity);

        this.logger.debug("Command saved: " + response.toString());

        return List.of(response);

    }

    @Override
    @Deprecated
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail) {
        this.logger.trace("getAllCommands(" + adminSystemID + ", " + adminEmail + ")");
        this.logger.error("Deprecated method");
        throw new UnsupportedOperationException("ERROR - Deprecated method");
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail, int page, int size) {
        this.logger.trace("getAllCommands(" + adminSystemID + ", " + adminEmail + ", " + page + ", " + size + ")");

        // Validate admin credentials
        if (adminSystemID == null || adminEmail == null) {
            this.logger.error("Admin credentials are required");
            throw new InvalidInputException("ERROR - Admin credentials are required");
        }

        if (!emailValidator.isEmailValid(adminEmail)) {
            this.logger.error("Invalid email format");
            throw new InvalidInputException("ERROR - Invalid email format");
        }

        UserRole role = this.users.getUserRole(adminSystemID, adminEmail);

        this.logger.debug("UserRole: " + role.toString());

        if (role != UserRole.ADMIN) {
            this.logger.error("User does not have permission to view all commands");
            throw new UserUnauthorizedException("ERROR - User does not have permission to view all commands");
        }

        this.logger.debug("Returning all commands");

        return this.commands
                .findAll(PageRequest.of(page, size, Direction.DESC, "invocationTimestamp", "commandId"))
                .stream()
                .map(CommandBoundary::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAllCommands(String adminSystemID, String adminEmail) {
        this.logger.trace("deleteAllCommands(" + adminSystemID + ", " + adminEmail + ")");

        // Validate admin credentials
        if (adminSystemID == null || adminEmail == null) {
            this.logger.error("Admin credentials are required");
            throw new InvalidInputException("[ERROR] - Admin credentials are required");
        }

        if (!emailValidator.isEmailValid(adminEmail)) {
            this.logger.error("Invalid email format");
            throw new InvalidInputException("[ERROR] - Invalid email format");
        }

        UserRole role = this.users.getUserRole(adminSystemID, adminEmail);

        this.logger.debug("UserRole: " + role.toString());

        if (role != UserRole.ADMIN) {
            this.logger.error("User does not have permission to delete all commands");
            throw new UserUnauthorizedException("[ERROR] - User does not have permission to delete all commands");
        }

        this.commands.deleteAll();
        this.logger.warn("All commands deleted by admin: " + adminSystemID + " / " + adminEmail);
    }

}
