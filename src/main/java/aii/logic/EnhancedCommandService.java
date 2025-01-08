package aii.logic;

import java.util.List;

public interface EnhancedCommandService extends CommandsService {

    public List<CommandBoundary> getAllCommands(String adminSystemID, String adminEmail, int page, int size);

}
