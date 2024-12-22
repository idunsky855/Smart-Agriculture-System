package aii.logic;
import java.util.List;


public interface CommandsService {

    public List<Object> invokeCommand(CommandBoundary command);
    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail);
    public void deleteAllCommands(String adminSystemID, String adminEmail);
}
