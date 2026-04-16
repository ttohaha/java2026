package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.MediaFileServiceFactory;
import blinov_first.service.MediaFileService;
import blinov_first.util.AttributeName;
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
        if (idStr == null) return PagePath.MEDIA_LIST;

        try {
            int fileId = Integer.parseInt(idStr);
            MediaFileService service = MediaFileServiceFactory.getMediaFileService();
            boolean success = service.deleteFile(fileId, userId);

            if (success) {
                request.setAttribute(AttributeName.SUCCESS_MSG, "File deleted successfully");
            } else {
                request.setAttribute(AttributeName.ERROR_MSG, "Failed to delete file");
            }
            return "redirect:/controller?command=list_files";
        } catch (NumberFormatException | ServiceException e) {
            LOGGER.error("File deletion failed", e);
            request.setAttribute(AttributeName.ERROR_MSG, "Deletion error: " + e.getMessage());
            return PagePath.MEDIA_LIST;
        }
    }
}