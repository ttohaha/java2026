package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.MediaFileServiceFactory;
import blinov_first.util.AttributeName;
import blinov_first.util.FlashMessage;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeleteFileCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(DeleteFileCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) return PagePath.INDEX;

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) return PagePath.INDEX;

        String idStr = request.getParameter(AttributeName.FILE_ID);
        if (idStr == null) return "redirect:/controller?command=list_files";

        try {
            int fileId = Integer.parseInt(idStr);
            if (MediaFileServiceFactory.getMediaFileService().deleteFile(fileId, userId)) {
                FlashMessage.success(request, "File deleted successfully");
            } else {
                FlashMessage.error(request, "Failed to delete file");
            }
        } catch (NumberFormatException | ServiceException e) {
            LOGGER.error("File deletion failed", e);
            FlashMessage.error(request, "Deletion error: " + e.getMessage());
        }

        return "redirect:/controller?command=list_files";
    }
}
